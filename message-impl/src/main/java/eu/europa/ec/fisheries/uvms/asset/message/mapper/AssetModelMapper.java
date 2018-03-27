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

import java.util.stream.Collectors;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;
import eu.europa.ec.fisheries.uvms.constant.AssetIdentity;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

public class AssetModelMapper {

    private AssetModelMapper() {}
    
    public static Asset toAssetEntity(eu.europa.ec.fisheries.wsdl.asset.types.Asset assetModel) {
        return new Asset();
    }
    
    public static eu.europa.ec.fisheries.wsdl.asset.types.Asset toAssetModel(Asset assetEntity) {
        return new eu.europa.ec.fisheries.wsdl.asset.types.Asset();
    }
    
    public static AssetGroup toAssetGroupEntity(eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup assetGroupModel) {
        return new AssetGroup();
    }
    
    public static eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup toAssetGroupModel(AssetGroup assetGroupEntity) {
        return new eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup();
    }
    
    public static AssetIdentity mapToAssetIdentity(AssetIdType assetIdType) {
        switch (assetIdType) {
            case CFR:
                return AssetIdentity.CFR;
            case GFCM:
                return AssetIdentity.GFCM;
            case GUID:
                return AssetIdentity.GUID;
            case ICCAT:
                return AssetIdentity.ICCAT;
            case IMO:
                return AssetIdentity.IMO;
            case IRCS:
                return AssetIdentity.IRCS;
            case MMSI:
                return AssetIdentity.MMSI;
            case UVI:
                return AssetIdentity.UVI;
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
