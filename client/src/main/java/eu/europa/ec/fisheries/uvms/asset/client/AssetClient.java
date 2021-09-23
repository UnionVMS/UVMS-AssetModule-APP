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
import eu.europa.ec.fisheries.uvms.asset.client.model.mt.MobileTerminal;
import eu.europa.ec.fisheries.uvms.asset.client.model.mt.VmsBillingDto;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.rest.security.InternalRestTokenHandler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequestScoped
public class AssetClient {

    private WebTarget webTarget;

    @Resource(name = "java:global/asset_endpoint")
    private String assetEndpoint;

    @Inject
    private JMSContext context;
    
    @Resource(mappedName = "java:/jms/queue/UVMSAssetEvent")
    private Destination destination;

    @Inject
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
        Response response = webTarget
                .path("asset")
                .path(type.toString().toLowerCase())
                .path(value)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(AssetDTO.class);
    }
    
    public List<AssetDTO> getAssetList(SearchBranch query) {
        Response response = webTarget
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), Response.class);

        checkForErrorResponse(response);
        AssetListResponse assetResponse = response.readEntity(AssetListResponse.class);
        return assetResponse.getAssetList();
    }

    public List<AssetDTO> getAssetList(String query, int page, int size, boolean includeInactivated) {
        Response response = webTarget
                .path("query")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("includeInactivated", includeInactivated)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), Response.class);

        checkForErrorResponse(response);
        AssetListResponse assetResponse = response.readEntity(AssetListResponse.class);
        return assetResponse.getAssetList();
    }

    public List<String> getAssetIdList(SearchBranch query, int page, int size, boolean includeInactivated) {
        Response response = webTarget
                .path("queryIdOnly")
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("includeInactivated", includeInactivated)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<String>>() {});
    }
    
    public List<AssetDTO> getAssetList(SearchBranch query, int page, int size) {
        Response response = webTarget
                    .path("query")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                    .post(Entity.json(query), Response.class);

        checkForErrorResponse(response);
        AssetListResponse assetResponse = response.readEntity(AssetListResponse.class);
        return assetResponse.getAssetList();
    }
    
    public List<AssetDTO> getAssetHistoryListByAssetId(UUID id) {
        Response response = webTarget
                .path("history/asset")
                .path(id.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<AssetDTO>>() {});
    }
    
    public AssetDTO getAssetFromAssetIdAndDate(AssetIdentifier type, String value, Instant date) {
        String formattedDate = DateUtils.dateToEpochMilliseconds(date);
        Response response = webTarget
                .path("history")
                .path(type.name().toLowerCase())
                .path(value)
                .path(formattedDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(AssetDTO.class);
    }

    public List<AssetDTO> getAssetsAtDate(List<UUID> assetIdList, Instant date) {
        String formattedDate = DateUtils.dateToEpochMilliseconds(date);
        Response response = webTarget
                .path("assets")
                .path(formattedDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(assetIdList), Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<AssetDTO>>() {});
    }

    public AssetDTO getAssetHistoryByAssetHistGuid(UUID historyId) {
        Response response = webTarget
                .path("history")
                .path(historyId.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(AssetDTO.class);
    }
    
    public AssetBO upsertAsset(AssetBO asset) {
        Response response = webTarget
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(asset), Response.class);

        checkForErrorResponse(response);
        return response.readEntity(AssetBO.class);
    }

    public String createPollForAsset(UUID assetId, String username, String comment, PollType pollType) {
        SimpleCreatePoll createPoll = new SimpleCreatePoll();
        createPoll.setComment(comment);
        createPoll.setPollType(pollType);

        Response response = webTarget
                .path("createPollForAsset")
                .path(assetId.toString())
                .queryParam("username", username)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(createPoll), Response.class);

        checkForErrorResponse(response);
        CreatePollResultDto createdPollResponse = response.readEntity(CreatePollResultDto.class);

        if(createdPollResponse.isUnsentPoll()){
            return createdPollResponse.getUnsentPolls().get(0);
        }else{
            return createdPollResponse.getSentPolls().get(0);
        }
    }

    public void upsertAssetAsync(AssetBO asset) throws JMSException {
        Jsonb jsonb = new JsonBConfigurator().getContext(AssetBO.class);

        TextMessage message = context.createTextMessage(jsonb.toJson(asset));
        message.setStringProperty("METHOD", "UPSERT_ASSET");
        context.createProducer().send(destination, message);
    }

    public String ping() {
        Response response = webTarget
                .path("ping")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(String.class);
    }

    public CustomCode createCustomCode(CustomCode customCode) {
        Response response = webTarget
                .path("customcode")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(customCode), Response.class);

        checkForErrorResponse(response);
        return response.readEntity(CustomCode.class);
    }

    public List<String> getConstants() {
        Response response = webTarget
                .path("listconstants")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<String>>() {});
        
    }

    public List<CustomCode> getCodesForConstant(String constant) {
        Response response = webTarget
                .path("listcodesforconstant")
                .path(constant)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<CustomCode>>() {});
    }

    public Boolean isCodeValid(String constant, String code, Instant date){
        String theDate = DateUtils.dateToEpochMilliseconds(date);
        Response response = webTarget
                .path(constant)
                .path(code)
                .path("verify")
                .queryParam("date", theDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        String boolResponse = response.readEntity(String.class);
        return Boolean.valueOf(boolResponse);
    }

    public List<CustomCode> getCodeForDate(String constant, String code, Instant date) {
        String theDate = DateUtils.dateToEpochMilliseconds(date);
        Response response = webTarget
                .path(constant)
                .path(code)
                .path("getfordate")
                .queryParam("date", theDate)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<CustomCode>>() {});
    }

    public CustomCode replace(CustomCode customCode) {
        Response response = webTarget
                .path("replace")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(customCode), Response.class);

        checkForErrorResponse(response);
        return response.readEntity(CustomCode.class);
    }

    public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) {
        Response response = webTarget
                .path("collectassetmt")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(request), Response.class);

        checkForErrorResponse(response);
        return response.readEntity(AssetMTEnrichmentResponse.class);
    }
    
    public String getAssetList(List<String> assetIdList){
        Response response = webTarget
                .path("assetList")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(assetIdList), Response.class);

        checkForErrorResponse(response);
        return response.readEntity(String.class);
    }

    public List<SanePollDto> getPollsForAssetInTheLastDay(UUID assetId){
        Response response = webTarget
                .path("pollListForAsset")
                .path(assetId.toString())
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<SanePollDto>>() {});
    }

    public SanePollDto getPollInfo(UUID pollId){
        Response response = webTarget
                .path("pollInfo")
                .path(pollId.toString())
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(SanePollDto.class);
    }

    public List<MobileTerminal> getMobileTerminals(boolean includeArchived, boolean includeHistory) {
        Response response = webTarget
                .path("mobileterminals")
                .queryParam("includeArchived", includeArchived)
                .queryParam("includeHistory", includeHistory)
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<MobileTerminal>>() {});
    }

    public MobileTerminal getMtAtDate(UUID mtId, Instant date){
        Response response = webTarget
                .path("mobileTerminalAtDate")
                .path(mtId.toString())
                .queryParam("date", "" + date.toEpochMilli())
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(MobileTerminal.class);
    }
    
    public MobileTerminal getMtFromMemberNumberAndDnidAtDate(Integer membernumber,Integer dnid, Instant date){
        Response response = webTarget
                .path("revision")
                .queryParam("memberNumber", "" + membernumber)
                .queryParam("dnid", "" + dnid)
                .queryParam("date", "" + date.toEpochMilli())
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(MobileTerminal.class);
    }
    
    public List<VmsBillingDto> getVmsBillingList(){
        Response response = webTarget
                .path("vmsBilling")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .get(Response.class);

        checkForErrorResponse(response);
        return response.readEntity(new GenericType<List<VmsBillingDto>>() {});
    }

    private void checkForErrorResponse(Response response){
        if(response.getStatus() != 200){
            throw new RuntimeException("Statuscode from asset was: " + response.getStatus() + " with payload " + response.readEntity(String.class));
        }
    }
}