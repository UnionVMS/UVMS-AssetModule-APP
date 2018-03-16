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
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetDaoMappingException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.model.AssetListResponsePaginated;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;

public interface AssetService {

    /**
     * Create a new Asset
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException
     */
    AssetSE createAsset(AssetSE asset, String username) throws AssetServiceException;

    /**
     * Get all Assets
     *
     * @param requestQuery
     * @return
     * @throws AssetServiceException
     */
    AssetListResponsePaginated getAssetList(AssetListQuery requestQuery) throws AssetServiceException, AssetDaoMappingException;

    /**
     * Get all Assets
     *
     * @param requestQuery
     * @return
     * @throws AssetServiceException
     */
    Long getAssetListCount(AssetListQuery requestQuery) throws AssetServiceException, AssetDaoMappingException;

    /**
     * Get a Asset by its asset id from the source queue
     *
     * @param assetId
     * @param source
     * @return
     * @throws AssetServiceException
     */
    AssetSE getAssetById(AssetId assetId, AssetDataSourceQueue source) throws AssetServiceException;

    /**
     * Get Asset By internal Id
     *
     * @param id
     * @return
     * @throws AssetServiceException
     */
    AssetSE getAssetById(UUID id) throws AssetServiceException;

    /**
     * Update a Asset
     *
     * @param asset
     * @param username
     * @param comment
     * @return
     * @throws AssetServiceException
     */
    AssetSE updateAsset(AssetSE asset, String username, String comment) throws AssetServiceException;

    /**
     * Archives an asset.
     *
     * @param asset   an asset
     * @param comment a comment to the archiving
     * @return the archived asset
     * @throws AssetServiceException if unsuccessful
     */
    AssetSE archiveAsset(AssetSE asset, String username, String comment) throws AssetServiceException;

    /**
     * Create asset if not exists, otherwise update asset
     *
     * @param asset
     * @return
     * @throws AssetServiceException
     */
    AssetSE upsertAsset(AssetSE asset, String username) throws AssetServiceException;

    /**
     * Returns a list of assets based on the searh criterias in the
     * assetgroups
     *
     * @param groups
     * @return
     * @throws AssetServiceException
     */
    List<AssetSE> getAssetListByAssetGroups(List<AssetGroup> groups) throws AssetServiceException;

    //AssetListGroupByFlagStateResponse getAssetListGroupByFlagState(List assetIds) throws AssetServiceException;
    Object getAssetListGroupByFlagState(List assetIds) throws AssetServiceException;

    String getNoteActivityCodes();

    void deleteAsset(AssetId assetId) throws AssetServiceException;


    /**
     * return all revisions for an asset
     *
     * @param asset
     * @return
     * @throws AssetServiceException
     */
    List<AssetSE> getRevisionsForAsset(AssetSE asset) throws AssetServiceException;


    /**
     * return asset for specific historyId
     *
     * @param asset
     * @param historyId
     * @return
     * @throws AssetServiceException
     */
    AssetSE getAssetRevisionForRevisionId(AssetSE asset, UUID historyId) throws AssetServiceException;


    /** return asset as it was specidied date
     *
     * @param idType
     * @param idValue
     * @param date
     * @return
     * @throws AssetServiceException
     */
    AssetSE getAssetFromAssetIdAtDate(String idType, String idValue, LocalDateTime date) throws AssetServiceException;
}

