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
package eu.europa.ec.fisheries.uvms.asset.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.constant.AssetIdentity;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.entity.Note;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

public interface AssetService {

    /**
     * @param asset @description the asset to create
     * @param username @description username
     * @return Asset @description the created asset
     */
    Asset createAsset(Asset asset, String username) ;

    /**
     * @param searchFields @description fields to use in search
     * @param page  @description page
     * @param listSize @description size of the list
     * @param dynamic @description dynamic true or false
     * @return AssetListResponse
     */
    AssetListResponse getAssetList(List<SearchKeyValue> searchFields, int page, int listSize, boolean dynamic) ;

    /**
     *
     * @param searchFields @description fields to use in search
     * @param dynamic @description dynamic true or false
     * @return Long @description number of assets in search
     */
    Long getAssetListCount(List<SearchKeyValue> searchFields, boolean dynamic) ;

    /**
     *
     * @param assetId @description id
     * @param value @description idvalue
     * @return Asset @description an asset
     */
    Asset getAssetById(AssetIdentity assetId, String value) ;


    /**
     *
     * @param id @description internal id
     * @return Asset @description an asset
     */
    Asset getAssetById(UUID id) ;

    /**
     *
     * @param asset @description an asset
     * @param username @description user that performs action
     * @param comment @description comment , reason of action
     * @return Asset @description
     */
    Asset updateAsset(Asset asset, String username, String comment) ;

    /**
     *
     * @param asset @description an asset
     * @param username @description user that performs action
     * @param comment @description comment , reason of action
     * @return Asset @description
     */
    Asset archiveAsset(Asset asset, String username, String comment) ;

    /**
     *
     * @param asset @description an asset
     * @param username @description user that performs the action
     * @return Asset @description an asset
     */
    Asset upsertAsset(Asset asset, String username) ;

    /**
     *
     * @param groups @description list of assetgroups
     * @return List of assets @description list of assets
     */
    List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups) ;


        //AssetListGroupByFlagStateResponse getAssetListGroupByFlagState(List assetIds) ;
    Object getAssetListGroupByFlagState(List assetIds) ;

    /**
     *
     * @param assetId @description id
     * @param value @description value of id
     */
    void deleteAsset(AssetIdentity assetId, String value) ;


    /**
     *
     * @param asset @description an asset
     * @return List of assets @description list of historic versions of this asset
     */
    List<Asset> getRevisionsForAsset(Asset asset) ;


    /**
     *
     * @param historyId @description id of history
     * @return asset @description an asset
     */
    Asset getAssetRevisionForRevisionId(UUID historyId) ;


    /**
     *
     * @param idType @description idtype
     * @param idValue @description value of id
     * @param date @description date to look up
     * @return asset @description an asset
     */
    Asset getAssetFromAssetIdAtDate(AssetIdentity idType, String idValue, LocalDateTime date) ;


    /**
     *
     * @param assetId @description an assets internal id
     * @return list of note @description a list of notes
     */
    List<Note> getNotesForAsset(UUID assetId);
    

    /**
     * Create a note for given asset UUID.
     * 
     * @param assetId @description an assets internal id
     * @param note @description a note object
     * @param username @description  user that performs the action
     * @return a Note @description a note
     */
    Note createNoteForAsset(UUID assetId, Note note, String username);
    
    /**
     * Update a note.
     * 
     * @param note @description a note object
     * @param username @description  user that performs the action
     * @return a Note @description a note
     */
    Note updateNote(Note note, String username);
    
    /**
     * Delete a note with given id
     * @param id @description  internal id of note
     */
    void deleteNote(UUID id);
    
    /**
     * Returns all contact info for given asset UUID.
     * @param assetId @description internal id of asset
     * @return List of ContactInfo @description
     */
    List<ContactInfo> getContactInfoForAsset(UUID assetId);


    /**
     *
     * @param assetId @description internal id of asset
     * @param contactInfo @description contactinfo object
     * @param username @description  user that performs the action
     * @return ContactInfo @description contactinfo
     */
    ContactInfo createContactInfoForAsset(UUID assetId, ContactInfo contactInfo, String username);

    /**
     *
     * @param contactInfo @description a contactinfo object
     * @param username @description  user that performs the action
     * @return ContactInfo @description contactinfo
     */
    ContactInfo updateContactInfo(ContactInfo contactInfo, String username);
    
    /**
     * Delete the contact info with given id
     * @param id @description internal id of contactinfo
     */
    void deleteContactInfo(UUID id);
}

