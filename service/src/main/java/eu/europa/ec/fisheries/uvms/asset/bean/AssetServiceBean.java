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
package eu.europa.ec.fisheries.uvms.asset.bean;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.ContactInfoDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.NoteDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetBO;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;

@Stateless
public class AssetServiceBean implements AssetService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetServiceBean.class);
    
    @Inject
    private AuditServiceBean auditService;
    
    @Inject
    private AssetGroupService assetGroupService;

    @Inject
    private AssetDao assetDao;
    
    @Inject
    private NoteDao noteDao;
    
    @Inject
    private ContactInfoDao contactDao;

    /**
     *
     * @param asset
     * @param username
     * @return
     */
    @Override
    public Asset createAsset(Asset asset, String username) {

        asset.setUpdatedBy(username);
        asset.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        asset.setActive(true);
        Asset createdAssetEntity = assetDao.createAsset(asset);

        auditService.logAssetCreated(createdAssetEntity, username);
        
        return createdAssetEntity;
    }

    /**
     *
     * @param searchFields
     * @param page
     * @param listSize
     * @param dynamic
     * @return
     */
    @Override
    public AssetListResponse getAssetList(List<SearchKeyValue> searchFields, int page, int listSize, boolean dynamic) {
        if (searchFields == null) {
            throw new IllegalArgumentException("Cannot get asset list because search values is null.");
        }
        
        Long numberOfAssets = assetDao.getAssetCount(searchFields, dynamic);

        int numberOfPages = 0;
        if (listSize != 0) {
            numberOfPages = (int) (numberOfAssets / listSize);
            if (numberOfAssets % listSize != 0) {
                numberOfPages += 1;
            }
        }

        List<Asset> assetEntityList = assetDao.getAssetListSearchPaginated(page, listSize, searchFields, dynamic);

        AssetListResponse listAssetResponse = new AssetListResponse();
        listAssetResponse.setCurrentPage(page);
        listAssetResponse.setTotalNumberOfPages(numberOfPages);
        listAssetResponse.getAssetList().addAll(assetEntityList);
        return listAssetResponse;
    }

    /**
     *
     * @param searchFields
     * @param dynamic
     * @return
     */
    @Override
    public Long getAssetListCount(List<SearchKeyValue> searchFields, boolean dynamic)  {
        if (searchFields == null || searchFields.isEmpty()) {
            throw new IllegalArgumentException("Cannot get asset list because query is null.");
        }

        return assetDao.getAssetCount(searchFields, dynamic);
    }

    /**
     *
     * @param asset
     * @param username
     * @param comment
     * @return
     */
    @Override
    public Asset updateAsset(Asset asset, String username, String comment)  {
        Asset updatedAsset = updateAssetInternal(asset, username);
        auditService.logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    /**
     *
     * @param asset   an asset
     * @param username
     * @param comment a comment to the archiving
     * @return
     */
    @Override
    public Asset archiveAsset(Asset asset, String username, String comment) {
        asset.setActive(false);
        Asset archivedAsset = updateAssetInternal(asset, username);
        auditService.logAssetArchived(archivedAsset, comment, username);
        return archivedAsset;
    }

    private Asset updateAssetInternal(Asset asset, String username)  {

        if (asset == null) {
            throw new IllegalArgumentException("No asset to update");
        }

        if (asset.getId() == null) {
            throw new IllegalArgumentException("No id on asset to update");
        }

        checkIdentifierNullValues(asset);

        asset.setUpdatedBy(username);
        asset.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
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

    /**
     *
     * @param asset
     * @param username
     * @return
     */
    @Override
    public Asset upsertAsset(Asset asset, String username) {
        if (asset == null) {
            throw new IllegalArgumentException("No asset to upsert");
        }

        if (asset.getId() == null) {
            return createAsset(asset, username);
        }

        return updateAsset(asset, username, "");
    }
    
    @Override
    public Asset upsertAssetBO(AssetBO assetBo, String username) {
        if (assetBo == null) {
            throw new IllegalArgumentException("No asset business object to upsert");
        }
        
        Asset asset = assetBo.getAsset();
        Asset existingAsset = getAssetById(AssetIdentifier.CFR, asset.getCfr());
        if (existingAsset != null) {
            asset.setId(existingAsset.getId());
        }
        Asset upsertedAsset = upsertAsset(asset, username);
        
        // Clear and create new contacts and notes for now
        if (assetBo.getContacts() != null) {
            getContactInfoForAsset(upsertedAsset.getId()).stream().forEach(c -> deleteContactInfo(c.getId()));
            assetBo.getContacts().stream().forEach(c -> createContactInfoForAsset(upsertedAsset.getId(), c, username));
        }
       
        if (assetBo.getNotes() != null) {
            getNotesForAsset(upsertedAsset.getId()).stream().forEach(n -> deleteNote(n.getId()));
            assetBo.getNotes().stream().forEach(c -> createNoteForAsset(upsertedAsset.getId(), c, username));
        }
        
        return upsertedAsset;
    }
    
    /**
     *
     * @param assetId
     * @param value
     * @return
     */
    @Override
    public Asset getAssetById(AssetIdentifier assetId, String value)  {

        if (assetId == null) {
            throw new IllegalArgumentException("AssetIdentity object is null");
        }

        if (value == null) {
            throw new IllegalArgumentException("AssetIdentity value is null");
        }

        return assetDao.getAssetFromAssetId(assetId, value);
    }

    /**
     *
     * @param idType
     * @param idValue
     * @param date
     * @return
     */
    @Override
    public Asset getAssetFromAssetIdAtDate(AssetIdentifier idType, String idValue, OffsetDateTime date)  {

        if (idType == null) {
            throw new IllegalArgumentException("Type is null");
        }
        if (idValue == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date is null");
        }
        if (idType == AssetIdentifier.GUID) {
            try {
                UUID.fromString(idValue);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Not a valid UUID");
            }
        }

        return assetDao.getAssetFromAssetIdAtDate(idType, idValue, date);
    }


    /**
     *
     * @param id
     * @return
     */
    @Override
    public Asset getAssetById(UUID id)  {
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }

        return assetDao.getAssetById(id);
    }

    /**
     *
     * @param groups
     * @return
     */
    @Override
    public List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups)  {
        LOG.debug("Getting asset by ID.");
        if (groups == null || groups.isEmpty()) {
            throw new IllegalArgumentException("No groups in query");
        }
        List<AssetGroupField> groupFields = groups.stream()
                                .map(g -> assetGroupService.retrieveFieldsForGroup(g.getId()))
                                .flatMap(x -> x.stream())
                                .collect(Collectors.toList());

        Set<Asset> assets = new HashSet<>();
        for (AssetGroupField groupField : groupFields) {
            if ("GUID".equals(groupField.getKey())) {
                assets.add(getAssetById(UUID.fromString(groupField.getValue())));
            }
        }
        return new ArrayList<>(assets);
    }

    @Override
    public void deleteAsset(AssetIdentifier assetId, String value)  {

        if (assetId == null) {
            throw new IllegalArgumentException("AssetId is null");
        }

        Asset assetEntity = getAssetById(assetId, value);
        assetDao.deleteAsset(assetEntity);
    }

    @Override
    public List<Asset> getRevisionsForAsset(UUID id)  {
        return assetDao.getRevisionsForAsset(id);
    }

    @Override
    public Asset getAssetRevisionForRevisionId(UUID historyId)  {
        return assetDao.getAssetRevisionForHistoryId(historyId);
    }

    @Override
	public List<Asset> getRevisionsForAssetLimited(UUID id, Integer maxNbr) {
	    List<Asset> revisions = assetDao.getRevisionsForAsset(id);
	    revisions.sort((a1, a2) -> a1.getUpdateTime().compareTo(a2.getUpdateTime()));
	    if (revisions.size() > maxNbr) {
	        return revisions.subList(0, maxNbr);
	    }
	    return revisions;
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
        note.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return noteDao.createNote(note);
    }

    @Override
    public Note updateNote(Note note, String username) {
        note.setUpdatedBy(username);
        note.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
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
        contactInfo.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return contactDao.createContactInfo(contactInfo);
    }

    @Override
    public ContactInfo updateContactInfo(ContactInfo contactInfo, String username) {
        contactInfo.setUpdatedBy(username);
        contactInfo.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
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