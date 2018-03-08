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
package eu.europa.ec.fisheries.wsdl.asset.mockdata;

import eu.europa.ec.fisheries.uvms.asset.types.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.asset.enums.CarrierSourceEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 **/
public class MockData {

    public static AssetDTO getAssetDto(Integer id) {
        AssetDTO dto = new AssetDTO();

        dto.setCfr("CFR" + id);
        dto.setCountryCode("SWE" + id);
        dto.setExternalMarking("MARKING" + 1);
        dto.setGrossTonnage(BigDecimal.valueOf(1.2));
        dto.setHasIrcs("Y");
        dto.setHasLicense(true);
        dto.setHomePort("PORT" + id);

        AssetId assetId = new AssetId();
        assetId.setValue(id.toString());
        dto.setAssetId(assetId);
        dto.setIrcs("IRCS-" + id);
        dto.setLengthBetweenPerpendiculars(BigDecimal.valueOf(0.5 + id));
        dto.setLengthOverAll(BigDecimal.valueOf(2.5 + id));
        dto.setName("ASSET-" + id);
        dto.setOtherGrossTonnage(BigDecimal.valueOf(11.5 + id));
        dto.setPowerAux(BigDecimal.valueOf(123.4 + id));
        dto.setPowerMain(BigDecimal.valueOf(586.2 + id));
        dto.setSafetyGrossTonnage(BigDecimal.valueOf(54.3 + id));
        dto.setSource(CarrierSourceEnum.INTERNAL);
        dto.setActive(true);

        /*if (id % 3 == 0) {
         dto.setSource(CarrierSource.EU);
         dto.setActive(true);
         }
         if (id % 2 == 0) {
         dto.setSource(CarrierSource.NATIONAL);
         dto.setActive(false);
         }
         if (id % 5 == 0) {
         dto.setSource(CarrierSource.THIRD_COUNTRY);
         dto.setActive(true);

         dto.setAssetType("VESSEL-TYPE: " + id);
         }*/
        dto.setGearType("ASSET-TYPE: " + id);
        return dto;
    }

    public static List<AssetDTO> getAssetDtoList(Integer amount) {
        List<AssetDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            dtoList.add(getAssetDto(i));
        }
        return dtoList;
    }

}