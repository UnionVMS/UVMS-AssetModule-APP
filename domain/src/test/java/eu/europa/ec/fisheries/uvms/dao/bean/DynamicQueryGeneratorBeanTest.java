/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2020.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.dao.bean.DynamicQueryGeneratorBean;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DynamicQueryGeneratorBeanTest {

    @Test
    public void dynamicQueryGeneratorShouldMapAllValues() {
        DynamicQueryGeneratorBean dynamicQueryGenerator = new DynamicQueryGeneratorBean();
        dynamicQueryGenerator.init();
        String jpql = dynamicQueryGenerator.findAssetGroupByAssetAndHistory();
        Stream.of(SearchFields.values()).forEach( s -> assertTrue(jpql.contains(s.toString())));
    }

    @Test
    public void searchFieldValueMapper() {

        DynamicQueryGeneratorBean bean = new DynamicQueryGeneratorBean();
        AssetEntity asset = new AssetEntity();
        asset.setIRCS("IRCS0");
        asset.setCFR("CYP123456789");
        asset.setMMSI("MMSIO");
        asset.setIMO("IMO0");
        asset.setGuid("1234-5678-90");
        asset.setIccat("ICCAT0");
        asset.setUvi("UVI0");
        asset.setGfcm("GFCM0");

        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setCountryOfRegistration("GRC");
        assetHistory.setExternalMarking("EXT0");
        assetHistory.setName("HISTORY0");
        assetHistory.setPortOfRegistration("PORT0");
        assetHistory.setLicenceType("LICENCE0");
        assetHistory.setGuid("7777-8888-99");
        assetHistory.setType(GearFishingTypeEnum.UNKNOWN);
        assetHistory.setLengthOverAll(BigDecimal.ONE);
        assetHistory.setPowerOfMainEngine(BigDecimal.TEN);

        Map<SearchFields, String> searchFieldsStringMap = bean.searchFieldValueMapper(asset, assetHistory);
        assertEquals(searchFieldsStringMap.get(SearchFields.IRCS),asset.getIRCS());
        assertEquals(searchFieldsStringMap.get(SearchFields.CFR),asset.getCFR());
        assertEquals(searchFieldsStringMap.get(SearchFields.MMSI),asset.getMMSI());
        assertEquals(searchFieldsStringMap.get(SearchFields.IMO),asset.getIMO());
        assertEquals(searchFieldsStringMap.get(SearchFields.GUID),asset.getGuid());
        assertEquals(searchFieldsStringMap.get(SearchFields.ICCAT),asset.getIccat());
        assertEquals(searchFieldsStringMap.get(SearchFields.UVI),assetHistory.getUvi());
        assertEquals(searchFieldsStringMap.get(SearchFields.GFCM),asset.getGfcm());

        assertEquals(searchFieldsStringMap.get(SearchFields.FLAG_STATE),assetHistory.getCountryOfRegistration());
        assertEquals(searchFieldsStringMap.get(SearchFields.EXTERNAL_MARKING),assetHistory.getExternalMarking());
        assertEquals(searchFieldsStringMap.get(SearchFields.NAME),assetHistory.getName());
        assertEquals(searchFieldsStringMap.get(SearchFields.HOMEPORT),assetHistory.getPortOfRegistration());
        assertEquals(searchFieldsStringMap.get(SearchFields.LICENSE),assetHistory.getLicenceType());
        assertEquals(searchFieldsStringMap.get(SearchFields.HIST_GUID),assetHistory.getGuid());
        assertEquals(searchFieldsStringMap.get(SearchFields.GEAR_TYPE),String.valueOf(assetHistory.getType()));
        assertEquals(searchFieldsStringMap.get(SearchFields.MIN_LENGTH),assetHistory.getLengthOverAll().toString());
        assertEquals(searchFieldsStringMap.get(SearchFields.MAX_LENGTH),assetHistory.getLengthOverAll().toString());
        assertEquals(searchFieldsStringMap.get(SearchFields.MIN_POWER),assetHistory.getPowerOfMainEngine().toString());
        assertEquals(searchFieldsStringMap.get(SearchFields.MAX_POWER),assetHistory.getPowerOfMainEngine().toString());
    }
}