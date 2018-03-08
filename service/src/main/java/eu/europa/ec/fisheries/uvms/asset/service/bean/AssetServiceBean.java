
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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.enums.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.types.*;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetSEDao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetListResponsePaginated;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetServiceBean implements AssetService {

	final static Logger LOG = LoggerFactory.getLogger(AssetServiceBean.class);

	@EJB
	MessageProducer messageProducer;

	@EJB
	AssetQueueConsumer reciever;


	@EJB
	ConfigServiceBean configModel;

	@EJB
	AssetSEDao assetSEDao;

	@EJB
	AssetGroupDao assetGroupDao;

	/**
	 * {@inheritDoc}
	 *
	 * @param asset
	 * @return
	 * @throws AssetException
	 */
	@Override
	public AssetSE createAsset(AssetSE asset, String username) throws AssetException {

		assertAssetDoesNotExist(asset);
		AssetSE createdAssetEntity = assetSEDao.createAsset(asset);

		try {
			String auditData = AuditModuleRequestMapper.mapAuditLogAssetCreated(createdAssetEntity.getId().toString(),
					username);
			messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
		} catch (AssetMessageException e) {
			LOG.warn("Failed to send audit log message! Asset with guid {} was created ",
					createdAssetEntity.getId());
		} catch (AuditModelMarshallException e) {
			LOG.error("Failed to send audit log message! Asset with guid {} was created ",
					createdAssetEntity.getId());
		}

		return createdAssetEntity;

	}

	/**
	 * {@inheritDoc}
	 *
	 * @param query
	 * @return
	 * @throws AssetException
	 */
	@Override
	public AssetListResponsePaginated getAssetList(AssetListQuery query) throws AssetException {


		if (query == null) {
			throw new InputArgumentException("Cannot get asset list because query is null.");
		}

		if (query.getAssetSearchCriteria() == null || query.getAssetSearchCriteria().isIsDynamic() == null
				|| query.getAssetSearchCriteria().getCriterias() == null) {
			throw new InputArgumentException("Cannot get asset list because criteria are null.");
		}

		if (query.getPagination() == null) {
			throw new InputArgumentException("Cannot get asset list because criteria pagination is null.");
		}


		int page = query.getPagination().getPage();
		int listSize = query.getPagination().getListSize();
		boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

		List<SearchKeyValue> searchFields = SearchFieldMapper
				.createSearchFields(query.getAssetSearchCriteria().getCriterias());

		String sql = SearchFieldMapper.createSelectSearchSql(searchFields, isDynamic);
		String countSql = SearchFieldMapper.createCountSearchSql(searchFields, isDynamic);
		Long numberOfAssets = assetSEDao.getAssetCount(countSql, searchFields);

		int numberOfPages = 0;
		if (listSize != 0) {
			numberOfPages = (int) (numberOfAssets / listSize);
			if (numberOfAssets % listSize != 0) {
				numberOfPages += 1;
			}
		}

		List<AssetSE> assetEntityList = assetSEDao.getAssetListSearchPaginated(page, listSize, sql, searchFields,
				isDynamic);


		AssetListResponsePaginated listAssetResponse = new AssetListResponsePaginated();
		listAssetResponse.setCurrentPage(page);
		listAssetResponse.setTotalNumberOfPages(numberOfPages);
		listAssetResponse.getAssetList().addAll(assetEntityList);
		return listAssetResponse;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param requestQuery
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
	 */
	@Override
	public Long getAssetListCount(AssetListQuery requestQuery) throws AssetException {
		LOG.debug("Getting AssetList.");
		//return getAssetListCount_FROM_DOMAINMODEL(requestQuery);
		return 42L;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param asset
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
	 */
	@Override
	public AssetDTO updateAsset(AssetDTO asset, String username, String comment) throws AssetException {
		AssetDTO updatedAsset = updateAssetInternal(asset, username);
		logAssetUpdated(updatedAsset, comment, username);
		return updatedAsset;
	}

	@Override
	public AssetDTO archiveAsset(AssetDTO asset, String username, String comment) throws AssetException {
		AssetDTO archivedAsset = updateAssetInternal(asset, username);
		logAssetArchived(archivedAsset, comment, username);
		return archivedAsset;
	}

	private void logAssetUpdated(AssetDTO asset, String comment, String username) throws AssetMessageException {
		try {
			String auditData = AuditModuleRequestMapper.mapAuditLogAssetUpdated(asset.getAssetId().getGuid(), comment,
					username);
			messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
		} catch (AuditModelMarshallException e) {
			LOG.error("Failed to send audit log message! Asset with guid {} was updated ",
					asset.getAssetId().getGuid());
		}
	}

	private void logAssetArchived(AssetDTO asset, String comment, String username) throws AssetMessageException {
		try {
			String auditData = AuditModuleRequestMapper.mapAuditLogAssetArchived(asset.getAssetId().getGuid(), comment,
					username);
			messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
		} catch (AuditModelMarshallException e) {
			LOG.error("Failed to send audit log message! Asset with guid {} was archived ",
					asset.getAssetId().getGuid());
		}
	}

	private AssetDTO updateAssetInternal(AssetDTO asset, String username) throws AssetException {
		LOG.debug("Updating Asset");
		AssetDTO updatedAsset;

		if (asset == null) {
			throw new InputArgumentException("No asset to update");
		}

		if (asset.getAssetId().getValue() == null) {
			throw new InputArgumentException("No id on asset to update");
		}


		/*
		AssetDTO storedAsset = getAssetById(asset.getAssetId());
		switch (storedAsset.getSource()) {
		case INTERNAL:
			updatedAsset = updateAsset_FROM_DOMAINMODEL(asset, username);
			break;
		default:
			throw new AssetServiceException("Not allowed to update");
		}
		return updatedAsset;
		*/
		return null;
	}

	@Override
	public AssetDTO upsertAsset(AssetDTO asset, String username) throws AssetException {

		if (asset == null) {
			throw new InputArgumentException("No asset to upsert");
		}

		//return upsertAsset_FROM_DOMAINMODEL(asset, username);
		return null;

	}

	/**
	 * {@inheritDoc}
	 *
	 * @param assetId
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
	 */
	@Override
	public AssetDTO getAssetById(AssetId assetId, AssetDataSourceQueue source) throws AssetException {
		AssetDTO assetById = null;

		if (assetId == null) {
			throw new InputArgumentException("AssetId object is null");
		}

		if (assetId.getValue() == null || assetId.getType() == null) {
			throw new InputArgumentException("AssetId value or type is null");
		}

		if (source == null) {
			throw new InputArgumentException("AssetDataSourceQueue is null");
		}

		LOG.debug("GETTING ASSET BY ID: {} : {} at {}.", assetId.getType(), assetId.getValue(), source.name());

		switch (source) {
		case INTERNAL:
			//assetById = getAssetById(assetId);
			break;
		default:
//			String data = AssetDataSourceRequestMapper.mapGetAssetById(assetId.getValue(), assetId.getType());
//			String messageId = messageProducer.sendDataSourceMessage(data, source);
//			TextMessage response = reciever.getMessage(messageId, TextMessage.class);
//			assetById = AssetDataSourceResponseMapper.mapToAssetFromResponse(response, messageId);
			break;
		}
		return assetById;

	}

	/**
	 *
	 * @param guid
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
	 */
	@Override
	public AssetDTO getAssetByGuid(String guid) throws AssetException {
		LOG.debug("Getting asset by ID.");
		if (guid == null || guid.isEmpty()) {
			throw new InputArgumentException("AssetId is null");
		}

		AssetId assetId = new AssetId();
		assetId.setType(AssetIdTypeEnum.GUID);
		assetId.setValue(guid);
//		return getAssetById(assetId);
		return null;
	}

	/**
	 *
	 * @param groups
	 * @return
	 * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
	 */
	@Override
		public List<AssetDTO> getAssetListByAssetGroups(List<AssetGroupDTO> groups) throws AssetException {
		LOG.debug("Getting asset by ID.");
		if (groups == null || groups.isEmpty()) {
			throw new InputArgumentException("No groups in query");
		}

		//return getAssetListByAssetGroup(groups);
		return null;
	}

	@Override
	    //public AssetListGroupByFlagStateResponse getAssetListGroupByFlagState(List assetIds) throws AssetException {
		public Object getAssetListGroupByFlagState(List assetIds) throws AssetException {
		LOG.debug("Getting asset list by asset ids group by flags State.");
		/*
		List assetListGroupByFlagState = getAssetListGroupByFlagState_FROM_DOMAINMODEL(assetIds);
		AssetListGroupByFlagStateResponse assetListGroupByFlagStateResponse = new AssetListGroupByFlagStateResponse();
		assetListGroupByFlagStateResponse.getNumberOfAssetsGroupByFlagState().addAll(assetListGroupByFlagState);
		return assetListGroupByFlagStateResponse;
		*/
		return null;

	}

	public NoteActivityCode getNoteActivityCodes() {
		//return getNoteActivityCodes_FROM_DOMAINMODEL();
		return null;
	}

	@Override
	public void deleteAsset(AssetId assetId) throws AssetException {
		//deleteAsset_FRROM_DOMAINMODEL(assetId);
	}

	private void assertAssetDoesNotExist(AssetSE asset) throws AssetModelException {


		List<String> messages = new ArrayList<>();
		try {
			if (asset.getCfr() != null && assetSEDao.getAssetByCfr(asset.getCfr()) != null) {
				messages.add("An asset with this CFR value already exists.");
			}
		} catch (NoAssetEntityFoundException e) {
			// OK
		}

		try {
			if (asset.getImo() != null && assetSEDao.getAssetByImo(asset.getImo()) != null) {
				messages.add("An asset with this IMO value already exists.");
			}
		} catch (NoAssetEntityFoundException e) {
			// OK
		}

		try {
			if (asset.getMmsi() != null && assetSEDao.getAssetByMmsi(asset.getMmsi()) != null) {
				messages.add("An asset with this MMSI value already exists.");
			}
		} catch (NoAssetEntityFoundException e) {
			// OK
		}
		try {
			if (asset.getIrcs() != null && assetSEDao.getAssetByIrcs(asset.getIrcs()) != null) {
				messages.add("An asset with this IRCS value already exists.");
			}
		} catch (NoAssetEntityFoundException e) {
			// OK
		}

		if (!messages.isEmpty()) {
			throw new AssetModelException(StringUtils.join(messages, " "));
		}

	}


	public Long getAssetListCount_FROM_DOMAINMODEL(AssetListQuery query)
			throws AssetModelException, InputArgumentException {
		if (query == null) {
			throw new InputArgumentException("Cannot get asset list count because query is null.");
		}

		if (query.getAssetSearchCriteria() == null || query.getAssetSearchCriteria().isIsDynamic() == null
				|| query.getAssetSearchCriteria().getCriterias() == null) {
			throw new InputArgumentException("Cannot get asset list count because criteria are null.");
		}

		if (query.getPagination() == null) {
			throw new InputArgumentException("Cannot get asset list count because criteria pagination is null.");
		}

		/*

		GetAssetListResponseDto response = new GetAssetListResponseDto();

		boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

		List<SearchKeyValue> searchFields = SearchFieldMapper
				.createSearchFields(query.getAssetSearchCriteria().getCriterias());

		String countSql = SearchFieldMapper.createCountSearchSql(searchFields, isDynamic);

		return assetDao.getAssetCount(countSql, searchFields, isDynamic);

		*/
		return null;

	}

	/*
	private AssetEntity getAssetEntityById_FROM_DOMAINMODEL(AssetId id)
			throws AssetDaoException, InputArgumentException {
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
	*/

	private void checkNumberAssetId(String id) throws InputArgumentException {
		try {
			Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new InputArgumentException(id + " can not be parsed to integer");
		}
	}

	/*
	public AssetDTO getAssetById(AssetId id) throws AssetModelException, InputArgumentException {
		AssetEntity assetEntity = getAssetEntityById_FROM_DOMAINMODEL(id);
		return EntityToModelMapper.toAssetFromEntity(assetEntity);
	}
	*/

	/*
	public AssetDTO updateAsset_FROM_DOMAINMODEL(AssetDTO asset, String username)
			throws AssetModelException, InputArgumentException {
		if (asset == null) {
			throw new InputArgumentException("Cannot update asset because the asset is null.");
		}

		if (asset.getAssetId() == null) {
			throw new InputArgumentException("Cannot update asset because the asset ID is null.");
		}

		if (asset.getCfr() == null || asset.getCfr().isEmpty())
			asset.setCfr(null);
		if (asset.getImo() == null || asset.getImo().isEmpty())
			asset.setImo(null);

		try {
			AssetEntity assetEntity = getAssetEntityById_FROM_DOMAINMODEL(asset.getAssetId());
			AssetDTO assetFromDb = EntityToModelMapper.toAssetFromEntity(assetEntity);

			if (MapperUtil.vesselEquals(asset, assetFromDb)) {
				if (asset.getAssetId() != null) {
					asset.getAssetId().setGuid(assetFromDb.getAssetId().getGuid());
				}
				return asset;
			}

			assetEntity = ModelToEntityMapper.mapToAssetEntity(assetEntity, asset, configModel.getLicenseType(),
					username);
			AssetEntity updated = assetDao.updateAsset(assetEntity);

			AssetDTO retVal = EntityToModelMapper.toAssetFromEntity(updated);
			return retVal;
		} catch (AssetDaoException e) {
			LOG.error("[ Error when updating asset. ] {}", e.getMessage());
			throw new AssetModelException(e.getMessage());
		}
	}
	*/

	/*
	public AssetDTO upsertAsset_FROM_DOMAINMODEL(AssetDTO asset, String username) throws AssetException {
		try {
			getAssetEntityById_FROM_DOMAINMODEL(asset.getAssetId());
			return updateAsset_FROM_DOMAINMODEL(asset, username);
		} catch (NoAssetEntityFoundException e) {
			return createAsset(asset, username);
		}
	}
	*/

	/*
	public List<AssetDTO> getAssetHistoryListByAssetId(AssetId assetId, Integer maxNbr)
			throws AssetModelException, InputArgumentException {
		AssetEntity vesselHistories = getAssetEntityById_FROM_DOMAINMODEL(assetId);
		return EntityToModelMapper.toAssetHistoryList(vesselHistories, maxNbr);
	}
	*/

	/*
	public AssetDTO getAssetHistory_FROM_DOMAINMODEL(AssetHistoryId historyId)
			throws AssetModelException, InputArgumentException {
		if (historyId == null || historyId.getEventId() == null) {
			throw new InputArgumentException("Cannot get asset history because asset history ID is null.");
		}

		AssetHistory assetHistory = assetDao.getAssetHistoryByGuid(historyId.getEventId());
		return EntityToModelMapper.toAssetFromAssetHistory(assetHistory);
	}
	*/

	/*
	public List<NumberOfAssetsGroupByFlagState> getAssetListGroupByFlagState_FROM_DOMAINMODEL(List<String> assetIds)
			throws AssetDaoException {
		List<AssetHistory> assetListByAssetGuids = assetDao.getAssetListByAssetGuids(assetIds);
		return EntityToModelMapper.mapEntityToNumberOfAssetsGroupByFlagState(assetListByAssetGuids);

	}
	*/

	/*
	private void assertAssetDoesNotExist_FROM_DOMAINMODEL(AssetDTO asset) throws AssetModelException {
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
	*/


	/*
	public NoteActivityCode getNoteActivityCodes_FROM_DOMAINMODEL() {
		return EntityToModelMapper.mapEntityToNoteActivityCode(assetDao.getNoteActivityCodes());
	}
	*/

	/*
	public void deleteAsset_FRROM_DOMAINMODEL(AssetId assetId) throws AssetModelException, InputArgumentException {

		if (assetId == null) {
			return;
		}

		AssetEntity assetEntity = null;
		try {
			// get an object based on what type of id it has
			assetEntity = getAssetEntityById_FROM_DOMAINMODEL(assetId);
			// remove it based on its db identity
			assetDao.deleteAsset(assetEntity);
		} catch (NoAssetEntityFoundException e) {
			LOG.warn(e.toString(), e);
			throw e;
		}

	}
	*/

	/*
	public FlagState getFlagStateByIdAndDate_FROM_DOMAINMODEL(String assetGuid, Date date)
			throws InputArgumentException, AssetDaoException {

		if (assetGuid == null) {
			throw new InputArgumentException("Cannot get asset  because asset  ID is null.");
		}
		if (date == null) {
			throw new InputArgumentException("Cannot get asset  because date   is null.");
		}

		try {
			FlagState flagState = assetDao.getAssetFlagStateByIdAndDate(assetGuid, date);
			return flagState;
		} catch (AssetDaoException e) {
			LOG.warn(e.toString(), e);
			throw e;
		}
	}
	*/

	/*

	public AssetDTO getAssetByIdAndDate_FROM_DOMAINMODEL(AssetId assetId, Date date) throws AssetModelException {
		try {
			AssetEntity assetEntity = assetDao.getAssetFromAssetIdAndDate(assetId, date);
			AssetDTO asset = EntityToModelMapper.toAssetFromEntity(assetEntity);
			return asset;
		} catch (AssetDaoException e) {
			throw new AssetModelException(e.toString());
		}
	}
	*/

	/*
	public List<AssetDTO> getAssetListByAssetGroup(List<AssetGroupWSDL> groups)
			throws AssetModelException, InputArgumentException {
		if (groups == null || groups.isEmpty()) {
			throw new InputArgumentException("Cannot get asset list because criteria are null.");
		}

		List<AssetGroupWSDL> dbAssetGroups = getAssetGroupsByGroupList(groups);

		Set<AssetHistory> assetHistories = new HashSet<>();
		for (AssetGroupWSDL group : dbAssetGroups) {
			List<SearchKeyValue> searchFields = SearchFieldMapper
					.createSearchFieldsFromGroupCriterias(group.getSearchFields());
			String sql = SearchFieldMapper.createSelectSearchSql(searchFields, group.isDynamic());
			List<AssetHistory> tmp = assetDao.getAssetListSearchNotPaginated(sql, searchFields, group.isDynamic());
			assetHistories.addAll(tmp);
		}
		List<AssetDTO> arrayList = new ArrayList<>();

		for (AssetHistory entity : assetHistories) {
			arrayList.add(EntityToModelMapper.toAssetFromAssetHistory(entity));
		}

		return arrayList;

	}
	*/

	public List<AssetGroupDTO> getAssetGroupsByGroupList(
			List<AssetGroupDTO> groups)
			throws AssetModelException, InputArgumentException {
		if (groups == null) {
			throw new InputArgumentException("Cannot get asset group list because the input is null.");
		}

		List<String> guidList = new ArrayList<>();
		for (AssetGroupDTO group : groups) {
			guidList.add(group.getGuid());
		}

		if (guidList.isEmpty()) {
			throw new InputArgumentException("Cannot get asset group list because the input missing guid.");
		}

		try {
			List<AssetGroupDTO> vesselGroupList = new ArrayList<>();
			// List<AssetGroup> filterGroupList =
			// assetGroupDao.getAssetGroupsByGroupGuidList(guidList);
			// for (AssetGroup group : filterGroupList) {
			// vesselGroupList.add(AssetGroupMapper.toAssetGroup(group));
			// }

			return vesselGroupList;
		} catch (Exception e) {
			LOG.error("[ Error when getting asset group list by List. ] groups {} exception: {}", groups,
					e.getMessage());
			throw new AssetModelException(e.getMessage());
		}
	}



}