package eu.europa.ec.fisheries.uvms.asset.service.sync;

import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class AssetRawHistoryRecordHelper {

    private static final String FLEETSYNC = "fleetsync";
    private static final String DEFAULT_UNIT_TONNAGE = "LONDON";
    private static final String EMPTY_NAME = "No Name";


    //////////////////////////////////
    //  public methods
    //////////////////////////////////

    public List<AssetRawHistory> createOneRawRecordForNewAsset() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();

        AssetRawHistory singleHistoryRecord = createDefaultHistoryRecord();
        singleHistoryRecord.setUvi("8740826");
        singleHistoryRecord.setEventActive(true);
        rawHistoryRecords.add(singleHistoryRecord);

        return rawHistoryRecords;
    }

    public List<AssetRawHistory> createOneDuplicatedRawRecordForExistingAsset() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();
        rawHistoryRecords.add(createDefaultHistoryRecord());
        return rawHistoryRecords;
    }

    public List<AssetRawHistory> createFourRawRecordsForNewAsset() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();
        rawHistoryRecords.add(createDefaultHistoryRecord());

        AssetRawHistory plusOneRecord = createDefaultHistoryRecord();
        plusOneRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB70");
        plusOneRecord.setPublicAid("P_AID");
        rawHistoryRecords.add(plusOneRecord);

        AssetRawHistory plusTwoRecord = createDefaultHistoryRecord();
        plusTwoRecord.setDateOfEvent(DateUtils.stringToDate("2020-04-20 01:00:00 Z"));
        plusTwoRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB71");
        plusTwoRecord.setUvi("8740826");
        plusTwoRecord.setMmsi("MMSIMMSII");
        plusTwoRecord.setGfcm("GFCMGFCM");
        plusTwoRecord.setImo("IMOIMOI");
        plusTwoRecord.setConstructionPlace("CONSTRUCTION_PLACE");
        plusTwoRecord.setConstructionAddress("CONSTRUCTION, ADDRESS #1");
        plusTwoRecord.setCountryOfImportOrExport("BEL");
        plusTwoRecord.setTypeOfExport(TypeOfExportEnum.EX.name());
        plusTwoRecord.setEventActive(false);
        rawHistoryRecords.add(plusTwoRecord);

        AssetRawHistory plusThreeRecord = createDefaultHistoryRecord();
        plusThreeRecord.setDateOfEvent(DateUtils.stringToDate("2020-05-20 02:00:00 Z"));
        plusThreeRecord.setOwnerName("OwnerNameA");
        plusThreeRecord.setOwnerAddress("Owner Address A");
        plusThreeRecord.setAgentAddress("Agent address A");
        plusThreeRecord.setName("Vessel Name A");
        plusThreeRecord.setUvi("8740999");
        plusThreeRecord.setIrcs("IRCS");
        plusThreeRecord.setExternalMarking("ExtMarktA");
        //plusThreeRecord.setCfr("TST090002021"); - changing CFR does not work well :(
        plusThreeRecord.setIccat("ICCATiccAA");
        plusThreeRecord.setLengthOverAll(BigDecimal.valueOf(2044L, 2));
        plusThreeRecord.setLengthBetweenPerpendiculars(BigDecimal.valueOf(2077L, 2));
        plusThreeRecord.setPowerOfMainEngine(new BigDecimal(654.00));
        plusThreeRecord.setPowerOfAuxEngine(new BigDecimal(210.00));
        plusThreeRecord.setGrossTonnage(new BigDecimal(98.00));
        plusThreeRecord.setOtherTonnage(BigDecimal.valueOf(1122L, 2));
        plusThreeRecord.setHasLicence(true);
        plusThreeRecord.setCountryOfRegistration("ESP");
        plusThreeRecord.setPlaceOfRegistration("PLACEOFREGAA");
        plusThreeRecord.setCountryOfImportOrExport("SWE");
        plusThreeRecord.setRegistrationNumber("REGNUMBER11");
        plusThreeRecord.setPublicAid(PublicAidEnum.EG.name());
        plusThreeRecord.setMmsi("MMSIMMSAA");
        plusThreeRecord.setImo("IMOIMAA");
        plusThreeRecord.setTypeOfExport(TypeOfExportEnum.SM.name());
        plusThreeRecord.setGfcm("GFCMGFAA");
        plusThreeRecord.setSegment(SegmentFUP.CA3.name());
        plusThreeRecord.setEventCodeType(EventCodeEnum.MOD.name());
        plusThreeRecord.setUpdateTime(DateUtils.stringToDate("2020-05-20 01:11:11 Z"));
        plusThreeRecord.setSafteyGrossTonnage(BigDecimal.valueOf(9945, 2));
        plusThreeRecord.setGrossTonnageUnit(UnitTonnage.LONDON.name());
        //Main FG - //TODO add this part
        //Sub FG - //TODO add this part
        plusThreeRecord.setDateOfConstruction(Date.valueOf("1998-07-07"));
        plusThreeRecord.setConstructionPlace("AMSTERDAM");
        plusThreeRecord.setDateOfServiceEntry(Date.valueOf("1998-12-12"));
        plusThreeRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB72");
        plusThreeRecord.setEventActive(true);
        plusThreeRecord.setAssetAgentIsAlsoOwner(false);
        plusThreeRecord.setHasVms(false);

        rawHistoryRecords.add(plusThreeRecord);

        return rawHistoryRecords;
    }

    public List<AssetRawHistory> createTwoUpdateRecordsIncludingTheActiveRecord() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();

        AssetRawHistory plusOneRecord = createDefaultHistoryRecord();
        plusOneRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB71");
        plusOneRecord.setDateOfEvent(DateUtils.stringToDate("2020-04-20 11:11:11 Z"));
        plusOneRecord.setUvi("8000826");
        plusOneRecord.setMmsi("MMSIAASII");
        plusOneRecord.setGfcm("GFCAAFCM");
        plusOneRecord.setImo("IMAAMOI");
        plusOneRecord.setConstructionPlace("CONSTRUCTION_AAACE");
        plusOneRecord.setConstructionAddress("CONSTRUCTION, AAARESS #1");
        plusOneRecord.setCountryOfImportOrExport("GER");
        plusOneRecord.setTypeOfExport(TypeOfExportEnum.SM.name());
        plusOneRecord.setEventActive(false);
        rawHistoryRecords.add(plusOneRecord);


        AssetRawHistory plusTwoRecord = createDefaultHistoryRecord();
        plusTwoRecord.setDateOfEvent(DateUtils.stringToDate("2020-05-21 22:22:22 Z"));
        plusTwoRecord.setOwnerName("OwnerNameB");
        plusTwoRecord.setOwnerAddress("Owner Address B");
        plusTwoRecord.setAgentAddress("Agent address B");
        plusTwoRecord.setName("Vessel Name B");
        plusTwoRecord.setUvi("8740111");
        plusTwoRecord.setIrcs("SCRI");
        plusTwoRecord.setExternalMarking("ExtMarktB");
        //plusTwoRecord.setCfr("TST090002022");
        plusTwoRecord.setIccat("ICCATiccBB");
        plusTwoRecord.setLengthOverAll(BigDecimal.valueOf(2055L, 2));
        plusTwoRecord.setLengthBetweenPerpendiculars(BigDecimal.valueOf(2088L, 2));
        plusTwoRecord.setPowerOfMainEngine(new BigDecimal(659.00));
        plusTwoRecord.setPowerOfAuxEngine(new BigDecimal(212.00));
        plusTwoRecord.setGrossTonnage(new BigDecimal(97.00));
        plusTwoRecord.setOtherTonnage(BigDecimal.valueOf(1182L, 2));
        plusTwoRecord.setHasLicence(true);
        plusTwoRecord.setCountryOfRegistration("NOR");
        plusTwoRecord.setPlaceOfRegistration("PLACEOFREGBB");
        plusTwoRecord.setCountryOfImportOrExport("POL");
        plusTwoRecord.setRegistrationNumber("REGNUMBER22");
        plusTwoRecord.setPublicAid(PublicAidEnum.AE.name());
        plusTwoRecord.setMmsi("MMSIMMSBB");
        plusTwoRecord.setImo("IMOIMBB");
        plusTwoRecord.setTypeOfExport(TypeOfExportEnum.EX.name());
        plusTwoRecord.setGfcm("GFCMGFBB");
        plusTwoRecord.setSegment(SegmentFUP.MFL.name());
        plusTwoRecord.setEventCodeType(EventCodeEnum.CST.name());
        plusTwoRecord.setUpdateTime(DateUtils.stringToDate("2020-05-20 01:17:17 Z"));
        plusTwoRecord.setSafteyGrossTonnage(BigDecimal.valueOf(9989, 2));
        plusTwoRecord.setGrossTonnageUnit(UnitTonnage.OSLO.name());
        //Main FG
        //Sub FG
        plusTwoRecord.setDateOfConstruction(Date.valueOf("1998-08-07"));
        plusTwoRecord.setConstructionPlace("AMSTERDAMA");
        plusTwoRecord.setDateOfServiceEntry(Date.valueOf("1999-12-15"));
        plusTwoRecord.setHullMaterial(HullMaterialEnum.WOOD.getId());
        plusTwoRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB72");
        plusTwoRecord.setEventActive(true);
        plusTwoRecord.setAssetAgentIsAlsoOwner(false);
        plusTwoRecord.setHasIrcs(false);
        plusTwoRecord.setHasVms(true);

        rawHistoryRecords.add(plusTwoRecord);

        return rawHistoryRecords;
    }


    public List<AssetRawHistory> createRawRecordsForNewAssetAndDistinctNewRecords() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();
        rawHistoryRecords.add(createDefaultHistoryRecord());

        AssetRawHistory plusOneRecord = createDefaultHistoryRecord();
        plusOneRecord.setDateOfEvent(DateUtils.stringToDate("2020-04-18 00:00:00 Z"));
        plusOneRecord.setUvi("8740827");
        plusOneRecord.setMmsi("MMSIMMSI9");
        plusOneRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB29");
        plusOneRecord.setConstructionPlace("A good place");
        plusOneRecord.setEventActive(true);
        rawHistoryRecords.add(plusOneRecord);

        return rawHistoryRecords;
    }

    public List<AssetRawHistory> createRawRecordsForExistingAssetTargetingDistinctNewRecords() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();
        rawHistoryRecords.add(createDefaultHistoryRecord());

        AssetRawHistory plusOneRecord = createDefaultHistoryRecord();
        plusOneRecord.setDateOfEvent(DateUtils.stringToDate("2020-04-18 00:00:00 Z"));
        plusOneRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB30");
        plusOneRecord.setEventActive(false);
        plusOneRecord.setConstructionPlace("WRONG ONE");
        plusOneRecord.setGfcm("GFCMGFCM");
        rawHistoryRecords.add(plusOneRecord);

        AssetRawHistory plusTwoRecord = createDefaultHistoryRecord();
        plusTwoRecord.setDateOfEvent(DateUtils.stringToDate("2020-04-20 00:00:00 Z"));
        plusTwoRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB31");
        plusTwoRecord.setUvi("8740826");
        plusTwoRecord.setMmsi("MMSIMMSII");
        plusTwoRecord.setGfcm("GFCMGFCM");
        plusTwoRecord.setImo("IMOIMOI");
        plusTwoRecord.setConstructionPlace("CONSTRUCTION_PLACE");
        plusTwoRecord.setConstructionAddress("CONSTRUCTION, ADDRESS #1");
        plusTwoRecord.setCountryOfImportOrExport("BEL");
        plusTwoRecord.setTypeOfExport("TYPE_OF_EXPORT");
        plusTwoRecord.setEventActive(true);
        rawHistoryRecords.add(plusTwoRecord);

        return rawHistoryRecords;
    }

    public List<AssetRawHistory> createRawRecordsForExistingAssetTargetingRecordUpdate() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();

        AssetRawHistory plusOneRecord = createDefaultHistoryRecord();
        plusOneRecord.setDateOfEvent(DateUtils.stringToDate("2019-05-21 00:00:00 Z"));
        plusOneRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB32");
        plusOneRecord.setEventActive(false);
        rawHistoryRecords.add(plusOneRecord);

        AssetRawHistory plusTwoRecord = createDefaultHistoryRecord();
        plusTwoRecord.setDateOfEvent(DateUtils.stringToDate("2019-07-27 04:00:00 Z"));
        plusTwoRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB28");
        plusTwoRecord.setUvi("8740826");
        plusTwoRecord.setMmsi("MMSIMMSI2");
        plusTwoRecord.setGfcm("GFCMGFC2");
        plusTwoRecord.setImo("IMOIMO2");
        plusTwoRecord.setIccat("ICCATiccat");
        plusTwoRecord.setConstructionPlace("CONSTRUCTION_PLACE");
        plusTwoRecord.setConstructionAddress("CONSTRUCTION, ADDRESS #1"); //what is this?
        plusTwoRecord.setCountryOfImportOrExport("BEL");
        plusTwoRecord.setTypeOfExport("TYPE_OF_EXPORT"); //what is this?
        plusTwoRecord.setEventActive(false);
        rawHistoryRecords.add(plusTwoRecord);

        return rawHistoryRecords;
    }

    public List<AssetRawHistory> createRawRecordsForExistingAssetTargetingMostRecentAndActiveExistingRecordUpdate() {
        List<AssetRawHistory> rawHistoryRecords = new ArrayList<>();

        AssetRawHistory plusOneRecord = createDefaultHistoryRecord();
        plusOneRecord.setDateOfEvent(DateUtils.stringToDate("2020-04-18 00:00:00 Z"));
        plusOneRecord.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB29");
        plusOneRecord.setUvi("8740828");
        plusOneRecord.setMmsi("MMSIMMSI5");
        plusOneRecord.setGfcm("GFCMGFC4");
        plusOneRecord.setImo("IMOIMO9");
        plusOneRecord.setIccat("ICCATiccat");
        plusOneRecord.setConstructionPlace("CONSTRUCTION_PLACE");
        plusOneRecord.setCountryOfImportOrExport("BEL");
        plusOneRecord.setTypeOfExport(TypeOfExportEnum.SM.name()); //what is this?
        plusOneRecord.setEventActive(true);
        rawHistoryRecords.add(plusOneRecord);

        return rawHistoryRecords;
    }


    //////////////////////////////////
    //  private methods
    //////////////////////////////////

    private AssetRawHistory createDefaultHistoryRecord () {
        AssetRawHistory assetRawHistory = new AssetRawHistory();

        //Event info
        mapEventInfo(assetRawHistory);
        //Vessel identifiers
        mapVesselIdentifiers(assetRawHistory);
        //Registration and Construction
        mapVesselRegistrationAndConstruction(assetRawHistory);
        //VesselEngine
        mapEngines(assetRawHistory);
        //SpecifiedVesselDimensions
        mapPhysicalVesselCharacteristics(assetRawHistory);
        //OnBoardFishingGear
        mapFishingGear(assetRawHistory);
        //ApplicableVesselAdministrativeCharacteristics
        mapAdministrativeCharacteristics(assetRawHistory);
        //ApplicableVesselEquipmentCharacteristics
        mapIndicators(assetRawHistory);
        //SpecifiedContactParty
        mapContacts(assetRawHistory);
        //Remaining values
        mapOtherValues(assetRawHistory);

        return assetRawHistory;
    }

    private void mapEventInfo(AssetRawHistory assetRawHistory) {
        assetRawHistory.setEventCodeType("MOD");
        assetRawHistory.setDateOfEvent(Date.valueOf("1989-01-01"));
        assetRawHistory.setEventActive(false);
    }

    private void mapVesselIdentifiers(AssetRawHistory assetRawHistory) {
        assetRawHistory.setCfr("TST031221985");
        assetRawHistory.setUvi(null);
        assetRawHistory.setRegistrationNumber("111");
        assetRawHistory.setExternalMarking("Z.122");
        assetRawHistory.setIrcs("OPER");
        assetRawHistory.setMmsi(null);
        assetRawHistory.setHashKey("6FB734930B9CAB5DB275F9A8AA38BB28");

        assetRawHistory.setIccat(null);
        assetRawHistory.setGfcm(null);

        assetRawHistory.setName("BBB944");
        assetRawHistory.setVesselType("FX");
    }

    private void mapVesselRegistrationAndConstruction(AssetRawHistory assetRawHistory) {
        assetRawHistory.setCountryOfRegistration("BEL");
        assetRawHistory.setPlaceOfRegistration("ZEEBR");
        assetRawHistory.setDateOfConstruction (Date.valueOf("1985-01-01"));
    }

    private void mapEngines(AssetRawHistory assetRawHistory) {
        assetRawHistory.setPowerOfMainEngine(new BigDecimal(221.00));
        assetRawHistory.setPowerOfAuxEngine(new BigDecimal(0.00));
    }

    private void mapPhysicalVesselCharacteristics(AssetRawHistory assetRawHistory) {
        assetRawHistory.setLengthOverAll(BigDecimal.valueOf(2108L, 2));
        assetRawHistory.setLengthBetweenPerpendiculars(new BigDecimal(18.00));
        assetRawHistory.setGrossTonnageUnit(DEFAULT_UNIT_TONNAGE);
        assetRawHistory.setGrossTonnage(new BigDecimal(67.00));
        assetRawHistory.setOtherTonnage(BigDecimal.valueOf(4767L, 2));
        assetRawHistory.setSafteyGrossTonnage(new BigDecimal(0.00));
        assetRawHistory.setHullMaterial(2L);
    }

    private void mapFishingGear(AssetRawHistory assetRawHistory) {
        assetRawHistory.setMainFishingGearCharacteristics("[]");
        assetRawHistory.setMainFishingGearRole("MAIN");
        assetRawHistory.setMainFishingGearType("TBB");
        assetRawHistory.setSubFishingGearCharacteristics("{}");
        assetRawHistory.setSubFishingGearRole("AUX");
        assetRawHistory.setSubFishingGearType("NO");

    }

    private void mapAdministrativeCharacteristics(AssetRawHistory assetRawHistory) {
        assetRawHistory.setHasLicence(true);
        assetRawHistory.setSegment("CA2");
        assetRawHistory.setDateOfServiceEntry(Date.valueOf("1985-05-31"));
        assetRawHistory.setPublicAid(null);
    }

    private void mapIndicators(AssetRawHistory assetRawHistory) {
        assetRawHistory.setHasIrcs(true);
        assetRawHistory.setHasVms(false);
        assetRawHistory.setHasErs(true);
        assetRawHistory.setHasAis(false);
    }

    private void mapContacts(AssetRawHistory assetRawHistory) {
        assetRawHistory.setOwnerName(EMPTY_NAME);
        assetRawHistory.setOwnerAddress("BBB");
        assetRawHistory.setOwnerEmailAddress(null);
        assetRawHistory.setOwnerPhoneNumber(null);

        assetRawHistory.setImo(null);

        assetRawHistory.setAgentName(null);
        assetRawHistory.setAgentAddress("BBB");
        assetRawHistory.setAgentEmailAddress(null);
        assetRawHistory.setAgentPhoneNumber(null);
    }

    private void mapOtherValues(AssetRawHistory assetRawHistory) {
        assetRawHistory.setConstructionPlace(null);
        assetRawHistory.setConstructionAddress(null);
        assetRawHistory.setUpdateTime(DateUtils.stringToDate("2020-04-17 00:00:00 Z"));
        assetRawHistory.setCountryOfImportOrExport(null);
        assetRawHistory.setTypeOfExport(null);
        assetRawHistory.setUpdatedBy(FLEETSYNC);
    }
    
}