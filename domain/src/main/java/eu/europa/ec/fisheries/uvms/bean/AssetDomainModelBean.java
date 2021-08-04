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
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.GetAssetListResponseDto;
import eu.europa.ec.fisheries.uvms.constant.VesselIdentifierPrecedenceEnum;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.FishingGearDao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.uvms.mapper.*;
import eu.europa.ec.fisheries.uvms.util.VesselIdentifiersUtil;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.*;
import java.util.function.Function;
import static java.util.stream.Collectors.*;

import static eu.europa.ec.fisheries.uvms.mapper.AssetGroupMapper.generateSearchFields;
import static eu.europa.ec.fisheries.uvms.mapper.SearchFieldMapper.createSearchFieldsFromGroupCriterias;

@Stateless
@LocalBean
public class AssetDomainModelBean {

    @EJB
    private AssetDao assetDao;

    @EJB
    private ConfigDomainModelBean configModel;

    @EJB
    private AssetGroupDomainModelBean assetGroupModel;

    @EJB
    private AssetGroupDao assetGroupDaoBean;

    @EJB
    private FishingGearDao fishingGearDao;

    private static final Logger LOG = LoggerFactory.getLogger(AssetDomainModelBean.class);

    public Asset createAsset(Asset asset, String username) throws AssetModelException {
        assertAssetDoesNotExist(asset);
        AssetEntity assetEntity = ModelToEntityMapper.mapToNewAssetEntity(asset, configModel.getLicenseType(), username);
        this.setFishingGearToAssetHistory(asset.getGearType(),assetEntity);
        assetDao.createAsset(assetEntity);
        return EntityToModelMapper.toAssetFromEntity(assetEntity);
    }

    private void setFishingGearToAssetHistory(String fishingGearCode, AssetEntity assetEntity) {
        Optional<AssetHistory> history = assetEntity.getHistories().stream().filter(AssetHistory::getActive).findAny();
        if (history.isPresent()) {
            history.get().setMainFishingGear(fishingGearDao.getFishingGearByCode(fishingGearCode));
        }
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
            case ICCAT:
                return assetDao.getAssetByIccat(id.getValue());
            case UVI:
                return assetDao.getAssetByUvi(id.getValue());
            case GFCM:
                return assetDao.getAssetByGfcm(id.getValue());
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
            this.setFishingGearToAssetHistory(asset.getGearType(), assetEntity);
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
            List<AssetHistory> tmp = assetDao.getAssetListSearchNotPaginated(sql, searchFields);
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
        String sql;
        if (query.getOrderByCriteria() != null && query.getOrderByCriteria().getOrderByParam() != null) {
            sql = SearchFieldMapper.createSelectSearchSqlWithSorting(searchFields, isDynamic, query.getOrderByCriteria());
        } else {
            sql = SearchFieldMapper.createSelectSearchSql(searchFields, isDynamic);
        }

        String countSql = SearchFieldMapper.createCountSearchSql(searchFields, isDynamic);
        Long numberOfAssets = assetDao.getAssetCount(countSql, searchFields);

        int numberOfPages = 0;
        if (listSize != 0) {
            numberOfPages = (int) (numberOfAssets / listSize);
            if (numberOfAssets % listSize != 0) {
                numberOfPages += 1;
            }
        }

        List<AssetHistory> assetEntityList = assetDao.getAssetListSearchPaginated(page, listSize, sql, searchFields);
        for (AssetHistory entity : assetEntityList) {
            arrayList.add(EntityToModelMapper.toAssetFromAssetHistory(entity));
        }

        response.setTotalCount(numberOfAssets);
        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(page);
        response.setAssetList(arrayList);

        return response;

    }

