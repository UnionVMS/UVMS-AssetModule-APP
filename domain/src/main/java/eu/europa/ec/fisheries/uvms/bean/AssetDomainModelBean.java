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
package eu.europa.ec.fisheries.uvms.bean;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.GetAssetListResponseDto;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.mapper.*;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
@LocalBean
public class AssetDomainModelBean {

    @EJB
    AssetDao assetDao;

    @EJB
    ConfigDomainModelBean configModel;

    @EJB
    AssetGroupDomainModelBean assetGroupModel;

    private static final Logger LOG = LoggerFactory
            .getLogger(AssetDomainModelBean.class);

    public Asset createAsset(Asset asset, String username) throws AssetModelException {
        assertAssetDoesNotExist(asset);
        AssetEntity assetEntity = ModelToEntityMapper.mapToNewAssetEntity(
                asset, configModel.getLicenseType(), username);
        assetDao.createAsset(assetEntity);
        return EntityToModelMapper.toAssetFromEntity(assetEntity);
    }

    public Asset getAssetById(AssetId id) throws AssetModelException {
        AssetEntity assetEntity = getAssetEntityById(id);
        return EntityToModelMapper.toAssetFromEntity(assetEntity);
    }

    private AssetEntity getAssetEntityById(AssetId id) throws AssetDaoException, InputArgumentException {
        if (id == null) {
            throw new NoAssetEntityFoundException("No asset id");
        }
        switch (id.getType()) {
            case CFR:
                return assetDao.getAssetByCfr(id.getValue());
            case IRCS:
                return assetDao.getAssetByIrcs(id.getValue());
            case INTERNAL_ID:
                checkNumberAssetId(id.getValue());
                return assetDao.getAssetById(Long.valueOf(id.getValue()));
            case GUID:
                return assetDao.getAssetByGuid(id.getValue());
            case IMO:
                checkNumberAssetId(id.getValue());
                return assetDao.getAssetByImo(id.getValue());
            case MMSI:
                checkNumberAssetId(id.getValue());
                return assetDao.getAssetByMmsi(id.getValue());
            default:
                throw new NoAssetEntityFoundException("Non valid asset id type");
        }
    }

