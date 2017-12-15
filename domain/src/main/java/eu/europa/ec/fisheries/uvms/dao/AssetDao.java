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

import javax.ejb.Local;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

@Local
public interface AssetDao {

    /**
     * Create asset in database
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public AssetEntity createAsset(AssetEntity asset) throws AssetDaoException;

    /**
     * Get vessel by internal vessel id
     *
     * @param id
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public AssetEntity getAssetById(Long id) throws AssetDaoException;

    /**
     * Get asset by cfr
     *
     * @param cfr
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public AssetEntity getAssetByCfr(String cfr) throws AssetDaoException;

    /**
     * Get asset by ircs
     *
     * @param ircs
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public AssetEntity getAssetByIrcs(String ircs) throws AssetDaoException;

    /**
     * Get asset by guid
     *
     * @param guid
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public AssetEntity getAssetByGuid(String guid) throws AssetDaoException;

    /**
     * Get asset by IMO
     * 
     * @param value
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
	public AssetEntity getAssetByImo(String value) throws AssetDaoException;

	/**
	 * Get asset by MMSI
	 * 
	 * @param value
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
	 */
	public AssetEntity getAssetByMmsi(String value) throws AssetDaoException;
    
    /**
     * Update asset in database
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public AssetEntity updateAsset(AssetEntity asset) throws AssetDaoException;

    /**
     * Delete asset from database
     *
     * @param assetEntity
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public void deleteAsset(AssetEntity assetEntity) throws AssetDaoException;

    /**
     * Get all assets (FIND_ALL)
     *
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public List<AssetEntity> getAssetListAll() throws AssetDaoException;

    /**
     * Get assetHistory by vesselHistoryId - guid
     *
     * @param guid
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public AssetHistory getAssetHistoryByGuid(String guid) throws AssetDaoException;

    /**
     * Get count of search in sql countSql must be count(*)
     *
     * @param countSql
     * @param searchFields
     * @param isDynamic
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public Long getAssetCount(String countSql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException;

    /**
     * Get a page of the list with assets matching the search in sql. This
     * search is Paginated
     *
     * @param pageNumber
     * @param pageSize
     * @param sql
     * @param searchFields
     * @param isDynamic
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public List<AssetHistory> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException;

    /**
     *
     * Get a page of the list with assets matching the search in sql. This
     * search is not Paginated
     *
     * @param sql
     * @param searchFields
     * @param isDynamic
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    public List<AssetHistory> getAssetListSearchNotPaginated(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException;

    List<AssetHistory> getAssetListByAssetGuids(List<String> assetGuids) throws AssetDaoException;

    public AssetEntity getAssetByCfrExcludeArchived(String cfr) throws AssetDaoException;
    public AssetEntity getAssetByIrcsExcludeArchived(String ircs) throws AssetDaoException;
    public AssetEntity getAssetByImoExcludeArchived(String value) throws AssetDaoException;
    public AssetEntity getAssetByMmsiExcludeArchived(String value) throws AssetDaoException;
    List<NotesActivityCode> getNoteActivityCodes();

    AssetEntity getAssetByIccat(String value)  throws AssetDaoException;
    AssetEntity getAssetByUvi(String value) throws AssetDaoException;
    AssetEntity getAssetByGfcm(String value) throws AssetDaoException;
}