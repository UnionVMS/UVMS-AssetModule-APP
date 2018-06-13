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
package eu.europa.ec.fisheries.uvms.asset;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import java.util.List;
import java.util.UUID;

public interface AssetGroupService {

    /**
     *
     * @param user @description user that perforns action
     * @return @description list of assetgroups
     */
    List<AssetGroup> getAssetGroupList(String user) ;


    /**
     *
     * @param assetId @description internal assetid
     * @return @description list of assetgroups
     */
    List<AssetGroup> getAssetGroupListByAssetId(UUID assetId)  ;

    /**
     *
     * @param guid @description internal id
     * @return @description assetgroup
     */
    AssetGroup getAssetGroupById(UUID guid) ;


    /**
     *
      * @param assetGroup @description an assetgroup object
     * @param username @description user that perforns action
     * @return  @description an assetgroup
     */
    AssetGroup createAssetGroup(AssetGroup assetGroup, String username) ;


    /**
     *
     * @param assetGroup @description an assetgroup object
     * @param username @description user that perforns action
     * @return @description an assetgroup
     */
    AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) ;


    /**
     *
      * @param guid @description internal id
     * @param username @description user that perforns action
     * @return  @description  an assetgroup
     */
    AssetGroup deleteAssetGroupById(UUID guid, String username) ;


    /**
     *
     * @param parentAssetgroup @description internal id of parent assetgroup
     * @param assetGroupField @description an assetgroupfield object
     * @param username @description user that perforns action
     * @return @description
     */
    AssetGroupField createAssetGroupField(UUID parentAssetgroup, AssetGroupField assetGroupField, String username) ;


    /**
     *
     * @param assetGroupField @description an assetgroupfield object
     * @param username @description user that perforns action
     * @return an assetgroupfield
     */
    AssetGroupField updateAssetGroupField(AssetGroupField assetGroupField, String username) ;


    /**
     *
     * @param id @description an internal id
     * @return @description an assetgroupfield
     */
    AssetGroupField getAssetGroupField(UUID id) ;


    /**
     *
     * @param id @description internal id
     * @param username @description user that perforns action
     * @return @description an assetgroupfield
     */
    AssetGroupField deleteAssetGroupField(UUID id, String username)  ;


    /**
     *
     * @param assetGroupId @description internal id
     * @return @description list of assetgroupfields
     */
    List<AssetGroupField> retrieveFieldsForGroup(UUID assetGroupId) ;


    /**
     *
     * @param assetGroupId @description internal id of assetgroup
     */
    void removeFieldsForGroup(UUID assetGroupId)  ;


}