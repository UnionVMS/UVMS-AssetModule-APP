package eu.europa.ec.fisheries.uvms.asset.client;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import eu.europa.ec.fisheries.uvms.asset.client.model.*;

public abstract class AssetHelper {

    private static Random rnd = new Random();

    public static Asset createBasicAsset() {
        Asset assetEntity = new Asset();

        assetEntity.setName("Test asset");
        assetEntity.setActive(true);
        assetEntity.setExternalMarking("EXT123");
        assetEntity.setFlagStateCode("SWE");

        assetEntity.setCommissionDate(OffsetDateTime.now(ZoneOffset.UTC));
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
        OffsetDateTime  now =  OffsetDateTime.now(ZoneOffset.UTC);


        assetEntity.setName("Test asset");
        assetEntity.setActive(true);
        assetEntity.setExternalMarking("EXT123");
        assetEntity.setFlagStateCode("SWE");

        assetEntity.setCommissionDate(OffsetDateTime.now(ZoneOffset.UTC));
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
        note.setActivityCode("Activity");
        note.setNotes("Notes: " + getRandomIntegers(10));
        note.setUser("Test");
        return note;
    }
    
    public static AssetGroup createBasicAssetGroup() {
        AssetGroup assetGroup = new AssetGroup();
        assetGroup.setName("Group: " + getRandomIntegers(5));
//        assetGroup.setOwner("User: " + getRandomIntegers(5));
        assetGroup.setArchived(false);
        return assetGroup;
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
        OffsetDateTime validFrom = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime validTo = validFrom.plusDays(30);
        CustomCodesPK pk = new CustomCodesPK(constant, "TEST_Code_" + UUID.randomUUID().toString(),validFrom, validTo);
        cc.setPrimaryKey(pk);
        cc.setDescription("This is a description");
        return cc;
    }
}
