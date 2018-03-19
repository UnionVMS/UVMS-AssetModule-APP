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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseCodeConstant;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.uvms.entity.model.AssetListResponsePaginated;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.entity.model.Note;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/asset")
@Stateless
public class AssetResource {

    @Inject
    AssetService assetService;

    @Context
    private HttpServletRequest servletRequest;

    private static final Logger LOG = LoggerFactory.getLogger(AssetResource.class);

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
            AssetListResponsePaginated assetList = assetService.getAssetList(assetQuery);
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
            String activityCodes = assetService.getNoteActivityCodes();
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
    public Response getAssetById(@PathParam(value = "id") final String id) throws AssetException {
        try {
            UUID theId = UUID.fromString(id);
            AssetSE asset = assetService.getAssetById(theId);
            Response.ResponseBuilder rb = Response.status(200).entity(asset).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId"));
            return rb.build();
        } catch (IllegalArgumentException e) {
            LOG.error("The Id is not a valid UUID",id,e);
            throw e;
        } catch (Exception e) {
            LOG.error("[ Error when getting asset by ID. {}] {} ",id,e);
            throw e;
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
    public Response createAsset(final AssetSE asset) throws AssetException {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            AssetSE createdAssetSE = assetService.createAsset(asset, remoteUser);

            Response.ResponseBuilder rb = Response.status(200).entity(createdAssetSE).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId"));

            return rb.build();
        } catch (AssetException e) {
            LOG.error("[ Error when creating asset. {}] {}" , asset, e.getMessage());
            throw e;
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
    public Response updateAsset(final AssetSE asset, @QueryParam("comment") String comment) throws AssetException {
        try {
            LOG.info("Updating asset:{}",asset);
            String remoteUser = servletRequest.getRemoteUser();
            AssetSE updatedAsset = assetService.updateAsset(asset, remoteUser, comment);
            Response.ResponseBuilder rb = Response.status(200).entity(updatedAsset).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId"));
            return rb.build();
        } catch (Exception e) {
            LOG.error("[ Error when updating asset. {}] {}",asset, e.getMessage());
            throw e;
        }
    }

    @PUT
    @Path("/archive")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public ResponseDto archiveAsset(final AssetSE asset, @QueryParam("comment") String comment) {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            AssetSE archivedAsset = assetService.archiveAsset(asset, remoteUser, comment);
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
            //AssetListGroupByFlagStateResponse assetListGroupByFlagState = assetService.getAssetListGroupByFlagState(assetIds);
            //return new ResponseDto(assetListGroupByFlagState, ResponseCodeConstant.OK);
            return null;
        } catch (Exception e) {
            LOG.error("[ Error when getting asset list:{} ] {}",assetIds,e);
            return ErrorHandler.getFault(e);
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
    public Response deleteNote(@PathParam("id") Long id) {
        assetService.deleteNote(id);
        return Response.ok().build();
    }

}