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
import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.bean.CustomCodesServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.dto.*;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.util.JsonBConfiguratorAsset;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.SanePollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalDtoMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.SimpleCreatePoll;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.VmsBillingDto;
import eu.europa.ec.fisheries.uvms.rest.asset.mapper.CustomAssetAdapter;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("internal")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
public class InternalRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(InternalRestResource.class);

    @Inject
    private AssetServiceBean assetService;

    @Inject
    private CustomCodesServiceBean customCodesService;

    @Inject
    private PollServiceBean pollServiceBean;
    
    @Inject
    private MobileTerminalServiceBean mobileTerminalService;

    @Inject
    AssetDao assetDao;

    @Inject
    PollDaoBean pollDaoBean;

    @Inject
    TerminalDaoBean terminalDaoBean;

    private Jsonb jsonb;
    private Jsonb customJsonb;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    @PostConstruct
    public void init() {
        jsonb =  new JsonBConfiguratorAsset().getContext(null);
        customJsonb = JsonbBuilder.create(new JsonbConfig().withAdapters(new CustomAssetAdapter()));
    }

    @GET
    @Path("asset/{idType : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetById(@PathParam("idType") String type, @PathParam("id") String id) throws Exception {
        try {
            AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
            Asset asset = assetService.getAssetById(assetId, id);
            String json = jsonb.toJson(asset);
            return Response.ok(json).build();
        } catch (Exception e) {
            LOG.error("getAssetById", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @POST
    @Path("query")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("100") @QueryParam("size") int size,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 SearchBranch query) throws Exception {
        try {
            AssetListResponse assetList = assetService.getAssetList(query, page, size, includeInactivated);
            String returnString = jsonb.toJson(assetList);
            return Response.ok(returnString).build();
        }catch (Exception e) {
            LOG.error("getAssetList", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }
    
    @POST
    @Path("assetList")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetList(List<String> assetIdList) {
        try {
        List<Asset> assetList = assetService.getAssetList(assetIdList);
        return Response.ok(assetList).build();
        } catch (Exception e) {
            LOG.error("Error in getAssetList Internal with arg. assetIdList: ", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @POST
    @Path("queryIdOnly")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetListIdOnly(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("10000000") @QueryParam("size") int size,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 SearchBranch query) {
        try {
            List<Asset> assetList = assetDao.getAssetListSearchPaginated(page, size, query, includeInactivated);
            List<UUID> assetIdList = assetList.stream().map(Asset::getId).collect(Collectors.toList());
            String returnString = jsonb.toJson(assetIdList);
            return Response.ok(returnString).build();
        }catch (Exception e) {
            LOG.error("getAssetListIdOnly", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
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
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("/history/{type : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}/{date}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetFromAssetIdAndDate(@PathParam("type") String type,
                                               @PathParam("id") String id,
                                               @PathParam("date") String date) {
        try {
            AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
            Instant instant = DateUtils.stringToDate(date);
            Asset assetRevision = assetService.getAssetFromAssetIdAtDate(assetId, id, instant);
            return Response.ok(assetRevision).build();
        } catch (Exception e) {
            LOG.error("getAssetFromAssetIdAndDate", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @POST
    @Path("assets/{date}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAssetsAtDate(@PathParam("date") String date,
                                    List<UUID> assetIdList) {
        try {
            Instant instant = DateUtils.stringToDate(date);
            List<Asset> assetsAtDate = assetService.getAssetsAtDate(assetIdList, instant);
            return Response.ok(customJsonb.toJson(assetsAtDate)).build();
        } catch (Exception e) {
            LOG.error("getAssetFromAssetIdAndDate", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
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
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @POST
    @Path("asset")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response upsertAsset(AssetBO assetBo) {
        try {
            AssetBO upsertedAsset = assetService.upsertAssetBO(assetBo, (assetBo.getAsset().getUpdatedBy() == null ? "UVMS (REST)" : assetBo.getAsset().getUpdatedBy()));
            return Response.ok(upsertedAsset).build();
        } catch (Exception e) {
            LOG.error("upsertAsset", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("ping")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response ping() {
        try {
        return Response.ok("pong").build();
        } catch (Exception e) {
            LOG.error("ping", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @POST
    @Path("customcode")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response createCustomCode(CustomCode customCode) {
        try {
            CustomCode customCodes = customCodesService.create(customCode);
            return Response.ok(customCodes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("create customcode failed", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("listconstants")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getAllConstants() {
        try {
            List<String> constants = customCodesService.getAllConstants();
            return Response.ok(constants).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("getAllConstants failed", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
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
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("{constant}/{code}/verify")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response verify(@PathParam("constant") String constant,
                           @PathParam("code") String code,
                           @QueryParam(value = "date") String date) {
        try {
            Instant aDate = (date == null ? Instant.now() : DateUtils.stringToDate(date));
            Boolean exists = customCodesService.verify(constant, code, aDate);
            return Response.ok(exists).header("MDC", MDC.get("requestId")).build();

        } catch (Exception e) {
            LOG.error("verify failed", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
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
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
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
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
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
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
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
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(ex)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @POST
    @Path("createPollForAsset/{id}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response createPollForAsset(@PathParam("id") String assetId, @QueryParam("username") String username, SimpleCreatePoll createPoll) {
        try {
            UUID asset = UUID.fromString(assetId);
            return Response.ok(pollServiceBean.createPollForAsset(asset, createPoll, username)).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll for {}] {}", assetId, ex);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(ex)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("/pollListForAsset/{assetId}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getPollListByAsset(@PathParam("assetId") UUID assetId) {
        LOG.info("Get poll list for asset:{}", assetId);
        try {
            List<PollBase> byAssetInTimespan = pollServiceBean.getAllPollsForAssetForTheLastDay(assetId);
            List<SanePollDto> sanePollDtos = PollEntityToModelMapper.toSanePollDto(byAssetInTimespan);
            return Response.ok(sanePollDtos).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting all polls for asset {}] {}",assetId, ex.getStackTrace());
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(ex)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("/pollInfo/{pollId}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getPollInfo(@PathParam("pollId") UUID pollId) {
        LOG.info("Get poll info for poll: {}", pollId);
        try {
            PollBase poll = pollDaoBean.getPollById(pollId);
            SanePollDto sanePollDto = PollEntityToModelMapper.toSanePollDto(poll);
            return Response.ok(jsonb.toJson(sanePollDto)).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting info for poll {}] {}", pollId, ex.getStackTrace());
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(ex)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("mobileterminals")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getMobileterminalList(@DefaultValue("false") @QueryParam("includeArchived") boolean includeArchived,
                                          @DefaultValue("false") @QueryParam("includeHistory") boolean includeHistory) {
        try {
            List<MobileTerminal> mobileTerminals;
            if (includeHistory) {
                mobileTerminals = terminalDaoBean.getMobileTerminalHistory();
            } else {
                mobileTerminals = terminalDaoBean.getMTListSearch(new ArrayList<>(), true, includeArchived);
            }
            return Response.ok(MobileTerminalDtoMapper.mapToMobileTerminalDtos(mobileTerminals)).build();
        } catch (Exception e) {
            LOG.error("Could not get mobile terminals", e);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(e)).header("MDC", MDC.get("requestId")).build();
        }
    }

    @GET
    @Path("/mobileTerminalAtDate/{mtId}")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getMobileTerminalAtDate(@PathParam("mtId") UUID mtId, @QueryParam("date") String date) {
        LOG.info("Get MT {} at date {}", mtId, date);
        try {
            Instant instant = (date == null ? Instant.now() : DateUtils.stringToDate(date));
            MobileTerminal mtAtDate = terminalDaoBean.getMtAtDate(mtId, instant);
            if(mtAtDate != null) {
                mtAtDate.getChannels().size();  //to force load
                mtAtDate.setPlugin(null);       //since the plugin for some reason does not want to be serialized
            }
            String returnString = jsonb.toJson(mtAtDate);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting MT {} at date {}] {}", mtId, date, ex.getStackTrace());
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(ex)).header("MDC", MDC.get("requestId")).build();
        }
    }
    
    @GET
    @Path("revision")
    @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getMobileTerminalAtDateWithMemberNumberAndDnid(@QueryParam("memberNumber") Integer memberNumber, @QueryParam("dnid") Integer dnid, @QueryParam("date") String date){
        try {
            Instant instant = DateUtils.stringToDate(date);
            MobileTerminal mt = terminalDaoBean.getMobileTerminalAtDateWithMemberNumberAndDnid(memberNumber, dnid, instant);
            MobileTerminalDto mtDto =  MobileTerminalDtoMapper.mapToMobileTerminalDto(mt);
            
            String returnString = jsonb.toJson(mtDto);
            
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        }catch (Exception ex) {
            LOG.error("[ Error when getting MT from memberNumber {} and dnid {} at date {}] {}", memberNumber, dnid, date, ex);
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(ex)).header("MDC", MDC.get("requestId")).build();
        }
    }
    
    @GET
    @Path("vmsBilling")
 //   @RequiresFeature(UnionVMSFeature.manageInternalRest)
    public Response getVmsBilling(){
        try {
            List<VmsBillingDto> VmsBilling = terminalDaoBean.getVmsBillingList();
            
            String returnString = jsonb.toJson(VmsBilling);
            
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        }catch (Exception ex) {
            LOG.error(" Error when getting vmsBilling  ");
            return Response.status(500).entity(ExceptionUtils.getRootCauseMessage(ex)).header("MDC", MDC.get("requestId")).build();
        }
    }
    
}
