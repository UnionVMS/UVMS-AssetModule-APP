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

import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.rest.security.InternalRestTokenHandler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Stateless
public class AssetClient {

    private WebTarget webTarget;

    @Resource(name = "java:global/asset_endpoint")
    private String assetEndpoint;

    @Inject
    private JMSContext context;
    
    @Resource(mappedName = "java:/" + MessageConstants.QUEUE_ASSET_EVENT)
    private Destination destination;

    @EJB
    private InternalRestTokenHandler tokenHandler;

    @PostConstruct
    private void setUpClient() {
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.connectTimeout(30, TimeUnit.SECONDS);
        clientBuilder.readTimeout(30, TimeUnit.SECONDS);
        Client client = clientBuilder.build();
        client.register(JsonBConfigurator.class);
        webTarget = client.target(assetEndpoint + "/internal");
    }

    public AssetDTO getAssetById(AssetIdentifier type, String value) {
        return webTarget
                .path("asset")
                .path(type.toString().toLowerCase())
                .path(value)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(AssetDTO.class);
    }
    
    public List<AssetDTO> getAssetList(SearchBranch query) {
        AssetListResponse assetResponse = webTarget
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), AssetListResponse.class);
    
        return assetResponse.getAssetList();
    }

    public List<AssetDTO> getAssetList(String query, int page, int size, boolean includeInactivated) {
        AssetListResponse assetResponse = webTarget
                .path("query")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("includeInactivated", includeInactivated)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), AssetListResponse.class);

        return assetResponse.getAssetList();
    }

    public List<String> getAssetIdList(SearchBranch query, int page, int size, boolean includeInactivated) {
        List<String> assetResponse = webTarget
                .path("queryIdOnly")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("includeInactivated", includeInactivated)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), new GenericType<List<String>>() {});

        return assetResponse;
    }
    
    public List<AssetDTO> getAssetList(SearchBranch query, int page, int size) {
        AssetListResponse assetResponse = webTarget
                    .path("query")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                    .post(Entity.json(query), AssetListResponse.class);
        
        return assetResponse.getAssetList();
    }
    
    public List<AssetGroup> getAssetGroupsByUser(String user) {
        return webTarget
                .path("group")
                .path("user")
                .path(user)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(new GenericType<List<AssetGroup>>() {});
    }
    
    public List<AssetGroup> getAssetGroupByAssetId(UUID assetId) {
        return webTarget
                .path("group")
                .path("asset")
                .path(assetId.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(new GenericType<List<AssetGroup>>() {});
    }

    public List<AssetDTO> getAssetsByGroupIds(List<UUID> groupIds) {
        return webTarget
                .path("group")
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(groupIds), new GenericType<List<AssetDTO>>(){});
    }
    
    public List<AssetDTO> getAssetHistoryListByAssetId(UUID id) {
        return webTarget
                .path("history/asset")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(new GenericType<List<AssetDTO>>() {});
    }
    
    public AssetDTO getAssetFromAssetIdAndDate(AssetIdentifier type, String value, Instant date) {
        String formattedDate = DateUtils.dateToEpochMilliseconds(date);
        return webTarget
                .path("history")
                .path(type.name().toLowerCase())
                .path(value)
                .path(formattedDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(AssetDTO.class);
    }

    public AssetDTO getAssetHistoryByAssetHistGuid(UUID historyId) {
        return webTarget
                .path("history")
                .path(historyId.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(AssetDTO.class);
    }
    
    public AssetBO upsertAsset(AssetBO asset) {
        return webTarget
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(asset), AssetBO.class);
    }
    
    public void upsertAssetAsync(AssetBO asset) throws JMSException {
        Jsonb jsonb = JsonbBuilder.create();

        TextMessage message = context.createTextMessage(jsonb.toJson(asset));
        message.setStringProperty("METHOD", "UPSERT_ASSET");
        context.createProducer().send(destination, message);
    }

    public String ping() {
        return webTarget
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(String.class);
    }

    public CustomCode createCustomCode(CustomCode customCode) {
        return webTarget
                .path("customcode")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(customCode), CustomCode.class);
    }

    public List<String> getConstants() {
        return webTarget
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(new GenericType<List<String>>() {});
        
    }

    public List<CustomCode> getCodesForConstant(String constant) {
        return webTarget
                .path("listcodesforconstant")
                .path(constant)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(new GenericType<List<CustomCode>>() {});
    }

    public Boolean isCodeValid(String constant, String code, Instant date){
        String theDate = DateUtils.dateToEpochMilliseconds(date);
        String response = webTarget
                .path(constant)
                .path(code)
                .path("verify")
                .queryParam("date", theDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(String.class);
        return Boolean.valueOf(response);
    }

    public List<CustomCode> getCodeForDate(String constant, String code, Instant date) {
        String theDate = DateUtils.dateToEpochMilliseconds(date);
        return webTarget
                .path(constant)
                .path(code)
                .path("getfordate")
                .queryParam("date", theDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(new GenericType<List<CustomCode>>() {});
    }

    public CustomCode replace(CustomCode customCode) {
        return webTarget
                .path("replace")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(customCode), CustomCode.class);
    }

    public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) {
        return webTarget
                .path("collectassetmt")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(request), AssetMTEnrichmentResponse.class);
    }

    public String getMicroAssetList(List<String> assetIdList){
        return webTarget
                .path("microAssets")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(assetIdList), String.class);
    }
}