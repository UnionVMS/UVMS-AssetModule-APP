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
package eu.europa.ec.fisheries.uvms.rest.asset.V2.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import eu.europa.ec.fisheries.uvms.rest.AppException;
import eu.europa.ec.fisheries.uvms.rest.AppInfoCodes;
import eu.europa.ec.fisheries.uvms.rest.asset.ObjectMapperContextResolver;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.UUID;

@Path("/group2")
@Stateless
@Api(value = "Asset Group Service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetGroupRestResource2 {

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupRestResource2.class);

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private AssetGroupService assetGroupService;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    private ObjectMapper objectMapper(){
        ObjectMapperContextResolver omcr = new ObjectMapperContextResolver();
        ObjectMapper objectMapper = omcr.getContext(AssetGroup.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules();
        return objectMapper;
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Get asset group list by user
     */
    @GET
    @ApiOperation(value = "Get a list of Assetgruops for user", notes = "Get a list of Assetgroups for user", response = AssetGroup.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving AssetGroup list"),
            @ApiResponse(code = 200, message = "AssetGroup list successfully retrieved")})
    @Path("list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetGroupListByUser(@ApiParam(value = "user", required = true) @QueryParam(value = "user") String user) {
        try {
            List<AssetGroup> assetGroups = assetGroupService.getAssetGroupList(user);
            String response = objectMapper().writeValueAsString(assetGroups);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            String msg = String.format(AppInfoCodes.AssetGroupListByUser.getDescription(), user);
            LOG.error(msg, user, e);
            throw new AppException(AppInfoCodes.AssetGroupListByUser.getCode(), msg, ExceptionUtils.getRootCause(e));       }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Get asset group by ID
     */
    @GET
    @ApiOperation(value = "Get a an AssetGroup by its id", notes = "Get a an AssetGroup by its id", response = AssetGroup.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving AssetGroup list"),
            @ApiResponse(code = 200, message = "AssetGroup list successfully retrieved")})
    @Path("/{assetGroupId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetGroupById(@ApiParam(value = "AssetGroup Id", required = true) @PathParam(value = "assetGroupId") final UUID id) {
        try {
            AssetGroup assetGroup = assetGroupService.getAssetGroupById(id);
            String response = objectMapper().writeValueAsString(assetGroup);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by ID. ", id, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Create a asset group
     */
    @POST
    @ApiOperation(value = "Create an AssetGroup", notes = "Create an AssetGroup", response = AssetGroup.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when creating AssetGroup"),
            @ApiResponse(code = 200, message = "AssetGroup successfully created")})
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetGroup(@ApiParam(value = "AssetGroup", required = true) final AssetGroup assetGroup) {
        try {
            String user = servletRequest.getRemoteUser();
            AssetGroup createdAssetGroup = assetGroupService.createAssetGroup(assetGroup, user);
            return Response.ok(createdAssetGroup).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating asset group: {}", assetGroup, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Update a asset group
     */
    @PUT
    @ApiOperation(value = "Update an AssetGroup", notes = "Update an AssetGroup", response = AssetGroup.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when update AssetGroup"),
            @ApiResponse(code = 200, message = "AssetGroup successfully updated")})
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetGroup(@ApiParam(value = "AssetGroup", required = true) final AssetGroup assetGroup) {
        try {
            String user = servletRequest.getRemoteUser();
            AssetGroup updatedAssetGroup = assetGroupService.updateAssetGroup(assetGroup, user);
            String response = objectMapper().writeValueAsString(updatedAssetGroup);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when updating asset group. {}", assetGroup, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Delete a asset group
     */
    @DELETE
    @ApiOperation(value = "Delete an AssetGroup", notes = "Delete an AssetGroup", response = AssetGroup.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete AssetGroup"),
            @ApiResponse(code = 200, message = "AssetGroup successfully deleted")})
    @Path("/{assetGroupId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteAssetGroup(@ApiParam(value = "AssetGroup id", required = true) @PathParam(value = "assetGroupId") final UUID id) {
        try {
            String user = servletRequest.getRemoteUser();
            assetGroupService.deleteAssetGroupById(id, user);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when deleting asset group by id: {}", id, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @GET
    @ApiOperation(value = "GetAssetGroupByAssetId", notes = "This works if field is stored with GUID and value pointing to AssetId", response = AssetGroup.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete AssetGroup"),
            @ApiResponse(code = 200, message = "AssetGroup successfully deleted")})
    @Path("/asset/{assetId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetGroupListByAssetId(@ApiParam(value = "Asset id", required = true) @PathParam(value = "assetId") UUID assetId) {
        try {
            List<AssetGroup> assetGroups = assetGroupService.getAssetGroupListByAssetId(assetId);
            String response = objectMapper().writeValueAsString(assetGroups);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset group list by user. {}", assetId, toString(), e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @POST
    @ApiOperation(value = "CreateAssetGroupField", notes = "CreateAssetGroupField", response = AssetGroupField.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when create AssetGroupField"),
            @ApiResponse(code = 200, message = "AssetGroupField successfully deleted")})
    @Path("/{assetGroupId}/field")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetGroupField(@ApiParam(value = "Parent AssetGroup id", required = true) @PathParam(value = "assetGroupId") UUID parentAssetGroupId,
                                          @ApiParam(value = "The AssetGroupField to be created", required = true) AssetGroupField assetGroupField) {
        try {
            String user = servletRequest.getRemoteUser();
            AssetGroupField createdAssetGroupField = assetGroupService.createAssetGroupField(parentAssetGroupId, assetGroupField, user);
            String response = objectMapper().writeValueAsString(createdAssetGroupField);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetGroupField. ", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @PUT
    @ApiOperation(value = "UpdateAssetGroupField", notes = "UpdateAssetGroupField", response = AssetGroupField.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when update AssetGroupField"),
            @ApiResponse(code = 200, message = "AssetGroupField successfully update")})
    @Path("/field")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetGroupField(@ApiParam(value = "Parent AssetgroupField", required = true) AssetGroupField assetGroupField) {

        try {
            String user = servletRequest.getRemoteUser();
            AssetGroupField updatedAssetGroupField = assetGroupService.updateAssetGroupField(assetGroupField, user);
            String response = objectMapper().writeValueAsString(updatedAssetGroupField);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetGroupField. ", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @GET
    @ApiOperation(value = "GetAssetGroupField by id", notes = "GetAssetGroupField by id", response = AssetGroupField.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when get AssetGroupField"),
            @ApiResponse(code = 200, message = "AssetGroupField successfully fetched")})
    @Path("/field/{assetGroupFieldId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetGroupField(@ApiParam(value = "AssetgroupField id", required = true) @PathParam(value = "assetGroupFieldId")  UUID id) {

        try {
            AssetGroupField fetchedAssetGroupField = assetGroupService.getAssetGroupField(id);
            String response = objectMapper().writeValueAsString(fetchedAssetGroupField);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetGroupField. ", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @DELETE
    @ApiOperation(value = "DeleteAssetGroupField by id", notes = "DeleteAssetGroupField by id", response = AssetGroupField.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete AssetGroupField"),
            @ApiResponse(code = 200, message = "AssetGroupField successfully deleted")})
    @Path("/field/{assetGroupFieldId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteAssetGroupField(@ApiParam(value = "AssetGroupField id", required = true)   @PathParam(value = "assetGroupFieldId")  UUID assetGroupFieldId) {

        try {
            String user = servletRequest.getRemoteUser();
            AssetGroupField fetchedAssetGroupField = assetGroupService.deleteAssetGroupField(assetGroupFieldId, user);
            String response = objectMapper().writeValueAsString(fetchedAssetGroupField);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when delete AssetGroupField. ", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    @GET
    @ApiOperation(value = "Retrieve Assetgroupfields  by AssetGroupId",  response = AssetGroupField.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving Assetgroupfields"),
            @ApiResponse(code = 200, message = "Assetgroupfields successfully retrieved")})
    @Path("/{assetGroupId}/fieldsForGroup")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response retrieveFieldsForGroup(@ApiParam(value = "AssetGroup id", required = true)  @PathParam(value = "assetGroupId")  UUID assetGroupId) {

        try {
            List<AssetGroupField> fetchedAssetGroupFields = assetGroupService.retrieveFieldsForGroup(assetGroupId);
            String response = objectMapper().writeValueAsString(fetchedAssetGroupFields);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when fetching AssetGroupFields. ", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }

    }

    @DELETE
    @ApiOperation(value = "Delete Assetgroupfields  for AssetGroupId",  response = AssetGroupField.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete Assetgroupfields"),
            @ApiResponse(code = 200, message = "Assetgroupfields successfully deleted")})
    @Path("/{assetGroupId}/fieldsForGroup")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteFieldsForGroup(@ApiParam(value = "AssetGroup id", required = true)  @PathParam(value = "assetGroupId")  UUID assetGroupId) {

        try {
            assetGroupService.removeFieldsForGroup(assetGroupId);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when fetching AssetGroupFields. ", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }

    }
}