    private void checkNumberAssetId(String id) throws InputArgumentException {
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new InputArgumentException(id + " can not be parsed to integer");
        }
    }

    public Asset updateAsset(Asset asset, String username) throws AssetModelException {
        if (asset == null) {
            throw new InputArgumentException("Cannot update asset because the asset is null.");
        }

        if (asset.getAssetId() == null) {
            throw new InputArgumentException("Cannot update asset because the asset ID is null.");
        }

        if (asset.getCfr() == null || asset.getCfr().isEmpty()) asset.setCfr(null);
        if (asset.getImo() == null || asset.getImo().isEmpty()) asset.setImo(null);

        try {
            AssetEntity assetEntity = getAssetEntityById(asset.getAssetId());
            Asset assetFromDb = EntityToModelMapper.toAssetFromEntity(assetEntity);

            if (MapperUtil.vesselEquals(asset, assetFromDb)) {
                if (asset.getAssetId() != null) {
                    asset.getAssetId().setGuid(assetFromDb.getAssetId().getGuid());
                }
                return asset;
            }

            assetEntity = ModelToEntityMapper.mapToAssetEntity(assetEntity, asset, configModel.getLicenseType(), username);
            AssetEntity updated = assetDao.updateAsset(assetEntity);

            Asset retVal = EntityToModelMapper.toAssetFromEntity(updated);
            return retVal;
        } catch (AssetDaoException e) {
            LOG.error("[ Error when updating asset. ] {}", e.getMessage());
            throw new AssetModelException(e.getMessage());
        }
    }

    public List<Asset> getAssetListByAssetGroup(List<AssetGroup> groups) throws AssetModelException {
        if (groups == null || groups.isEmpty()) {
            throw new InputArgumentException("Cannot get asset list because criteria are null.");
        }

        List<AssetGroup> dbAssetGroups = assetGroupModel.getAssetGroupsByGroupList(groups);

        Set<AssetHistory> assetHistories = new HashSet<>();
        for (AssetGroup group : dbAssetGroups) {
            List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFieldsFromGroupCriterias(group.getSearchFields());
            String sql = SearchFieldMapper.createSelectSearchSql(searchFields, group.isDynamic());
            List<AssetHistory> tmp = assetDao.getAssetListSearchNotPaginated(sql, searchFields, group.isDynamic());
            assetHistories.addAll(tmp);
        }
        List<Asset> arrayList = new ArrayList<>();

        for (AssetHistory entity : assetHistories) {
            arrayList.add(EntityToModelMapper.toAssetFromAssetHistory(entity));
        }

        return arrayList;

    }

    public GetAssetListResponseDto getAssetList(AssetListQuery query)
            throws AssetModelException {
        if (query == null) {
            throw new InputArgumentException("Cannot get asset list because query is null.");
        }

        if (query.getAssetSearchCriteria() == null
                || query.getAssetSearchCriteria().isIsDynamic() == null
                || query.getAssetSearchCriteria().getCriterias() == null) {
            throw new InputArgumentException(
                    "Cannot get asset list because criteria are null.");
        }

        if (query.getPagination() == null) {
            throw new InputArgumentException(
                    "Cannot get asset list because criteria pagination is null.");
        }

        GetAssetListResponseDto response = new GetAssetListResponseDto();
        List<Asset> arrayList = new ArrayList<>();

        int page = query.getPagination().getPage();
        int listSize = query.getPagination().getListSize();

        boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

        List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria().getCriterias());
        String sql = SearchFieldMapper.createSelectSearchSql(searchFields, isDynamic);

        String countSql = SearchFieldMapper.createCountSearchSql(searchFields, isDynamic);
        Long numberOfAssets = assetDao.getAssetCount(countSql, searchFields, isDynamic);

        int numberOfPages = 0;
        if (listSize != 0) {
            numberOfPages = (int) (numberOfAssets / listSize);
            if (numberOfAssets % listSize != 0) {
                numberOfPages += 1;
            }
        }

        List<AssetHistory> assetEntityList = assetDao.getAssetListSearchPaginated(page, listSize, sql, searchFields, isDynamic);

        for (AssetHistory entity : assetEntityList) {
            arrayList.add(EntityToModelMapper.toAssetFromAssetHistory(entity));
        }

        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(page);
        response.setAssetList(arrayList);

        return response;

    }

    public Long getAssetListCount(AssetListQuery query)
            throws AssetModelException {
        if (query == null) {
            throw new InputArgumentException("Cannot get asset list count because query is null.");
        }

        if (query.getAssetSearchCriteria() == null
                || query.getAssetSearchCriteria().isIsDynamic() == null
                || query.getAssetSearchCriteria().getCriterias() == null) {
            throw new InputArgumentException("Cannot get asset list count because criteria are null.");
        }

        if (query.getPagination() == null) {
            throw new InputArgumentException("Cannot get asset list count because criteria pagination is null.");
        }

        GetAssetListResponseDto response = new GetAssetListResponseDto();

        boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

        List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria().getCriterias());

        String countSql = SearchFieldMapper.createCountSearchSql(searchFields, isDynamic);

        return assetDao.getAssetCount(countSql, searchFields, isDynamic);

    }

    public Asset upsertAsset(Asset asset, String username) throws AssetModelException {
        try {
            getAssetEntityById(asset.getAssetId());
            return updateAsset(asset, username);
        } catch (NoAssetEntityFoundException e) {
            return createAsset(asset, username);
        }
    }

    public List<Asset> getAssetHistoryListByAssetId(AssetId assetId,
                                                    Integer maxNbr) throws AssetModelException {
        AssetEntity vesselHistories = getAssetEntityById(assetId);
        return EntityToModelMapper.toAssetHistoryList(vesselHistories, maxNbr);
    }

    public Asset getAssetHistory(AssetHistoryId historyId)
            throws AssetModelException {
        if (historyId == null || historyId.getEventId() == null) {
            throw new InputArgumentException(
                    "Cannot get asset history because asset history ID is null.");
        }

        AssetHistory assetHistory = assetDao.getAssetHistoryByGuid(historyId
                .getEventId());
        return EntityToModelMapper.toAssetFromAssetHistory(assetHistory);
    }

    public List<NumberOfAssetsGroupByFlagState> getAssetListGroupByFlagState(List<String> assetIds) throws AssetDaoException {
        List<AssetHistory> assetListByAssetGuids = assetDao.getAssetListByAssetGuids(assetIds);
        return EntityToModelMapper.mapEntityToNumberOfAssetsGroupByFlagState(assetListByAssetGuids);

    }

    /**
     * An asset is considered to exist if an asset can be found with the same
     * CFR, IMO, IRCS or MMSI value.
     *
     * @throws AssetDaoException if an asset with the same CFR, IMO, IRCS or MMSI already exists
     */
    private void assertAssetDoesNotExist(Asset asset) throws AssetModelException {
        List<String> messages = new ArrayList<>();
        try {
            if (asset.getCfr() != null && assetDao.getAssetByCfrExcludeArchived(asset.getCfr()) != null) {
                messages.add("An asset with this CFR value already exists.");
            }
        } catch (NoAssetEntityFoundException e) {
            // OK
        }

        try {
            if (asset.getImo() != null && assetDao.getAssetByImoExcludeArchived(asset.getImo()) != null) {
                messages.add("An asset with this IMO value already exists.");
            }
        } catch (NoAssetEntityFoundException e) {
            // OK
        }

        try {
            if (asset.getMmsiNo() != null && assetDao.getAssetByMmsiExcludeArchived(asset.getMmsiNo()) != null) {
                messages.add("An asset with this MMSI value already exists.");
            }
        } catch (NoAssetEntityFoundException e) {
            // OK
        }
        try {
            if (asset.getIrcs() != null && assetDao.getAssetByIrcsExcludeArchived(asset.getIrcs()) != null) {
                messages.add("An asset with this IRCS value already exists.");
            }
        } catch (NoAssetEntityFoundException e) {
            // OK
        }

        if (!messages.isEmpty()) {
            throw new AssetModelException(StringUtils.join(messages, " "));
        }
    }

    public NoteActivityCode getNoteActivityCodes() {
        return EntityToModelMapper.mapEntityToNoteActivityCode(assetDao.getNoteActivityCodes());
    }

    public void deleteAsset(AssetId assetId) throws AssetModelException {

        if(assetId == null){
            return ;
        }

        AssetEntity assetEntity = null;
        try {
            // get an object based on what type of id it has
            assetEntity = getAssetEntityById(assetId);
            // remove it based on its db identity
            assetDao.deleteAsset(assetEntity);
        } catch (NoAssetEntityFoundException e) {
            LOG.warn(e.toString(), e);
            throw e;
        }

    }


}