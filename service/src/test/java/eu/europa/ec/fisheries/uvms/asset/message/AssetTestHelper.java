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
package eu.europa.ec.fisheries.uvms.asset.message;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

import eu.europa.ec.fisheries.wsdl.asset.types.*;

public class AssetTestHelper {

    public static Asset createBasicAsset() {
        String cfr = "CFR" + getRandomIntegers(7);
        Asset asset = new Asset();
        AssetId assetId = new AssetId();
        assetId.setType(AssetIdType.CFR);
        assetId.setValue(cfr);
        asset.setAssetId(assetId);

        asset.setActive(true);

        asset.setSource(CarrierSource.INTERNAL);
        asset.setName("Ship" + getRandomIntegers(10));
        asset.setCountryCode("SWE");
        asset.setGearType("DERMERSAL");
        asset.setHasIrcs("Y");

        asset.setIrcs("I" + getRandomIntegers(7));
        asset.setExternalMarking("EXT3");

        asset.setCfr(cfr);

        asset.setImo("0" + getRandomIntegers(6));
        String mmsi = getRandomIntegers(9);
        asset.setMmsiNo(mmsi);
        asset.setHasLicense(true);
        asset.setLicenseType("MOCK-license-DB");
        asset.setHomePort("TEST_GOT");
        asset.setLengthOverAll(new BigDecimal(15l));
        asset.setLengthBetweenPerpendiculars(new BigDecimal(3l));
        asset.setGrossTonnage(new BigDecimal(200));

        asset.setGrossTonnageUnit("OSLO");
        asset.setSafetyGrossTonnage(new BigDecimal(80));
        asset.setPowerMain(new BigDecimal(10));
        asset.setPowerAux(new BigDecimal(10));

        AssetProdOrgModel assetProdOrgModel = new AssetProdOrgModel();
        assetProdOrgModel.setName("NAME" + getRandomIntegers(10));
        assetProdOrgModel.setCode("CODE" + getRandomIntegers(10));
        asset.setProducer(assetProdOrgModel);
        
        asset.getNotes().add(createBasicNote());
        asset.getNotes().add(createBasicNote());

        asset.getContact().add(createBasicContact());
        asset.getContact().add(createBasicContact());
        return asset;
    }
    
    public static AssetNotes createBasicNote() {
        AssetNotes note = new AssetNotes();
        note.setDate(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        note.setActivity("Activity");
        note.setContact("Contact: " + getRandomIntegers(5));
        note.setNotes("Notes: " + getRandomIntegers(10));
        return note;
    }
    
    public static AssetContact createBasicContact() {
        AssetContact contact = new AssetContact();
        contact.setName("Contact: " + getRandomIntegers(5));
        contact.setEmail(getRandomIntegers(10) + "@mail.com");
        contact.setSource(ContactSource.NATIONAL);
        return contact;
    }
    
    public static AssetListQuery createBasicAssetQuery() {
        AssetListQuery assetListQuery = new AssetListQuery();
        AssetListPagination assetListPagination = new AssetListPagination();
        assetListPagination.setListSize(1000);
        assetListPagination.setPage(1);
        assetListQuery.setPagination(assetListPagination);
        AssetListCriteria assetListCriteria = new AssetListCriteria();
        assetListCriteria.setIsDynamic(true);
        assetListQuery.setAssetSearchCriteria(assetListCriteria);
        return assetListQuery;
    }
    
    public static String getRandomIntegers(int length) {
        return new Random()
                .ints(0,9)
                .mapToObj(i -> String.valueOf(i))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
    
}
