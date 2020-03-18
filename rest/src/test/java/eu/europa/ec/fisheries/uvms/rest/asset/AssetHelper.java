package eu.europa.ec.fisheries.uvms.rest.asset;

import eu.europa.ec.fisheries.uvms.asset.model.constants.UnitTonnage;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

import java.time.Instant;
import java.util.Random;

public abstract class AssetHelper {

    private static Random rnd = new Random();

    public static Asset createBasicAsset() {
        Asset assetEntity = new Asset();

        assetEntity.setName("Test asset");
        assetEntity.setActive(true);
        assetEntity.setExternalMarking("EXT123");
        assetEntity.setFlagStateCode("SWE");

        assetEntity.setCommissionDate(Instant.now());
        assetEntity.setCfr("CRF" + getRandomIntegers(9));
        assetEntity.setIrcs("F" + getRandomIntegers(7));
        assetEntity.setImo(getRandomIntegers(7));
        assetEntity.setMmsi("MMSI" + getRandomIntegers(5));
        assetEntity.setIccat("ICCAT" + getRandomIntegers(20));
        assetEntity.setUvi("UVI" + getRandomIntegers(20));
        assetEntity.setGfcm("GFCM" + getRandomIntegers(20));
        
        assetEntity.setGrossTonnage(10d);
        assetEntity.setPowerOfMainEngine(10d);
        
        assetEntity.setGearFishingType("Demersal");

        assetEntity.setOwnerName("Foo Bar");
        assetEntity.setOwnerAddress("Hacker st. 1337");

        assetEntity.setProdOrgCode("ORGCODE");
        assetEntity.setProdOrgName("ORGNAME");

        return assetEntity;
    }

    public static Asset createBiggerAsset() {

        Asset assetEntity = new Asset();
        Instant  now =  Instant.now();

        assetEntity.setName("Test asset");
        assetEntity.setActive(true);
        assetEntity.setExternalMarking("EXT123");
        assetEntity.setFlagStateCode("SWE");

        assetEntity.setCommissionDate(Instant.now());
        assetEntity.setCfr("CRF" + getRandomIntegers(9));
        assetEntity.setIrcs("F" + getRandomIntegers(7));
        assetEntity.setImo(getRandomIntegers(7));
        assetEntity.setMmsi("MMSI" + getRandomIntegers(5));
        assetEntity.setIccat("ICCAT" + getRandomIntegers(20));
        assetEntity.setUvi("UVI" + getRandomIntegers(20));
        assetEntity.setGfcm("GFCM" + getRandomIntegers(20));

        assetEntity.setGrossTonnage(10d);
        assetEntity.setPowerOfMainEngine(10d);

        assetEntity.setOwnerName("Foo Bar");
        assetEntity.setOwnerAddress("Hacker st. 1337");

        assetEntity.setProdOrgCode("ORGCODE");
        assetEntity.setProdOrgName("ORGNAME");
        assetEntity.setGrossTonnageUnit(UnitTonnage.LONDON);
        assetEntity.setLicenceType("DEMERSAL_AND_PELAGIC");
        assetEntity.setSegment("3");
        assetEntity.setConstructionYear("1914");
        assetEntity.setConstructionPlace("GBG");

        assetEntity.setHullMaterial("GLAS_PLASTIC_FIBER");
        assetEntity.setUpdateTime(now);
        assetEntity.setUpdatedBy("TEST");
        assetEntity.setAssetAgentAddress("assetagentadress_" + rnd.nextInt());
        assetEntity.setCountryOfImportOrExport("SWE");
        assetEntity.setAdministrativeDecisionDate(now);
        assetEntity.setSegmentOfAdministrativeDecision("3");
        assetEntity.setLengthBetweenPerpendiculars(17d);
        assetEntity.setProdOrgCode("prodorgcode");
        assetEntity.setProdOrgName("prodorg name");
        assetEntity.setHasLicence(true);
        assetEntity.setLicenceType("AllFish");
        assetEntity.setLengthOverAll(25d);
        assetEntity.setPortOfRegistration("GBG");
        assetEntity.setPowerOfAuxEngine(1000d);
        assetEntity.setPublicAid("EG");
        String regnbr = "THOFAN" + rnd.nextInt();
        if (regnbr.length() > 14) regnbr = regnbr.substring(0, 14);
        assetEntity.setRegistrationNumber(regnbr);

        assetEntity.setSafteyGrossTonnage(24000d);
        assetEntity.setOtherTonnage(23000d);
        assetEntity.setTypeOfExport("SM");
        assetEntity.setHasVms(false);
        assetEntity.setAgentIsAlsoOwner(true);
        assetEntity.setEventCode("EVENTCODE");
        assetEntity.setIrcsIndicator(true);
        assetEntity.setSource("INTERNAL");

        return assetEntity;
    }

    public static Note createBasicNote() {
        Note note = new Note();
        note.setNote("Notes: " + getRandomIntegers(10));
        note.setCreatedBy("Test");
        note.setCreatedOn(Instant.now());
        return note;
    }

    public static ContactInfo createBasicContactInfo() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setName("Kasim");
        contactInfo.setEmail("kasim.gul@havochvatten.se");
        contactInfo.setPhoneNumber("+46737112233");
        return contactInfo;
    }
    
    public static AssetGroup createBasicAssetGroup() {
        AssetGroup assetGroup = new AssetGroup();
        assetGroup.setName("Group: " + getRandomIntegers(5));
        assetGroup.setArchived(false);
        return assetGroup;
    }
    
    public static AssetFilter createBasicAssetFilter(String name) {
    	AssetFilter assetFilter = new AssetFilter();
    	assetFilter.setName(name);
    	assetFilter.setOwner(name);
        return assetFilter;
    }
    
    public static AssetFilterQuery createBasicAssetFilterQuery(AssetFilter assetFilter) {
    	AssetFilterQuery assetFilterQuery = new AssetFilterQuery();
    	assetFilterQuery.setIsNumber(true);
    	assetFilterQuery.setType("TEST_TYPE");
    	assetFilterQuery.setAssetFilter(assetFilter);
        return assetFilterQuery;
    }

    public static AssetFilterValue createBasicAssetFilterValue(AssetFilterQuery assetFilterQuery) {
    	AssetFilterValue assetFilterValue = new AssetFilterValue();
    	assetFilterValue.setOperator("greater then");
    	assetFilterValue.setValueNumber((double) 42);
    	assetFilterValue.setAssetFilterQuery(assetFilterQuery);
        return assetFilterValue;
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
