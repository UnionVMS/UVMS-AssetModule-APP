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

import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseCodeConstant;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/group")
@Stateless
public class AssetGroupResource {

    @EJB
    AssetGroupService assetGroupService;

    @Context
    private HttpServletRequest servletRequest;

    final static Logger LOG = LoggerFactory.getLogger(AssetGroupResource.class);

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Get asset group list by user
     *
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getAssetGroupListByUser(@QueryParam(value = "user") String user) {
        try {
            LOG.info("Getting asset group list by user {}",user);
            return new ResponseDto(assetGroupService.getAssetGroupList(user), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting asset group list by user. ] {}", e.getMessage(), e.getStackTrace());
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Get asset group by ID
     *
     */
    @GET
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto getAssetById(@PathParam(value = "id") final String id) {
        try {
            LOG.info("Getting asset group by ID {}",id);
            return new ResponseDto(assetGroupService.getAssetGroupById(id), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting asset by ID. ] {}", e.getMessage(), e.getStackTrace());
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Create a asset group
     *
     */
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto createAssetGroup(final AssetGroupEntity assetGroup) {
        try {
            LOG.info("Creating asset group: {}",assetGroup);
            return new ResponseDto(assetGroupService.createAssetGroup(assetGroup, servletRequest.getRemoteUser()), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when creating asset group: {} ] {}",assetGroup, e.getMessage());
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Update a asset group
     *
     */
    @PUT
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto updateAssetGroup(final AssetGroupEntity assetGroup) {
        try {
            LOG.info("Updating asset group:{}",assetGroup);
            return new ResponseDto(assetGroupService.updateAssetGroup(assetGroup, servletRequest.getRemoteUser()), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when updating asset group. ] {}", e.getMessage(), e.getStackTrace());
            return ErrorHandler.getFault(e);
        }
    }

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Delete a asset group
     *
     */
    @DELETE
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public ResponseDto deleteAssetGroup(@PathParam(value = "id") final String id) {
        try {
            LOG.info("Deleting asset group: {}",id);
            return new ResponseDto(assetGroupService.deleteAssetGroupById(id, servletRequest.getRemoteUser()), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when deleting asset group. ] {}", e.getMessage(), e.getStackTrace());
            return ErrorHandler.getFault(e);
        }
    }
}