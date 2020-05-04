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
package eu.europa.ec.fisheries.uvms.asset.message.event;

import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetBO;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.model.constants.UnitTonnage;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.wsdl.asset.types.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class AssetModelMapper {

    @Inject
    private AssetServiceBean assetService;
    
    public Asset toAssetEntity(eu.europa.ec.fisheries.wsdl.asset.types.Asset assetModel) {
        Asset asset = new Asset();
        
        if (assetModel.getAssetId() != null && assetModel.getAssetId().getGuid() != null) {
            asset.setId(UUID.fromString(assetModel.getAssetId().getGuid()));
        }
        
        asset.setActive(assetModel.isActive());
        if (assetModel.getSource() != null) {
            asset.setSource(assetModel.getSource().toString());
        }
        if (assetModel.getEventHistory() != null) {
            asset.setHistoryId(UUID.fromString(assetModel.getEventHistory().getEventId()));
            Instant instant = assetModel.getEventHistory().getEventDate().toInstant();
            asset.setUpdateTime(instant);
            asset.setEventCode(assetModel.getEventHistory().getEventCode().toString());
        }
        asset.setName(assetModel.getName());
        asset.setFlagStateCode(assetModel.getCountryCode());
        asset.setGearFishingType(assetModel.getGearType());
        asset.setIrcsIndicator(assetModel.getHasIrcs().equals("Y"));
        asset.setIrcs(assetModel.getIrcs());
        asset.setExternalMarking(assetModel.getExternalMarking());
        asset.setCfr(assetModel.getCfr());
        asset.setImo(assetModel.getImo());
        asset.setMmsi(assetModel.getMmsiNo());
        asset.setHasLicence(assetModel.isHasLicense());
        asset.setPortOfRegistration(assetModel.getHomePort());
        if (assetModel.getLengthOverAll() != null) {
            asset.setLengthOverAll(assetModel.getLengthOverAll().doubleValue());
        }
        if (assetModel.getLengthBetweenPerpendiculars() != null) {
            asset.setLengthBetweenPerpendiculars(assetModel.getLengthBetweenPerpendiculars().doubleValue());
        }
        if (assetModel.getGrossTonnage() != null) {
            asset.setGrossTonnage(assetModel.getGrossTonnage().doubleValue());
        }
        if (assetModel.getGrossTonnageUnit() != null) {
            asset.setGrossTonnageUnit(UnitTonnage.getType(assetModel.getGrossTonnageUnit()));
        }
        if (assetModel.getOtherGrossTonnage() != null) {
            asset.setOtherTonnage(assetModel.getOtherGrossTonnage().doubleValue());
        }
        if (assetModel.getSafetyGrossTonnage() != null) {
            asset.setSafteyGrossTonnage(assetModel.getSafetyGrossTonnage().doubleValue());
        } 
        if (assetModel.getPowerMain() != null) {
            asset.setPowerOfMainEngine(assetModel.getPowerMain().doubleValue());
        }
        if (assetModel.getPowerAux() != null) {
            asset.setPowerOfAuxEngine(assetModel.getPowerAux().doubleValue());
        }
        if (assetModel.getProducer() != null) {
            asset.setProdOrgCode(assetModel.getProducer().getCode());
            asset.setProdOrgName(assetModel.getProducer().getName());
        }
        asset.setIccat(assetModel.getIccat());
        asset.setUvi(assetModel.getUvi());
        asset.setGfcm(assetModel.getGfcm());
        
        return asset;
    }
    
    public AssetBO toAssetBO(eu.europa.ec.fisheries.wsdl.asset.types.Asset assetModel) {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(toAssetEntity(assetModel));
        assetBo.setContacts(toAssetContacts(assetModel.getContact()));
        assetBo.setNotes(toAssetNotes(assetModel.getNotes()));
        return assetBo;
    }
    
    public eu.europa.ec.fisheries.wsdl.asset.types.Asset toAssetModel(Asset assetEntity) {
        if (assetEntity == null) {
            return null;
        }

        eu.europa.ec.fisheries.wsdl.asset.types.Asset assetModel = new eu.europa.ec.fisheries.wsdl.asset.types.Asset();
        
        AssetId assetId = new AssetId();
        assetId.setGuid(assetEntity.getId().toString());
        assetId.setValue(assetEntity.getId().toString());
        assetId.setType(AssetIdType.GUID);
        
        assetModel.setAssetId(assetId);
        assetModel.setActive(assetEntity.getActive());
        if (assetEntity.getSource() !=  null && !assetEntity.getSource().isEmpty()) {
            assetModel.setSource(CarrierSource.fromValue(assetEntity.getSource()));
        }
        AssetHistoryId assetHistory = new AssetHistoryId();
        assetHistory.setEventId(assetEntity.getHistoryId().toString());
        if (assetEntity.getUpdateTime() != null) {
            Date d = Date.from(assetEntity.getUpdateTime());
            assetHistory.setEventDate(d);
        }
        if (assetEntity.getEventCode() != null && !assetEntity.getEventCode().isEmpty()) {
            assetHistory.setEventCode(getEventCode(assetEntity));
        }
        assetModel.setEventHistory(assetHistory);
        assetModel.setName(assetEntity.getName());
        assetModel.setCountryCode(assetEntity.getFlagStateCode());
        assetModel.setGearType(assetEntity.getGearFishingType());
        assetModel.setHasIrcs(assetEntity.getIrcsIndicator() != null && assetEntity.getIrcsIndicator() ? "Y" : "N");
        assetModel.setIrcs(assetEntity.getIrcs());
        assetModel.setExternalMarking(assetEntity.getExternalMarking());
        assetModel.setCfr(assetEntity.getCfr());
        assetModel.setImo(assetEntity.getImo());
        assetModel.setMmsiNo(assetEntity.getMmsi());
        if (assetEntity.getHasLicence() != null) {
            assetModel.setHasLicense(assetEntity.getHasLicence());
        }
        assetModel.setHomePort(assetEntity.getPortOfRegistration());
        if (assetEntity.getLengthOverAll() != null) {
            assetModel.setLengthOverAll(BigDecimal.valueOf(assetEntity.getLengthOverAll()));
        }
        if (assetEntity.getLengthBetweenPerpendiculars() != null) {
            assetModel.setLengthBetweenPerpendiculars(BigDecimal.valueOf(assetEntity.getLengthBetweenPerpendiculars()));
        }
        if (assetEntity.getGrossTonnage() != null) {
            assetModel.setGrossTonnage(BigDecimal.valueOf(assetEntity.getGrossTonnage()));
        }
        if (assetEntity.getGrossTonnageUnit() != null) {
            assetModel.setGrossTonnageUnit(assetEntity.getGrossTonnageUnit().toString());
        }
        if (assetEntity.getOtherTonnage() != null) {
            assetModel.setOtherGrossTonnage(BigDecimal.valueOf(assetEntity.getOtherTonnage()));
        }
        if (assetEntity.getSafteyGrossTonnage() != null) {
            assetModel.setSafetyGrossTonnage(BigDecimal.valueOf(assetEntity.getSafteyGrossTonnage()));
        }
        if (assetEntity.getPowerOfMainEngine() != null) {
            assetModel.setPowerMain(BigDecimal.valueOf(assetEntity.getPowerOfMainEngine()));
        }
        if (assetEntity.getPowerOfAuxEngine() != null) {
            assetModel.setPowerAux(BigDecimal.valueOf(assetEntity.getPowerOfAuxEngine()));
        }
        AssetProdOrgModel prodOrg = new AssetProdOrgModel();
        prodOrg.setCode(assetEntity.getProdOrgCode());
        prodOrg.setName(assetEntity.getProdOrgName());
        assetModel.setProducer(prodOrg);
        
        List<Note> notes = assetService.getNotesForAsset(assetEntity.getId());
        for (Note note : notes) {
            AssetNotes assetNote = new AssetNotes();
            assetNote.setId(note.getId().toString());
            if (note.getCreatedOn() != null) {
                assetNote.setDate(DateUtils.dateToEpochMilliseconds(note.getCreatedOn()));
            }
            assetNote.setNotes(note.getNote());
            assetModel.getNotes().add(assetNote);
        }

        List<ContactInfo> contacts = assetService.getContactInfoForAsset(assetEntity.getId());
        for (ContactInfo contactInfo : contacts) {
            AssetContact contact = new AssetContact();
            contact.setName(contactInfo.getName());
            contact.setNumber(contactInfo.getPhoneNumber());
            contact.setEmail(contactInfo.getEmail());
            if (contactInfo.getOwner() != null) { 
                contact.setOwner(contactInfo.getOwner());
            }
            if (contactInfo.getSource() != null) {
                contact.setSource(ContactSource.fromValue(contactInfo.getSource()));
            }
            assetModel.getContact().add(contact);
        }
        
        assetModel.setIccat(assetEntity.getIccat());
        assetModel.setUvi(assetEntity.getUvi());
        assetModel.setGfcm(assetEntity.getGfcm());
        
        return assetModel;
    }

    private EventCode getEventCode(Asset assetEntity) {
        try {
            return EventCode.fromValue(assetEntity.getEventCode());
        } catch (Exception e) {
            return EventCode.UNK;
        }
    }
    

    public AssetIdentifier mapToAssetIdentity(AssetIdType assetIdType) {
        switch (assetIdType) {
            case CFR:
                return AssetIdentifier.CFR;
            case GFCM:
                return AssetIdentifier.GFCM;
            case GUID:
                return AssetIdentifier.GUID;
            case ICCAT:
                return AssetIdentifier.ICCAT;
            case IMO:
                return AssetIdentifier.IMO;
            case IRCS:
                return AssetIdentifier.IRCS;
            case MMSI:
                return AssetIdentifier.MMSI;
            case UVI:
                return AssetIdentifier.UVI;
            default:
                throw new IllegalArgumentException("Asset identifier is not valid/supported!");
        }
    }
    
    public ListAssetResponse toListAssetResponse(AssetListResponse assetListResponse) {
        ListAssetResponse listAssetResponse = new ListAssetResponse();
        listAssetResponse.setCurrentPage(assetListResponse.getCurrentPage());
        listAssetResponse.setTotalNumberOfPages(assetListResponse.getTotalNumberOfPages());
        listAssetResponse.getAsset().addAll(assetListResponse.getAssetList().stream()
                                                        .map(this::toAssetModel)
                                                        .collect(Collectors.toList()));
        return listAssetResponse;
    }
    
    public List<Note> toAssetNotes(List<AssetNotes> assetNotes) {
        List<Note> notes = new ArrayList<>();
        for (AssetNotes assetNote : assetNotes) {
            Note note = new Note();
            if(assetNote.getId() != null) {
                note.setId(UUID.fromString(assetNote.getId()));
            }
            if (assetNote.getDate() != null) {
                note.setCreatedOn(DateUtils.stringToDate(assetNote.getDate()));
            }
            note.setNote(assetNote.getNotes());
            notes.add(note);
        }
        return notes;
    }
    
    public List<ContactInfo> toAssetContacts(List<AssetContact> contacts) {
        List<ContactInfo> contactInfos = new ArrayList<>();
        for (AssetContact assetContact : contacts) {
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setName(assetContact.getName());
            contactInfo.setPhoneNumber(assetContact.getNumber());
            contactInfo.setEmail(assetContact.getEmail());
            contactInfo.setOwner(assetContact.isOwner());
            if (assetContact.getSource() != null) {
                contactInfo.setSource(assetContact.getSource().toString());
            }
            contactInfos.add(contactInfo);
        }
        return contactInfos;
    }
}
