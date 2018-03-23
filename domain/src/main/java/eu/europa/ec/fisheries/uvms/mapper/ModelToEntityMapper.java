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
package eu.europa.ec.fisheries.uvms.mapper;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import eu.europa.ec.fisheries.uvms.entity.model.*;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearType;
import eu.europa.ec.fisheries.uvms.util.DateUtil;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ModelToEntityMapper {

    private static final Logger LOG = LoggerFactory.getLogger(ModelToEntityMapper.class);

    public static AssetEntity mapToNewAssetEntity(Asset asset, List<String> validLicenseTypes, String username) throws AssetModelValidationException {
        AssetEntity entity = new AssetEntity();
        Carrier carrier = new Carrier();

        if (asset.getSource() != null) {
            switch (asset.getSource()) {
                case NATIONAL:
                    carrier.setSource(CarrierSourceEnum.NATIONAL);
                    break;
                case XEU:
                    carrier.setSource(CarrierSourceEnum.XEU);
                    break;
                case THIRD_COUNTRY:
                    carrier.setSource(CarrierSourceEnum.THIRD_COUNTRY);
                    break;
                case INTERNAL:
                default:
                    carrier.setSource(CarrierSourceEnum.INTERNAL);
                    break;
            }
        } else {
            carrier.setSource(CarrierSourceEnum.INTERNAL);
        }

        carrier.setUpdatedBy(username);
        carrier.setUpdatetime(DateUtils.getNowDateUTC());

        entity.setCarrier(carrier);

        return mapToAssetEntity(entity, asset, validLicenseTypes, username);
    }

    public static AssetEntity mapToAssetEntity(AssetEntity entity, Asset asset, List<String> validLicenseTypes, String username) throws AssetModelValidationException {

        entity.getCarrier().setActive(asset.isActive());

        entity.setIRCS(asset.getIrcs());
        entity.setCFR(asset.getCfr());
        entity.setMMSI(asset.getMmsiNo());
        entity.setIMO(asset.getImo());
        entity.setIccat(asset.getIccat());
        entity.setUvi(asset.getUvi());
        entity.setGfcm(asset.getGfcm());

        entity.setAisIndicator(asset.isAisIndicator());
        entity.setErsIndicator(asset.isErsIndicator());
        entity.setVesselType(asset.getVesselType());
        entity.setVesselDateOfEntry(asset.getVesselDateOfEntry());
        entity.setHullMaterial(asset.getHullMaterial());
        entity.setConstructionYear(asset.getYearOfConstruction());
        entity.setIrcsIndicator(asset.getHasIrcs());
        entity.setRegistrationNumber(asset.getRegistrationNumber());
        entity.setHasVms(asset.isVmsIndicator());
        entity.setMainFishingGearCode(asset.getMainFishingGear());
        entity.setSubFishingGearCode(asset.getSubsidiaryFishingGear());
        entity.setCountryOfImportOrExport(asset.getCountryOfImportOrExport());
        entity.setPlaceOfConstruction(asset.getPlaceOfConstruction());

        if (asset.getSegment() != null) {
            entity.setSegment(SegmentFUP.valueOf(asset.getSegment()));
        }
        if (asset.getTypeOfExport() != null) {
            entity.setTypeOfExport(TypeOfExportEnum.valueOf(asset.getTypeOfExport()));
        }
        if (asset.getPublicAid() != null) {
            entity.setPublicAid(PublicAidEnum.valueOf(asset.getPublicAid()));
        }



        List<Notes> notesList = new ArrayList<>();
        for (AssetNotes notes : asset.getNotes()) {
            Notes notesEntity = null;
            if (notes.getId() != null) {
                notesEntity = findNotesEntity(notes.getId().longValue(), entity);
            }

            if (notesEntity == null){
                notesEntity = new Notes();
            }
            notesEntity = mapNotesModelToNotesEntity(entity, username, notes, notesEntity);

            notesList.add(notesEntity);
        }
        entity.setNotes(notesList);

        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setAsset(entity);
        assetHistory.setActive(true);

        AssetHistoryId history = asset.getEventHistory();

        EventCodeEnum eventCode;

        if (asset.getVesselEventType() == null) {
            eventCode = EventCodeEnum.MOD;
        } else if (history != null) {
            eventCode = EventCodeEnum.getType(history.getEventCode());
        } else {
            eventCode = EventCodeEnum.valueOf(asset.getVesselEventType().toString());
        }

        assetHistory.setEventCode(eventCode);

        Date dateOfEvent;
        if (asset.getDateOfEvent() == null) {
            dateOfEvent = DateUtils.getNowDateUTC();
        } else {
            dateOfEvent = asset.getDateOfEvent();
        }

        assetHistory.setDateOfEvent(dateOfEvent);

        //TODO set gear fishing type
        assetHistory.setType(GearFishingTypeEnum.getType(asset.getGearType()));

        assetHistory.setCountryOfRegistration(asset.getCountryCode());
        assetHistory.setExternalMarking(asset.getExternalMarking());

        assetHistory.setName(asset.getName());
        assetHistory.setPortOfRegistration(asset.getHomePort());

        assetHistory.setSafteyGrossTonnage(asset.getSafetyGrossTonnage());
        assetHistory.setPowerOfAuxEngine(asset.getPowerAux());
        assetHistory.setPowerOfMainEngine(asset.getPowerMain());
        assetHistory.setLengthBetweenPerpendiculars(asset.getLengthBetweenPerpendiculars());
        assetHistory.setOtherTonnage(asset.getOtherGrossTonnage());
        assetHistory.setGrossTonnage(asset.getGrossTonnage());
        assetHistory.setGrossTonnageUnit(UnitTonnage.getType(asset.getGrossTonnageUnit()));
        assetHistory.setLengthOverAll(asset.getLengthOverAll());
        assetHistory.setHasLicence(asset.isHasLicense());
        assetHistory.setIrcs(asset.getIrcs());
        assetHistory.setImo(asset.getImo());
        assetHistory.setMmsi(asset.getMmsiNo());
        assetHistory.setCfr(asset.getCfr());
        assetHistory.setIccat(asset.getIccat());
        assetHistory.setUvi(asset.getUvi());
        assetHistory.setGfcm(asset.getGfcm());

        assetHistory.setAisIndicator(asset.isAisIndicator());
        assetHistory.setErsIndicator(asset.isErsIndicator());
        assetHistory.setVesselType(asset.getVesselType());
        assetHistory.setVesselDateOfEntry(asset.getVesselDateOfEntry());
        assetHistory.setAssetIrcsindicator(asset.getHasIrcs());
        assetHistory.setHullMaterial(asset.getHullMaterial());
        assetHistory.setConstructionYear(asset.getYearOfConstruction());
        assetHistory.setRegistrationNumber(asset.getRegistrationNumber());
        assetHistory.setHasVms(asset.isVmsIndicator());
        assetHistory.setMainFishingGearCode(asset.getMainFishingGear());
        assetHistory.setSubsidiaryFishingGearCode(asset.getSubsidiaryFishingGear());
        assetHistory.setCountryOfImportOrExport(asset.getCountryOfImportOrExport());
        assetHistory.setPlaceOfConstruction(asset.getPlaceOfConstruction());

        if (asset.getSegment() != null) {
            assetHistory.setSegment(SegmentFUP.valueOf(asset.getSegment()));
        }
        if (asset.getTypeOfExport() != null) {
            assetHistory.setTypeOfExport(TypeOfExportEnum.valueOf(asset.getTypeOfExport()));
        }
        if (asset.getPublicAid() != null) {
            assetHistory.setPublicAid(PublicAidEnum.valueOf(asset.getPublicAid()));
        }

        List<AssetContact> contacts = asset.getContact();
        if (contacts != null) {
            if (assetHistory.getContactInfo() == null) {
                assetHistory.setContactInfo(new ArrayList<ContactInfo>());
            }
            for (AssetContact contact : contacts) {
                ContactInfo contactInfo = new ContactInfo();
                contactInfo.setAsset(assetHistory);
                contactInfo.setEmail(contact.getEmail());
                contactInfo.setName(contact.getName());
                contactInfo.setOwner(contact.isOwner());
                contactInfo.setPhoneNumber(contact.getNumber());
                contactInfo.setNationality(contact.getNationality());
                contactInfo.setType(contact.getType());
                contactInfo.setCountryCode(contact.getCountryCode());
                contactInfo.setPostOfficeBox(contact.getPostOfficeBox());
                contactInfo.setCityName(contact.getCityName());
                contactInfo.setPostalCode(contact.getPostalCode());
                contactInfo.setStreetName(contact.getStreetName());

                if (contact.getSource() != null) {
                    contactInfo.setSource(ContactInfoSourceEnum.valueOf(contact.getSource().toString()));
                } else {
                    contactInfo.setSource(ContactInfoSourceEnum.NATIONAL);
                }
                contactInfo.setUpdatedBy(username);
                contactInfo.setUpdateTime(DateUtils.getNowDateUTC());
                assetHistory.getContactInfo().add(contactInfo);
            }
        }

        if(asset.getProducer() != null && asset.getProducer().getId() > 0) {
            AssetProdOrg assetProdOrg = new AssetProdOrg();
            assetProdOrg.setId(asset.getProducer().getId());
            assetProdOrg.setCode(asset.getProducer().getCode());
            assetProdOrg.setName(asset.getProducer().getName());
            assetProdOrg.setAddress(asset.getProducer().getAddress());
            assetProdOrg.setCity(asset.getProducer().getCity());
            assetProdOrg.setZipCode(asset.getProducer().getZipcode());
            assetProdOrg.setPhone(asset.getProducer().getPhone());
            assetProdOrg.setMobile(asset.getProducer().getMobile());
            assetProdOrg.setFax(asset.getProducer().getFax());
            assetHistory.setAssetProdOrg(assetProdOrg);
        }

        assetHistory.setLicenceType(checkValidType(asset.getLicenseType(), validLicenseTypes, "License type not found in configured types"));

        assetHistory.setUpdateTime(DateUtils.getNowDateUTC());
        assetHistory.setUpdatedBy(username);

        List<AssetHistory> histories = entity.getHistories();
        if (histories == null) {
            histories = new ArrayList<>();
        } else {
            for (AssetHistory historyEntity : histories) {
                historyEntity.setActive(Boolean.FALSE);
            }
        }

        histories.add(assetHistory);

        entity.setHistories(histories);
        entity.setUpdateTime(DateUtils.getNowDateUTC());
        entity.setUpdatedBy(username);

        entity.setIccat(asset.getIccat());
        entity.setUvi(asset.getUvi());
        entity.setGfcm(asset.getGfcm());

        return entity;
    }

    private static Notes findNotesEntity(Long id, AssetEntity asset) {
        for (Notes note : asset.getNotes()) {
            if (note.getId() != null && note.getId().equals(id)) {
                return note;
            }
        }
        return null;
    }

    private static Notes mapNotesModelToNotesEntity(AssetEntity entity, String username, AssetNotes notes, Notes notesEntity) {
        notesEntity.setUpdateTime(DateUtils.getNowDateUTC());
        notesEntity.setUpdatedBy(username);
        notesEntity.setActivity(notes.getActivity());
        notesEntity.setAsset(entity);
        notesEntity.setContact(notes.getContact());
        notesEntity.setDate(DateUtil.parseToUTCDate(notes.getDate()));
        notesEntity.setDocument(notes.getDocument());
        notesEntity.setLicenseHolder(notes.getLicenseHolder());
        notesEntity.setNotes(notes.getNotes());
        notesEntity.setReadyDate(DateUtil.parseToUTCDate(notes.getReadyDate()));
        notesEntity.setContact(notes.getContact());
        notesEntity.setSheetNumber(notes.getSheetNumber());
        notesEntity.setUser(notes.getUser());
        if (notes.getSource() != null) {
            notesEntity.setSource(NotesSourceEnum.valueOf(notes.getSource().toString()));
        }
        return notesEntity;
    }

    private static String checkValidType(String type, List<String> types, String errorMsg) throws AssetModelValidationException {
        if (type == null) {
            return null;
        }
        if (types != null) {
            for (String validType : types) {
                if (validType.equalsIgnoreCase(type)) {
                    return type;
                }
            }
        }
        throw new AssetModelValidationException(errorMsg);
    }

    public static FishingGear mapFishingGearToEntity(eu.europa.ec.fisheries.wsdl.asset.types.FishingGear fishingGear, String username){
        FishingGear entity = new FishingGear();
        entity.setCode(fishingGear.getCode());
        entity.setDescription(fishingGear.getDescription());
        entity.setExternalId(fishingGear.getExternalId());
        entity.setMobility(FishingGearMobilityEnum.STATIONARY);
        entity.setUpdateTime(DateUtils.getNowDateUTC());
        entity.setUpdatedBy(username);
        return entity;
    }

    public static FishingGearType mapFishingGearTypeToEntity(eu.europa.ec.fisheries.wsdl.asset.types.FishingGearType fishingGearType, String username ){
        FishingGearType entity = new FishingGearType();
        entity.setDescription(fishingGearType.getName());
        entity.setCode(fishingGearType.getCode());
        entity.setUpdateDateTime(DateUtils.getNowDateUTC());
        entity.setUpdateUser(username);
        return entity;
    }
}