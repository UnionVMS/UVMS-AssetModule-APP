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

import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;

import javax.ejb.Local;
import java.util.List;
import java.util.UUID;

@Local
public interface AssetGroupService {

    /**
     *
     * @param user
     * @return
     * @throws InputArgumentException
     */
    List<AssetGroup> getAssetGroupList(String user) throws InputArgumentException;

    /**
     *
     * @param assetId
     * @return
     * @throws InputArgumentException
     */
    List<AssetGroup> getAssetGroupListByAssetId(UUID assetId) throws InputArgumentException ;

    /**
     *
     * @param guid
     * @return
     * @throws InputArgumentException
     */
    AssetGroup getAssetGroupById(UUID guid) throws InputArgumentException;

    /**
     *
     * @param assetGroup
     * @param username
     * @return
     * @throws InputArgumentException
     */
    AssetGroup createAssetGroup(AssetGroup assetGroup, String username) throws InputArgumentException;

    /**
     *
     * @param assetGroup
     * @param username
     * @return
     * @throws InputArgumentException
     */
    AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) throws InputArgumentException;

    /**
     *
     * @param guid
     * @param username
     * @return
     * @throws InputArgumentException
     */
    AssetGroup deleteAssetGroupById(UUID guid, String username) throws InputArgumentException;


    /**
     *
     * @param parentAssetgroup
     * @param assetGroupField
     * @param username
     * @return
     * @throws InputArgumentException
     */
    AssetGroupField createAssetGroupField(AssetGroup parentAssetgroup, AssetGroupField assetGroupField, String username) throws InputArgumentException;

    /**
     *
     * @param assetGroupField
     * @param username
     * @return
     * @throws InputArgumentException
     */
    AssetGroupField updateAssetGroupField(AssetGroupField assetGroupField, String username) throws InputArgumentException;

    /**
     *
     * @param id
     * @return
     * @throws InputArgumentException
     */
    AssetGroupField getAssetGroupField(UUID id) throws InputArgumentException;


    /**
     *
     * @param id
     * @param username
     * @return
     * @throws InputArgumentException
     */
    AssetGroupField deleteAssetGroupField(UUID id, String username)  throws InputArgumentException;


    /**
     *
     * @param assetGroup
     * @return
     * @throws InputArgumentException
     */
    List<AssetGroupField> retrieveFieldsForGroup(AssetGroup assetGroup) throws InputArgumentException;

    /**
     *
     * @param assetGroup
     * @throws InputArgumentException
     */
    void removeFieldsForGroup(AssetGroup assetGroup)  throws InputArgumentException;


}