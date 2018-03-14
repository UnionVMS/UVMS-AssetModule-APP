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
package eu.europa.ec.fisheries.uvms.dao;

import java.util.List;
import java.util.UUID;

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;

@Local
public interface AssetGroupDao {

	/**
	 * Create asset group
	 *
	 * @param group
	 * @return
	 */
	public AssetGroupEntity createAssetGroup(AssetGroupEntity group) throws AssetGroupDaoException;

	/**
	 * Get asset group by guid
	 *
	 * @param guid
	 * @return
	 */
	public AssetGroupEntity getAssetGroupByGuid(UUID guid) throws AssetGroupDaoException;

	/**
	 * Update asset group
	 *
	 * @param group
	 * @return
	 */
	public AssetGroupEntity updateAssetGroup(AssetGroupEntity group) throws AssetGroupDaoException;

	/**
	 * Delete asset group
	 *
	 * @param group
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException
	 */
	public AssetGroupEntity deleteAssetGroup(AssetGroupEntity group) throws AssetGroupDaoException;

	/**
	 * Get all asset groups (FIND_ALL)
	 *
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException
	 */
	public List<AssetGroupEntity> getAssetGroupAll() throws AssetGroupDaoException;

	/**
	 * Get asset groups by user
	 *
	 * @param user
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException
	 */
	public List<AssetGroupEntity> getAssetGroupByUser(String user) throws AssetGroupDaoException;

	/**
	 * Get asset groups by guidList
	 *
	 * @param guidList
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException
	 */
	List<AssetGroupEntity> getAssetGroupsByGroupGuidList(List<UUID> guidList) throws AssetGroupDaoException;


}
