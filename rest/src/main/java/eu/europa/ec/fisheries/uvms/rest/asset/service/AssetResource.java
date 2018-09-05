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

import java.time.OffsetDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.rest.asset.ObjectMapperContextResolver;
import eu.europa.ec.fisheries.uvms.rest.asset.dto.AssetQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.rest.asset.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
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


    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    private ObjectMapper objectMapper(){
        ObjectMapperContextResolver omcr = new ObjectMapperContextResolver();
        return omcr.getContext(Asset.class);
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
    @ApiOperation(value = "Get a list of Assets", notes = "Assemble an AssetListQuery", response = Asset.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving asset list"),
            @ApiResponse(code = 200, message = "Asset list successfully retrieved") })
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("100") @QueryParam("size") int size,
                                 @DefaultValue("true") @QueryParam("dynamic") boolean dynamic, 
                                 AssetQuery query) {
        try {
            List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query);
            AssetListResponse assetList = assetService.getAssetList(searchFields, page, size, dynamic);
            //This is needed to force Hibernate to fetch everything related to the assets, reason it does not is that AuditQuery, used to find stuff, does not support eager fetching
            String returnString = objectMapper().writeValueAsString(assetList);
            return Response.ok(returnString).build();
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
    public Response getAssetListItemCount(@DefaultValue("true") @QueryParam("dynamic") boolean dynamic, 
                                          AssetQuery query) {
        try {
            List<SearchKeyValue> searchValues = SearchFieldMapper.createSearchFields(query);
            Long assetListCount = assetService.getAssetListCount(searchValues, dynamic);
            return Response.ok(assetListCount).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list: {}", query, e);
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
            //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
            String returnString = objectMapper().writeValueAsString(asset);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON)
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
            //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
            String returnString = objectMapper().writeValueAsString(createdAssetSE);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON )
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
            //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
            String returnString = objectMapper().writeValueAsString(updatedAsset);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON )
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
            //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
            String returnString = objectMapper().writeValueAsString(archivedAsset);
            return Response.ok(returnString).build();
        } catch (Exception e) {
            LOG.error("Error when archiving asset. {}",asset, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    /**
    *
    * @responseMessage 200 Success
    * @responseMessage 500 Error
    *
    * @summary Gets a list of all revisions for a specific asset
    *
    */
    @GET
    @ApiOperation(value = "Get an assethistorylist by asset id", notes = "The ID is the internal UUID", response = Asset.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when querying the system"),
            @ApiResponse(code = 200, message = "Successful retrieval of resultset") })
    @Path("/history/asset/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetHistoryListByAssetId(@ApiParam(value="The assets GUID", required=true)  @PathParam("id") UUID id,
                                                 @ApiParam(value="Max size of resultset") @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr) {
        try {
            List<Asset> assetRevisions = assetService.getRevisionsForAssetLimited(id, maxNbr);
            //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
            String returnString = objectMapper().writeValueAsString(assetRevisions);
            return Response.ok(returnString).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset history list by asset ID. {}]", id, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    /**
     * @summary Get a specific asset by identifier (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)
     *          at given date (DateTimeFormatter.ISO_OFFSET_DATE_TIME format, eg 2018-03-23T18:25:43).
     * 
     * @param type
     * @param id
     * @param date DateTimeFormatter.ISO_OFFSET_DATE_TIME format
     * @return
     */
    @GET
    @ApiOperation(value = "Get a specific asset by identifier (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)  at given date", notes = "DateTimeFormatter.ISO_OFFSET_DATE_TIME, eg 2018-03-23T18:25:43+01:00", response = Asset.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when querying the system"),
            @ApiResponse(code = 200, message = "Successful retrieval of resultset") })
    @Path("/history/{type : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}/{date}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetFromAssetIdAndDate(@ApiParam(value="Type of id", required=true) @PathParam("type") String type,
                                               @ApiParam(value="Value of id", required=true) @PathParam("id") String id,
                                               @ApiParam(value="Point in time", required=true) @PathParam("date") String date) {
        try {
            AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            Asset assetRevision = assetService.getAssetFromAssetIdAtDate(assetId, id, offsetDateTime);
            //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
            String returnString = objectMapper().writeValueAsString(assetRevision);
            return Response.ok(returnString).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset. Type: {}, Value: {}, Date: {}", type, id, date, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

   /**
    *
    * @responseMessage 200 Success
    * @responseMessage 500 Error
    *
    * @summary Gets a specific asset revision by history id
    *
    */
    @GET
    @ApiOperation(value = "Gets a specific asset revision by history id", notes = "Gets a specific asset revision by history id", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when querying the system"),
            @ApiResponse(code = 200, message = "Successful retrieval of resultset") })
    @Path("history/{guid}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAssetHistoryByAssetHistGuid(@ApiParam(value="Id", required=true) @PathParam("guid") UUID guid) {
        try {
            Asset asset = assetService.getAssetRevisionForRevisionId(guid);
            //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
            String returnString = objectMapper().writeValueAsString(asset);
            return Response.ok(returnString).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by asset history guid. {}] ", guid, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    

    @GET
    @ApiOperation(value = "Get notes for an asset", notes = "Get notes for an asset", response = Note.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving notes for asset"),
            @ApiResponse(code = 200, message = "Notes successfully retrieved") })
    @Path("{id}/notes")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getNotesForAsset(@ApiParam(value="The id of asset to retrieve notes", required = true)  @PathParam("id") UUID assetId) {
        List<Note> notes = assetService.getNotesForAsset(assetId);
        return Response.ok(notes).build();
    }

    @POST
    @ApiOperation(value = "Create a note for an asset", notes = "Create a note for an asset", response = Note.class)
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
    @ApiOperation(value = "Update a note", notes = "Update a note", response = Note.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when updating note"),
            @ApiResponse(code = 200, message = "Note successfully updated") })
    @Path("/notes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response updateNote(@ApiParam(value="A Note to be updated", required=true)  Note note) {
        String user = servletRequest.getRemoteUser();
        Note updatedNote = assetService.updateNote(note, user);
        return Response.ok(updatedNote).build();
    }
    
    @DELETE
    @ApiOperation(value = "Remove a note", notes = "Remove a note", response = Note.class)
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
    @ApiOperation(value = "Get contactinfo for asset", notes = "Get contactinfo for asset", response = ContactInfo.class,  responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when querying the system"),
            @ApiResponse(code = 200, message = "Successful processing of query") })
    @Path("{id}/contacts")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getContactInfoForAsset(@ApiParam(value="Id of asset", required=true)  @PathParam("id") UUID assetId) {
        List<ContactInfo> contacts = assetService.getContactInfoForAsset(assetId);
        return Response.ok(contacts).build();
    }

    @POST
    @ApiOperation(value = "Create contactinfo for asset", notes = "Create contactinfo for asset", response = ContactInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when create contact info"),
            @ApiResponse(code = 200, message = "Successful created contactinfo") })
    @Path("{id}/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createContactInfoForAsset(
            @ApiParam(value="Id of asset", required=true) @PathParam("id") UUID assetId,
            @ApiParam(value="Contact info", required=true) ContactInfo contactInfo) {
        String user = servletRequest.getRemoteUser();
        ContactInfo createdContactInfo = assetService.createContactInfoForAsset(assetId, contactInfo, user);
        return Response.ok(createdContactInfo).build();
    }
    
    @PUT
    @ApiOperation(value = "Update contactinfo for asset", notes = "Update contactinfo for asset", response = ContactInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when updating contact info"),
            @ApiResponse(code = 200, message = "Successful updated contactinfo") })
    @Path("/contacts")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response udpateContactInfo(
            @ApiParam(value="Contact info", required=true)  ContactInfo contactInfo) {
        String user = servletRequest.getRemoteUser();
        ContactInfo updatedContactInfo = assetService.updateContactInfo(contactInfo, user);
        return Response.ok(updatedContactInfo).build();
    }
    
    @DELETE
    @ApiOperation(value = "Delete contactinfo for asset", notes = "Delete contactinfo for asset", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete contact info"),
            @ApiResponse(code = 200, message = "Successful delete contactinfo") })
    @Path("/contacts/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteContactInfo( @ApiParam(value="Id off contact info", required=true)  @PathParam("id") UUID id) {
        assetService.deleteContactInfo(id);
        return Response.ok().build();
    }

}