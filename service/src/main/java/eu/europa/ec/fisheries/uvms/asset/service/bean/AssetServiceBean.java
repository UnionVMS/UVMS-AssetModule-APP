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
import java.time.ZoneOffset;
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
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.asset.types.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.ContactInfoDao;
import eu.europa.ec.fisheries.uvms.dao.NoteDao;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.entity.Note;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

@Stateless
public class AssetServiceBean implements AssetService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetServiceBean.class);
    
    @Inject
    private AuditServiceBean auditService;

    @Inject
    private AssetDao assetDao;
    
    @Inject
    private NoteDao noteDao;
    
    @Inject
    private ContactInfoDao contactDao;

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws AssetException
     */
    @Override
    public Asset createAsset(Asset asset, String username) {

        asset.setUpdatedBy(username);
        Asset createdAssetEntity = assetDao.createAsset(asset);

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
    public AssetListResponsePaginated getAssetList(AssetListQuery query) {

        if (query == null) {
            throw new IllegalArgumentException("Cannot get asset list because query is null.");
        }

        if (query.getAssetSearchCriteria() == null || query.getAssetSearchCriteria().isIsDynamic() == null || query
                .getAssetSearchCriteria().getCriterias() == null) {
            throw new IllegalArgumentException("Cannot get asset list because criteria are null.");
        }

        if (query.getPagination() == null) {
            throw new IllegalArgumentException("Cannot get asset list because criteria pagination is null.");
        }

        int page = query.getPagination().getPage();
        int listSize = query.getPagination().getListSize();
        boolean isDynamic = query.getAssetSearchCriteria().isIsDynamic();

        List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria()
                .getCriterias());

        Long numberOfAssets = assetDao.getAssetCount(searchFields, isDynamic);

        int numberOfPages = 0;
        if (listSize != 0) {
            numberOfPages = (int) (numberOfAssets / listSize);
            if (numberOfAssets % listSize != 0) {
                numberOfPages += 1;
            }
        }

        List<Asset> assetEntityList = assetDao.getAssetListSearchPaginated(page, listSize, searchFields, isDynamic);

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

        List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query.getAssetSearchCriteria()
                .getCriterias());

        return assetDao.getAssetCount(searchFields, isDynamic);
    }

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Asset updateAsset(Asset asset, String username, String comment) throws AssetServiceException {
        Asset updatedAsset = updateAssetInternal(asset, username);
        auditService.logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    @Override
    public Asset archiveAsset(Asset asset, String username, String comment) throws AssetServiceException {
        Asset archivedAsset = updateAssetInternal(asset, username);
        auditService.logAssetArchived(archivedAsset, comment, username);
        return archivedAsset;
    }

    private Asset updateAssetInternal(Asset asset, String username) throws AssetServiceException {

        if (asset == null) {
            throw new InputArgumentException("No asset to update");
        }

        if (asset.getId() == null) {
            throw new InputArgumentException("No id on asset to update");
        }

        checkIdentifierNullValues(asset);

        asset.setUpdatedBy(username);
        return assetDao.updateAsset(asset);
    }
    
    private void checkIdentifierNullValues(Asset asset) {
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
    public Asset upsertAsset(Asset asset, String username) throws AssetServiceException {

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
    public Asset getAssetById(AssetId assetId, AssetDataSourceQueue source) throws AssetServiceException {
        Asset asset = null;

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

    public Asset getAssetById(AssetId assetId) throws AssetServiceException {

        if (assetId == null) {
            throw new InputArgumentException("AssetId object is null");
        }

        if (assetId.getValue() == null || assetId.getType() == null) {
            throw new InputArgumentException("AssetId value or type is null");
        }

        Asset asset = null;
        switch (assetId.getType()) {
            case CFR:
                asset = assetDao.getAssetByCfr(assetId.getValue());
                break;
            case IRCS:
                asset = assetDao.getAssetByIrcs(assetId.getValue());
                break;
            case INTERNAL_ID:
                asset = assetDao.getAssetById(assetId.getGuid());
                break;
            case IMO:
                checkNumberAssetId(assetId.getValue());
                asset = assetDao.getAssetByImo(assetId.getValue());
                break;
            case MMSI:
                checkNumberAssetId(assetId.getValue());
                asset = assetDao.getAssetByMmsi(assetId.getValue());
                break;
            case ICCAT:
                asset = assetDao.getAssetByIccat(assetId.getValue());
                break;
            case UVI:
                asset = assetDao.getAssetByUvi(assetId.getValue());
                break;
            case GFCM:
                asset = assetDao.getAssetByGfcm(assetId.getValue());
                break;
            default:
                throw new AssetServiceException("Non valid asset id type");
        }
        return asset;
    }

    @Override
    public Asset getAssetFromAssetIdAtDate(String idType, String idValue, LocalDateTime date)
            throws AssetServiceException {

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
        if (assetType == AssetIdTypeEnum.GUID || assetType == AssetIdTypeEnum.INTERNAL_ID) {
            try {
                UUID.fromString(idValue);
            } catch (IllegalArgumentException e) {
                throw new InputArgumentException("Not a valid UUID");
            }
        }

        AssetId assetId = new AssetId();
        assetId.setType(assetType);
        assetId.setValue(idValue);
        if(assetType == AssetIdTypeEnum.GUID || assetType == AssetIdTypeEnum.INTERNAL_ID){
            assetId.setGuid(UUID.fromString(idValue));
        }
        Asset asset = assetDao.getAssetFromAssetIdAtDate(assetId, date);
        return asset;
    }


    /**
     * @param id
     * @return
     * @throws AssetException
     */
    @Override
    public Asset getAssetById(UUID id) throws AssetServiceException {
        if (id == null) {
            throw new InputArgumentException("Id is null");
        }

        return assetDao.getAssetById(id);
    }

    /**
     * @param groups
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups) throws AssetServiceException {
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

    public String getNoteActivityCodes() {
        //return getNoteActivityCodes_FROM_DOMAINMODEL();
        return null;
    }

    @Override
    public void deleteAsset(AssetId assetId) throws AssetServiceException {

        if (assetId == null) {
            throw new IllegalArgumentException("AssetId is null");
        }

        // get an object based on what type of id it has
        Asset assetEntity = getAssetById(assetId);
        assetDao.deleteAsset(assetEntity);
    }

    @Override
    public List<Asset> getRevisionsForAsset(Asset asset) throws AssetServiceException {
        return assetDao.getRevisionsForAsset(asset);
    }

    @Override
    public Asset getAssetRevisionForRevisionId(UUID historyId) throws AssetServiceException {
        return assetDao.getAssetRevisionForHistoryId(historyId);
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

    public List<AssetGroup> getAssetGroupsByGroupList(
            List<AssetGroup> groups)
            throws AssetModelException, InputArgumentException {
        if (groups == null) {
            throw new InputArgumentException("Cannot get asset group list because the input is null.");
        }

        List<UUID> guidList = new ArrayList<>();
        for (AssetGroup group : groups) {
            guidList.add(group.getId());
        }

        if (guidList.isEmpty()) {
            throw new InputArgumentException("Cannot get asset group list because the input missing guid.");
        }

        try {
            List<AssetGroup> vesselGroupList = new ArrayList<>();
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

    @Override
    public List<Note> getNotesForAsset(UUID assetId) {
        Asset asset = assetDao.getAssetById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Could not find any asset with id: " + assetId);
        }
        return noteDao.getNotesByAsset(asset);
    }
    
    @Override
    public Note createNoteForAsset(UUID assetId, Note note, String username) {
        Asset asset = assetDao.getAssetById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Could not find any asset with id: " + assetId);
        }
        note.setAssetId(asset.getId());
        note.setUpdatedBy(username);
        note.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return noteDao.createNote(note);
    }

    @Override
    public Note updateNote(Note note, String username) {
        note.setUpdatedBy(username);
        note.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return noteDao.updateNote(note);
    }
    
    @Override
    public void deleteNote(UUID id) {
        Note note = noteDao.findNote(id);
        if (note == null) {
            throw new IllegalArgumentException("Could not find any note with id: " + id);
        }
        noteDao.deleteNote(note);
    }

    @Override
    public List<ContactInfo> getContactInfoForAsset(UUID assetId) {
        Asset asset = assetDao.getAssetById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Could not find any asset with id: " + assetId);
        }
        return contactDao.getContactInfoByAsset(asset);
    }

    @Override
    public ContactInfo createContactInfoForAsset(UUID assetId, ContactInfo contactInfo, String username) {
        Asset asset = assetDao.getAssetById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Could not find any asset with id: " + assetId);
        }
        contactInfo.setAssetId(asset.getId());
        contactInfo.setUpdatedBy(username);
        contactInfo.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return contactDao.createContactInfo(contactInfo);
    }

    @Override
    public ContactInfo updateContactInfo(ContactInfo contactInfo, String username) {
        contactInfo.setUpdatedBy(username);
        contactInfo.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
        return contactDao.updateContactInfo(contactInfo);
    }

    @Override
    public void deleteContactInfo(UUID id) {
        ContactInfo contactInfo = contactDao.findContactInfo(id);
        if (contactInfo == null) {
            throw new IllegalArgumentException("Could not find any contact info with id: " + id);
        }
        contactDao.deleteContactInfo(contactInfo);
    }
}