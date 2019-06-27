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
package eu.europa.ec.fisheries.uvms.rest.asset.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.CustomCodesService;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.dto.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.rest.asset.ObjectMapperContextResolver;
import eu.europa.ec.fisheries.uvms.rest.asset.dto.AssetQuery;
import eu.europa.ec.fisheries.uvms.rest.asset.mapper.SearchFieldMapper;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("internal")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class InternalResource {

    private final static Logger LOG = LoggerFactory.getLogger(InternalResource.class);

    @Inject
    private AssetService assetService;

    @Inject
    private AssetGroupService assetGroupService;

    @Inject
    private CustomCodesService customCodesService;

    @Inject
    private PollServiceBean pollServiceBean;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    private ObjectMapper objectMapper(){
        ObjectMapperContextResolver omcr = new ObjectMapperContextResolver();
        ObjectMapper objectMapper = omcr.getContext(InternalResource.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules();
        return objectMapper;
    }

    @GET
    @Path("asset/{idType : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}")
    public Response getAssetById(@PathParam("idType") String type, @PathParam("id") String id) {
        AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
        Asset asset = assetService.getAssetById(assetId, id);
        return Response.ok(asset).build();
    }

    @POST
    @Path("query")
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("100") @QueryParam("size") int size,
                                 @DefaultValue("true") @QueryParam("dynamic") boolean dynamic,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 AssetQuery query) {
        try {
            List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query);
            AssetListResponse assetList = assetService.getAssetList(searchFields, page, size, dynamic, includeInactivated);
            String returnString = objectMapper().writeValueAsString(assetList);
            return Response.ok(returnString).build();
        } catch (JsonProcessingException e) {
            LOG.error("Error when getting getAssetList", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @GET
    @Path("group/user/{user}")
    public Response getAssetGroupByUser(@PathParam("user") String user) {
        List<AssetGroup> assetGroups = assetGroupService.getAssetGroupList(user);
        return Response.ok(assetGroups).build();
    }

    @GET
    @Path("group/asset/{id}")
    public Response getAssetGroupByAssetId(@PathParam("id") UUID assetId) {
        List<AssetGroup> assetGroups = assetGroupService.getAssetGroupListByAssetId(assetId);
        return Response.ok(assetGroups).build();
    }

    @POST
    @Path("group/asset")
    public Response getAssetByGroupIds(List<UUID> groupIds) {
        List<AssetGroup> assetGroups = groupIds.stream()
                                            .map(assetGroupService::getAssetGroupById)
                                            .collect(Collectors.toList());
        List<Asset> assets = assetService.getAssetListByAssetGroups(assetGroups);
        return Response.ok(assets).build();
    }

    @GET
    @Path("/history/asset/{id}")
    public Response getAssetHistoryListByAssetId(@PathParam("id") UUID id, @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr) {
        List<Asset> assetRevisions = assetService.getRevisionsForAssetLimited(id, maxNbr);
        return Response.ok(assetRevisions).build();
    }

    @GET
    @Path("/history/{type : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}/{date}")
    public Response getAssetFromAssetIdAndDate(@PathParam("type") String type,
                                               @PathParam("id") String id,
                                               @PathParam("date") String date) {
        AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        Asset assetRevision = assetService.getAssetFromAssetIdAtDate(assetId, id, offsetDateTime);
        return Response.ok(assetRevision).build();
    }

    @GET
    @Path("history/{guid}")
    public Response getAssetHistoryByAssetHistGuid(@PathParam("guid") UUID guid) {
        Asset asset = assetService.getAssetRevisionForRevisionId(guid);
        return Response.ok(asset).build();
    }
    
    @POST
    @Path("asset")
    public Response upsertAsset(AssetBO assetBo) {
        AssetBO upsertedAsset = assetService.upsertAssetBO(assetBo, (assetBo.getAsset().getUpdatedBy() == null ?  "UVMS (REST)" : assetBo.getAsset().getUpdatedBy()));
        return Response.ok(upsertedAsset).build();
    }

    @POST
    @Path("microAssets")
    public Response getMicroAssets(List<String> assetIdList){
        List<MicroAsset> assetList = assetService.getInitialDataForRealtime(assetIdList);
        return Response.ok(assetList).build();
    }
    
    @GET
    @Path("ping")
    public Response ping() {
        return Response.ok("pong").build();
    }

    @POST
    @Path("customcode")
    public Response createCustomCode(CustomCode customCode) {
        try {
            CustomCode customCodes = customCodesService.create(customCode);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionUtils.getRootCause(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("listconstants")
    public Response getAllConstants() {
        try {
            List<String> constants = customCodesService.getAllConstants();
            return Response.ok(constants).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionUtils.getRootCause(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("listcodesforconstant/{constant}")
    public Response getCodesForConstant(@PathParam("constant") String constant) {
        try {
            List<CustomCode> customCodes = customCodesService.getAllFor(constant);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionUtils.getRootCause(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("verify/{constant}/{code}/{date}")
    public Response verify(@ApiParam(value = "constants", required = true) @PathParam("constant") String constant,
                           @ApiParam(value = "code", required = true) @PathParam("code") String code,
                           @ApiParam(value = "validToDate", required = true) @PathParam(value = "date") String date)
    {
        try {
            OffsetDateTime aDate = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Boolean exists = customCodesService.verify(constant, code, aDate);
            return Response.ok(exists).header("MDC", MDC.get("requestId")).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionUtils.getRootCause(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("getfordate/{constant}/{code}/{date}")
    public Response getForDate(@PathParam("constant") String constant,
                               @PathParam("code") String code,
                               @PathParam(value = "date") String date)
    {
        try {
            OffsetDateTime aDate = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            List<CustomCode> customCodes = customCodesService.getForDate(constant, code,aDate);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionUtils.getRootCause(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @POST
    @Path("replace")
    public Response replace(CustomCode customCode) {
        try {
            CustomCode customCodes = customCodesService.replace(customCode);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionUtils.getRootCause(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Gets a specific asset revision by history id
     */
    @POST
    @Path("collectassetmt")
    public Response enrich( AssetMTEnrichmentRequest request) {
        AssetMTEnrichmentResponse assetMTEnrichmentResponse = assetService.collectAssetMT(request);
        return Response.ok(assetMTEnrichmentResponse).header("MDC", MDC.get("requestId")).build();
    }

    @POST
    @Path("poll")
    public Response createPoll(PollRequestType createPoll) {
        try {
            CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(createPoll, createPoll.getUserName());
            return Response.ok(createPollResultDto.isUnsentPoll()).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll {}] {}",createPoll, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }
}
