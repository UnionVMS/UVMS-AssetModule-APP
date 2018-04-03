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
package eu.europa.ec.fisheries.uvms.asset.message.mapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Collectors;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetProdOrgModel;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.EventCode;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

public class AssetModelMapper {

    private AssetModelMapper() {}
    
    public static Asset toAssetEntity(eu.europa.ec.fisheries.wsdl.asset.types.Asset assetModel) {
        Asset asset = new Asset();
        if (assetModel.getAssetId() != null && assetModel.getAssetId().getGuid() != null) {
            asset.setId(UUID.fromString(assetModel.getAssetId().getGuid()));
        }
        asset.setActive(assetModel.isActive());
        if (asset.getSource() != null) {
            asset.setSource(assetModel.getSource().toString());
        }
        if (assetModel.getEventHistory() != null) {
            asset.setHistoryId(UUID.fromString(assetModel.getEventHistory().getEventId()));
            asset.setUpdateTime(LocalDateTime.ofInstant(assetModel.getEventHistory().getEventDate().toInstant(),ZoneOffset.UTC));
            asset.setEventCode(assetModel.getEventHistory().getEventCode().toString());
        }
        asset.setName(assetModel.getName());
        asset.setFlagStateCode(assetModel.getCountryCode());
        asset.setMainFishingGearCode(assetModel.getGearType());
        asset.setIrcsIndicator(assetModel.getHasIrcs().equals("Y") ? true : false);
        asset.setIrcs(assetModel.getIrcs());
        asset.setExternalMarking(assetModel.getExternalMarking());
        asset.setCfr(assetModel.getCfr());
        asset.setImo(assetModel.getImo());
        asset.setMmsi(assetModel.getMmsiNo());
        asset.setHasLicence(assetModel.isHasLicense());
        asset.setPortOfRegistration(assetModel.getHomePort());
        asset.setLengthOverAll(assetModel.getLengthOverAll().doubleValue());
        asset.setLengthBetweenPerpendiculars(assetModel.getLengthBetweenPerpendiculars().doubleValue());
        asset.setGrossTonnage(assetModel.getGrossTonnage().doubleValue());
        asset.setGrossTonnageUnit(UnitTonnage.getType(assetModel.getGrossTonnageUnit()));
        asset.setOtherTonnage(assetModel.getOtherGrossTonnage().doubleValue());
        asset.setSafteyGrossTonnage(assetModel.getSafetyGrossTonnage().doubleValue());
        asset.setPowerOfMainEngine(assetModel.getPowerMain().doubleValue());
        asset.setPowerOfAuxEngine(assetModel.getPowerAux().doubleValue());
        if (assetModel.getProducer() != null) {
            asset.setProdOrgCode(assetModel.getProducer().getCode());
            asset.setProdOrgName(assetModel.getProducer().getName());
        }
        // TODO populate Notes and Contacts. Create bean and inject services
        asset.setIccat(assetModel.getIccat());
        asset.setUvi(assetModel.getUvi());
        asset.setGfcm(assetModel.getGfcm());
        
        return asset;
    }
    
