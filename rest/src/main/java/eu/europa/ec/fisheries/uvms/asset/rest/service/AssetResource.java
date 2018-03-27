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
import org.slf4j.MDC;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.rest.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.entity.Note;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/asset")
@Stateless
@Api(value = "Asset Service")
public class AssetResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetResource.class);

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private AssetService assetService;

    /**
     *
     * @responseMessage 200 Asset list successfully retrieved
     * @responseMessage 500 Error when retrieving asset list
     *
     * @summary Gets a list of assets filtered by a query
     *
     */
    @POST
    @ApiOperation(value = "Get a list of Assets", notes = "Assemble an AssetListQuery", response = Asset.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving asset list"),
            @ApiResponse(code = 200, message = "Asset list successfully retrieved") })
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetList(@ApiParam(value="Query to execute", required=true) final AssetListQuery assetQuery) {
        try {
            // TODO
            List<SearchKeyValue> searchValues = SearchFieldMapper.createSearchFields(assetQuery.getAssetSearchCriteria().getCriterias());
            int page = assetQuery.getPagination().getPage();
            int listSize = assetQuery.getPagination().getListSize();
            Boolean dynamic = assetQuery.getAssetSearchCriteria().isIsDynamic();
            AssetListResponse assetList = assetService.getAssetList(searchValues, page, listSize, dynamic);
            return Response.ok(assetList).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list.", e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @ApiOperation(value = "Count number of Assets for supplied query", notes = "Assemble an AssetListQuery", response = Long.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving asset list"),
            @ApiResponse(code = 200, message = "Asset list successfully retrieved") })
    @Path("listcount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetListItemCount(@ApiParam(value="Query to execute", required=true)  final AssetListQuery assetQuery) {
        try {
            List<SearchKeyValue> searchValues = SearchFieldMapper.createSearchFields(assetQuery.getAssetSearchCriteria().getCriterias());
            Boolean dynamic = assetQuery.getAssetSearchCriteria().isIsDynamic();
            Long assetListCount = assetService.getAssetListCount(searchValues, dynamic);
            return Response.ok(assetListCount).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list: {}",assetQuery,e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @ApiOperation(value = "Get an Asset", notes = "Retrieve an asset based on the assets UUID", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving asset"),
            @ApiResponse(code = 200, message = "Asset successfully retrieved") })
    @Path(value = "/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetById(@ApiParam(value="UUID of the asset to retrieve", required=true) @PathParam("id") UUID id) {
        try {
            Asset asset = assetService.getAssetById(id);
            return Response.status(200).entity(asset).type(MediaType.APPLICATION_JSON)
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by ID. {}",id,e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @ApiOperation(value = "Create an Asset", notes = "The ID is expected NOT to be set", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when creating asset"),
            @ApiResponse(code = 200, message = "Asset successfully created") })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createAsset(@ApiParam(value="An asset to retrieve", required=true)  final Asset asset)  {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset createdAssetSE = assetService.createAsset(asset, remoteUser);

            return Response.status(200).entity(createdAssetSE).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating asset. {}", asset, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @ApiOperation(value = "Update an Asset", notes = "Update an Asset", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when updating asset"),
            @ApiResponse(code = 200, message = "Asset successfully updated") })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response updateAsset(@ApiParam(value="The asset to update", required=true) final Asset asset, @ApiParam(value="Update comment", required=true)  @QueryParam("comment") String comment) {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset updatedAsset = assetService.updateAsset(asset, remoteUser, comment);
            return Response.status(200).entity(updatedAsset).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when updating asset: {}",asset, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @ApiOperation(value = "Archive an Asset", notes = "Archive an Asset", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when archiving asset"),
            @ApiResponse(code = 200, message = "Asset successfully archived") })
    @Path("/archive")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response archiveAsset(@ApiParam(value="The asset to update", required=true)  final Asset asset,  @ApiParam(value="Archive comment", required=true)  @QueryParam("comment") String comment) {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset archivedAsset = assetService.archiveAsset(asset, remoteUser, comment);
            return Response.ok(archivedAsset).build();
        } catch (Exception e) {
            LOG.error("Error when archiving asset. {}",asset, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @ApiOperation(value = "List group by flagstate", notes = "List group by flagstate", response = Asset.class,  responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when executing request"),
            @ApiResponse(code = 200, message = "Ok") })
    @Path("/listGroupByFlagState")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response assetListGroupByFlagState(@ApiParam(value="The assetlist", required=true)  final List<String> assetIds) {
        try {
            //AssetListGroupByFlagStateResponse assetListGroupByFlagState = assetService.getAssetListGroupByFlagState(assetIds);
            assetService.getAssetListGroupByFlagState(assetIds);
            //return new ResponseDto(assetListGroupByFlagState, ResponseCodeConstant.OK);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list: {}", assetIds, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving notes for asset"),
            @ApiResponse(code = 200, message = "Notes successfully retrieved") })
    @Path("{id}/notes")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getNotesForAsset(@ApiParam(value="The id of asset to retrieve notes", required=true)  @PathParam("id") UUID assetId) {
        List<Note> notes = assetService.getNotesForAsset(assetId);
        return Response.ok(notes).build();
    }

    @POST
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when creating note for asset"),
            @ApiResponse(code = 200, message = "Note successfully created") })
    @Path("{id}/notes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createNoteForAsset(@ApiParam(value="The id of asset for which to create note", required=true)  @PathParam("id") UUID assetId, @ApiParam(value="The Note to store" , required=true) Note note) {
        String user = servletRequest.getRemoteUser();
        Note createdNote = assetService.createNoteForAsset(assetId, note, user);
        return Response.ok(createdNote).build();
    }
    
    @PUT
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when updating note"),
            @ApiResponse(code = 200, message = "Note successfully updated") })
    @Path("/notes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response udpateNote(@ApiParam(value="A Note to be updated", required=true)  Note note) {
        String user = servletRequest.getRemoteUser();
        Note updatedNote = assetService.updateNote(note, user);
        return Response.ok(updatedNote).build();
    }
    
    @DELETE
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when deleting note"),
            @ApiResponse(code = 200, message = "Note successfully deleted") })
    @Path("/notes/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteNote(@ApiParam(value="Id of note to be deleted", required=true) @PathParam("id") UUID id) {
        assetService.deleteNote(id);
        return Response.ok().build();
    }
    
    @GET
    @Path("{id}/contacts")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getContactInfoForAsset(@PathParam("id") UUID assetId) {
        List<ContactInfo> contacts = assetService.getContactInfoForAsset(assetId);
        return Response.ok(contacts).build();
    }

    @POST
    @Path("{id}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createContactInfoForAsset(@PathParam("id") UUID assetId, ContactInfo contactInfo) {
        String user = servletRequest.getRemoteUser();
        ContactInfo createdContactInfo = assetService.createContactInfoForAsset(assetId, contactInfo, user);
        return Response.ok(createdContactInfo).build();
    }
    
    @PUT
    @Path("/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response udpateContactInfo(ContactInfo contactInfo) {
        String user = servletRequest.getRemoteUser();
        ContactInfo updatedContactInfo = assetService.updateContactInfo(contactInfo, user);
        return Response.ok(updatedContactInfo).build();
    }
    
    @DELETE
    @Path("/contacts/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteContactInfo(@PathParam("id") UUID id) {
        assetService.deleteContactInfo(id);
        return Response.ok().build();
    }

}