    public List<AssetHistory> getAssetListSearchPaginated(String guid, Date occurrenceDate, int page, int listSize) throws AssetException{
        if (page < 1) {
            throw new InputArgumentException("Cannot get asset history list, page must be greater than 0.");
        }
        eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup group = assetGroupDaoBean.getAssetGroupByGuid(guid);
        List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField> assetGroupSearchFields = generateSearchFields(group);
        List<SearchKeyValue> searchKeyValues = createSearchFieldsFromGroupCriterias(assetGroupSearchFields );
        String sql = SearchFieldMapper.createSelectSearchSql(searchKeyValues, Boolean.TRUE.equals(group.getDynamic()) , occurrenceDate);
        return assetDao.getAssetListSearchPaginated(page,listSize,sql,searchKeyValues);
    }

    public Long getAssetListCount(AssetListQuery query) throws AssetModelException {
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
        boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();
        List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria().getCriterias());
        String countSql = SearchFieldMapper.createCountSearchSql(searchFields, isDynamic);
        return assetDao.getAssetCount(countSql, searchFields);

    }

    public Asset upsertAsset(Asset asset, String username) throws AssetModelException {
        try {
            getAssetEntityById(asset.getAssetId());
            return updateAsset(asset, username);
        } catch (NoAssetEntityFoundException e) {
            return createAsset(asset, username);
        }
    }

    public List<Asset> getAssetHistoryListByAssetId(AssetId assetId, Integer maxNbr) throws AssetModelException {
        AssetEntity vesselHistories = getAssetEntityById(assetId);
        return EntityToModelMapper.toAssetHistoryList(vesselHistories, maxNbr);
    }

    public Asset getAssetHistory(AssetHistoryId historyId) throws AssetModelException {
        if (historyId == null || historyId.getEventId() == null) {
            throw new InputArgumentException("Cannot get asset history because asset history ID is null.");
        }
        AssetHistory assetHistory = getAssetHistory(historyId.getEventId());
        return EntityToModelMapper.toAssetFromAssetHistory(assetHistory);
    }


    public List<Asset> getAssetHistories(List<String> guids) throws AssetModelException {
        if (guids == null || guids.isEmpty()) {
            throw new InputArgumentException("Cannot get asset histories because no asset history guids were provided");
        }

        List<AssetHistory> assetHistory = assetDao.getAssetHistoriesByGuids(guids);
        return EntityToModelMapper.toAssetFromAssetHistory(assetHistory);
    }

    private AssetHistory getAssetHistory(String guid) throws AssetModelException {

        if(guid == null){
            throw new InputArgumentException("Cannot get asset history because guid is null");
        }

        return assetDao.getAssetHistoryByGuid(guid);
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


    public FlagState getFlagStateByIdAndDate(String assetGuid, Date date) throws InputArgumentException, AssetDaoException {

        if (assetGuid == null ) {
            throw new InputArgumentException(
                    "Cannot get asset  because asset  ID is null.");
        }
        if (date == null ) {
            throw new InputArgumentException(
                    "Cannot get asset  because date   is null.");
        }

        try {
            FlagState flagState = assetDao.getAssetFlagStateByIdAndDate(assetGuid, date);
            return flagState;
        } catch (AssetDaoException e) {
            LOG.warn(e.toString(), e);
            throw e;
        }
    }

    public Asset getAssetByIdAndDate(AssetId assetId, Date date) throws AssetModelException {
        try {
            AssetEntity assetEntity = assetDao.getAssetFromAssetIdAndDate(assetId, date);
            Asset asset = EntityToModelMapper.toAssetFromEntity(assetEntity);
            return asset;
        } catch (AssetDaoException e) {
            throw new AssetModelException(e.toString());
        }
    }

    public Asset getAssetHistoryByAssetGuidAndOccurrenceDate(String assetGuid, Date occurrenceDate) throws AssetModelException {
        try {
            AssetHistory assetHistory = assetDao.getAssetHistoryFromAssetGuidAndOccurrenceDate(assetGuid, occurrenceDate);
            return assetHistory != null ? EntityToModelMapper.toAssetFromAssetHistory(assetHistory) : null;
        } catch (AssetDaoException e) {
            throw new AssetModelException(e.toString());
        }
    }

    public List<AssetGroupsForAssetResponseElement> findAssetGroupsForAssets(List<AssetGroupsForAssetQueryElement> assetGroupsForAssetQueryElementList) throws AssetException {
        List<AssetGroupsForAssetResponseElement> assetGroupList = new ArrayList<>();

        for(AssetGroupsForAssetQueryElement assetGroupsForAssetQueryElement: assetGroupsForAssetQueryElementList) {
            String refUuid = assetGroupsForAssetQueryElement.getRefUuid();
            String connectId = assetGroupsForAssetQueryElement.getConnectId();
            List<AssetId> assetIdList = assetGroupsForAssetQueryElement.getAssetId();
            List<String> uuidList;

            if (refUuid == null || (connectId == null && (assetIdList == null || assetIdList.isEmpty()))) {
                throw new InputArgumentException("Empty arguments in request. Value for connectId: " + connectId + " refUuid: " + refUuid + " and asset:" + assetIdList);
            }

            if (connectId != null) { //search in asset history
                uuidList = findUuidsForAssetHistory(connectId);
                addAssetGroupToList(uuidList,refUuid,assetGroupList);
            }
            else { //search in assets
                uuidList = findUuidsForAsset(assetGroupsForAssetQueryElement);
                addAssetGroupToList(uuidList,refUuid,assetGroupList);
            }
        }
        return assetGroupList;
    }

    private List<String> findUuidsForAssetHistory(String connectId) throws AssetModelException {
        AssetHistory assetHistory = getAssetHistory(connectId);
        return assetGroupDaoBean.getAssetGroupForAssetAndHistory(assetHistory.getAsset(),assetHistory);
    }

    private List<String> findUuidsForAsset( AssetGroupsForAssetQueryElement assetGroupsForAssetQueryElement) {
        AssetEntity asset;
        AssetHistory assetHistory;
        List<AssetId> assetIdList = assetGroupsForAssetQueryElement.getAssetId();
        //used getAssetEntityById which takes advantage of most db indexes
        if(assetIdList.size() == 1){
            try {
                asset = getAssetEntityById(assetIdList.get(0));
            } catch (AssetDaoException | InputArgumentException e) {
                LOG.warn(e.toString(), e);
                return new ArrayList<>();
            }
        }
        else{
            Optional<AssetEntity> assetByAssetIdList = assetDao.getAssetByAssetIdList(assetIdList);
            if(!assetByAssetIdList.isPresent()){
                LOG.warn("AssetEntity was not found for the specified criteria");
                return new ArrayList<>();
            }
            asset = assetByAssetIdList.get();
        }

        try {
            assetHistory = findHighestFromLowestDate(assetGroupsForAssetQueryElement.getOccurrenceDate(),asset.getHistories());
        } catch (AssetException e) {
            LOG.warn(e.toString(), e);
            return new ArrayList<>();
        }

        return assetGroupDaoBean.getAssetGroupForAssetAndHistory(asset,assetHistory);
    }

    public AssetHistory findHighestFromLowestDate(Date occurrenceDate,List<AssetHistory>  assetHistoryList) throws AssetException {
        if(occurrenceDate == null){
            throw new AssetException("Occurrence date is null");
        }

        if(assetHistoryList.size() == 1 ){
            if(assetHistoryList.get(0).getDateOfEvent().before(occurrenceDate)) {
                return assetHistoryList.get(0);
            }
            else {
                throw new AssetException("Found only one date and is after occurrence date");
            }
        }

        try {
            return assetHistoryList.stream().sorted(Comparator.comparing(AssetHistory::getDateOfEvent).reversed()).filter(history -> history.getDateOfEvent().before(occurrenceDate)).findFirst().get();
        }
        catch(NoSuchElementException ex){
            throw new AssetException("Found only dates after occurrence date", ex);
        }
    }

    private void addAssetGroupToList(List<String> groupUuidList,String refUuid,List<AssetGroupsForAssetResponseElement> assetGroupsForAssetResponseElementList){
        AssetGroupsForAssetResponseElement assetGroup = new AssetGroupsForAssetResponseElement();
        assetGroup.setRefUuid(refUuid);
        assetGroup.getGroupUuid().addAll(groupUuidList);
        assetGroupsForAssetResponseElementList.add(assetGroup);
    }

    /**
     * Returns the asset that matches best based on the given criteria and the predefined precedence order as defined
     * by @{@link VesselIdentifierPrecedenceEnum}:
     * <ul>
     *     <li>CFR</li>
     *     <li>UVI</li>
     *     <li>IRCS</li>
     *     <li>EXTERNAL_MARKING</li>
     *     <li>ICCAT</li>
     *     <li>GFCM</li>
     *     <li>MMSI</li>
     * </ul>
     */
    public Asset findAssetByIdentifierPrecedence(AssetListCriteria assetListCriteria) {

        //Check for UVI check bit and remove from criteria if invalid
        AssetListCriteriaPair uviPair =
                assetListCriteria.getCriterias().stream()
                        .filter(p-> ConfigSearchField.UVI == p.getKey())
                        .findAny().orElse(null);
        if (uviPair != null) {
            boolean invalid = VesselIdentifiersUtil.isLastCheckBitInvalidInUVISchemeId(uviPair.getValue());
            if (invalid) {
                assetListCriteria.getCriterias().remove(uviPair);
            }
        }

        //Retrieve the assets from database
        List<AssetHistory> assetHistories = assetDao.getAssetsByVesselIdientifiers(assetListCriteria);
        final List<Asset> assets = assetHistories.stream()
                .map(EntityToModelMapper::toAssetFromAssetHistory).collect(toList());
        if (CollectionUtils.isEmpty(assetHistories)) {
            return null;
        }


        //Sort the criteria
        List<AssetListCriteriaPair> sortedPairs =
        assetListCriteria.getCriterias().stream()
                .filter(p-> (VesselIdentifierPrecedenceEnum.getByField(p.getKey())) != null)
                .collect(toMap(pair-> VesselIdentifierPrecedenceEnum.getByField(pair.getKey()), Function.identity()))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(e->e.getKey().getPrecedence()))
                .collect(mapping(entry->entry.getValue(), toList()));

        //Loop the sorted criteria and return the first asset that matches
        Asset entity = null;
        for (AssetListCriteriaPair pair : sortedPairs) {
            if ((entity = this.getAssetEntityByConfigField(assets, pair)) != null) {
                return entity;
            }
        }
        return null;
    }

    private Asset getAssetEntityByConfigField(List<Asset> assets, AssetListCriteriaPair pair)  {
        if (pair == null)   return  null;
        switch (pair.getKey()) {
            case CFR:
                return assets.stream().filter(a->a.getCfr().equalsIgnoreCase(pair.getValue())).findAny().orElse(null);
            case UVI:
                return assets.stream().filter(a->a.getUvi().equalsIgnoreCase(pair.getValue())).findAny().orElse(null);
            case IRCS:
                return assets.stream().filter(a->a.getIrcs().equalsIgnoreCase(pair.getValue())).findAny().orElse(null);
            case EXTERNAL_MARKING:
                return assets.stream().filter(a->a.getExternalMarking().equalsIgnoreCase(pair.getValue())).findAny().orElse(null);
            case ICCAT:
                return assets.stream().filter(a->a.getIccat().equalsIgnoreCase(pair.getValue())).findAny().orElse(null);
            case GFCM:
                return assets.stream().filter(a->a.getGfcm().equalsIgnoreCase(pair.getValue())).findAny().orElse(null);
            case MMSI:
                return assets.stream().filter(a->a.getMmsiNo().equalsIgnoreCase(pair.getValue())).findAny().orElse(null);
            default:
                return null;
        }
    }
}