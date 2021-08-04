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
package eu.europa.ec.fisheries.uvms.asset.rest.service;

import eu.europa.ec.fisheries.uvms.asset.facade.AssetFacadeNew;
import eu.europa.ec.fisheries.uvms.asset.rest.error.AssetError;
import eu.europa.ec.fisheries.uvms.asset.rest.exception.AssetFacadeException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetEventBean;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.*;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/asset-gateway")
@Stateless
public class AssetFacadeResource {

    final static Logger LOG = LoggerFactory.getLogger(AssetFacadeResource.class);

    @Inject
    private AssetFacadeNew assetFacade;

    @Inject
    private GetAssetEventBean getAssetEventBean;

    @Inject
    private AssetService assetService;

    @Inject
    private AssetGroupService assetGroupService;

    @Inject
    private AssetHistoryService assetHistoryService;

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/get-asset-groups-by-asset-guid")
    public List<AssetGroup> findAssetGroupsByAssetGuid(@QueryParam("assetGuid") String assetGuid) throws AssetFacadeException {
        try {
            LOG.info("Getting asset group list by asset guid:{}", assetGuid);
            return assetGroupService.getAssetGroupListByAssetGuid(assetGuid);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.ASSET_ERROR, "Exception when getting getAssetGroupsByAssetGuid [ " + e.getMessage() + "]");
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/find-asset-by-id")
    public Asset findAssetById(AssetId assetId) throws AssetFacadeException {
        try {
            LOG.info("Getting asset");
            return getAssetEventBean.getAsset(assetId);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.ASSET_ERROR, e.getMessage());
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/get-asset-list-by-connect-ids")
    public List<BatchAssetListResponseElement> getAssetListBatch(List<AssetListQuery> assetBatchRequest) throws AssetFacadeException {
        try {
            LOG.info("Getting asset list batch");
            return assetService.getAssetListBatch(assetBatchRequest);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/find-asset-history-by-cfr")
    public List<Asset> findHistoryOfAssetByCfr(@QueryParam("cfr") String cfr) throws AssetFacadeException {
        try {
            LOG.info("Getting asset list by cfr:{}", cfr);
            return assetFacade.findHistoryOfAssetByCfr(cfr);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/find-asset-history-by-criteria")
    public List<Asset> findHistoryOfAssetBy(@QueryParam("reportDate") String reportDate,
                                            @QueryParam("cfr") String cfr,
                                            @QueryParam("regCountry") String regCountry,
                                            @QueryParam("ircs") String ircs,
                                            @QueryParam("extMark") String extMark,
                                            @QueryParam("iccat") String iccat,
                                            @QueryParam("uvi") String uvi) throws AssetFacadeException {
        try {
            LOG.info("Getting asset list by cfr:{}", cfr);
            return assetFacade.findHistoryOfAssetBy(reportDate, cfr, regCountry, ircs, extMark, iccat, uvi);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/asset/history/by-guid")
    public FindVesselIdsByAssetHistGuidResponse findHistoryOfAssetsByGuids(FindVesselIdsByAssetHistGuidRequest request) throws AssetFacadeException {
        try {
            LOG.info("Find History Of Assets By Guids:{}", request);
            return assetFacade.findHistoryOfAssetsByGuids(request);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/assets/history/by-guids")
    public FindVesselIdsByMultipleAssetHistGuidsResponse findHistoriesOfAssetsByGuids(FindVesselIdsByMultipleAssetHistGuidsRequest request) throws AssetFacadeException {
        try {
            LOG.info("Find Histories Of Assets By Guids:{}", request);
            return assetFacade.findHistoriesOfAssetsByGuids(request);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/asset/history/by-guid-and-date")
    public FindAssetHistGuidByAssetGuidAndOccurrenceDateResponse findHistoryOfAssetsByGuidAndDate(FindAssetHistGuidByAssetGuidAndOccurrenceDateRequest request) throws AssetFacadeException {
        try {
            LOG.info("Find History Of Assets By Guid And Date:{}", request);
            return assetFacade.findHistoryOfAssetsByGuidAndDate(request);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/asset/groups/")
    public AssetGroupsForAssetResponse findAssetGroupsForAsset(AssetGroupsForAssetRequest request) throws AssetFacadeException {
        try {
            LOG.info("Find Asset Groups For Asset:{}", request);
            return assetFacade.findAssetGroupsForAsset(request);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/asset/identifiers/group/by-guid")
    public AssetIdsForGroupGuidResponseElement findAssetIdentifiersForGroupGuid(AssetIdsForGroupRequest request) throws AssetFacadeException {
        try {
            LOG.info("Find Asset Identifiers For Group Guid:{}", request);
            return assetFacade.findAssetIdentifiersForGroupGuid(request);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/get-asset-list-by-query")
    public ListAssetResponse getAssetListByQuery(AssetListQuery query) throws AssetFacadeException {
        try {
            LOG.info("Getting asset list by query: " + query.toString());
            return assetFacade.getAssetList(query);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }


    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/find-asset-by-asset-group-list")
    public List<Asset> getAssetGroup(List<AssetGroup> assetGroupQuery) throws AssetFacadeException {
        try {
            LOG.info("Received the following AssetGroup List");
            int i = 1;
            for (AssetGroup assetGroup : assetGroupQuery) {
                LOG.info("Received " + i + " element from list with data: " + assetGroup.toString());
                i++;
            }
            return assetFacade.getAssetGroup(assetGroupQuery);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }


    @GET
    @Path("/find-asset-by-guid-occurrence-date")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Asset getAssetFromAssetGuidAndDate(@QueryParam("assetGuid") String assetGuid, @QueryParam("occurrenceDate") Date occurrenceDate) throws AssetFacadeException {
        try {
            return assetHistoryService.getAssetHistoryByAssetIdAndOccurrenceDate(assetGuid, occurrenceDate);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, "Error when getting asset with assetGuid: " + assetGuid + " and occurrenceDate: " + occurrenceDate);
        }
    }


    @GET
    @Path("/find-asset-by-asset-hist-id")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Asset getAssetFromAssetHistId(@QueryParam("assetHistId") String assetHistId) throws AssetFacadeException {
        try {
            return assetHistoryService.getAssetHistoryByAssetHistGuid(assetHistId);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, "Error when getting asset with assetHistId: " + assetHistId);
        }
    }


    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path("/find-asset-by-identifier-precedence")
    public Asset getAssetByIdentifierPrecedence(AssetListCriteria assetListCriteria) throws AssetFacadeException {
        try {
            LOG.info("Getting asset by assetListCriteria: " + assetListCriteria.toString());
            return assetFacade.getAssetByIdentifierPrecedence(assetListCriteria);
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }


}