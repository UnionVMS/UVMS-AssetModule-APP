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
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.bean.AssetListResponsePaginated;
import eu.europa.ec.fisheries.uvms.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.Note;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/asset")
@Stateless
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
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetList(final AssetListQuery assetQuery) {
        try {
            AssetListResponsePaginated assetList = assetService.getAssetList(assetQuery);
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
    @Path("listcount")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetListItemCount(final AssetListQuery assetQuery) {
        try {
            Long assetListCount = assetService.getAssetListCount(assetQuery);
            return Response.ok(assetListCount).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list: {}",assetQuery,e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @Path("activitycodes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getNoteActivityCodes() {
        try {
            String activityCodes = assetService.getNoteActivityCodes();
            return Response.ok(activityCodes).build();
        } catch (Exception e) {
            LOG.error("Could not get NoteActivityCodes",e);
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
    @Path(value = "/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetById(@PathParam("id") UUID id) {
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createAsset(final Asset asset) throws AssetException {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset createdAssetSE = assetService.createAsset(asset, remoteUser);

            return Response.status(200).entity(createdAssetSE).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId")).build();
        } catch (AssetException e) {
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response updateAsset(final Asset asset, @QueryParam("comment") String comment) {
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
    @Path("/archive")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response archiveAsset(final Asset asset, @QueryParam("comment") String comment) {
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
    @Path("/listGroupByFlagState")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response assetListGroupByFlagState(final List<String> assetIds) {
        try {
            //AssetListGroupByFlagStateResponse assetListGroupByFlagState = assetService.getAssetListGroupByFlagState(assetIds);
            //return new ResponseDto(assetListGroupByFlagState, ResponseCodeConstant.OK);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list: {}", assetIds, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("{id}/notes")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getNotesForAsset(@PathParam("id") UUID assetId) {
        List<Note> notes = assetService.getNotesForAsset(assetId);
        return Response.ok(notes).build();
    }

    @POST
    @Path("{id}/notes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createNoteForAsset(@PathParam("id") UUID assetId, Note note) {
        String user = servletRequest.getRemoteUser();
        Note createdNote = assetService.createNoteForAsset(assetId, note, user);
        return Response.ok(createdNote).build();
    }
    
    @PUT
    @Path("/notes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response udpateNote(Note note) {
        String user = servletRequest.getRemoteUser();
        Note updatedNote = assetService.updateNote(note, user);
        return Response.ok(updatedNote).build();
    }
    
    @DELETE
    @Path("/notes/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteNote(@PathParam("id") UUID id) {
        assetService.deleteNote(id);
        return Response.ok().build();
    }

}