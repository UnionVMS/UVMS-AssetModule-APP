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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseCodeConstant;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListGroupByFlagStateResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.NoteActivityCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/asset")
@Stateless
public class AssetResource {

    @EJB
    private AssetService assetService;

    @EJB
    private AssetHistoryService assetHistoryService;

    @Context
    private HttpServletRequest servletRequest;

    final static Logger LOG = LoggerFactory.getLogger(AssetResource.class);

    /**
     *
     * @responseMessage 200 Asset list successfully retrieved
     * @responseMessage 500 Error when retrieving asset list
     *
     * @summary Gets a list of assets filtered by a query
     *
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getAssetList(final AssetListQuery assetQuery) {
        try {
            LOG.info("Getting asset list:{}",assetQuery);
            ListAssetResponse assetList = assetService.getAssetList(assetQuery);
            return new ResponseDto(assetList, ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting asset list. ] ");
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Asset list successfully retrieved
     * @responseMessage 500 Error when retrieving asset list
     *
     * @summary Gets a list of assets filtered by a query
     *
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("listcount")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getAssetListItemCount(final AssetListQuery assetQuery) {
        try {
            LOG.info("Get Asset List Item Count: {}",assetQuery);
            Long assetListCount = assetService.getAssetListCount(assetQuery);
            return new ResponseDto(assetListCount, ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting asset list: {} ] {}",assetQuery,e);
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Asset list successfully retrieved
     * @responseMessage 500 Error when retrieving asset list
     *
     * @summary Gets a list of asset note activity codes
     *
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("activitycodes")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getNoteActivityCodes() {
        try {
            NoteActivityCode activityCodes = assetService.getNoteActivityCodes();
            return new ResponseDto(activityCodes, ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ getNoteActivityCodes error. ] ",e);
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Asset successfully retrieved
     * @responseMessage 500 Error when retrieving asset
     *
     * @summary Gets a asset by ID
     *
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path(value = "/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getAssetById(@PathParam(value = "id") final String id) {
        try {
            LOG.info("Getting asset by ID: {}",id);
            return new ResponseDto(assetService.getAssetByGuid(id), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting asset by ID. {}] {} ",id,e);
            return ErrorHandler.getFault(e);
        }
    }

    /**
     * Creates a new asset
     *
     * @param asset
     *            the new asset to be created
     *
     * @return Response with status OK (200) in case of success otherwise status
     *         NOT_MODIFIED or a BAD_REQUEST error code in case the provided
     *         input incomplete, with an INTERNAL_SERVER_ERROR error code in
     *         case an internal error prevented fulfilling the request or
     *         UnauthorisedException with an FORBIDDEN error code in case the
     *         end user is not authorized to perform the operation
     *
     * @summary Create a asset
     *
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public ResponseDto createAsset(final Asset asset) {
        try {
            LOG.info("Creating asset: {}",asset);
            String remoteUser = servletRequest.getRemoteUser();
            return new ResponseDto(assetService.createAsset(asset, remoteUser), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when creating asset. {}] {}",asset, e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Asset successfully updated
     * @responseMessage 500 Error when updating asset
     *
     * @summary Update a asset
     *
     */
    @PUT
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public ResponseDto updateAsset(final Asset asset, @QueryParam("comment") String comment) {
        try {
            LOG.info("Updating asset:{}",asset);
            String remoteUser = servletRequest.getRemoteUser();
            return new ResponseDto(assetService.updateAsset(asset, remoteUser, comment), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when updating asset. {}] {}",asset, e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @PUT
    @Path("/archive")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public ResponseDto archiveAsset(final Asset asset, @QueryParam("comment") String comment) {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset archivedAsset = assetService.archiveAsset(asset, remoteUser, comment);
            return new ResponseDto(archivedAsset, ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when archiving asset. {}] {}",asset, e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/listGroupByFlagState")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto assetListGroupByFlagState(final List<String> assetIds) {
        try {
            LOG.info("Getting asset list group by flag state:{}",assetIds);
            AssetListGroupByFlagStateResponse assetListGroupByFlagState = assetService.getAssetListGroupByFlagState(assetIds);
            return new ResponseDto(assetListGroupByFlagState, ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting asset list:{} ] {}",assetIds,e);
            return ErrorHandler.getFault(e);
        }
    }



}