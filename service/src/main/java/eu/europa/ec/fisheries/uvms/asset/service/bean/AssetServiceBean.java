
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.types.AssetGroupDTO;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.asset.types.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.uvms.asset.types.NoteActivityCode;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetSEDao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetDaoMappingException;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetListResponsePaginated;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

@Stateless
public class AssetServiceBean implements AssetService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetServiceBean.class);
    
    @Inject
    private AuditServiceBean auditService;

    @Inject
    private AssetSEDao assetSEDao;

    @Inject
    private AssetGroupDao assetGroupDao;

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws AssetException
     */
    @Override
    public AssetSE createAsset(AssetSE asset, String username) throws AssetServiceException {

        AssetSE createdAssetEntity;
        try {
            createdAssetEntity = assetSEDao.createAsset(asset);
        } catch (AssetDaoException e) {
            throw new AssetServiceException("Could not create asset", e);
        }
        
        auditService.logAssetCreated(createdAssetEntity, username);
        
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
    public AssetListResponsePaginated getAssetList(AssetListQuery query) throws AssetServiceException {
        
        if (query == null) {
            throw new InputArgumentException("Cannot get asset list because query is null.");
        }

        if (query.getAssetSearchCriteria() == null || query.getAssetSearchCriteria().isIsDynamic() == null || query
                .getAssetSearchCriteria().getCriterias() == null) {
            throw new InputArgumentException("Cannot get asset list because criteria are null.");
        }

        if (query.getPagination() == null) {
            throw new InputArgumentException("Cannot get asset list because criteria pagination is null.");
        }

        int page = query.getPagination().getPage();
        int listSize = query.getPagination().getListSize();
        boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

        try {
            List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria()
                    .getCriterias());

            Long numberOfAssets = assetSEDao.getAssetCount(searchFields, isDynamic);

            int numberOfPages = 0;
            if (listSize != 0) {
                numberOfPages = (int) (numberOfAssets / listSize);
                if (numberOfAssets % listSize != 0) {
                    numberOfPages += 1;
                }
            }

            List<AssetSE> assetEntityList = assetSEDao.getAssetListSearchPaginated(page, listSize, searchFields,
                    isDynamic);

            AssetListResponsePaginated listAssetResponse = new AssetListResponsePaginated();
            listAssetResponse.setCurrentPage(page);
            listAssetResponse.setTotalNumberOfPages(numberOfPages);
            listAssetResponse.getAssetList().addAll(assetEntityList);
            return listAssetResponse;
        } catch (AssetDaoException | AssetDaoMappingException e) {
            throw new AssetServiceException("Could not get asset list", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param query
     * @return
     * @throws AssetException
     */
    @Override
    public Long getAssetListCount(AssetListQuery query) throws AssetServiceException {
        if (query == null) {
            throw new InputArgumentException("Cannot get asset list count because query is null.");
        }

        if (query.getAssetSearchCriteria() == null || query.getAssetSearchCriteria().isIsDynamic() == null || query
                .getAssetSearchCriteria().getCriterias() == null) {
            throw new InputArgumentException("Cannot get asset list count because criteria are null.");
        }

        if (query.getPagination() == null) {
            throw new InputArgumentException("Cannot get asset list count because criteria pagination is null.");
        }

        boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

        try {
            List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria()
                    .getCriterias());

            return assetSEDao.getAssetCount(searchFields, isDynamic);
        } catch (AssetDaoException | AssetDaoMappingException e) {
            throw new AssetServiceException("Could not get asset list count", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public AssetSE updateAsset(AssetSE asset, String username, String comment) throws AssetServiceException {
        AssetSE updatedAsset = updateAssetInternal(asset, username);
        auditService.logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    @Override
    public AssetSE archiveAsset(AssetSE asset, String username, String comment) throws AssetServiceException {
        AssetSE archivedAsset = updateAssetInternal(asset, username);
        auditService.logAssetArchived(archivedAsset, comment, username);
        return archivedAsset;
    }

    private AssetSE updateAssetInternal(AssetSE asset, String username) throws AssetServiceException {

        if (asset == null) {
            throw new InputArgumentException("No asset to update");
        }

        if (asset.getId() == null) {
            throw new InputArgumentException("No id on asset to update");
        }

        checkIdentifierNullValues(asset);

        try {
            AssetSE assetDB = getAssetById((asset.getId()));
            if(assetDB != null){
                asset.setUpdatedBy(username);
                return assetSEDao.updateAsset(asset);
            } else {
                throw new AssetServiceException("Asset with that id does not exist");
            }
        } catch (AssetException e) {
            throw new AssetServiceException("Could not update asset, id: " + asset.getId(), e);
        }
    }

    private void checkIdentifierNullValues(AssetSE asset) {
        if (asset.getCfr() == null || asset.getCfr().isEmpty())
            asset.setCfr(null);
        if (asset.getImo() == null || asset.getImo().isEmpty())
            asset.setImo(null);
        if (asset.getMmsi() == null || asset.getMmsi().isEmpty())
            asset.setMmsi(null);
        if (asset.getIrcs() == null || asset.getIrcs().isEmpty())
            asset.setIrcs(null);
        if (asset.getImo() == null || asset.getImo().isEmpty())
            asset.setImo(null);
        if (asset.getGfcm() == null || asset.getGfcm().isEmpty())
            asset.setGfcm(null);
        if (asset.getIccat() == null || asset.getIccat().isEmpty())
            asset.setIccat(null);
        if (asset.getUvi() == null || asset.getUvi().isEmpty())
            asset.setUvi(null);
    }


    @Override
    public AssetSE upsertAsset(AssetSE asset, String username) throws AssetServiceException {

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
    public AssetSE getAssetById(AssetId assetId, AssetDataSourceQueue source) throws AssetServiceException {
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


    public AssetSE getAssetById(AssetId assetId) throws AssetServiceException {

        if (assetId == null) {
            throw new InputArgumentException("AssetId object is null");
        }

        if (assetId.getValue() == null || assetId.getType() == null) {
            throw new InputArgumentException("AssetId value or type is null");
        }

        try {
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
        } catch (AssetDaoException e) {
            throw new AssetServiceException("Could not get asset by id: " + assetId.getType() + "=" + assetId
                    .getValue(), e);
        }
    }

    @Override
    public AssetSE getAssetFromAssetIdAtDate(String idType, String idValue, LocalDateTime date) throws AssetServiceException {

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
			return assetSEDao.getAssetFromAssetIdAtDate(assetId, date);
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
    public AssetSE getAssetById(UUID id) throws AssetServiceException {
        if (id == null) {
            throw new InputArgumentException("Id is null");
        }

        try {
            return assetSEDao.getAssetById(id);
        } catch (AssetDaoException e) {
            throw new AssetServiceException("Could not get asset by id: " + id, e);
        }
    }

    /**
     * @param groups
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public List<AssetSE> getAssetListByAssetGroups(List<AssetGroupDTO> groups) throws AssetServiceException {
        LOG.debug("Getting asset by ID.");
        if (groups == null || groups.isEmpty()) {
            throw new InputArgumentException("No groups in query");
        }

        //return getAssetListByAssetGroup(groups);
        return null;
    }

    @Override
    //public AssetListGroupByFlagStateResponse getAssetListGroupByFlagState(List assetIds) throws AssetException {
    public Object getAssetListGroupByFlagState(List assetIds) throws AssetServiceException {
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
    public void deleteAsset(AssetId assetId) throws AssetServiceException {

        if (assetId == null) {
            return;
        }

        AssetSE assetEntity = null;
        try {
            // get an object based on what type of id it has
            assetEntity = getAssetById(assetId);
            assetSEDao.deleteAsset(assetEntity);
        } catch (AssetDaoException e) {
            throw new AssetServiceException("Could not delet asset with id: " + assetId.getType() + "=" + assetId
                    .getValue(), e);
        }
    }

    @Override
    public List<AssetSE> getRevisionsForAsset(AssetSE asset) throws AssetServiceException {
        try {
            return assetSEDao.getRevisionsForAsset(asset);
        } catch (AssetDaoException e) {
            throw new AssetServiceException("Could not get revisions for asset: " + asset.getId(), e);
        }
    }

    @Override
    public AssetSE getAssetRevisionForRevisionId(AssetSE asset, UUID historyId) throws AssetServiceException {
        try {
            return assetSEDao.getAssetRevisionForHistoryId(asset, historyId);
        } catch (AssetDaoException e) {
            throw new AssetServiceException("Could not get asset for revision id: " + historyId, e);
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