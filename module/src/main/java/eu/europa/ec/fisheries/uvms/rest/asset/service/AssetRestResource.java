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

import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.FishingLicence;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.util.JsonBConfiguratorAsset;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChangeHistoryRow;
import eu.europa.ec.fisheries.uvms.asset.mapper.HistoryMapper;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/asset")
@Stateless
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

    private Jsonb jsonb;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    @PostConstruct
    public void init() {
    	jsonb = new JsonBConfiguratorAsset().getContext(null);
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
    @Path("list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("1000000") @QueryParam("size") int size,
                                 @DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                 SearchBranch query)  throws Exception {
        try {
            AssetListResponse assetList = assetService.getAssetList(query, page, size, includeInactivated);
            String returnString = jsonb.toJson(assetList);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset list.", e);
            throw e;
        }
    }
    
    @POST
    @Path("assetList")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetList(List<String> assetIdList) throws Exception{
        try {
        List<Asset> assetList = assetService.getAssetList(assetIdList);
        return Response.ok(assetList).build();
        } catch (Exception e) {
            LOG.error("Error in getAssetList with arg assetIdList: ", e);
            throw e;
        }
    }

    @GET
    @Path("vesselTypes")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getVesselTypes()  throws Exception  {
        try {
            List<String> vesselTypes = assetDao.getAllAvailableVesselTypes();
            String returnString = jsonb.toJson(vesselTypes);
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
    @Path("listcount")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetListItemCount(@DefaultValue("false") @QueryParam("includeInactivated") boolean includeInactivated,
                                          SearchBranch query)  throws Exception  {
        try {
            Long assetListCount = assetService.getAssetListCount(query, includeInactivated);
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
    @Path(value = "/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetById(@PathParam("id") UUID id)  throws Exception {
        try {
            Asset asset = assetService.getAssetById(id);
            String returnString = jsonb.toJson(asset);
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
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createAsset(final Asset asset)   throws Exception  {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset createdAssetSE = assetService.createAsset(asset, remoteUser);
            String returnString = jsonb.toJson(createdAssetSE);
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
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response updateAsset(final Asset asset)  throws Exception {
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset assetWithMT = assetService.populateMTListInAsset(asset);
            Asset updatedAsset = assetService.updateAsset(assetWithMT, remoteUser, asset.getComment());
            String returnString = jsonb.toJson(updatedAsset);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when updating asset: {}",asset, e);
            throw e;
        }
    }

    @PUT
    @Path("/{assetId}/archive")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response archiveAsset(@PathParam("assetId") UUID assetId, @QueryParam("comment") String comment)  throws Exception {
        try {
            if(comment == null || comment.isEmpty()){
                return Response.status(400).entity("Parameter comment is required").build();
            }
            String remoteUser = servletRequest.getRemoteUser();
            Asset asset = assetService.getAssetById(assetId);
            Asset archivedAsset = assetService.archiveAsset(asset, remoteUser, comment);
            String returnString = jsonb.toJson(archivedAsset);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when archiving asset. {}",assetId, e);
            throw e;
        }
    }

    @PUT
    @Path("/{assetId}/unarchive")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response unarchiveAsset(@PathParam("assetId") final UUID assetId, @QueryParam("comment") String comment)  throws Exception {

        if(comment == null || comment.isEmpty()){
            return Response.status(400).entity("Parameter comment is required").build();
        }
        try {
            String remoteUser = servletRequest.getRemoteUser();
            Asset unarchivedAsset = assetService.unarchiveAsset(assetId, remoteUser, comment);
            String returnString = jsonb.toJson(unarchivedAsset);
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
    @Path("/{id}/history")
    public Response getAssetHistoryListByAssetId(@PathParam("id") UUID id, @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr) throws Exception {
        try {
            List<Asset> assetRevisions = assetService.getRevisionsForAssetLimited(id, maxNbr);
            String returnString = jsonb.toJson(assetRevisions);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset history list by asset ID. {}]", id, e);
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
    @Path("/{id}/changeHistory")
    public Response getAssetHistoryChangesListByAssetId(@PathParam("id") UUID id, @DefaultValue("100") @QueryParam("maxNbr") Integer maxNbr)  throws Exception {
        try {
            List<Asset> assetRevisions = assetService.getRevisionsForAssetLimited(id, maxNbr);
            List<ChangeHistoryRow> changeHistory = HistoryMapper.assetChangeHistory(assetRevisions);
            String returnString = jsonb.toJson(changeHistory);

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
    @Path("/{type : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}/history/")
    public Response getAssetFromAssetIdAndDate(@PathParam("type") String type,
                                               @PathParam("id") String id,
                                               @QueryParam("date") String date)  throws Exception {
        try {

            AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
            Instant instant = (date == null ? Instant.now() : DateUtils.stringToDate(date));
            Asset assetRevision = assetService.getAssetFromAssetIdAtDate(assetId, id, instant);
            String returnString = jsonb.toJson(assetRevision);
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
    @Path("history/{guid}")
    public Response getAssetHistoryByAssetHistGuid(@PathParam("guid") UUID guid) throws Exception {
        try {
            Asset asset = assetService.getAssetRevisionForRevisionId(guid);
            String returnString = jsonb.toJson(asset);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by asset history guid. {}] ", guid, e);
            throw e;
        }
    }

    @GET
    @Path("{id}/notes")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getNotesForAsset(@PathParam("id") UUID assetId) {
        try {
            Map<UUID, Note> notes = assetService.getNotesForAsset(assetId);
            return Response.ok(notes).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while getting notes for asset {}. {}] ", assetId, e);
            throw e;
        }
    }

    @POST
    @Path("/notes")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createNoteForAsset(Note note) {
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
    @Path("/notes")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response updateNote(Note note) {
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
    @Path("/note/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response getNoteById(@PathParam("id") UUID id) throws Exception  {
        try {
        Note gottenNote = assetService.getNoteById(id);
        return Response.ok(gottenNote).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error getNoteById ", e);
            throw e;
        }
    }
    
    @DELETE
    @Path("/notes/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteNote(@PathParam("id") UUID id) throws Exception {
        try {
            assetService.deleteNote(id, servletRequest.getRemoteUser());
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error deleting note.", e);
            throw e;
        }
    }

    @GET
    @Path("{id}/contacts")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getContactInfoListForAssetHistory(@PathParam("id") UUID assetId,
                                                      @QueryParam("ofDate") String updatedDate) throws Exception  {
        try {
            Instant instant = (updatedDate == null ? Instant.now() : DateUtils.stringToDate(updatedDate));
            List<ContactInfo> resultList = assetService.getContactInfoRevisionForAssetHistory(assetId, instant);
            return Response.ok(resultList).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while getting contact info list for asset history", e);
            throw e;
        }
    }

    @GET
    @Path("contact/{contactId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getContact(@PathParam("contactId") UUID contactId) throws Exception {
        try{
            return Response.ok(assetDao.getContactById(contactId)).header("MDC", MDC.get("requestId")).build();
        }catch (Exception e){
            LOG.error("Error while getting contact by id {}.  {}", contactId, e);
            throw e;
        }
    }

    @POST
    @Path("contacts")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response createContactInfoForAsset(ContactInfo contactInfo) throws Exception  {
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
    @Path("/contacts")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response udpateContactInfo(ContactInfo contactInfo) throws Exception {
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
    @Path("/contacts/{id}")
    @RequiresFeature(UnionVMSFeature.manageVessels)
    public Response deleteContactInfo(@PathParam("id") UUID id) throws Exception {
        try{
            assetService.deleteContactInfo(id);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while deleting contact info.", e);
            throw e;
        }
    }

    @GET
    @Path("{id}/licence")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getFishingLicenceForAsset(@PathParam("id") UUID assetId) {
        try {
            FishingLicence licence = assetService.getFishingLicenceByAssetId(assetId);
            return Response.ok(licence).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error while getting fishing licence for asset {}.", assetId, e);
            throw e;
        }
    }
    
}
