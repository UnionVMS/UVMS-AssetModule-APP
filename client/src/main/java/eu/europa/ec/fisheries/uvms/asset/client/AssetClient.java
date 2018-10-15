/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.client;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;

@Stateless
public class AssetClient {




    private WebTarget webTarget;


    private void setUpClient(Boolean testMode){
        Client client = ClientBuilder.newClient();
        client.register(new ContextResolver<ObjectMapper>() {
            @Override
            public ObjectMapper getContext(Class<?> type) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper;
            }
        });
        String assetEndpoint = getAssetEndpoint(testMode);
        webTarget = client.target(assetEndpoint + "internal/");

    }


    // postConstruct is default productionmode
    @PostConstruct
    public void postConstruct() throws ConfigServiceException {
        setUpClient(false);
    }


    // run from test execute this method for reinintialization
    public void setTestMode(){
        setUpClient(true);
    }


    // this is a temporary solution
    private String getAssetEndpoint(Boolean testMode) {

        if(testMode){
            return "http://localhost:8080/asset/rest/";
        }
        else{
            return "http://localhost:8080/UnionVMS/asset/rest/";
        }
    }

    public AssetDTO getAssetById(AssetIdentifier type, String value) {
        return webTarget
                .path("asset")
                .path(type.toString().toLowerCase())
                .path(value)
                .request(MediaType.APPLICATION_JSON)
                .get(AssetDTO.class);
    }
    
    public List<AssetDTO> getAssetList(AssetQuery query) {
        AssetListResponse assetResponse = webTarget
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
    
        return assetResponse.getAssetList();
    }
    
    public List<AssetDTO> getAssetList(AssetQuery query, boolean dynamic) {
        AssetListResponse assetResponse = webTarget
                .path("query")
                .queryParam("dynamic", dynamic)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
    
        return assetResponse.getAssetList();
    }
    
    public List<AssetDTO> getAssetList(AssetQuery query, int page, int size, boolean dynamic) {
        AssetListResponse assetResponse = webTarget
                    .path("query")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .queryParam("dynamic", dynamic)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(query), AssetListResponse.class);
        
        return assetResponse.getAssetList();
    }
    
    public List<AssetGroup> getAssetGroupsByUser(String user) {
        Response response = webTarget
                    .path("group")
                    .path("user")
                    .path(user)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
        List<AssetGroup> assetGroups = response.readEntity(new GenericType<List<AssetGroup>>() {});
        response.close();
        return assetGroups;
    }
    
    public List<AssetGroup> getAssetGroupByAssetId(UUID assetId) {
        Response response = webTarget
                .path("group")
                .path("asset")
                .path(assetId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<AssetGroup> assetGroups = response.readEntity(new GenericType<List<AssetGroup>>() {});
        response.close();
        return assetGroups;
    }
    
    /*
    @GET
    @Path("asset/group/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetByGroupIds(@PathParam("ids") List<UUID> groupIds) {
        List<AssetGroup> assetGroups = groupIds.stream()
                                            .map(assetGroupService::getAssetGroupById)
                                            .collect(Collectors.toList());
        List<AssetDTO> assets = assetService.getAssetListByAssetGroups(assetGroups);
        return Response.ok(assets).build();
    }
     */
    public List<AssetDTO> getAssetsByGroupIds(List<UUID> groupIds) {
        Response response = webTarget
                .path("group")
                .path("asset")
//                .path(groupIds)
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<AssetDTO> assets = response.readEntity(new GenericType<List<AssetDTO>>() {});
        response.close();
        return assets;
    }
    
    public AssetDTO upsertAsset(AssetBO asset) {
        return webTarget
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), AssetDTO.class);
    }
    
    public void upsertAssetAsync(AssetBO asset) throws MessageException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        Map<String, String> properties = new HashMap<>();
        properties.put("METHOD", "UPSERT_ASSET");
        new AbstractProducer() {
            @Override
            public String getDestinationName() {
                return MessageConstants.QUEUE_ASSET_EVENT;
            }
        }.sendModuleMessageWithProps(mapper.writeValueAsString(asset), null, properties);
    }

    public String ping() {
        return webTarget
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
    }


    public CustomCode createCustomCode(CustomCode customCode) {
        return webTarget
                .path("customcode")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(customCode), CustomCode.class);
    }


    public List<String> getConstants() {
        Response response = webTarget
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<String> constants = response.readEntity(new GenericType<List<String>>() {});
        response.close();
        return constants;
    }


    public List<CustomCode> getCodesForConstant(String constant) {
        Response response = webTarget
                .path("listcodesforconstant")
                .path(constant)
                .request(MediaType.APPLICATION_JSON)
                .get();
        
        List<CustomCode> codes = response.readEntity(new GenericType<List<CustomCode>>() {});
        response.close();
        return codes;
    }

    public Boolean isCodeValid(String constant, String code, OffsetDateTime date){
        String theDate = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return webTarget
                .path("verify")
                .path(constant)
                .path(code)
                .path(theDate)
                .request(MediaType.APPLICATION_JSON)
                .get(Boolean.class);
    }

    public List<CustomCode> getCodeForDate(String constant, String code, OffsetDateTime date) {
        String theDate = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Response response = webTarget
                .path("getfordate")
                .path(constant)
                .path(code)
                .path(theDate)
                .request(MediaType.APPLICATION_JSON)
                .get();

        List<CustomCode> codes = response.readEntity(new GenericType<List<CustomCode>>() {});
        response.close();
        return codes;
    }

    public CustomCode replace(CustomCode customCode) {
        return webTarget
                .path("replace")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(customCode), CustomCode.class);
    }


    public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) throws Exception {

        // @formatter:off
        Response ret =  webTarget
                .path("collectassetmt")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), Response.class);
        // @formatter:on

        AssetMTEnrichmentResponse response = ret.readEntity(new GenericType<AssetMTEnrichmentResponse>() {
        });

        return response;
    }





}