    public static eu.europa.ec.fisheries.wsdl.asset.types.Asset toAssetModel(Asset assetEntity) {
        eu.europa.ec.fisheries.wsdl.asset.types.Asset assetModel = new eu.europa.ec.fisheries.wsdl.asset.types.Asset();
        
        AssetId assetId = new AssetId();
        assetId.setGuid(assetEntity.getId().toString());
        assetModel.setAssetId(assetId);
        assetModel.setActive(assetEntity.getActive());
        if (assetEntity.getSource() !=  null && !assetEntity.getSource().isEmpty()) {
            assetModel.setSource(CarrierSource.fromValue(assetEntity.getSource()));
        }
        AssetHistoryId assetHistory = new AssetHistoryId();
        assetHistory.setEventId(assetEntity.getHistoryId().toString());
        assetHistory.setEventDate(Date.from(assetEntity.getUpdateTime().toInstant(ZoneOffset.UTC)));
        if (assetEntity.getEventCode() != null && !assetEntity.getEventCode().isEmpty()) {
            assetHistory.setEventCode(EventCode.fromValue(assetEntity.getEventCode()));
        }
        assetModel.setEventHistory(assetHistory);
        assetModel.setName(assetEntity.getName());
        assetModel.setCountryCode(assetEntity.getFlagStateCode());
        assetModel.setGearType(assetEntity.getMainFishingGearCode());
        assetModel.setHasIrcs(assetEntity.getIrcsIndicator() ? "Y" : "N");
        assetModel.setIrcs(assetEntity.getIrcs());
        assetModel.setExternalMarking(assetEntity.getExternalMarking());
        assetModel.setCfr(assetEntity.getCfr());
        assetModel.setImo(assetEntity.getImo());
        assetModel.setMmsiNo(assetEntity.getMmsi());
        assetModel.setHasLicense(assetEntity.getHasLicence());
        assetModel.setHomePort(assetEntity.getPortOfRegistration());
        assetModel.setLengthOverAll(new BigDecimal(assetEntity.getLengthOverAll()));
        assetModel.setLengthBetweenPerpendiculars(new BigDecimal(assetEntity.getLengthBetweenPerpendiculars()));
        assetModel.setGrossTonnage(new BigDecimal(assetEntity.getGrossTonnage()));
        if (assetEntity.getGrossTonnageUnit() != null) {
            assetModel.setGrossTonnageUnit(assetEntity.getGrossTonnageUnit().toString());
        }
        assetModel.setOtherGrossTonnage(new BigDecimal(assetEntity.getOtherTonnage()));
        assetModel.setSafetyGrossTonnage(new BigDecimal(assetEntity.getSafteyGrossTonnage()));
        assetModel.setPowerMain(new BigDecimal(assetEntity.getPowerOfMainEngine()));
        assetModel.setPowerAux(new BigDecimal(assetEntity.getPowerOfAuxEngine()));
        AssetProdOrgModel prodOrg = new AssetProdOrgModel();
        prodOrg.setCode(assetEntity.getProdOrgCode());
        prodOrg.setName(assetEntity.getProdOrgName());
        assetModel.setProducer(prodOrg);
//        // TODO populate Notes and Contacts. Create bean and inject services
        assetModel.setIccat(assetEntity.getIccat());
        assetModel.setUvi(assetEntity.getUvi());
        assetModel.setGfcm(assetEntity.getGfcm());
        
        return assetModel;
    }
    
    public static AssetGroup toAssetGroupEntity(eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup assetGroupModel) {
        AssetGroup assetGroup = new AssetGroup();
        assetGroup.setId(UUID.fromString(assetGroupModel.getGuid()));
        assetGroup.setName(assetGroupModel.getName());
        assetGroup.setOwner(assetGroupModel.getUser());
        assetGroup.setDynamic(assetGroupModel.isDynamic());
        assetGroup.setGlobal(assetGroupModel.isGlobal());
        // TODO populate group fields
        return assetGroup;
    }
    
    public static eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup toAssetGroupModel(AssetGroup assetGroupEntity) {
        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup assetGroupModel = new eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup();
        assetGroupModel.setGuid(assetGroupEntity.getId().toString());
        assetGroupModel.setName(assetGroupEntity.getName());
        assetGroupModel.setUser(assetGroupEntity.getOwner());
        assetGroupModel.setDynamic(assetGroupEntity.getDynamic());
        assetGroupModel.setGlobal(assetGroupEntity.getGlobal());
        // TODO populate group fields
        return assetGroupModel;
    }
    
    public static AssetIdentifier mapToAssetIdentity(AssetIdType assetIdType) {
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
    
    public static ListAssetResponse toListAssetResponse(AssetListResponse assetListResponse) {
        ListAssetResponse listAssetResponse = new ListAssetResponse();
        listAssetResponse.setCurrentPage(assetListResponse.getCurrentPage());
        listAssetResponse.setTotalNumberOfPages(assetListResponse.getTotalNumberOfPages());
        listAssetResponse.getAsset().addAll(assetListResponse.getAssetList().stream()
                                                        .map(AssetModelMapper::toAssetModel)
                                                        .collect(Collectors.toList()));
        return listAssetResponse;
    }
}
