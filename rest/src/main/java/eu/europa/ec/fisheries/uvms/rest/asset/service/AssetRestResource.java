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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.dto.MicroAsset;
import eu.europa.ec.fisheries.uvms.rest.asset.ObjectMapperContextResolver;
import eu.europa.ec.fisheries.uvms.rest.asset.dto.AssetQuery;
import eu.europa.ec.fisheries.uvms.rest.asset.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.*;
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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Path("/asset")
@Stateless
@Api(value = "Asset Service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetRestResource.class);

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private AssetServiceBean assetService;

    @Inject
    private AssetDao assetDao;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    private ObjectMapper objectMapper(){
        ObjectMapperContextResolver omcr = new ObjectMapperContextResolver();
        ObjectMapper objectMapper = omcr.getContext(Asset.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules();
        return objectMapper;
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
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("1000000") @QueryParam("size") int size,
                                 @DefaultValue("true") @QueryParam("dynamic") boolean dynamic,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 AssetQuery query)  throws Exception {
        try {
            List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query);
            AssetListResponse assetList = assetService.getAssetList(searchFields, page, size, dynamic, includeInactivated);
            String returnString = objectMapper().writeValueAsString(assetList);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list.", e);
            throw e;
        }
    }

    @GET
    @Path("vesselTypes")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getVesselTypes()  throws Exception  {
        try {
            List<String> vesselTypes = assetDao.getAllAvailableVesselTypes();
            String returnString = objectMapper().writeValueAsString(vesselTypes);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting vessel types list.", e);
            throw e;
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
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetListItemCount(@DefaultValue("true") @QueryParam("dynamic") boolean dynamic,
                                          @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                          AssetQuery query)  throws Exception  {
        try {
            List<SearchKeyValue> searchValues = SearchFieldMapper.createSearchFields(query);
            Long assetListCount = assetService.getAssetListCount(searchValues, dynamic, includeInactivated);
            return Response.ok(assetListCount).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list: {}", query, e);
            throw e;
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
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetById(@ApiParam(value="UUID of the asset to retrieve", required=true) @PathParam("id") UUID id)  throws Exception {
        try {
            Asset asset = assetService.getAssetById(id);
            String returnString = objectMapper().writeValueAsString(asset);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON)
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by ID. {}",id,e);
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
    @ApiOperation(value = "Create an Asset", notes = "The ID is expected NOT to be set", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when creating asset"),
            @ApiResponse(code = 200, message = "Asset successfully created") })
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createAsset(@ApiParam(value="An asset to create", required=true)  final Asset asset)   throws Exception  {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset createdAssetSE = assetService.createAsset(asset, remoteUser);
            String returnString = objectMapper().writeValueAsString(createdAssetSE);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating asset. {}", asset, e);
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
    @ApiOperation(value = "Update an Asset", notes = "Update an Asset", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when updating asset"),
            @ApiResponse(code = 200, message = "Asset successfully updated") })
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response updateAsset(@ApiParam(value="The asset to update", required=true) final Asset asset)  throws Exception {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset assetWithMT = assetService.populateMTListInAsset(asset);
            Asset updatedAsset = assetService.updateAsset(assetWithMT, remoteUser, asset.getComment());
            String returnString = objectMapper().writeValueAsString(updatedAsset);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when updating asset: {}",asset, e);
            throw e;
        }
    }

    @PUT
    @ApiOperation(value = "Archive an Asset", notes = "Archive an Asset", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when archiving asset"),
            @ApiResponse(code = 200, message = "Asset successfully archived") })
    @Path("/{assetId}/archive")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response archiveAsset(@ApiParam(value="The asset to update", required=true)  @PathParam("assetId") UUID assetId,
                                 @ApiParam(value="Archive comment", required=true) @QueryParam("comment") String comment)  throws Exception {
        try {
            if(comment == null || comment.isEmpty()){
                return Response.status(400).entity("Parameter comment is required").build();
            }
            String remoteUser = servletRequest.getRemoteUser();
            Asset asset = assetService.getAssetById(assetId);
            Asset archivedAsset = assetService.archiveAsset(asset, remoteUser, comment);
            String returnString = objectMapper().writeValueAsString(archivedAsset);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when archiving asset. {}",assetId, e);
            throw e;
        }
    }

    @PUT
    @ApiOperation(value = "Unarchive an Asset", notes = "Unarchive an Asset", response = Asset.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when unarchiving asset"),
            @ApiResponse(code = 200, message = "Asset successfully unarchived") })
    @Path("/{assetId}/unarchive")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response unarchiveAsset(@ApiParam(value="The asset to update", required=true)  @PathParam("assetId") final UUID assetId,
                                 @ApiParam(value="Unarchive comment", required=true) @QueryParam("comment") String comment)  throws Exception {

        if(comment == null || comment.isEmpty()){
            return Response.status(400).entity("Parameter comment is required").build();
        }
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset unarchivedAsset = assetService.unarchiveAsset(assetId, remoteUser, comment);
            String returnString = objectMapper().writeValueAsString(unarchivedAsset);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when unarchiving Asset with ID: {}", assetId, e);
            throw e;
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
    @Path("/{id}/history")
    public Response getAssetHistoryListByAssetId(@ApiParam(value="The assets GUID", required=true)  @PathParam("id") UUID id,
                                                 @ApiParam(value="Max size of resultset") @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr)  throws Exception {
        try {
            List<Asset> assetRevisions = assetService.getRevisionsForAssetLimited(id, maxNbr);
            String returnString = objectMapper().writeValueAsString(assetRevisions);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset history list by asset ID. {}]", id, e);
            throw e;
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
    @ApiOperation(value = "Get a specific asset by identifier (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)  at given date",
            notes = "DateTimeFormatter.ISO_OFFSET_DATE_TIME, eg 2018-03-23T18:25:43+01:00", response = Asset.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when querying the system"),
            @ApiResponse(code = 200, message = "Successful retrieval of resultset") })
    @Path("/{type : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}/history/")
    public Response getAssetFromAssetIdAndDate(@ApiParam(value="Type of id", required=true) @PathParam("type") String type,
                                               @ApiParam(value="Value of id", required=true) @PathParam("id") String id,
                                               @ApiParam(value="Point in time", required=true) @QueryParam("date") String date)  throws Exception {
        try {

            AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
            OffsetDateTime offsetDateTime = (date == null ? OffsetDateTime.now() : OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            Asset assetRevision = assetService.getAssetFromAssetIdAtDate(assetId, id, offsetDateTime);
            String returnString = objectMapper().writeValueAsString(assetRevision);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset. Type: {}, Value: {}, Date: {}", type, id, date, e);
            throw e;
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
    public Response getAssetHistoryByAssetHistGuid(@ApiParam(value="Id", required=true) @PathParam("guid") UUID guid)  throws Exception {
        try {
            Asset asset = assetService.getAssetRevisionForRevisionId(guid);
            String returnString = objectMapper().writeValueAsString(asset);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by asset history guid. {}] ", guid, e);
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Get notes for an asset", notes = "Get notes for an asset", response = Note.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving notes for asset"),
            @ApiResponse(code = 200, message = "Notes successfully retrieved") })
    @Path("{id}/notes")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getNotesForAsset(@ApiParam(value="The id of asset to retrieve notes", required = true)  @PathParam("id") UUID assetId) {
        try {
            List<Note> notes = assetService.getNotesForAsset(assetId);
            return Response.ok(notes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while getting notes for asset {}. {}] ", assetId, e);
            throw e;
        }
    }

    @POST
    @ApiOperation(value = "Create a note for an asset", notes = "Create a note for an asset", response = Note.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when creating note for asset"),
            @ApiResponse(code = 200, message = "Note successfully created") })
    @Path("/notes")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createNoteForAsset(@ApiParam(value="The Note to store" , required=true) Note note) {
        try {
            String user = servletRequest.getRemoteUser();
            Note createdNote = assetService.createNoteForAsset(note.getAssetId(), note, user);
            return Response.ok(createdNote).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while creating notes for asset.", e);
            throw e;
        }
    }
    
    @PUT
    @ApiOperation(value = "Update a note", notes = "Update a note", response = Note.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when updating note"),
            @ApiResponse(code = 200, message = "Note successfully updated") })
    @Path("/notes")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response updateNote(@ApiParam(value="A Note to be updated", required=true)  Note note) {
        try {
            String user = servletRequest.getRemoteUser();
            Note updatedNote = assetService.updateNote(note, user);
            return Response.ok(updatedNote).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error updating note.", e);
            throw e;
        }
    }
    
    @GET
    @ApiOperation(value = "Get a note", notes = "Get a note", response = Note.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when getting note"),
            @ApiResponse(code = 200, message = "Note successfully found") })
    @Path("/note/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response getNoteById(@ApiParam(value="Id of note to get", required=true) @PathParam("id") UUID id)   throws Exception  {
        try {
        Note gottenNote = assetService.getNoteById(id);
        return Response.ok(gottenNote).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error getNoteById ", e);
            throw e;
        }
    }
    
    @DELETE
    @ApiOperation(value = "Remove a note", notes = "Remove a note", response = Note.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when deleting note"),
            @ApiResponse(code = 200, message = "Note successfully deleted") })
    @Path("/notes/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteNote(@ApiParam(value="Id of note to be deleted", required=true) @PathParam("id") UUID id)  throws Exception {
        try {
            assetService.deleteNote(id);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error deleting note.", e);
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Get ContactInfo history for asset", notes = "Get ContactInfo history for asset", response = ContactInfo.class,  responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when querying the system"),
            @ApiResponse(code = 200, message = "Successful processing of query") })
    @Path("{id}/contacts")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getContactInfoListForAssetHistory(@ApiParam(value="Id of asset", required=true)  @PathParam("id") UUID assetId,
                                                      @QueryParam("ofDate") String updatedDate)  throws Exception  {
        try {
            OffsetDateTime offsetDateTime = (updatedDate == null ? OffsetDateTime.now() : OffsetDateTime.parse(updatedDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            List<ContactInfo> resultList = assetService.getContactInfoRevisionForAssetHistory(assetId, offsetDateTime);
            return Response.ok(resultList).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while getting contact info list for asset history", e);
            throw e;
        }
    }

    @GET
    @Path("contact/{contactId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getContact(@PathParam("contactId") UUID contactId)  throws Exception {
        try{
            return Response.ok(assetDao.getContactById(contactId)).header("MDC", MDC.get("requestId")).build();
        }catch (Exception e){
            LOG.error("Error while getting contact by id {}.  {}", contactId, e);
            throw e;
        }
    }

    @POST
    @ApiOperation(value = "Create contactinfo for asset", notes = "Create contactinfo for asset", response = ContactInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when create contact info"),
            @ApiResponse(code = 200, message = "Successful created contactinfo") })
    @Path("contacts")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createContactInfoForAsset (
            @ApiParam(value="Contact info", required=true) ContactInfo contactInfo)  throws Exception  {
        try {
            String user = servletRequest.getRemoteUser();
            ContactInfo createdContactInfo = assetService.createContactInfoForAsset(contactInfo.getAssetId(), contactInfo, user);
            return Response.ok(createdContactInfo).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while creating contact info for asset.", e);
            throw e;
        }
    }
    
    @PUT
    @ApiOperation(value = "Update contactinfo for asset", notes = "Update contactinfo for asset", response = ContactInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when updating contact info"),
            @ApiResponse(code = 200, message = "Successful updated contactinfo") })
    @Path("/contacts")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response udpateContactInfo(@ApiParam(value="Contact info", required=true)  ContactInfo contactInfo)  throws Exception {
        try{
            String username = servletRequest.getRemoteUser();
            ContactInfo updatedContactInfo = assetService.updateContactInfo(contactInfo, username);
            return Response.ok(updatedContactInfo).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while updating contact info.", e);
            throw e;
        }
    }
    
    @DELETE
    @ApiOperation(value = "Delete contactinfo for asset", notes = "Delete contactinfo for asset", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete contact info"),
            @ApiResponse(code = 200, message = "Successful delete contactinfo") })
    @Path("/contacts/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteContactInfo( @ApiParam(value="Id off contact info", required=true)  @PathParam("id") UUID id)  throws Exception {
        try{
            assetService.deleteContactInfo(id);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while deleting contact info.", e);
            throw e;
        }
    }

    @POST
    @Path("microAssets")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response getMicroAssets(List<String> assetIdList)  throws Exception {
        try {
            List<MicroAsset> assetList = assetService.getInitialDataForRealtime(assetIdList);
            return Response.ok(assetList).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting microAssets.", e);
            throw e;
        }
    }
}
