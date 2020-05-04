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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.bean.CustomCodesServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.dto.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("internal")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class InternalRestResource {

    private final static Logger LOG = LoggerFactory.getLogger(InternalRestResource.class);

    @Inject
    private AssetServiceBean assetService;

    @Inject
    private CustomCodesServiceBean customCodesService;

    @Inject
    private PollServiceBean pollServiceBean;

    @Inject
    AssetDao assetDao;

    private Jsonb jsonb;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    @PostConstruct
    public void init() {
        jsonb =  new JsonBConfigurator().getContext(null);
    }

    @GET
    @Path("asset/{idType : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetById(@PathParam("idType") String type, @PathParam("id") String id) throws Exception {
        try {
            AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
            Asset asset = assetService.getAssetById(assetId, id);
            return Response.ok(asset).build();
        } catch (Exception e) {
            LOG.error("getAssetById", e);
            throw e;
        }
    }

    @POST
    @Path("query")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("100") @QueryParam("size") int size,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 SearchBranch query) throws Exception {
            AssetListResponse assetList = assetService.getAssetList(query, page, size,  includeInactivated);
            String returnString = jsonb.toJson(assetList);
            return Response.ok(returnString).build();
    }

    @POST
    @Path("queryIdOnly")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetListIdOnly(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("10000000") @QueryParam("size") int size,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 SearchBranch query) {
            List<Asset> assetList = assetDao.getAssetListSearchPaginated( page, size, query,  includeInactivated);
            List<UUID> assetIdList = assetList.stream().map(Asset::getId).collect(Collectors.toList());
            String returnString = jsonb.toJson(assetIdList);
            return Response.ok(returnString).build();
    }


    @GET
    @Path("/history/asset/{id}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetHistoryListByAssetId(@PathParam("id") UUID id, @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr) throws Exception {
        try {
            List<Asset> assetRevisions = assetService.getRevisionsForAssetLimited(id, maxNbr);
            return Response.ok(assetRevisions).build();
        } catch (Exception e) {
            LOG.error("getAssetHistoryListByAssetId", e);
            throw e;
        }
    }

    @GET
    @Path("/history/{type : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}/{date}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetFromAssetIdAndDate(@PathParam("type") String type,
                                               @PathParam("id") String id,
                                               @PathParam("date") String date) throws Exception {
        try {
            AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
            Instant instant = DateUtils.stringToDate(date);
            Asset assetRevision = assetService.getAssetFromAssetIdAtDate(assetId, id, instant);
            return Response.ok(assetRevision).build();
        } catch (Exception e) {
            LOG.error("getAssetFromAssetIdAndDate", e);
            throw e;
        }
    }

    @GET
    @Path("history/{guid}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetHistoryByAssetHistGuid(@PathParam("guid") UUID guid) throws Exception {
        try {
            Asset asset = assetService.getAssetRevisionForRevisionId(guid);
            return Response.ok(asset).build();
        } catch (Exception e) {
            LOG.error("getAssetHistoryByAssetHistGuid", e);
            throw e;
        }
    }

    @POST
    @Path("asset")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response upsertAsset(AssetBO assetBo) throws Exception {
        try {
            AssetBO upsertedAsset = assetService.upsertAssetBO(assetBo, (assetBo.getAsset().getUpdatedBy() == null ? "UVMS (REST)" : assetBo.getAsset().getUpdatedBy()));
            return Response.ok(upsertedAsset).build();
        } catch (Exception e) {
            LOG.error("upsertAsset", e);
            throw e;
        }
    }

    @POST
    @Path("microAssets")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getMicroAssets(List<String> assetIdList) throws Exception {
        try {
        List<MicroAsset> assetList = assetService.getInitialDataForRealtime(assetIdList);
        return Response.ok(assetList).build();
        } catch (Exception e) {
            LOG.error("getMicroAssets", e);
            throw e;
        }
    }

    @GET
    @Path("ping")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response ping() throws Exception {
        try {
        return Response.ok("pong").build();
        } catch (Exception e) {
            LOG.error("ping", e);
            throw e;
        }
    }

    @POST
    @Path("customcode")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response createCustomCode(CustomCode customCode) throws Exception {
        try {
            CustomCode customCodes = customCodesService.create(customCode);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("create customcode failed", e);
            throw e;
        }
    }

    @GET
    @Path("listconstants")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAllConstants() throws Exception {
        try {
            List<String> constants = customCodesService.getAllConstants();
            return Response.ok(constants).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("getAllConstants failed", e);
            throw e;
        }
    }

    @GET
    @Path("listcodesforconstant/{constant}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getCodesForConstant(@PathParam("constant") String constant) {
        try {
            List<CustomCode> customCodes = customCodesService.getAllFor(constant);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("getCodesForConstant failed", e);
            throw e;
        }
    }

    @GET
    @Path("{constant}/{code}/verify")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response verify(@ApiParam(value = "constants", required = true) @PathParam("constant") String constant,
                           @ApiParam(value = "code", required = true) @PathParam("code") String code,
                           @ApiParam(value = "validToDate", required = true) @QueryParam(value = "date") String date) {
        try {
            Instant aDate = (date == null ? Instant.now() : DateUtils.stringToDate(date));
            Boolean exists = customCodesService.verify(constant, code, aDate);
            return Response.ok(exists).header("MDC", MDC.get("requestId")).build();

        } catch (Exception e) {
            LOG.error("verify failed", e);
            throw e;
        }
    }

    @GET
    @Path("{constant}/{code}/getfordate")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getForDate(@PathParam("constant") String constant,
                               @PathParam("code") String code,
                               @QueryParam(value = "date") String date) {
        try {
            Instant aDate = (date == null ? Instant.now() : DateUtils.stringToDate(date));
            List<CustomCode> customCodes = customCodesService.getForDate(constant, code, aDate);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();

        } catch (Exception e) {
            LOG.error("getForDate failed", e);
            throw e;
        }
    }

    @POST
    @Path("replace")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response replace(CustomCode customCode) {
        try {
            CustomCode customCodes = customCodesService.replace(customCode);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("replace failed", e);
            throw e;
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Gets a specific asset revision by history id
     */
    @POST
    @Path("collectassetmt")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response enrich(AssetMTEnrichmentRequest request) {
        try {
            AssetMTEnrichmentResponse assetMTEnrichmentResponse = assetService.collectAssetMT(request);
            return Response.ok(assetMTEnrichmentResponse).header("MDC", MDC.get("requestId")).build();
        }catch (Exception e){
            LOG.error("enrich failed", e);
            throw e;
        }
    }

    @POST
    @Path("poll")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response createPoll(PollRequestType createPoll) {
        try {
            CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(createPoll);
            return Response.ok(createPollResultDto.isUnsentPoll()).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll {}] {}", createPoll, ex);
            throw ex;
        }
    }

    @POST
    @Path("createPollForAsset/{id}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response createPollForAsset(@PathParam("id") String assetId, @QueryParam("username") String username, @QueryParam("comment") String comment) {
        try {
            UUID asset = UUID.fromString(assetId);
            return Response.ok(pollServiceBean.createPollForAsset(asset, PollType.AUTOMATIC_POLL, username, comment)).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll for {}] {}", assetId, ex);
            throw ex;
        }
    }
}
