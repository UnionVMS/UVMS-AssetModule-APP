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

import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/group")
@Stateless
public class AssetGroupResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupResource.class);

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    AssetGroupService assetGroupService;

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Get asset group list by user
     *
     */
    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetGroupListByUser(@QueryParam(value = "user") String user) {
        try {
            List<AssetGroup> assetGroups = assetGroupService.getAssetGroupList(user);
            return Response.ok(assetGroups).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset group list by user. {}", user, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetById(@PathParam(value = "id") final UUID id) {
        try {
            AssetGroup assetGroup = assetGroupService.getAssetGroupById(id);
            return Response.ok(assetGroup).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by ID. ", id, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetGroup(final AssetGroup assetGroup) {
        try {
            String user = servletRequest.getRemoteUser();
            AssetGroup createdAssetGroup = assetGroupService.createAssetGroup(assetGroup, user);
            return Response.ok(createdAssetGroup).build();
        } catch (Exception e) {
            LOG.error("Error when creating asset group: {}",assetGroup, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetGroup(final AssetGroup assetGroup) {
        try {
            String user = servletRequest.getRemoteUser();
            AssetGroup updatedAssetGroup = assetGroupService.updateAssetGroup(assetGroup, user);
            return Response.ok(updatedAssetGroup).build();
        } catch (Exception e) {
            LOG.error("Error when updating asset group. {}", assetGroup, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteAssetGroup(@PathParam(value = "id") final UUID id) {
        try {
            String user = servletRequest.getRemoteUser();
            assetGroupService.deleteAssetGroupById(id, user);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when deleting asset group by id: {}", id, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}