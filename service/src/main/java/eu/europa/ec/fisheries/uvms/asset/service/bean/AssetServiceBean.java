
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

import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.types.*;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        Long numberOfAssets = assetSEDao.getAssetCount(countSql, searchFields, isDynamic);

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
     * @param query
     * @return
     * @throws AssetException
     */
    @Override
    public Long getAssetListCount(AssetListQuery query) throws AssetException {
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

        boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

        List<SearchKeyValue> searchFields = SearchFieldMapper
                .createSearchFields(query.getAssetSearchCriteria().getCriterias());

        String countSql = SearchFieldMapper.createCountSearchSql(searchFields, isDynamic);
        Long result = assetSEDao.getAssetCount(countSql, searchFields, isDynamic);
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public AssetSE updateAsset(AssetSE asset, String username, String comment) throws AssetException {
        AssetSE updatedAsset = updateAssetInternal(asset, username);
        logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    @Override
    public AssetSE archiveAsset(AssetSE asset, String username, String comment) throws AssetException {
        AssetSE archivedAsset = updateAssetInternal(asset, username);
        logAssetArchived(archivedAsset, comment, username);
        return archivedAsset;
    }

    private void logAssetUpdated(AssetSE asset, String comment, String username) throws AssetMessageException {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetUpdated(asset.getId().toString(), comment,
                    username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was updated ",
                    asset.getId().toString());
        }
    }

    private void logAssetArchived(AssetSE asset, String comment, String username) throws AssetMessageException {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetArchived(asset.getId().toString(), comment,
                    username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was archived ",
                    asset.getId().toString());
        }
    }

    private AssetSE updateAssetInternal(AssetSE asset, String username) throws AssetException {

        if (asset == null) {
            throw new InputArgumentException("No asset to update");
        }

        if (asset.getId() == null) {
            throw new InputArgumentException("No id on asset to update");
        }

        if (asset.getCfr() == null || asset.getCfr().isEmpty()) asset.setCfr(null);
        if (asset.getImo() == null || asset.getImo().isEmpty()) asset.setImo(null);
        if (asset.getMmsi() == null || asset.getMmsi().isEmpty()) asset.setMmsi(null);
        if (asset.getIrcs() == null || asset.getIrcs().isEmpty()) asset.setIrcs(null);
        if (asset.getImo() == null || asset.getImo().isEmpty()) asset.setImo(null);
        if (asset.getGfcm() == null || asset.getGfcm().isEmpty()) asset.setGfcm(null);
        if (asset.getIccat() == null || asset.getIccat().isEmpty()) asset.setIccat(null);
        if (asset.getUvi() == null || asset.getUvi().isEmpty()) asset.setUvi(null);

        try {
            AssetSE assetDB = getAssetById((asset.getId()));
            if(assetDB != null){
                asset.setUpdatedBy(username);
                AssetSE updated = assetSEDao.updateAsset(asset);
                return updated;
            } else {
                throw new AssetException("Asset with that id does not exist");
            }
        } catch (AssetException e) {
            LOG.error("[ Error when updating asset. ] {}", e.getMessage());
            throw new AssetModelException(e.getMessage(), e);
        }
    }


    @Override
    public AssetSE upsertAsset(AssetSE asset, String username) throws AssetException {

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
    public AssetSE getAssetById(AssetId assetId, AssetDataSourceQueue source) throws AssetException {
        AssetSE asset = null;

        if (assetId == null) {
            throw new InputArgumentException("AssetId object is null");
        }

        if (assetId.getValue() == null || assetId.getType() == null) {
            throw new InputArgumentException("AssetId value or type is null");
        }

        if (source == null) {
            throw new InputArgumentException("AssetDataSourceQueue is null");
        }


        switch (source) {
            case INTERNAL:
                return getAssetById(assetId);

            default:
                //String data = AssetDataSourceRequestMapper.mapGetAssetById(assetId.getValue(), assetId.getType());
                //String messageId = messageProducer.sendDataSourceMessage(data, source);
                //TextMessage response = reciever.getMessage(messageId, TextMessage.class);
                //asset = AssetDataSourceResponseMapper.mapToAssetFromResponse(response, messageId);
                //break;
        }
        return asset;

    }


    public AssetSE getAssetById(AssetId assetId) throws AssetException {

        if (assetId == null) {
            throw new InputArgumentException("AssetId object is null");
        }

        if (assetId.getValue() == null || assetId.getType() == null) {
            throw new InputArgumentException("AssetId value or type is null");
        }


        switch (assetId.getType()) {
            case CFR:
                return assetSEDao.getAssetByCfr(assetId.getValue());
            case IRCS:
                return assetSEDao.getAssetByIrcs(assetId.getValue());
            case INTERNAL_ID:
                UUID uuid = UUID.fromString(assetId.getGuid());
                return assetSEDao.getAssetById(uuid);
            case IMO:
                checkNumberAssetId(assetId.getValue());
                return assetSEDao.getAssetByImo(assetId.getValue());
            case MMSI:
                checkNumberAssetId(assetId.getValue());
                return assetSEDao.getAssetByMmsi(assetId.getValue());
            case ICCAT:
                return assetSEDao.getAssetByIccat(assetId.getValue());
            case UVI:
                return assetSEDao.getAssetByUvi(assetId.getValue());
            case GFCM:
                return assetSEDao.getAssetByGfcm(assetId.getValue());
            default:
                throw new NoAssetEntityFoundException("Non valid asset id type");
        }
    }

    @Override
    public AssetSE getAssetFromAssetIdAtDate(String idType, String idValue, LocalDateTime date) throws AssetException {

        if (idType == null) {
            throw new InputArgumentException("Type is null");
        }
        AssetIdTypeEnum assetType = AssetIdTypeEnum.fromValue(idType);
        if (assetType == null) {
            throw new InputArgumentException("Not a valid type: " + idType);
        }
        if (idValue == null) {
            throw new InputArgumentException("Value is null");
        }
        if (date == null) {
            throw new InputArgumentException("Date is null");
        }
        if(assetType == AssetIdTypeEnum.GUID || assetType == AssetIdTypeEnum.INTERNAL_ID){
            try{
                UUID.fromString(idValue);
            }
            catch(IllegalArgumentException e){
                throw new InputArgumentException("Not a valid UUID");
            }
        }

		try {
			AssetId assetId = new AssetId();
			assetId.setType(assetType);
			assetId.setValue(idValue);
			assetId.setGuid(idValue);
			AssetSE asset = assetSEDao.getAssetFromAssetIdAtDate(assetId, date);
			return asset;
		} catch (AssetDaoException e) {
			throw new AssetServiceException("Could not get asset by id and date", e);
		}
    }


    /**
     * @param id
     * @return
     * @throws AssetException
     */
    @Override
    public AssetSE getAssetById(UUID id) throws AssetException {
        if (id == null) {
            throw new InputArgumentException("Id is null");
        }
        AssetSE fetchedAsset = assetSEDao.getAssetById(id);
        return fetchedAsset;
    }


    /**
     * @param groups
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public List<AssetSE> getAssetListByAssetGroups(List<AssetGroupDTO> groups) throws AssetException {
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

        if (assetId == null) {
            return;
        }

        AssetSE assetEntity = null;
        try {
            // get an object based on what type of id it has
            assetEntity = getAssetById(assetId);
            assetSEDao.deleteAsset(assetEntity);
        } catch (NoAssetEntityFoundException e) {
            LOG.warn(e.toString(), e);
            throw e;
        }
    }

    @Override
    public List<AssetSE> getRevisionsForAsset(AssetSE asset) throws AssetException {
        try {
            List<AssetSE> ret = assetSEDao.getRevisionsForAsset(asset);
            return ret;
        } catch (NoAssetEntityFoundException e) {
            LOG.warn(e.toString(), e);
            throw e;
        }
    }

    @Override
    public AssetSE getAssetRevisionForRevisionId(AssetSE asset, UUID historyId) throws AssetException {
        try {
            AssetSE ret = assetSEDao.getAssetRevisionForHistoryId(asset, historyId);
            return ret;
        } catch (NoAssetEntityFoundException e) {
            LOG.warn(e.toString(), e);
            throw e;
        }
    }

    private void assertAssetDoesNotExist(AssetSE asset) throws AssetException {


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


    private void checkNumberAssetId(String id) throws InputArgumentException {
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new InputArgumentException(id + " can not be parsed to integer");
        }
    }



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
	public NoteActivityCode getNoteActivityCodes_FROM_DOMAINMODEL() {
		return EntityToModelMapper.mapEntityToNoteActivityCode(assetDao.getNoteActivityCodes());
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