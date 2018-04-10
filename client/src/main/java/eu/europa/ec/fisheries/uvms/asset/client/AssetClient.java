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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.fisheries.uvms.asset.client.model.Asset;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

@Stateless
public class AssetClient {

    // TODO read from config?
    private static final String REST_END_POINT = "http://localhost:8080/asset/rest/internal";
    
    private Client client;
    
    public AssetClient() {
        client = ClientBuilder.newClient();
        client.register(new ContextResolver<ObjectMapper>() {
            @Override
            public ObjectMapper getContext(Class<?> type) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return mapper;
            }
        });
    }
    
    public Asset getAssetById(AssetIdentifier type, String value) {
        return client.target(REST_END_POINT)
                .path("asset")
                .path(type.toString().toLowerCase())
                .path(value)
                .request(MediaType.APPLICATION_JSON)
                .get(Asset.class);
    }
    
    public List<Asset> getAssetList(AssetQuery query) {
        AssetListResponse assetResponse = client.target(REST_END_POINT)
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
    
        return assetResponse.getAssetList();
    }
    
    public List<Asset> getAssetList(AssetQuery query, boolean dynamic) {
        AssetListResponse assetResponse = client.target(REST_END_POINT)
                .path("query")
                .queryParam("dynamic", dynamic)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(query), AssetListResponse.class);
    
        return assetResponse.getAssetList();
    }
    
    public List<Asset> getAssetList(AssetQuery query, int page, int size, boolean dynamic) {
        AssetListResponse assetResponse = client.target(REST_END_POINT)
                    .path("query")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .queryParam("dynamic", dynamic)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(query), AssetListResponse.class);
        
        return assetResponse.getAssetList();
    }
    
    public List<AssetGroup> getAssetGroupsByUser(String user) {
        Response response = client.target(REST_END_POINT)
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
        Response response = client.target(REST_END_POINT)
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
        List<Asset> assets = assetService.getAssetListByAssetGroups(assetGroups);
        return Response.ok(assets).build();
    }
     */
    public List<Asset> getAssetsByGroupIds(List<UUID> groupIds) {
        Response response = client.target(REST_END_POINT)
                .path("group")
                .path("asset")
//                .path(groupIds)
                .request(MediaType.APPLICATION_JSON)
                .get();
        List<Asset> assets = response.readEntity(new GenericType<List<Asset>>() {});
        response.close();
        return assets;
    }
    
    public Asset upsertAsset(Asset asset) {
        return client.target(REST_END_POINT)
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), Asset.class);
    }
    
    public void upsertAssetAsync(Asset asset) throws MessageException, JsonProcessingException {
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
        return client.target(REST_END_POINT)
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
    }
}