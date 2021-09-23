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

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.ContactInfoDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.NoteDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.*;
import eu.europa.ec.fisheries.uvms.asset.dto.*;
import eu.europa.ec.fisheries.uvms.asset.message.event.UpdatedAssetEvent;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.util.AssetComparator;
import eu.europa.ec.fisheries.uvms.asset.util.AssetUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.rest.security.InternalRestTokenHandler;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.EventCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
public class AssetServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(AssetServiceBean.class);

    @Resource(name = "java:global/movement_endpoint")
    private String movementEndpoint;

    @Inject
    private AuditServiceBean auditService;

    @Inject
    private AssetFilterServiceBean assetFilterService;

    @Inject
    private AssetDao assetDao;

    @Inject
    private NoteDao noteDao;

    @Inject
    private ContactInfoDao contactDao;

    @Inject
    private MobileTerminalServiceBean mobileTerminalService;

    @Inject
    private InternalRestTokenHandler tokenHandler;

    @Inject
    @UpdatedAssetEvent
    private Event<Asset> updatedAssetEvent;

    @PersistenceContext
    private EntityManager em;

    public Asset createAsset(Asset asset, String username) {
        asset.setUpdatedBy(username);
        asset.setUpdateTime(Instant.now());
        asset.setActive(true);
        asset.setEventCode(EventCode.MOD.value());
        asset.getMobileTerminals(); // instantiate list
        Asset createdAssetEntity = assetDao.createAsset(asset);

        auditService.logAssetCreated(createdAssetEntity, username);
        updatedAssetEvent.fire(asset);

        return createdAssetEntity;
    }

    public AssetListResponse getAssetList(SearchBranch queryTree, int page, int listSize, boolean includeInactivated) {
        nullValidation(queryTree, "Cannot get asset list because search values is null.");
        Long numberOfAssets = assetDao.getAssetCount(queryTree, includeInactivated);
        int numberOfPages = 0;
        if (listSize != 0) {
            numberOfPages = (int) (numberOfAssets / listSize);
            if (numberOfAssets % listSize != 0) {
                numberOfPages += 1;
            }
        }
        List<Asset> assetEntityList = assetDao.getAssetListSearchPaginated(page, listSize, queryTree, includeInactivated);
      
        // force to load children. FetchType.EAGER didn't work.
        assetEntityList.forEach(asset -> asset.getMobileTerminals().size());
        AssetListResponse listAssetResponse = new AssetListResponse();
        listAssetResponse.setCurrentPage(page);
        listAssetResponse.setTotalNumberOfPages(numberOfPages);
        listAssetResponse.getAssetList().addAll(assetEntityList);
        return listAssetResponse;
    }

    public Long getAssetListCount(SearchBranch queryTree, boolean includeInactivated) {
        if (queryTree == null || queryTree.getFields().isEmpty()) {
            throw new IllegalArgumentException("Cannot get asset list because query is null.");
        }
        return assetDao.getAssetCount(queryTree, includeInactivated);
    }
    
    public List<Asset> getAssetList(List<String> assetIdList) {
        List<UUID> assetUuidList = new ArrayList<>(assetIdList.size());
        for (String s : assetIdList) {
            assetUuidList.add(UUID.fromString(s));
        }
        return assetDao.getAssetListByAssetGuids(assetUuidList);
    }

    public Asset updateAsset(Asset asset, String username, String comment) {
        Asset updatedAsset = updateAssetInternal(asset, username, comment);
        auditService.logAssetUpdated(updatedAsset, comment, username);
        updatedAssetEvent.fire(updatedAsset);
        return updatedAsset;
    }

    public Asset populateMTListInAsset(Asset asset) {
        if (asset.getMobileTerminalUUIDList() != null && !asset.getMobileTerminalUUIDList().isEmpty()) {
            for (String s : asset.getMobileTerminalUUIDList()) {
                asset.getMobileTerminals().add(mobileTerminalService.getMobileTerminalEntityById(UUID.fromString(s)));
            }
        }
        return asset;
    }

    public Asset archiveAsset(Asset asset, String username, String comment) {
        Set<MobileTerminal> mtList = asset.getMobileTerminals();
        if (mtList != null && !mtList.isEmpty()) {
            mobileTerminalService.inactivateAndUnlink(asset, comment, username);
        }
        asset.setActive(false);
        asset.getMobileTerminals().clear();
        Asset archivedAsset = updateAssetInternal(asset, username, comment);
        auditService.logAssetArchived(archivedAsset, comment, username);
        return archivedAsset;
    }

    public Asset unarchiveAsset(UUID assetId, String username, String comment) {
        Asset asset = assetDao.getAssetById(assetId);
        asset.setActive(true);
        Asset unarchivedAsset = updateAssetInternal(asset, username, comment);
        auditService.logAssetUnarchived(unarchivedAsset, comment, username);
        return unarchivedAsset;
    }

    private Asset updateAssetInternal(Asset asset, String username, String comment) {
        nullValidation(asset, "No asset to update");
        nullValidation(asset.getId(), "No id on asset to update");
        checkIdentifierNullValues(asset);

        asset.setUpdatedBy(username);
        asset.setUpdateTime(Instant.now());
        if (asset.getEventCode() == null) {
            asset.setEventCode(EventCode.MOD.value());
        }
        asset.setComment(comment);
        asset.getMobileTerminals(); // instantiate list
        Asset updatedAsset = assetDao.updateAsset(asset);
        updatedAsset.getMobileTerminals().forEach(mt -> mt.setUpdatetime(Instant.now()));
        return updatedAsset;
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

    public Asset upsertAsset(Asset asset, String username) {
        nullValidation(asset, "No asset to upsert");
        if (asset.getId() == null) {
            return createAsset(asset, username);
        }
        return updateAsset(asset, username, asset.getComment());
    }

    public AssetBO upsertAssetBO(AssetBO assetBo, String username) {
        nullValidation(assetBo, "No asset business object to upsert");
        Asset asset = assetBo.getAsset();
        Map<AssetIdentifier, String> assetIds = createAssetId(asset);
        Asset existingAsset = null;
        if (assetIds.get(assetBo.getDefaultIdentifier()) != null) {
            existingAsset = getAssetById(assetBo.getDefaultIdentifier(), assetIds.get(assetBo.getDefaultIdentifier()));
        }
        if (existingAsset == null) {
            existingAsset = getAssetByCfrIrcsOrMmsi(assetIds);
        }
        if (existingAsset != null) {
            asset.setId(existingAsset.getId());

            asset.setMmsi(asset.getMmsi() == null ? existingAsset.getMmsi() : asset.getMmsi());                     //to save values we already have and dont get from the external source
            asset.setComment(asset.getComment() == null ? existingAsset.getComment() : asset.getComment());
            asset.setParked(asset.getParked() == null ? existingAsset.getParked() : asset.getParked());
        }
        if (!AssetComparator.assetEquals(asset, existingAsset)) {
            asset = upsertAsset(asset, username);
        }
        // Clear and create new contacts and notes for now
        UUID assetId = asset.getId();
        if (assetBo.getContacts() != null) {
            getContactInfoForAsset(assetId).forEach(c -> deleteContactInfo(c.getId()));
            assetBo.getContacts().forEach(c -> createContactInfoForAsset(assetId, c, username));
        }
        if (assetBo.getNotes() != null) {
            assetBo.getNotes().forEach(c -> createNoteForAsset(assetId, c, username));
        }
        addLicenceToAsset(assetId, assetBo.getFishingLicence());
        return assetBo;
    }

    public Asset getAssetById(AssetIdentifier assetId, String value) {
        nullValidation(assetId, "AssetIdentity object is null");
        nullValidation(value, "AssetIdentity value is null");
        return assetDao.getAssetFromAssetId(assetId, value);
    }

    public Asset getAssetFromAssetIdAtDate(AssetIdentifier idType, String idValue, Instant date) {
        nullValidation(idType, "Type is null");
        nullValidation(idValue, "Value is null");
        nullValidation(date, "Date is null");
        if (idType == AssetIdentifier.GUID) {
            try {
                UUID.fromString(idValue); // Just to verify incoming UUID is valid.
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Not a valid UUID: " + idValue);
            }
        }
        Asset asset = assetDao.getAssetFromAssetIdAtDate(idType, idValue, date);
        if (asset != null && asset.getMobileTerminals() != null)
            asset.getMobileTerminals().size(); // force to load children. FetchType.EAGER didn't work.
        return asset;
    }

    public List<Asset> getAssetsAtDate(List<UUID> assetIds, Instant date) {
        List<Asset> assets = assetDao.getAssetsAtDate(assetIds, date);
        Map<UUID, Asset> assetIdMap = assets.stream().collect(Collectors.toMap(Asset::getId, Function.identity()));
        assetIds.stream().forEach(assetId -> assetIdMap.computeIfAbsent(assetId, id -> assetDao.getFirstRevision(id)));
        return new ArrayList<>(assetIdMap.values());
    }

    public Asset getAssetById(UUID id) {
        nullValidation(id, "Id is null");
        return assetDao.getAssetById(id);
    }


    public void deleteAsset(AssetIdentifier assetId, String value) {
        nullValidation(assetId, "AssetId is null");
        Asset assetEntity = getAssetById(assetId, value);
        assetDao.deleteAsset(assetEntity);
    }

    public List<Asset> getRevisionsForAsset(UUID id) {
        return assetDao.getRevisionsForAsset(id);
    }

    public Asset getAssetRevisionForRevisionId(UUID historyId) {
        Asset revision = assetDao.getAssetRevisionForHistoryId(historyId);
        if (revision.getMobileTerminals() != null)
            revision.getMobileTerminals().size(); // force to load children. FetchType.EAGER didn't work.
        return revision;
    }

    public List<Asset> getRevisionsForAssetLimited(UUID id, Integer maxNbr) {
        List<Asset> revisions = assetDao.getRevisionsForAsset(id);
        // force to load children. FetchType.EAGER didn't work.
        revisions.forEach(asset -> {
            if (asset.getMobileTerminals() != null)
                asset.getMobileTerminals().size();
        });
        revisions.sort(Comparator.comparing(Asset::getUpdateTime));
        if (revisions.size() > maxNbr) {
            return revisions.subList(revisions.size() - maxNbr, revisions.size());  //we should get the latest ones right?
        }
        return revisions;
    }

    public Map<UUID, Note> getNotesForAsset(UUID assetId) {
        List<Note> notesByAsset = noteDao.getNotesByAsset(assetId);
        Map<UUID, Note> noteMap = notesByAsset.stream().collect(Collectors.toMap(Note::getId, Function.identity()));
        return noteMap;
    }

    public Note createNoteForAsset(UUID assetId, Note note, String username) {
        Asset asset = assetDao.getAssetById(assetId);
        nullValidation(asset, "Could not find any asset with id: " + assetId);
        note.setAssetId(asset.getId());
        note.setCreatedBy(username);
        note.setCreatedOn(Instant.now());
        return noteDao.createNote(note);
    }

    public Note updateNote(Note note, String username) {
        Note oldNote = noteDao.findNote(note.getId());
        if(oldNote != null && !oldNote.getCreatedBy().equals(username)){
            throw new IllegalArgumentException("Can only change notes created by the same user");
        }
        note.setCreatedBy(username);
        return noteDao.updateNote(note);
    }

    public void deleteNote(UUID id, String username) {
        Note note = noteDao.findNote(id);
        nullValidation(note, "Could not find any note with id: " + id);
        if(!note.getCreatedBy().equals(username)){
            throw new IllegalArgumentException("Can only delete notes created by the same user");
        }

        noteDao.deleteNote(note);
    }

    public List<ContactInfo> getContactInfoForAsset(UUID assetId) {
        Asset asset = assetDao.getAssetById(assetId);
        nullValidation(asset, "Could not find any asset with id: " + assetId);
        return contactDao.getContactInfoByAssetId(asset.getId());
    }

    public ContactInfo createContactInfoForAsset(UUID assetId, ContactInfo contactInfo, String username) {
        Asset asset = assetDao.getAssetById(assetId);
        nullValidation(asset, "Could not find any asset with id: " + assetId);
        contactInfo.setAssetId(asset.getId());
        contactInfo.setUpdatedBy(username);
        if (contactInfo.getId() == null) {
            contactInfo.setCreateTime(Instant.now());
        }
        contactInfo.setAssetUpdateTime(asset.getUpdateTime());
        return contactDao.createContactInfo(contactInfo);
    }

    public ContactInfo updateContactInfo(ContactInfo contactInfo, String username) {
        Asset asset = assetDao.getAssetById(contactInfo.getAssetId());
        nullValidation(asset, "Could not find any asset with id: " + contactInfo.getAssetId());
        ContactInfo old = contactDao.findContactInfo(contactInfo.getId());
        contactInfo.setCreateTime(old.getCreateTime());
        contactInfo.setUpdatedBy(username);
        contactInfo.setAssetUpdateTime(asset.getUpdateTime());
        return contactDao.updateContactInfo(contactInfo);
    }

    public void deleteContactInfo(UUID id) {
        ContactInfo contactInfo = contactDao.findContactInfo(id);
        nullValidation(contactInfo, "Could not find any contact info with id: " + id);
        contactDao.deleteContactInfo(contactInfo);
    }

    public List<ContactInfo> getContactInfoRevisionForAssetHistory(UUID assetId, Instant updatedDate) {
        List<ContactInfo> contactInfoListByAssetId = contactDao.getContactInfoByAssetId(assetId);
        return contactDao.getContactInfoRevisionForAssetHistory(contactInfoListByAssetId, updatedDate);
    }

    public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) {

        // Get Mobile Terminal if it exists
        MobileTerminal terminal = mobileTerminalService.getMobileTerminalByAssetMTEnrichmentRequest(request);

        Asset asset = terminal == null ? null : terminal.getAsset();

        if (asset == null) {
            asset = getAssetByCfrIrcsOrMmsi(createAssetId(request));
        }

        MobileTerminalTypeEnum transponderType = getTransponderType(request);
        if (shouldANewShipBeCreated(request,asset, transponderType)) {
            asset = AssetUtil.createNewAssetFromRequest(request, assetDao.getNextUnknownShipNumber());
            createAsset(asset, asset.getUpdatedBy());
        }

        AssetMTEnrichmentResponse assetMTEnrichmentResponse = new AssetMTEnrichmentResponse();
        enrichAssetAndMobileTerminal(request, assetMTEnrichmentResponse, terminal, asset);
        enrichAssetFilter(assetMTEnrichmentResponse, asset);

        return assetMTEnrichmentResponse;
    }

    private static final int MMSI_MAX_LENGHT = 9;

    private boolean shouldANewShipBeCreated(AssetMTEnrichmentRequest request, Asset asset, MobileTerminalTypeEnum transponderType){
        return asset == null &&
                (request.getMmsiValue() == null || request.getMmsiValue().length() <= MMSI_MAX_LENGHT) &&
                transponderType == null;
    }

    private void enrichAssetAndMobileTerminal(AssetMTEnrichmentRequest request,
                                              AssetMTEnrichmentResponse assetMTEnrichmentResponse, MobileTerminal terminal, Asset asset) {
        if (asset != null) {
            enrichmentHelper(assetMTEnrichmentResponse, asset);
        }
        if (terminal != null) {
            enrichmentHelper(request, assetMTEnrichmentResponse, terminal);
        }
    }

    private void enrichAssetFilter(AssetMTEnrichmentResponse assetMTEnrichmentResponse, Asset asset) {
        if (asset != null) {
            List<String> assetGroupList = new ArrayList<>();
            List<AssetFilter> assetFilters = assetFilterService.getAssetFilterListByAssetId(asset.getId());
            for (AssetFilter filter : assetFilters) {
                UUID assetFilterId = filter.getId();
                assetGroupList.add(assetFilterId.toString());
            }
            assetMTEnrichmentResponse.setAssetFilterList(assetGroupList);
        }
    }

    private MobileTerminalTypeEnum getTransponderType(AssetMTEnrichmentRequest request) {
        try {
            return MobileTerminalTypeEnum.valueOf(request.getTranspondertypeValue());
        } catch (Exception e) {
            return null;
        }
    }

    private AssetMTEnrichmentResponse enrichmentHelper(AssetMTEnrichmentResponse resp, Asset asset) {
        resp.setAssetUUID(asset.getId() == null ? null : asset.getId().toString());
        resp.setAssetName(asset.getName());
        resp.setAssetHistoryId(asset.getHistoryId() == null ? null : asset.getHistoryId().toString());
        resp.setFlagstate(asset.getFlagStateCode());
        resp.setExternalMarking(asset.getExternalMarking());
        resp.setGearType(asset.getGearFishingType());
        resp.setCfr(asset.getCfr());
        resp.setIrcs(asset.getIrcs());
        resp.setMmsi(asset.getMmsi());
        resp.setImo(asset.getImo());
        resp.setVesselType(asset.getVesselType());
        resp.setParked(asset.getParked() != null ? asset.getParked() : false);
        return resp;
    }

    private AssetMTEnrichmentResponse enrichmentHelper(AssetMTEnrichmentRequest req, AssetMTEnrichmentResponse resp, MobileTerminal mobTerm) {

        // here we put into response data about mobiletreminal / channels etc etc
        String channelGuid = getChannelGuid(mobTerm, req);
        resp.setChannelGuid(channelGuid);
        resp.setSerialNumber(mobTerm.getSerialNo());
        resp.setMobileTerminalConnectId(mobTerm.getAsset() == null ? null : mobTerm.getAsset().getId().toString());
        resp.setMobileTerminalType(mobTerm.getMobileTerminalType().name());
        if (mobTerm.getId() != null) {
            resp.setMobileTerminalGuid(mobTerm.getId().toString());
        }
        resp.setMobileTerminalIsInactive(!mobTerm.getActive());

        if (mobTerm.getChannels() != null) {
            Set<Channel> channels = mobTerm.getChannels();
            for (Channel channel : channels) {
                if (!channel.getId().toString().equals(channelGuid)) {
                    continue;
                }

                resp.setDNID(String.valueOf(channel.getDnid()));
                resp.setMemberNumber(String.valueOf(channel.getMemberNumber()));
            }
        }
        return resp;
    }

    private String getChannelGuid(MobileTerminal mobileTerminal, AssetMTEnrichmentRequest request) {
        String dnid = "";
        String memberNumber = "";
        String channelGuid = "";

        dnid = request.getDnidValue();
        memberNumber = request.getMemberNumberValue();

        // Get the channel guid
        boolean correctDnid = false;
        boolean correctMemberNumber = false;
        Set<Channel> channels = mobileTerminal.getChannels();
        for (Channel channel : channels) {
            correctDnid = channel.getDnid().equals(dnid);
            correctMemberNumber = channel.getMemberNumber().equals(memberNumber);

            if (correctDnid && correctMemberNumber) {
                channelGuid = channel.getId().toString();
            }
        }
        return channelGuid;
    }

    private Map<AssetIdentifier, String> createAssetId(Asset asset) {
        Map<AssetIdentifier, String> assetId = new HashMap<>();

        if (asset.getCfr() != null && asset.getCfr().length() > 0) {
            assetId.put(AssetIdentifier.CFR, asset.getCfr());
        }
        if (asset.getId() != null) {
            assetId.put(AssetIdentifier.GUID, asset.getId().toString());
        }
        if (asset.getImo() != null && asset.getImo().length() > 0) {
            assetId.put(AssetIdentifier.IMO, asset.getImo());
        }
        if (asset.getIrcs() != null && asset.getIrcs().length() > 0) {
            assetId.put(AssetIdentifier.IRCS, asset.getIrcs());
        }
        if (asset.getMmsi() != null && asset.getMmsi().length() > 0) {
            assetId.put(AssetIdentifier.MMSI, asset.getMmsi());
        }
        if (asset.getGfcm() != null && asset.getGfcm().length() > 0) {
            assetId.put(AssetIdentifier.GFCM, asset.getGfcm());
        }
        if (asset.getUvi() != null && asset.getUvi().length() > 0) {
            assetId.put(AssetIdentifier.UVI, asset.getUvi());
        }
        if (asset.getIccat() != null && asset.getIccat().length() > 0) {
            assetId.put(AssetIdentifier.ICCAT, asset.getIccat());
        }
        if (asset.getNationalId() != null ) {
            assetId.put(AssetIdentifier.NATIONAL, asset.getNationalId().toString());
        }
        return assetId;
    }

    private Map<AssetIdentifier, String> createAssetId(AssetMTEnrichmentRequest request) {
        Map<AssetIdentifier, String> assetId = new HashMap<>();

        if (request.getCfrValue() != null && request.getCfrValue().length() > 0) {
            assetId.put(AssetIdentifier.CFR, request.getCfrValue());
        }
        if (request.getIdValue() != null) {
            assetId.put(AssetIdentifier.GUID, request.getIdValue().toString());
        }
        if (request.getImoValue() != null && request.getImoValue().length() > 0) {
            assetId.put(AssetIdentifier.IMO, request.getImoValue());
        }
        if (request.getIrcsValue() != null && request.getIrcsValue().length() > 0) {
            assetId.put(AssetIdentifier.IRCS, request.getIrcsValue());
        }
        if (request.getMmsiValue() != null && request.getMmsiValue().length() > 0) {
            assetId.put(AssetIdentifier.MMSI, request.getMmsiValue());
        }
        if (request.getGfcmValue() != null && request.getGfcmValue().length() > 0) {
            assetId.put(AssetIdentifier.GFCM, request.getGfcmValue());
        }
        if (request.getUviValue() != null && request.getUviValue().length() > 0) {
            assetId.put(AssetIdentifier.UVI, request.getUviValue());
        }
        if (request.getIccatValue() != null && request.getIccatValue().length() > 0) {
            assetId.put(AssetIdentifier.ICCAT, request.getIccatValue());
        }
        return assetId;
    }

    private Asset getAssetByCfrIrcsOrMmsi(Map<AssetIdentifier, String> assetId) {
        Asset asset = null;

        // If no asset information exists, don't look for one
        if (assetId == null || assetId.size() < 1) {
            LOG.warn("No asset information exists!");
            return null;
        }

        // Get possible search parameters
        String cfr = assetId.getOrDefault(AssetIdentifier.CFR, null);
        String ircs = assetId.getOrDefault(AssetIdentifier.IRCS, null);
        String mmsi = assetId.getOrDefault(AssetIdentifier.MMSI, null);


        if (cfr != null) {
            asset = getAssetById(AssetIdentifier.CFR, cfr);
        }

        if (asset == null && ircs != null) {
            asset = getAssetById(AssetIdentifier.IRCS, ircs);
        }

        if (asset == null && mmsi != null) {
            asset = getAssetById(AssetIdentifier.MMSI, mmsi);
        }

        return asset;
    }

    // if more than 1 hit put data from ais into fartyg2record
    // remove the duplicate
    private Asset normalizeAssetOnMMSI_IRCS(String mmsi, String ircs, String updatedBy) {

        List<Asset> assets = assetDao.getAssetByMmsiOrIrcs(mmsi, ircs);

        int assetsSize = assets.size();
        if (assetsSize == 0) {
            return null;
        } else if (assetsSize == 1) {
            return assets.get(0);
        } else {
            Asset fartyg2Asset = null;
            Asset nonFartyg2Asset = null;
            // find the fartyg2 record

            for (Asset asset : assets) {
                if ((asset.getSource() != null) && (asset.getSource().equals(CarrierSource.NATIONAL.toString()))) {
                    fartyg2Asset = asset;
                } else {
                    nonFartyg2Asset = asset;
                }
            }
            if ((fartyg2Asset != null) && (nonFartyg2Asset != null)) {
                String nonFartyg2AssetMmsi = mmsi;
                nonFartyg2Asset.setMmsi(null);
                nonFartyg2Asset.setActive(false);
                String comment = "Found to be a duplicate of another asset with IRCS: " + ircs + " " + (nonFartyg2Asset.getComment() != null ? nonFartyg2Asset.getComment() : "");
                nonFartyg2Asset.setComment((comment.length() > 255 ? comment.substring(0, 255) : comment));
                // flush is necessary to avoid dumps on MMSI
                em.flush();
                fartyg2Asset.setMmsi(nonFartyg2AssetMmsi);
                fartyg2Asset.setUpdateTime(Instant.now());
                fartyg2Asset.setUpdatedBy(updatedBy);
                em.merge(fartyg2Asset);
                assetDao.createAssetRemapMapping(createAssetRemapMapping(nonFartyg2Asset.getId(), fartyg2Asset.getId()));
                remapAssetsInMovement(nonFartyg2Asset.getId().toString(), fartyg2Asset.getId().toString());
                updatedAssetEvent.fire(fartyg2Asset);
                return fartyg2Asset;
            }
        }
        return null;
    }

    private AssetRemapMapping createAssetRemapMapping(UUID oldAssetId, UUID newAssetId) {
        AssetRemapMapping mapping = new AssetRemapMapping();
        mapping.setOldAssetId(oldAssetId);
        mapping.setNewAssetId(newAssetId);
        mapping.setCreatedDate(Instant.now());

        return mapping;
    }

    public int remapAssetsInMovement(String oldAssetId, String newAssetId) {
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                //.readTimeout(30, TimeUnit.MINUTES) //what should this number be
                .newClient();

        Response remapResponse = client.target(movementEndpoint)
                .path("internal/remapMovementConnectInMovement")
                .queryParam("MovementConnectFrom", oldAssetId)
                .queryParam("MovementConnectTo", newAssetId)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .put(Entity.json(""), Response.class);

        if (remapResponse.getStatus() != 200) { //do we want this?
            throw new RuntimeException("Response from remapping from old asset to new asset was not 200. Return status: " + remapResponse.getStatus() + " Return error: " + remapResponse.readEntity(String.class));
        }

        return Integer.parseInt(remapResponse.readEntity(String.class));
    }

    public void removeMovementConnectInMovement(String assetId) {
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .newClient();

        Response remapResponse = client.target(movementEndpoint)
                .path("internal/removeMovementConnect")
                .queryParam("MovementConnectId", assetId)
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        if (remapResponse.getStatus() != 200) { //do we want this?
            throw new RuntimeException("Response from remapping from old asset to new asset was not 200. Return status: " + remapResponse.getStatus() + " Return error: " + remapResponse.getEntity());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void assetInformation(Asset assetFromAIS, String user) {

        if (assetFromAIS == null) {
            return;
        }

        Asset assetFromDB = normalizeAssetOnMMSI_IRCS(assetFromAIS.getMmsi(), assetFromAIS.getIrcs(), user);

        boolean shouldUpdate = false;

        if (assetFromDB == null || CarrierSource.NATIONAL.toString().equals(assetFromDB.getSource())) {    //if we have data from fartyg 2 then we should not update with data from ais
            return;
        }

        if ((assetFromDB.getMmsi() == null || !assetFromDB.getMmsi().equals(assetFromAIS.getMmsi())) && (assetFromAIS.getMmsi() != null)) {
            shouldUpdate = true;
            assetFromDB.setMmsi(assetFromAIS.getMmsi());
        }
        if (assetFromAIS.getIrcs() != null && (assetFromDB.getIrcs() == null || !assetFromDB.getIrcs().equals(assetFromAIS.getIrcs().replace(" ", "")))) {
            shouldUpdate = true;
            assetFromDB.setIrcs(assetFromAIS.getIrcs().replace(" ", ""));
        }
        if ((assetFromDB.getVesselType() == null || !assetFromDB.getVesselType().equals(assetFromAIS.getVesselType())) && (assetFromAIS.getVesselType() != null)) {
            shouldUpdate = true;
            assetFromDB.setVesselType(assetFromAIS.getVesselType());
        }
        if ((assetFromDB.getImo() == null || !assetFromDB.getImo().equals(assetFromAIS.getImo())) && (assetFromAIS.getImo() != null)) {
            shouldUpdate = true;
            assetFromDB.setImo(assetFromAIS.getImo());
        }

        if ((assetFromDB.getName() == null || assetFromDB.getName().startsWith("Unknown") || !assetFromDB.getName().equals(assetFromAIS.getName())) && (assetFromAIS.getName() != null)) {
            if (!assetFromAIS.getName().isEmpty()) {
                shouldUpdate = true;
                assetFromDB.setName(assetFromAIS.getName());
            }
        }
        if ((assetFromDB.getFlagStateCode() == null || assetFromDB.getFlagStateCode().startsWith("UNK") || !assetFromDB.getFlagStateCode().equals(assetFromAIS.getFlagStateCode())) && (assetFromAIS.getFlagStateCode() != null)) {
            shouldUpdate = true;
            assetFromDB.setFlagStateCode(assetFromAIS.getFlagStateCode());
        }
        if (shouldUpdate) {
            assetFromDB.setUpdatedBy(user);
            assetFromDB.setUpdateTime(Instant.now());
            assetFromDB.setSource(CarrierSource.INTERNAL.toString());
            em.merge(assetFromDB);
            updatedAssetEvent.fire(assetFromDB);
        }
    }

    public List<Asset> getInitialDataForRealtime(List<String> assetIdList) {
        List<UUID> assetUuidList = new ArrayList<>(assetIdList.size());
        for (String s : assetIdList) {
            assetUuidList.add(UUID.fromString(s));
        }
        return assetDao.getAssetListByAssetGuids(assetUuidList);
    }

    public Note getNoteById(UUID id) {
        nullValidation(id, "Cant search for noteId: " + id);
        Note note = noteDao.findNote(id);
        return note;
    }

    public void addLicenceToAsset(UUID assetId, FishingLicence fishingLicence) {
        FishingLicence existingLicence = getFishingLicenceByAssetId(assetId);
        if (fishingLicence == null && existingLicence != null) {
            deleteFishingLicense(existingLicence);
            return;
        }
        if (fishingLicence == null) {
            return;
        }
        if (existingLicence == null) {
            existingLicence = new FishingLicence();
        }
        existingLicence.setAssetId(assetId);
        existingLicence.setLicenceNumber(fishingLicence.getLicenceNumber());
        existingLicence.setCivicNumber(fishingLicence.getCivicNumber());
        existingLicence.setName(fishingLicence.getName());
        existingLicence.setFromDate(fishingLicence.getFromDate());
        existingLicence.setToDate(fishingLicence.getToDate());
        existingLicence.setDecisionDate(fishingLicence.getDecisionDate());
        existingLicence.setConstraints(fishingLicence.getConstraints());
        em.persist(existingLicence);
    }

    public FishingLicence createFishingLicence(FishingLicence licence) {
        em.persist(licence);
        return licence;
    }

    public void deleteFishingLicense(FishingLicence licence) {
        em.remove(licence);
    }

    public FishingLicence getFishingLicenceByAssetId(UUID assetId) {
        try {
            return em.createNamedQuery(FishingLicence.FIND_BY_ASSET, FishingLicence.class)
                    .setParameter("assetId", assetId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private void nullValidation(Object obj, String message) {
        if (obj == null) throw new IllegalArgumentException(message);
    }
}
