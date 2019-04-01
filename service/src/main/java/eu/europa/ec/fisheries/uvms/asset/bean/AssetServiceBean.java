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

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.ContactInfoDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.NoteDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.*;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetBO;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentResponse;
import eu.europa.ec.fisheries.uvms.asset.util.AssetComparator;
import eu.europa.ec.fisheries.uvms.asset.util.AssetUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.wsdl.asset.types.EventCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class AssetServiceBean implements AssetService {

    private static final String DNID = "DNID";
    private static final String MEMBER_NUMBER = "MEMBER_NUMBER";
    private static final String GUID = "GUID";
    private static final String IMO = "IMO";
    private static final String IRCS = "IRCS";
    private static final String MMSI = "MMSI";
    private static final String CFR = "CFR";
    private static final String GFCM = "GFCM";
    private static final String UVI = "UVI";
    private static final String ICCAT = "ICCAT";

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

    @Inject
    private MobileTerminalServiceBean mobileTerminalService;

    @PersistenceContext
    private EntityManager em;


    @Override
    public Asset createAsset(Asset asset, String username) {

        asset.setUpdatedBy(username);
        asset.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        asset.setActive(true);
        asset.setEventCode(EventCode.MOD.value());
        Asset createdAssetEntity = assetDao.createAsset(asset);

        auditService.logAssetCreated(createdAssetEntity, username);

        return createdAssetEntity;
    }

    @Override
    public AssetListResponse getAssetList(List<SearchKeyValue> searchFields, int page, int listSize, boolean dynamic, boolean includeInactivated) {
        if (searchFields == null) {
            throw new IllegalArgumentException("Cannot get asset list because search values is null.");
        }

        Long numberOfAssets = assetDao.getAssetCount(searchFields, dynamic, includeInactivated);

        int numberOfPages = 0;
        if (listSize != 0) {
            numberOfPages = (int) (numberOfAssets / listSize);
            if (numberOfAssets % listSize != 0) {
                numberOfPages += 1;
            }
        }

        List<Asset> assetEntityList = assetDao.getAssetListSearchPaginated(page, listSize, searchFields, dynamic, includeInactivated);

        AssetListResponse listAssetResponse = new AssetListResponse();
        listAssetResponse.setCurrentPage(page);
        listAssetResponse.setTotalNumberOfPages(numberOfPages);
        listAssetResponse.getAssetList().addAll(assetEntityList);
        return listAssetResponse;
    }

    @Override
    public Long getAssetListCount(List<SearchKeyValue> searchFields, boolean dynamic, boolean includeInactivated) {
        if (searchFields == null || searchFields.isEmpty()) {
            throw new IllegalArgumentException("Cannot get asset list because query is null.");
        }
        return assetDao.getAssetCount(searchFields, dynamic, includeInactivated);
    }

    @Override
    public Asset updateAsset(Asset asset, String username, String comment) {
        Asset updatedAsset = updateAssetInternal(asset, username);
        auditService.logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    @Override
    public Asset archiveAsset(Asset asset, String username, String comment) {
        asset.setActive(false);
        Asset archivedAsset = updateAssetInternal(asset, username);
        auditService.logAssetArchived(archivedAsset, comment, username);
        return archivedAsset;
    }

    private Asset updateAssetInternal(Asset asset, String username) {
        if (asset == null) {
            throw new IllegalArgumentException("No asset to update");
        }
        if (asset.getId() == null) {
            throw new IllegalArgumentException("No id on asset to update");
        }
        checkIdentifierNullValues(asset);

        asset.setUpdatedBy(username);
        asset.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        asset.setEventCode(EventCode.MOD.value());
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
    public AssetBO upsertAssetBO(AssetBO assetBo, String username) {
        if (assetBo == null) {
            throw new IllegalArgumentException("No asset business object to upsert");
        }

        Asset asset = assetBo.getAsset();
        Asset existingAsset = getAssetByCfrIrcs(createAssetId(asset));
        if (existingAsset != null) {
            asset.setId(existingAsset.getId());
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
            getNotesForAsset(assetId).forEach(n -> deleteNote(n.getId()));
            assetBo.getNotes().forEach(c -> createNoteForAsset(assetId, c, username));
        }
        return assetBo;
    }

    @Override
    public Asset getAssetById(AssetIdentifier assetId, String value) {

        if (assetId == null) {
            throw new IllegalArgumentException("AssetIdentity object is null");
        }

        if (value == null) {
            throw new IllegalArgumentException("AssetIdentity value is null");
        }
        return assetDao.getAssetFromAssetId(assetId, value);
    }

    @Override
    public Asset getAssetFromAssetIdAtDate(AssetIdentifier idType, String idValue, OffsetDateTime date) {

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
                UUID fromString = UUID.fromString(idValue); // Result is ignored?
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Not a valid UUID");
            }
        }
        return assetDao.getAssetFromAssetIdAtDate(idType, idValue, date);
    }

    @Override
    public Asset getAssetById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }
        return assetDao.getAssetById(id);
    }

    @Override
    public List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups) {
        LOG.debug("Getting asset by ID.");
        if (groups == null || groups.isEmpty()) {
            throw new IllegalArgumentException("No groups in query");
        }
        List<AssetGroupField> groupFields = groups.stream()
                .map(g -> assetGroupService.retrieveFieldsForGroup(g.getId()))
                .flatMap(Collection::stream)
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
    public void deleteAsset(AssetIdentifier assetId, String value) {

        if (assetId == null) {
            throw new IllegalArgumentException("AssetId is null");
        }

        Asset assetEntity = getAssetById(assetId, value);
        assetDao.deleteAsset(assetEntity);
    }

    @Override
    public List<Asset> getRevisionsForAsset(UUID id) {
        return assetDao.getRevisionsForAsset(id);
    }

    @Override
    public Asset getAssetRevisionForRevisionId(UUID historyId) {
        return assetDao.getAssetRevisionForHistoryId(historyId);
    }

    @Override
    public List<Asset> getRevisionsForAssetLimited(UUID id, Integer maxNbr) {
        List<Asset> revisions = assetDao.getRevisionsForAsset(id);
        revisions.sort(Comparator.comparing(Asset::getUpdateTime));
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
        return contactDao.getContactInfoByAssetId(asset.getId());
    }

    @Override
    public ContactInfo createContactInfoForAsset(UUID assetId, ContactInfo contactInfo, String username) {
        Asset asset = assetDao.getAssetById(assetId);
        if (asset == null) {
            throw new IllegalArgumentException("Could not find any asset with id: " + assetId);
        }
        contactInfo.setAssetId(asset.getId());
        contactInfo.setUpdatedBy(username);
        if (contactInfo.getId() == null) {
            contactInfo.setCreateTime(OffsetDateTime.now(ZoneOffset.UTC));
        }
        contactInfo.setAssetUpdateTime(asset.getUpdateTime());
        return contactDao.createContactInfo(contactInfo);
    }

    @Override
    public ContactInfo updateContactInfo(ContactInfo contactInfo, String username) {
        Asset asset = assetDao.getAssetById(contactInfo.getAssetId());
        if (asset == null) {
            throw new IllegalArgumentException("Could not find any asset with id: " + contactInfo.getAssetId());
        }
        ContactInfo old = contactDao.findContactInfo(contactInfo.getId());
        contactInfo.setCreateTime(old.getCreateTime());
        contactInfo.setUpdatedBy(username);
        contactInfo.setAssetUpdateTime(asset.getUpdateTime());
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

    @Override
    public List<ContactInfo> getContactInfoRevisionForAssetHistory(UUID assetId, OffsetDateTime updatedDate) {
        List<ContactInfo> contactInfoListByAssetId = contactDao.getContactInfoByAssetId(assetId);
        List<ContactInfo> revisionList = contactDao.getContactInfoRevisionForAssetHistory(contactInfoListByAssetId, updatedDate);
        return revisionList;
    }

    @Override
    public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) {

        // Get Mobile Terminal if it exists
        MobileTerminal terminal = mobileTerminalService.getMobileTerminalByAssetMTEnrichmentRequest(request);

        Asset asset = terminal == null ? null : terminal.getAsset();

        if (asset == null) {
            asset = getAssetByCfrIrcs(createAssetId(request));
        }

        if (isPluginTypeWithoutMobileTerminal(request.getPluginType()) && asset != null) {
            MobileTerminal mobileTerminal = mobileTerminalService.findMobileTerminalByAsset(asset.getId());
            if (mobileTerminal != null) {
                terminal = mobileTerminal;
            }
        }

        MobileTerminalTypeEnum transponderType = getTransponderType(request);
        if (asset == null &&
                (transponderType == null || !transponderType.equals(MobileTerminalTypeEnum.INMARSAT_C))) {
            asset = AssetUtil.createNewAssetFromRequest(request, assetDao.getNextUnknownShipNumber());
            assetDao.createAsset(asset);
        }

        AssetMTEnrichmentResponse assetMTEnrichmentResponse = new AssetMTEnrichmentResponse();
        enrichAssetAndMobileTerminal(request, assetMTEnrichmentResponse, terminal, asset);
        enrichAssetGroup(assetMTEnrichmentResponse, asset);

        return assetMTEnrichmentResponse;
    }

    private void enrichAssetAndMobileTerminal(AssetMTEnrichmentRequest request,
                                              AssetMTEnrichmentResponse assetMTEnrichmentResponse, MobileTerminal terminal, Asset asset) {
        if (asset != null) {
            enrichementHelper(assetMTEnrichmentResponse, asset);
        }
        if (terminal != null) {
            enrichementHelper(request, assetMTEnrichmentResponse, terminal);
        }
    }

    private void enrichAssetGroup(AssetMTEnrichmentResponse assetMTEnrichmentResponse, Asset asset) {
        if (asset != null) {
            List<String> assetGroupList = new ArrayList<>();
            List<AssetGroup> list = assetGroupService.getAssetGroupListByAssetId(asset.getId());
            for (AssetGroup assetGroup : list) {
                UUID assetGroupId = assetGroup.getId();
                assetGroupList.add(assetGroupId.toString());
            }
            assetMTEnrichmentResponse.setAssetGroupList(assetGroupList);
        }
    }

    private MobileTerminalTypeEnum getTransponderType(AssetMTEnrichmentRequest request) {
        try {
            return MobileTerminalTypeEnum.valueOf(request.getTranspondertypeValue());
        } catch (Exception e) {
            return null;
        }
    }

    private AssetMTEnrichmentResponse enrichementHelper(AssetMTEnrichmentResponse resp, Asset asset) {
        Map<String, String> assetId = createAssetId(asset);
        resp.setAssetId(assetId);
        resp.setAssetUUID(asset.getId() == null ? null : asset.getId().toString());
        resp.setAssetName(asset.getName());
        resp.setAssetHistoryId(asset.getHistoryId() == null ? null : asset.getHistoryId().toString());
        resp.setFlagstate(asset.getFlagStateCode());
        resp.setExternalMarking(asset.getExternalMarking());
        resp.setGearType(asset.getGearFishingType());
        resp.setCfr(asset.getCfr());
        resp.setIrcs(asset.getIrcs());
        resp.setMmsi(asset.getMmsi());
        return resp;
    }

    private AssetMTEnrichmentResponse enrichementHelper(AssetMTEnrichmentRequest req, AssetMTEnrichmentResponse resp, MobileTerminal mobTerm) {

        // here we put into response data about mobiletreminal / channels etc etc
        String channelGuid = getChannelGuid(mobTerm, req);
        resp.setChannelGuid(channelGuid);
        resp.setMobileTerminalConnectId(mobTerm.getAsset().getId().toString());
        resp.setMobileTerminalType(mobTerm.getMobileTerminalType().name());
        if (mobTerm.getId() != null) {
            resp.setMobileTerminalGuid(mobTerm.getId().toString());
        }
        resp.setMobileTerminalIsInactive(mobTerm.getInactivated());

        if (mobTerm.getChannels() != null) {
            Set<Channel> channels = mobTerm.getChannels();
            for (Channel channel : channels) {
                if (!channel.getId().toString().equals(channelGuid)) {
                    continue;
                }

                resp.setDNID(channel.getDNID());
                resp.setMemberNumber(channel.getMemberNumber());
            }
        }

        if (mobTerm.getId() != null) {
            try {
                MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(mobTerm.getId());
                if (mobileTerminal != null) {
                    resp.setSerialNumber(mobileTerminal.getSerialNo());
                }
            } catch (IllegalArgumentException IllegalSoWeSkipTryingToFetchIt) {
                // DON'T CARE
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
            correctDnid = channel.getDNID().equalsIgnoreCase(dnid);
            correctMemberNumber = channel.getMemberNumber().equalsIgnoreCase(memberNumber);

            if (correctDnid && correctMemberNumber) {
                channelGuid = channel.getId().toString();
            }
        }
        return channelGuid;
    }

    private Map<String, String> createAssetId(Asset asset) {
        Map<String, String> assetId = new HashMap<>();

        if (asset.getCfr() != null && asset.getCfr().length() > 0) {
            assetId.put(CFR, asset.getCfr());
        }
        if (asset.getId() != null) {
            assetId.put(GUID, asset.getId().toString());
        }
        if (asset.getImo() != null && asset.getImo().length() > 0) {
            assetId.put(IMO, asset.getImo());
        }
        if (asset.getIrcs() != null && asset.getIrcs().length() > 0) {
            assetId.put(IRCS, asset.getIrcs());
        }
        if (asset.getMmsi() != null && asset.getMmsi().length() > 0) {
            assetId.put(MMSI, asset.getMmsi());
        }
        if (asset.getGfcm() != null && asset.getGfcm().length() > 0) {
            assetId.put(GFCM, asset.getGfcm());
        }
        if (asset.getUvi() != null && asset.getUvi().length() > 0) {
            assetId.put(UVI, asset.getUvi());
        }
        if (asset.getIccat() != null && asset.getIccat().length() > 0) {
            assetId.put(ICCAT, asset.getIccat());
        }
        return assetId;
    }

    private Map<String, String> createAssetId(AssetMTEnrichmentRequest request) {
        Map<String, String> assetId = new HashMap<>();

        if (request.getCfrValue() != null && request.getCfrValue().length() > 0) {
            assetId.put(CFR, request.getCfrValue());
        }
        if (request.getIdValue() != null) {
            assetId.put(GUID, request.getIdValue().toString());
        }
        if (request.getImoValue() != null && request.getImoValue().length() > 0) {
            assetId.put(IMO, request.getImoValue());
        }
        if (request.getIrcsValue() != null && request.getIrcsValue().length() > 0) {
            assetId.put(IRCS, request.getIrcsValue());
        }
        if (request.getMmsiValue() != null && request.getMmsiValue().length() > 0) {
            assetId.put(MMSI, request.getMmsiValue());
        }
        if (request.getGfcmValue() != null && request.getGfcmValue().length() > 0) {
            assetId.put(GFCM, request.getGfcmValue());
        }
        if (request.getUviValue() != null && request.getUviValue().length() > 0) {
            assetId.put(UVI, request.getUviValue());
        }
        if (request.getIccatValue() != null && request.getIccatValue().length() > 0) {
            assetId.put(ICCAT, request.getIccatValue());
        }
        return assetId;
    }


    private Asset getAssetByCfrIrcs(Map<String, String> assetId) {
        Asset asset = null;

        // If no asset information exists, don't look for one
        if (assetId == null || assetId.size() < 1) {
            LOG.warn("No asset information exists!");
            return null;
        }

        // Get possible search parameters
        String cfr = assetId.getOrDefault(CFR, null);
        String ircs = assetId.getOrDefault(IRCS, null);
        String mmsi = assetId.getOrDefault(MMSI, null);

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

    private boolean isPluginTypeWithoutMobileTerminal(String pluginType) {
        if (pluginType == null) {
            return true;
        }
        try {
            PluginType type = PluginType.valueOf(pluginType);
            switch (type) {
                case MANUAL:
                case NAF:
                case OTHER:
                    return true;
                default:
                    return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // if more than 1 hit put data from ais into fartyg2record
    // remove the duplicate
    private Asset normalizeAssetOnMMSI_IRCS(String mmsi, String ircs) {


        List<Asset> assets = null;

        if ((mmsi != null) && (ircs != null)) {
            assets = getAssetList(Arrays.asList(
                    new SearchKeyValue(SearchFields.MMSI, Collections.singletonList(mmsi)),
                    new SearchKeyValue(SearchFields.IRCS, Collections.singletonList(ircs))),
                    1, 10, false, false).getAssetList();
        } else if (mmsi != null) {
            assets = getAssetList(Collections.singletonList(
                    new SearchKeyValue(SearchFields.MMSI, Collections.singletonList(mmsi))),
                    1, 10, false, false).getAssetList();
        } else if (ircs != null) {
            assets = getAssetList(Collections.singletonList(
                    new SearchKeyValue(SearchFields.IRCS, Collections.singletonList(ircs))),
                    1, 10, false, false).getAssetList();
        } else {
            return null;
        }

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
                if ((asset.getSource() != null) && (asset.getSource().equals("NATIONAL"))) {
                    fartyg2Asset = asset;
                } else {
                    nonFartyg2Asset = asset;
                }
            }
            if ((fartyg2Asset != null) && (nonFartyg2Asset != null)) {
                assetDao.deleteAsset(nonFartyg2Asset);
                // flush is necessary to avoid dumps on MMSI
                em.flush();
                return fartyg2Asset;
            }
        }
        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void assetInformation(Asset assetFromAIS, String user) {

        if (assetFromAIS == null) {
            return;
        }

        Asset assetFromDB = normalizeAssetOnMMSI_IRCS(assetFromAIS.getMmsi(), assetFromAIS.getIrcs());

        boolean shouldUpdate = false;


        if (assetFromDB == null) {
            return;
        }

        if ((assetFromDB.getMmsi() == null || !assetFromDB.getMmsi().equals(assetFromAIS.getMmsi())) && (assetFromAIS.getMmsi() != null)) {
            shouldUpdate = true;
            assetFromDB.setMmsi(assetFromAIS.getMmsi());
        }
        if ((assetFromDB.getIrcs() == null) && (assetFromAIS.getIrcs() != null) && (!assetFromDB.getIrcs().equals(assetFromAIS.getIrcs()))) {
            shouldUpdate = true;
            assetFromDB.setIrcs(assetFromAIS.getIrcs());
        }
        if ((assetFromDB.getVesselType() == null) && (assetFromAIS.getVesselType() != null) && (!assetFromDB.getVesselType().equals(assetFromAIS.getVesselType()))) {
            shouldUpdate = true;
            assetFromDB.setVesselType(assetFromAIS.getVesselType());
        }
        if ((assetFromDB.getImo() == null) && (assetFromAIS.getImo() != null) && (!assetFromDB.getImo().equals(assetFromAIS.getImo()))) {
            shouldUpdate = true;
            assetFromDB.setImo(assetFromAIS.getImo());
        }

        if ((assetFromDB.getName() == null || assetFromDB.getName().startsWith("Unknown") || !assetFromDB.getName().equals(assetFromAIS.getName())) && (assetFromAIS.getName() != null)) {
            if (!assetFromAIS.getName().isEmpty()) {
                shouldUpdate = true;
                assetFromDB.setName(assetFromAIS.getName());
            }
        }
        if ((assetFromDB.getFlagStateCode() == null || assetFromDB.getFlagStateCode().startsWith("UNK")) && (assetFromAIS.getFlagStateCode() != null) && (!assetFromDB.getFlagStateCode().equals(assetFromAIS.getFlagStateCode()))) {
            shouldUpdate = true;
            assetFromDB.setFlagStateCode(assetFromAIS.getFlagStateCode());
        }
        if (shouldUpdate) {
            assetFromDB.setUpdatedBy(user);
            assetFromDB.setUpdateTime(OffsetDateTime.now());
            em.merge(assetFromDB);
        }
    }
}

