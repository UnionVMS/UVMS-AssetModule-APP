package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.model.constants.UnitTonnage;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AssetTestsHelper {

    private static Random rnd = new Random();

    public static Asset createBasicAsset() {
        Asset assetEntity = new Asset();
        
        assetEntity.setName("Test asset");
        assetEntity.setActive(true);
        assetEntity.setExternalMarking("EXT123");
        assetEntity.setFlagStateCode("SWE");
        assetEntity.setGearFishingType("gearFishingType");

        assetEntity.setCommissionDate(Instant.now());
        assetEntity.setCfr("CRF" + getRandomIntegers(9));
        assetEntity.setIrcs("F" + getRandomIntegers(7));
        assetEntity.setImo(getRandomIntegers(7));
        assetEntity.setMmsi("M" + getRandomIntegers(8));
        assetEntity.setIccat("ICCAT" + getRandomIntegers(20));
        assetEntity.setUvi("UVI" + getRandomIntegers(20));
        assetEntity.setGfcm("GFCM" + getRandomIntegers(20));
        
        assetEntity.setGrossTonnage(10D);
        assetEntity.setPowerOfMainEngine(10D);
        
        assetEntity.setOwnerName("Foo Bar");
        assetEntity.setOwnerAddress("Hacker st. 1337");
        
        assetEntity.setProdOrgCode("ORGCODE");
        assetEntity.setProdOrgName("ORGNAME");
        
        assetEntity.setUpdateTime(Instant.now());
        assetEntity.setUpdatedBy("TEST");
        
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
        assetEntity.setMmsi("M" + getRandomIntegers(8));
        assetEntity.setIccat("ICCAT" + getRandomIntegers(20));
        assetEntity.setUvi("UVI" + getRandomIntegers(20));
        assetEntity.setGfcm("GFCM" + getRandomIntegers(20));

        assetEntity.setGrossTonnage(10D);
        assetEntity.setPowerOfMainEngine(10D);

        assetEntity.setOwnerName("Foo Bar");
        assetEntity.setOwnerAddress("Hacker st. 1337");

        assetEntity.setProdOrgCode("ORGCODE");
        assetEntity.setProdOrgName("ORGNAME");
        assetEntity.setGrossTonnageUnit(UnitTonnage.LONDON);
        assetEntity.setLicenceType("DEMERSAL_AND_PELAGIC");
        assetEntity.setGearFishingType("Demersal");
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
        assetEntity.setLengthBetweenPerpendiculars(17D);
        assetEntity.setProdOrgCode("prodorgcode");
        assetEntity.setProdOrgName("prodorg name");
        assetEntity.setHasLicence(true);
        assetEntity.setLicenceType("AllFish");
        assetEntity.setLengthOverAll(25D);
        assetEntity.setPortOfRegistration("GBG");
        assetEntity.setPowerOfAuxEngine(1000D);
        assetEntity.setPublicAid("EG");
        String regnbr = "THOFAN" + rnd.nextInt();
        if (regnbr.length() > 14) regnbr = regnbr.substring(0, 14);
        assetEntity.setRegistrationNumber(regnbr);

        assetEntity.setSafteyGrossTonnage(24000D);
        assetEntity.setOtherTonnage(23000D);
        assetEntity.setTypeOfExport("SM");
        assetEntity.setHasVms(false);
        assetEntity.setAgentIsAlsoOwner(true);
        assetEntity.setEventCode("EC" + rnd.nextLong());
        assetEntity.setIrcsIndicator(true);
        assetEntity.setSource("INTERNAL");

        List<ContactInfo> contacts = new ArrayList<>();
        ContactInfo ci = new ContactInfo();
        ci.setSource("INTERNAL");
        contacts.add(ci);

        return assetEntity;
    }

    public static Note createBasicNote() {
        Note note = new Note();
        note.setNote("Notes: " + getRandomIntegers(10));
        note.setCreatedBy("Test");
        return note;
    }

    public static ContactInfo createBasicContactInfo() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setName("Fantomen " + getRandomIntegers(5));
        contactInfo.setEmail("fantomen@mail.com");
        contactInfo.setPhoneNumber("" + getRandomIntegers(9));
        contactInfo.setCountry("SWE");
        return contactInfo;
    }

    public static String getRandomIntegers(int length) {
        return new Random()
                .ints(0,9)
                .mapToObj(i -> String.valueOf(i))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public Asset createAssetHelper(AssetIdentifier key, String value) {
        Asset assetEntity = createBiggerAsset();
        assetEntity.setCfr(null);
        assetEntity.setIrcs(null);
        assetEntity.setImo(null);
        assetEntity.setMmsi(null);
        assetEntity.setIccat(null);
        assetEntity.setUvi(null);
        assetEntity.setGfcm(null);

        switch (key) {
            case CFR:
                if (value.length() > 12) value = value.substring(0, 12);
                assetEntity.setCfr(value);
                break;
            case IMO:
                assetEntity.setImo(value);
                break;
            case IRCS:
                assetEntity.setIrcs(value);
                assetEntity.setIrcsIndicator(true);
                break;
            case MMSI:
                assetEntity.setMmsi(value);
                break;
            case ICCAT:
                assetEntity.setIccat(value);
                break;
            case UVI:
                assetEntity.setUvi(value);
                break;
            case GFCM:
                assetEntity.setGfcm(value);
                break;
        }
        return assetEntity;
    }

    static AssetFilterQuery createAssetFilterQuery(AssetFilter assetFilterEntity) {
    	AssetFilterQuery afq = new AssetFilterQuery();
    	afq.setAssetFilter(assetFilterEntity);
    	afq.setType("GUID");
    	afq.setIsNumber(true);
    	afq.setInverse(true);
        return afq;
    }

    public static FishingLicence createFishingLicence() {
        FishingLicence licence = new FishingLicence();
        licence.setLicenceNumber(rnd.nextLong());
        licence.setCivicNumber(getRandomIntegers(10));
        licence.setDecisionDate(Instant.now().minus(1,ChronoUnit.DAYS));
        licence.setFromDate(Instant.now().minus(1,ChronoUnit.DAYS));
        licence.setToDate(Instant.now().plus(1,ChronoUnit.DAYS));
        licence.setConstraints("Test");
        return licence;
    }
}
