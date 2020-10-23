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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.facade.AssetFacadeNew;
import eu.europa.ec.fisheries.uvms.asset.rest.error.AssetError;
import eu.europa.ec.fisheries.uvms.asset.rest.exception.AssetFacadeException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.GetAssetEventBean;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.BatchAssetListResponseElement;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        } catch (SQLGrammarException e) { // should be caught at dao level or facade
            throw new AssetFacadeException(AssetError.SQL_ERROR, e.getMessage());
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
        } catch (SQLGrammarException e) { // should be caught at dao level or facade
            throw new AssetFacadeException(AssetError.SQL_ERROR, e.getMessage());
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
        } catch (SQLGrammarException e) { // should be caught at dao level or facade
            throw new AssetFacadeException(AssetError.SQL_ERROR, e.getMessage());
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
            int i =1;
            for(AssetGroup assetGroup: assetGroupQuery) {
                LOG.info("Received " + i + " element from list with data: " + assetGroup.toString());
                i++;
            }
            return assetFacade.getAssetGroup(assetGroupQuery);
        } catch (SQLGrammarException e) {
            throw new AssetFacadeException(AssetError.SQL_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new AssetFacadeException(AssetError.UNKNOWN_ERROR, e.getMessage());
        }
    }


}