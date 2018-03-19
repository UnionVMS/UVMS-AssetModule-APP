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

    List<AssetGroup> getAssetGroupList(String user) throws InputArgumentException;

    List<AssetGroup> getAssetGroupListByAssetGuid(UUID assetGuid) throws InputArgumentException;

    AssetGroup getAssetGroupById(UUID guid) throws InputArgumentException;

    AssetGroup createAssetGroup(AssetGroup assetGroup, String username) throws InputArgumentException;

    AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) throws InputArgumentException;

    AssetGroup deleteAssetGroupById(UUID guid, String username) throws InputArgumentException;


    AssetGroupField createAssetGroupField(UUID parentAssetgroupId, AssetGroupField assetGroupField, String username) throws InputArgumentException;

    AssetGroupField updateAssetGroupField(AssetGroupField assetGroupField, String username) throws InputArgumentException;

    AssetGroupField getAssetGroupField(UUID id) throws InputArgumentException;

    AssetGroupField deleteAssetGroupField(UUID id, String username)  throws InputArgumentException;

    List<AssetGroupField> retrieveFieldsForGroup(AssetGroup assetGroup) throws InputArgumentException;

    void removeFieldsForGroup(AssetGroup assetGroup)  throws InputArgumentException;


}