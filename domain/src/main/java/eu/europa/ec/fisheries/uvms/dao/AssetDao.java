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

import javax.ejb.Local;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteria;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;

@Local
public interface AssetDao {

    /**
     * Create asset in database
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity createAsset(AssetEntity asset) throws AssetDaoException;

    /**
     * Get vessel by internal vessel id
     *
     * @param id
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity getAssetById(Long id) throws AssetDaoException;

    /**
     * Get asset by cfr
     *
     * @param cfr
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity getAssetByCfr(String cfr) throws AssetDaoException;

    /**
     * Get asset by ircs
     *
     * @param ircs
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity getAssetByIrcs(String ircs) throws AssetDaoException;

    /**
     * Get asset by guid
     *
     * @param guid
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity getAssetByGuid(String guid) throws AssetDaoException;

    /**
     * Get asset by IMO
     *
     * @param value
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity getAssetByImo(String value) throws AssetDaoException;

    /**
     * Get asset by MMSI
     *
     * @param value
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity getAssetByMmsi(String value) throws AssetDaoException;

    /**
     * Update asset in database
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    AssetEntity updateAsset(AssetEntity asset) throws AssetDaoException;

    /**
     * Delete asset from database
     *
     * @param assetEntity
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    void deleteAsset(AssetEntity assetEntity) throws AssetDaoException;

    /**
     * Get all assets (FIND_ALL)
     *
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    List<AssetEntity> getAssetListAll() throws AssetDaoException;

    List<AssetHistory> getAssetHistoryByCriteria(List<AssetListCriteriaPair> criteriaPairs, Integer maxResult) throws AssetDaoException;

    /**
     * replacement for the above method, used by remote ejb only
     */
    List<AssetHistory> _getAssetHistoryByCriteria(String reportDate, String cfr, String regCountry, String ircs, String extMark, String iccat,String uvi) throws AssetDaoException;

    /**
     * Get assetHistory by vesselHistoryId - guid
     *
     * @param guid
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */

    AssetHistory getAssetHistoryByGuid(String guid) throws AssetDaoException;

    AssetHistory getAssetHistoryByHashKey(String hashKey) throws AssetDaoException;

    /**
     * Get assetHistories by multiple vesselHistoryIds - guids
     *
     * @param guids
     * @return assetHistories
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    List<AssetHistory> getAssetHistoriesByGuids(List<String> guids) throws AssetDaoException;

    /**
     * Get count of search in sql countSql must be count(*)
     *
     * @param countSql
     * @param searchFields
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    Long getAssetCount(String countSql, List<SearchKeyValue> searchFields) throws AssetDaoException;

    /**
     * Get a page of the list with assets matching the search in sql. This
     * search is Paginated
     *
     * @param pageNumber
     * @param pageSize
     * @param sql
     * @param searchFields
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    List<AssetHistory> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, String sql, List<SearchKeyValue> searchFields) throws AssetDaoException;

    /**
     * Get a page of the list with assets matching the search in sql. This
     * search is not Paginated
     *
     * @param sql
     * @param searchFields
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException
     */
    List<AssetHistory> getAssetListSearchNotPaginated(String sql, List<SearchKeyValue> searchFields) throws AssetDaoException;

    List<AssetHistory> getAssetListByAssetGuids(List<String> assetGuids) throws AssetDaoException;

    AssetEntity getAssetByCfrExcludeArchived(String cfr) throws AssetDaoException;

    AssetEntity getAssetByIrcsExcludeArchived(String ircs) throws AssetDaoException;

    AssetEntity getAssetByImoExcludeArchived(String value) throws AssetDaoException;

    AssetEntity getAssetByMmsiExcludeArchived(String value) throws AssetDaoException;

    List<NotesActivityCode> getNoteActivityCodes();

    AssetEntity getAssetByIccat(String value) throws AssetDaoException;

    AssetEntity getAssetByUvi(String value) throws AssetDaoException;

    AssetEntity getAssetByGfcm(String value) throws AssetDaoException;

    FlagState getAssetFlagStateByIdAndDate(String  assetGuid, Date date) throws AssetDaoException;

    AssetEntity getAssetFromAssetIdAndDate(AssetId assetId, Date date) throws AssetDaoException;

    /**
     *
     * Get asset history of asset by asset guid and occurrence date
     *
     * @param assetGuid guid of asset
     * @param occurrenceDate occurrence date for asset history
     * @return AssetHistory entity
     */
    AssetHistory getAssetHistoryFromAssetGuidAndOccurrenceDate(String assetGuid, Date occurrenceDate) throws AssetDaoException;

    Optional<AssetEntity> getAssetByAssetIdList(List<AssetId> idList);

    List<AssetHistory> getAssetsByVesselIdientifiers(AssetListCriteria criteria);
}
