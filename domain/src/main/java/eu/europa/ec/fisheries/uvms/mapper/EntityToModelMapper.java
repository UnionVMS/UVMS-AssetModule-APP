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

import eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.entity.model.*;
import eu.europa.ec.fisheries.uvms.util.DateUtil;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGear;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGearType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class EntityToModelMapper {

    private static final Logger LOG = LoggerFactory.getLogger(EntityToModelMapper.class);

    private static Asset toAssetFromAssetAndEntity(Asset asset, AssetEntity entity) {

        if (asset == null) {
            asset = new Asset();
        }

        AssetId assetId = new AssetId();
        assetId.setValue(entity.getGuid());
        assetId.setType(AssetIdType.GUID);
        assetId.setGuid(entity.getGuid());
        asset.setAssetId(assetId);

        asset.setActive(entity.getCarrier().getActive());

        switch (entity.getCarrier().getSource()) {
            case NATIONAL:
                asset.setSource(CarrierSource.NATIONAL);
                break;
            case XEU:
                asset.setSource(CarrierSource.XEU);
                break;
            case THIRD_COUNTRY:
                asset.setSource(CarrierSource.THIRD_COUNTRY);
                break;
            case INTERNAL:
            default:
                asset.setSource(CarrierSource.INTERNAL);
                break;
        }

        asset.setCfr(entity.getCFR());
        asset.setMmsiNo(entity.getMMSI());
        asset.setHasIrcs(entity.getIrcsIndicator());
        asset.setImo(entity.getIMO());
        asset.setIrcs(entity.getIRCS());
        asset.setIccat(entity.getIccat());
        asset.setUvi(entity.getUvi());
        asset.setGfcm(entity.getGfcm());

        if (entity.getNotes() != null) {
            List<AssetNotes> noteList = new ArrayList<>();
            for (Notes notes : entity.getNotes()) {
                AssetNotes assetNotes = new AssetNotes();
                assetNotes.setId(BigInteger.valueOf(notes.getId()));
                assetNotes.setDate(DateUtil.parseUTCDateToString(notes.getDate()));
                assetNotes.setActivity(notes.getActivity());
                assetNotes.setContact(notes.getContact());
                assetNotes.setDocument(notes.getDocument());
                assetNotes.setLicenseHolder(notes.getLicenseHolder());
                assetNotes.setNotes(notes.getNotes());
                assetNotes.setReadyDate(DateUtil.parseUTCDateToString(notes.getReadyDate()));
                assetNotes.setSheetNumber(notes.getSheetNumber());
                assetNotes.setSource(NoteSource.valueOf(notes.getSource().toString()));
                assetNotes.setUser(notes.getUser());
                noteList.add(assetNotes);
            }
            asset.getNotes().clear();
            asset.getNotes().addAll(noteList);
        }

        return asset;
    }

    private static void toAssetFromAssetHistory(Asset asset, AssetHistory historyEntity) {

        if (historyEntity != null) {
            AssetHistoryId assetHistoryId = new AssetHistoryId();

            EventCode eventCode = EventCodeEnum.getModel(historyEntity.getEventCode());
            assetHistoryId.setEventCode(eventCode);
            assetHistoryId.setEventDate(historyEntity.getDateOfEvent());


            if (historyEntity.getGuid() != null) {
                assetHistoryId.setEventId(historyEntity.getGuid());
            }

            asset.setEventHistory(assetHistoryId);

            asset.setCountryCode(historyEntity.getCountryOfRegistration());
            asset.setExternalMarking(historyEntity.getExternalMarking());
            asset.setHasLicense(historyEntity.getHasLicence());
            asset.setName(historyEntity.getName());
            asset.setHomePort(historyEntity.getPortOfRegistration());
            asset.setGrossTonnage(historyEntity.getGrossTonnage());
            asset.setGrossTonnageUnit(historyEntity.getGrossTonnageUnit().name());
            asset.setLengthBetweenPerpendiculars(historyEntity.getLengthBetweenPerpendiculars());
            asset.setLengthOverAll(historyEntity.getLengthOverAll());
            asset.setOtherGrossTonnage(historyEntity.getOtherTonnage());
            asset.setPowerAux(historyEntity.getPowerOfAuxEngine());
            asset.setPowerMain(historyEntity.getPowerOfMainEngine());
            asset.setSafetyGrossTonnage(historyEntity.getSafteyGrossTonnage());
            asset.setIrcs(historyEntity.getIrcs());
            asset.setCfr(historyEntity.getCfr());
            asset.setMmsiNo(historyEntity.getMmsi());
            asset.setImo(historyEntity.getImo());
            asset.setIccat(historyEntity.getIccat());
            asset.setUvi(historyEntity.getUvi());
            asset.setGfcm(historyEntity.getGfcm());


            if (historyEntity.getContactInfo() == null) {
                historyEntity.setContactInfo(new ArrayList<ContactInfo>());
            }
            for (ContactInfo contactInfo : historyEntity.getContactInfo()) {
                AssetContact contact = new AssetContact();

                contact.setName(contactInfo.getName());
                contact.setNumber(contactInfo.getPhoneNumber());
                contact.setEmail(contactInfo.getEmail());
                contact.setOwner(contactInfo.getOwner());

                if (contactInfo.getSource() != null) {
                    contact.setSource(ContactSource.valueOf(contactInfo.getSource().toString()));
                } else {
                    contact.setSource(ContactSource.NATIONAL);
                }
                asset.getContact().add(contact);
            }

            asset.setLicenseType(historyEntity.getLicenceType());
            if (historyEntity.getType() != null) {
                asset.setGearType(historyEntity.getType().name());
            }

            if(historyEntity.getAssetProdOrg() != null) {
                AssetProdOrgModel assetProdOrgModel = new AssetProdOrgModel();
                assetProdOrgModel.setId(historyEntity.getAssetProdOrg().getId());
                assetProdOrgModel.setCode(historyEntity.getAssetProdOrg().getCode());
                assetProdOrgModel.setName(historyEntity.getAssetProdOrg().getName());
                assetProdOrgModel.setAddress(historyEntity.getAssetProdOrg().getAddress());
                assetProdOrgModel.setCity(historyEntity.getAssetProdOrg().getCity());
                assetProdOrgModel.setZipcode(historyEntity.getAssetProdOrg().getZipCode());
                assetProdOrgModel.setPhone(historyEntity.getAssetProdOrg().getPhone());
                assetProdOrgModel.setMobile(historyEntity.getAssetProdOrg().getMobile());
                assetProdOrgModel.setFax(historyEntity.getAssetProdOrg().getFax());
                asset.setProducer(assetProdOrgModel);
            }
        }
    }

    public static Asset toAssetFromEntity(AssetEntity entity) {
        Asset asset = toAssetFromAssetAndEntity(null, entity);
        List<AssetHistory> historyList = entity.getHistories();

        if (historyList != null && !historyList.isEmpty()) {
            for (AssetHistory history : historyList) {
                if (history.getActive()) {
                    toAssetFromAssetHistory(asset, history);
                }
            }
        }
        return asset;
    }

    public static List<Asset> toAssetHistoryList(AssetEntity assetHistory, Integer maxNbr) {

        List<Asset> assets = new ArrayList<>();
        if (assetHistory == null) {
            return assets;
        }

        List<AssetHistory> historyList = assetHistory.getHistories();

        //TODO use DateUtil to compare update and/or event time instead
        if (maxNbr != null) {
            if (historyList != null && !historyList.isEmpty()) {
                if (historyList.size() > maxNbr) {
                    Collections.sort(historyList, new Comparator<AssetHistory>() {
                        @Override
                        public int compare(AssetHistory o1, AssetHistory o2) {
                            if (o1.getUpdateTime() == null && o2.getUpdateTime() == null) {
                                return 0;
                            } else if (o2.getUpdateTime() == null) {
                                return 1;
                            } else if (o1.getUpdateTime() == null) {
                                return -1;
                            } else {
                                return (o1.getUpdateTime().compareTo(o2.getUpdateTime())>0 ? -1 : 1);
                            }
                        }
                    });

                    historyList = historyList.subList(0, maxNbr);
                }
            }
        }

        if (historyList != null) {
            for (AssetHistory history : historyList) {
                Asset asset = toAssetFromAssetAndEntity(null, assetHistory);
                toAssetFromAssetHistory(asset, history);
                assets.add(asset);
            }
        }

        return assets;
    }

    public static Asset toAssetFromAssetHistory(AssetHistory assetHistory) {
        Asset asset = toAssetFromAssetAndEntity(null, assetHistory.getAsset());
        toAssetFromAssetHistory(asset, assetHistory);
        return asset;
    }


    public static List<NumberOfAssetsGroupByFlagState> mapEntityToNumberOfAssetsGroupByFlagState(List<AssetHistory> assetHistories) {
        Map<String, Integer> mapNumberOfAsset = new HashMap<>();
        for (AssetHistory entity : assetHistories) {
            String countryCode = entity.getCountryOfRegistration();
            if (mapNumberOfAsset.containsKey(countryCode)) {
                Integer numberOfAssets = mapNumberOfAsset.get(countryCode);
                numberOfAssets++;
                mapNumberOfAsset.put(countryCode, numberOfAssets);

            } else {
                mapNumberOfAsset.put(countryCode, 1);
            }
        }
        Iterator<Map.Entry<String, Integer>> iterator = mapNumberOfAsset.entrySet().iterator();
        List<NumberOfAssetsGroupByFlagState> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> next = iterator.next();
            NumberOfAssetsGroupByFlagState numberOfAssetsGroupByFlagState = new NumberOfAssetsGroupByFlagState();
            numberOfAssetsGroupByFlagState.setFlagState(next.getKey());
            numberOfAssetsGroupByFlagState.setNumberOfAssets(next.getValue());
            list.add(numberOfAssetsGroupByFlagState);
        }
        return list;
    }

    public static FishingGear mapEntityToFishingGear(eu.europa.ec.fisheries.uvms.entity.model.FishingGear entity){
        FishingGear fishingGear = new FishingGear();
        fishingGear.setName(entity.getDescription());
        fishingGear.setId(entity.getId());
        fishingGear.setDescription(entity.getDescription());
        fishingGear.setCode(entity.getCode());
        fishingGear.setExternalId(entity.getExternalId());
        fishingGear.setFishingGearType(mapEntityToFishingGearType(entity.getFishingGearType()));
        return fishingGear;
    }

    public static FishingGearType mapEntityToFishingGearType(eu.europa.ec.fisheries.uvms.entity.model.FishingGearType entity){
        FishingGearType fishingGearType = new FishingGearType();
        fishingGearType.setCode(entity.getCode());
        fishingGearType.setId(entity.getId());
        fishingGearType.setName(entity.getName());
        return fishingGearType;
    }

    public static NoteActivityCode mapEntityToNoteActivityCode(List<NotesActivityCode> entities) {
        NoteActivityCode codes = new NoteActivityCode();
        for (NotesActivityCode entity : entities) {
            codes.getCode().add(entity.getId());
        }

        return codes;
    }

}