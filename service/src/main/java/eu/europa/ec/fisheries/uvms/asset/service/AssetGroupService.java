/*
﻿﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
 
This file is part of the Integrated Data Fisheries Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a copy
of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.service;

import java.util.List;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;

@Local
public interface AssetGroupService {

	public List<AssetGroup> getAssetGroupList(String user) throws AssetException;

	List<AssetGroup> getAssetGroupListByAssetGuid(String assetGuid) throws AssetException;

	public AssetGroup getAssetGroupById(String guid) throws AssetException;

	public AssetGroup createAssetGroup(AssetGroup assetGroup, String username) throws AssetException;

	public AssetGroup updateAssetGroup(AssetGroup assetGroup, String username) throws AssetException;

	public AssetGroup deleteAssetGroupById(String guid, String username) throws AssetException;


}