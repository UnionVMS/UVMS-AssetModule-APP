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
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetBO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetMTEnrichmentResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.client.model.CustomCode;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

@Stateless
public class AssetClient {

    private WebTarget webTarget;

    @Resource(name = "java:global/asset_endpoint")
    private String assetEndpoint;
    
    @PostConstruct
    private void setUpClient() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        clientBuilder.readTimeout(30, TimeUnit.SECONDS);
        Client client = clientBuilder.build();
        webTarget = client.target(assetEndpoint + "/internal");
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
        return webTarget
                .path("group")
                .path("user")
                .path(user)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<AssetGroup>>() {});
    }
    
    public List<AssetGroup> getAssetGroupByAssetId(UUID assetId) {
        return webTarget
                .path("group")
                .path("asset")
                .path(assetId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<AssetGroup>>() {});
    }

    public List<AssetDTO> getAssetsByGroupIds(List<UUID> groupIds) {
        return webTarget
                .path("group")
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(groupIds), new GenericType<List<AssetDTO>>(){});
    }
    
    public List<AssetDTO> getAssetHistoryListByAssetId(UUID id) {
        return webTarget
                .path("history/asset")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<AssetDTO>>() {});
    }
    
    public AssetDTO getAssetFromAssetIdAndDate(AssetIdentifier type, String value, OffsetDateTime date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return webTarget
                .path("history")
                .path(type.name().toLowerCase())
                .path(value)
                .path(formattedDate)
                .request(MediaType.APPLICATION_JSON)
                .get(AssetDTO.class);
    }

    public AssetDTO getAssetHistoryByAssetHistGuid(UUID historyId) {
        return webTarget
                .path("history")
                .path(historyId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(AssetDTO.class);
    }
    
    public AssetBO upsertAsset(AssetBO asset) {
        return webTarget
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(asset), AssetBO.class);
    }
    
    public void upsertAssetAsync(AssetBO asset) throws MessageException {
        Jsonb jsonb = JsonbBuilder.create();

        Map<String, String> properties = new HashMap<>();
        properties.put("METHOD", "UPSERT_ASSET");
        new AbstractProducer() {
            @Override
            public String getDestinationName() {
                return MessageConstants.QUEUE_ASSET_EVENT;
            }
        }.sendModuleMessageWithProps(jsonb.toJson(asset), null, properties);
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
        return webTarget
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<String>>() {});
        
    }

    public List<CustomCode> getCodesForConstant(String constant) {
        return webTarget
                .path("listcodesforconstant")
                .path(constant)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<CustomCode>>() {});
    }

    public Boolean isCodeValid(String constant, String code, OffsetDateTime date){
        String theDate = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String response = webTarget
                .path("verify")
                .path(constant)
                .path(code)
                .path(theDate)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        return Boolean.valueOf(response);
    }

    public List<CustomCode> getCodeForDate(String constant, String code, OffsetDateTime date) {
        String theDate = date.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return webTarget
                .path("getfordate")
                .path(constant)
                .path(code)
                .path(theDate)
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<CustomCode>>() {});
    }

    public CustomCode replace(CustomCode customCode) {
        return webTarget
                .path("replace")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(customCode), CustomCode.class);
    }

    public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) {
        return webTarget
                .path("collectassetmt")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(request), AssetMTEnrichmentResponse.class);
    }

    public String getMicroAssetList(List<String> assetIdList){
        return webTarget
                .path("microAssets")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.json(assetIdList), String.class);
    }
}