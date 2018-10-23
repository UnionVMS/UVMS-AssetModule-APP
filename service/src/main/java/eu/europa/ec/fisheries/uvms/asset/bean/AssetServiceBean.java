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
import java.util.*;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentResponse;
import eu.europa.ec.fisheries.uvms.asset.util.AssetComparator;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.EventCode;
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


    /**
     * @param asset
     * @param username
     * @return
     */
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

    /**
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
     * @param searchFields
     * @param dynamic
     * @return
     */
    @Override
    public Long getAssetListCount(List<SearchKeyValue> searchFields, boolean dynamic) {
        if (searchFields == null || searchFields.isEmpty()) {
            throw new IllegalArgumentException("Cannot get asset list because query is null.");
        }

        return assetDao.getAssetCount(searchFields, dynamic);
    }

    /**
     * @param asset
     * @param username
     * @param comment
     * @return
     */
    @Override
    public Asset updateAsset(Asset asset, String username, String comment) {
        Asset updatedAsset = updateAssetInternal(asset, username);
        auditService.logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    /**
     * @param asset    an asset
     * @param username
     * @param comment  a comment to the archiving
     * @return
     */
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

    /**
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
    public AssetBO upsertAssetBO(AssetBO assetBo, String username) {
        if (assetBo == null) {
            throw new IllegalArgumentException("No asset business object to upsert");
        }
        
        Asset asset = assetBo.getAsset();
        Asset existingAsset = getAssetById(AssetIdentifier.CFR, asset.getCfr());
        if (existingAsset != null) {
            asset.setId(existingAsset.getId());
            asset.setHistoryId(existingAsset.getHistoryId());
        }
        
        if (!AssetComparator.assetEquals(asset, existingAsset)) {
            asset = upsertAsset(asset, username);
        }

        // Clear and create new contacts and notes for now
        UUID assetId = asset.getId();
        if (assetBo.getContacts() != null) {
            getContactInfoForAsset(assetId).stream().forEach(c -> deleteContactInfo(c.getId()));
            assetBo.getContacts().stream().forEach(c -> createContactInfoForAsset(assetId, c, username));
        }

        if (assetBo.getNotes() != null) {
            getNotesForAsset(assetId).stream().forEach(n -> deleteNote(n.getId()));
            assetBo.getNotes().stream().forEach(c -> createNoteForAsset(assetId, c, username));
        }

        return assetBo;
    }

    /**
     * @param assetId
     * @param value
     * @return
     */
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

    /**
     * @param idType
     * @param idValue
     * @param date
     * @return
     */
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
                UUID.fromString(idValue);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Not a valid UUID");
            }
        }

        return assetDao.getAssetFromAssetIdAtDate(idType, idValue, date);
    }


    /**
     * @param id
     * @return
     */
    @Override
    public Asset getAssetById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Id is null");
        }

        return assetDao.getAssetById(id);
    }

    /**
     * @param groups
     * @return
     */
    @Override
    public List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups) {
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

    @Override
    public Asset getAssetByConnectId(UUID uuid) {
        return assetDao.getAssetByConnectId(uuid);
    }


    @Override
    public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) {


        AssetMTEnrichmentResponse assetMTEnrichmentResponse = new AssetMTEnrichmentResponse();

        // Get Mobile Terminal if it exists
        MobileTerminalType mobileTerminalType = mobileTerminalService.getMobileTerminalByAssetMTEnrichmentRequest(request);

        // Get Asset
        Asset asset = null;
        if (mobileTerminalType != null) {
            String connectId = mobileTerminalType.getConnectId();
            if (connectId != null) {
                UUID connectId_UUID = UUID.fromString(connectId);
                asset = getAssetByConnectId(connectId_UUID);
                if (asset != null) {
                    assetMTEnrichmentResponse = enrichementHelper(assetMTEnrichmentResponse, asset);
                }
                // test this as well  OBS TEST
                else{
                    asset = getAssetByCfrIrcs(createAssetId(request));
                    if (asset != null) {
                        assetMTEnrichmentResponse = enrichementHelper(assetMTEnrichmentResponse, asset);
                    }
                }
            }
        } else {
            asset = getAssetByCfrIrcs(createAssetId(request));
            if (isPluginTypeWithoutMobileTerminal(request.getPluginType()) && asset != null) {
                assetMTEnrichmentResponse = enrichementHelper(assetMTEnrichmentResponse, asset);
                MobileTerminal mobileTerminal = mobileTerminalService.findMobileTerminalByAsset(asset.getId());
                if(mobileTerminal != null){
                    mobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal);
                }
            }
        }
        if (asset != null) {
            assetMTEnrichmentResponse = enrichementHelper(assetMTEnrichmentResponse, asset);
        }

        List<String> assetGroupList = new ArrayList<>();
        if (asset != null) {
            List<AssetGroup> list = assetGroupService.getAssetGroupListByAssetId(asset.getId());
            for (AssetGroup assetGroup : list) {
                UUID assetGroupId = assetGroup.getId();
                assetGroupList.add(assetGroupId.toString());
            }
        }
        assetMTEnrichmentResponse.setAssetGroupList(assetGroupList);
        if (mobileTerminalType != null) {
            assetMTEnrichmentResponse = enrichementHelper(request, assetMTEnrichmentResponse,mobileTerminalType);
            assetMTEnrichmentResponse.setMobileTerminalType(mobileTerminalType.getType());
        }
        return assetMTEnrichmentResponse;
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


    private AssetMTEnrichmentResponse enrichementHelper(AssetMTEnrichmentRequest req, AssetMTEnrichmentResponse resp, MobileTerminalType mobTerm) {

        // here we put into response data about mobiletreminal / channels etc etc
        String channelGuid = getChannelGuid(mobTerm, req);
        resp.setChannelGuid(channelGuid);
        if (mobTerm.getConnectId() != null) {
            UUID connectidUUID = null;
            try {
                connectidUUID = UUID.fromString(mobTerm.getConnectId());
            } catch (IllegalArgumentException e) {
                connectidUUID = null;
            }
            resp.setMobileTerminalConnectId(connectidUUID == null ? null : connectidUUID.toString());
        }
        resp.setMobileTerminalType(mobTerm.getType());
        if(mobTerm.getMobileTerminalId() != null) {
            resp.setMobileTerminalGuid(mobTerm.getMobileTerminalId().getGuid());
        }
        resp.setMobileTerminalIsInactive(mobTerm.isInactive());

        if(mobTerm.getChannels() != null){
            List<ComChannelType> channelTypes = mobTerm.getChannels();
            for(ComChannelType channelType : channelTypes){
                if(!channelType.getGuid().equals(channelGuid)){
                    continue;
                }
                List<ComChannelAttribute> attributes = channelType.getAttributes();
                for(ComChannelAttribute attr : attributes){
                    String type = attr.getType();
                    String val = attr.getValue();
                    if (DNID.equals(type)) {
                        resp.setDNID(val);
                    }
                    if (MEMBER_NUMBER.equals(type)) {
                        resp.setMemberNumber(val);
                    }
                }
            }
        }

        if(mobTerm.getMobileTerminalId() != null){
            String guid = mobTerm.getMobileTerminalId().getGuid();

            try {
                UUID mobileTerminalId = UUID.fromString(guid);
                MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(mobileTerminalId);
                if(mobileTerminal != null){
                    resp.setSerialNumber(mobileTerminal.getSerialNo());
                }
            }
            catch(IllegalArgumentException IllegalSoWeSkipTryingToFetchIt){
                // DONT CARE
            }
        }


        return resp;
    }

    private String getChannelGuid(MobileTerminalType mobileTerminal, AssetMTEnrichmentRequest request) {
        String dnid = "";
        String memberNumber = "";
        String channelGuid = "";

        dnid = request.getDnidValue();
        memberNumber = request.getMemberNumberValue();

        // Get the channel guid
        boolean correctDnid = false;
        boolean correctMemberNumber = false;
        List<ComChannelType> channels = mobileTerminal.getChannels();
        for (ComChannelType channel : channels) {

            List<ComChannelAttribute> attributes = channel.getAttributes();

            for (ComChannelAttribute attribute : attributes) {
                String type = attribute.getType();
                String value = attribute.getValue();

                if (DNID.equals(type)) {
                    correctDnid = value.equals(dnid);
                }
                if (MEMBER_NUMBER.equals(type)) {
                    correctMemberNumber = value.equals(memberNumber);
                }
            }

            if (correctDnid && correctMemberNumber) {
                channelGuid = channel.getGuid();
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


    // TODO ? the belgian constants as well if so how ?? no spec !!!
    private Asset getAssetByCfrIrcs(Map<String, String> assetId) {

        try {
            // If no asset information exists, don't look for one
            if (assetId == null || assetId.size() < 1) {
                LOG.warn("No asset information exists!");
                return null;
            }
            // Get possible search parameters
            String cfr = assetId.getOrDefault(CFR, null);
            String ircs = assetId.getOrDefault(IRCS, null);
            String mmsi = assetId.getOrDefault(MMSI, null);

            Asset asset = null;
            if (ircs != null && cfr != null && mmsi != null) {
                try {
                    asset = getAssetById(AssetIdentifier.CFR, cfr);
                    // If the asset matches on ircs as well we have a winner
                    if (asset != null && asset.getIrcs().equals(ircs)) {
                        return asset;
                    }
                    // If asset is null, try fetching by IRCS (cfr will fail for SE national db)
                    if (asset == null) {
                        asset = getAssetById(AssetIdentifier.IRCS, ircs);
                        // If asset is still null, try mmsi (this should be the case for movement coming from AIS)
                        if (asset == null) {
                            return getAssetById(AssetIdentifier.MMSI, mmsi);
                        }
                    }
                } catch (Exception e) {
                    return getAssetById(AssetIdentifier.IRCS, ircs);
                }
            } else if (cfr != null) {
                return getAssetById(AssetIdentifier.CFR, cfr);
            } else if (ircs != null) {
                return getAssetById(AssetIdentifier.IRCS, ircs);
            } else if (mmsi != null) {
                return getAssetById(AssetIdentifier.MMSI, mmsi);
            }

        } catch (Exception e) {
            // Log and continue validation
            LOG.warn("Could not find asset!");
        }
        return null;
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


}