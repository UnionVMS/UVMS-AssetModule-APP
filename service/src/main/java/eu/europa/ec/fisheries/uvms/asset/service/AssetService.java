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

import javax.ejb.Local;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListGroupByFlagStateResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.NoteActivityCode;

@Local
public interface AssetService {

    /**
     * Create a new Asset
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException
     */
    Asset createAsset(Asset asset, String username) throws AssetException;

    /**
     * Get all Assets
     *
     * @param requestQuery
     * @return
     * @throws AssetException
     */
    ListAssetResponse getAssetList(AssetListQuery requestQuery) throws AssetException;

    /**
     * Get all Assets
     *
     * @param requestQuery
     * @return
     * @throws AssetException
     */
    Long getAssetListCount(AssetListQuery requestQuery) throws AssetException;

    /**
     * Get a Asset by its asset id from the source queue
     *
     * @param assetId
     * @param source
     * @return
     * @throws AssetException
     */
    Asset getAssetById(AssetId assetId, AssetDataSourceQueue source) throws AssetException;

    /**
     * Get a Asset by guid
     *
     * @param guid
     * @return
     * @throws AssetException
     */
    Asset getAssetByGuid(String guid) throws AssetException;

    /**
     * Update a Asset
     *
     * @param asset
     * @param username
     * @param comment
     * @return
     * @throws AssetException
     */
    Asset updateAsset(Asset asset,String username, String comment) throws AssetException;

    /**
     * Archives an asset.
     * 
     * @param asset an asset
     * @param comment a comment to the archiving
     * @return the archived asset
     * @throws AssetException if unsuccessful
     */
    Asset archiveAsset(Asset asset, String username, String comment) throws AssetException;

    /**
     * Create asset if not exists, otherwise update asset
     *
     * @param asset
     * @return
     * @throws AssetException
     */
    Asset upsertAsset(Asset asset, String username) throws AssetException;

    /**
     *
     * Returns a list of assets based on the searh criterias in the
     * assetgroups
     *
     * @param groups
     * @return
     * @throws AssetException
     */
    List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups) throws AssetException;

    AssetListGroupByFlagStateResponse getAssetListGroupByFlagState(List assetIds) throws AssetException;

    NoteActivityCode getNoteActivityCodes();


    /**
     *
     *
     * @param assetId
     * @return number of entities affected
     * @throws AssetException
     */
    void deleteAsset(AssetId assetId) throws AssetException;

    List<Asset> getAssetHistoryListByAssetId(AssetId assetId, Integer maxResults) throws AssetModelException;


}