package eu.europa.ec.fisheries.uvms.asset.client;

import eu.europa.ec.fisheries.uvms.asset.client.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class AssetHelper {

    private static Random rnd = new Random();

    public static AssetDTO createBasicAsset() {
        AssetDTO assetEntity = new AssetDTO();

        assetEntity.setName("Test asset");
        assetEntity.setActive(true);
        assetEntity.setExternalMarking("EXT123");
        assetEntity.setFlagStateCode("SWE");

        assetEntity.setCommissionDate(Instant.now().truncatedTo(ChronoUnit.MILLIS));
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




    public static AssetDTO createBiggerAsset() {

        AssetDTO assetEntity = new AssetDTO();
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
        assetEntity.setGrossTonnageUnit("LONDON");
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
        note.setCreatedOn(Instant.now());
        return note;
    }
    
    public static String getRandomIntegers(int length) {
        return new Random()
                .ints(0,9)
                .mapToObj(i -> String.valueOf(i))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static CustomCode createCustomCode(String constant) {

        CustomCode cc = new CustomCode();
        Instant validFrom = Instant.now();
        Instant validTo = validFrom.plus(30, ChronoUnit.DAYS);
        CustomCodesPK pk = new CustomCodesPK(constant, "TEST_Code_" + UUID.randomUUID().toString(),validFrom, validTo);
        cc.setPrimaryKey(pk);
        cc.setDescription("This is a description");
        return cc;
    }
}
