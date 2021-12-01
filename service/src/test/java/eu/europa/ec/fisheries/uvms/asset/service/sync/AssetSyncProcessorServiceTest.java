package eu.europa.ec.fisheries.uvms.asset.service.sync;

import eu.europa.ec.fisheries.uvms.asset.service.sync.processor.AssetSyncProcessorService;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetSyncProcessorServiceTest {

    private static final String EMPTY_NAME = "No Name";
    @Inject
    AssetRawHistoryDao assetRawHistoryDao;

    @Inject
    AssetDao assetDao;

    @Inject
    AssetRawHistoryRecordHelper recordHelper;

    @Inject
    AssetSyncProcessorService processorService;

    private List<AssetRawHistory> records = null;

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(AssetSyncProcessorService.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void setUp() {
        //clean up table -- !! CAREFUL here, the app table will be deleted !!
        assetRawHistoryDao.cleanUpRawRecordsTable();
    }

    @After
    public void tearDown() throws Exception {
        assetDao.deleteAssetByCfr("TST031221985");
        assetRawHistoryDao.cleanUpRawRecordsTable();
    }

    @Test
    public void whenNewAssetWithOneHistory_thenAssetIsCreatedWithHistory() {
        //given
        records = recordHelper.createOneRawRecordForNewAsset();
        assetRawHistoryDao.createRawHistoryEntry(records);
        //when
        processorService.syncRawRecordsWithExisting();
        //then
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertEquals("8740826", asset.getUvi());
            assertEquals("OPER", asset.getIRCS());
            assertEquals("T", asset.getIrcsIndicator());
            assertEquals(null, asset.getMMSI());
            assertEquals(null, asset.getIMO());
            assertEquals(null, asset.getIccat());
            assertEquals(null, asset.getGfcm());
            assertEquals("1985", asset.getConstructionYear());
            assertEquals(null, asset.getConstructionPlace());
            assertEquals("1985", asset.getVessYearofcommissioning());
            assertEquals("05", asset.getCommissionMonth());
            assertEquals("31", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.METAL, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());

            List<AssetHistory> records = asset.getHistories();
            assertEquals(1, records.size());
            AssetHistory record = records.get(0);
            assertEquals("BBB944", record.getName());
            assertEquals("BEL", record.getCountryOfRegistration());
            assertEquals("6FB734930B9CAB5DB275F9A8AA38BB28", record.getHashKey());
            assertEquals(BigDecimal.valueOf(22100,2), record.getPowerOfMainEngine());
            assertEquals(BigDecimal.valueOf(0,2), record.getPowerOfAuxEngine());
            assertEquals(BigDecimal.valueOf(2108,2), record.getLengthOverAll());
            assertEquals(BigDecimal.valueOf(1800,2), record.getLengthBetweenPerpendiculars());
            assertEquals(UnitTonnage.LONDON, record.getGrossTonnageUnit());
            assertEquals(BigDecimal.valueOf(6700,2), record.getGrossTonnage());
            assertEquals(BigDecimal.valueOf(4767,2), record.getOtherTonnage());
            assertEquals(BigDecimal.valueOf(0,2), record.getSafteyGrossTonnage());
            FishingGear mainGear = record.getMainFishingGear();
            assertEquals("TBB", mainGear.getCode());
            FishingGear auxGear = record.getSubFishingGear();
            assertEquals("NO", auxGear.getCode());
            assertEquals(SegmentFUP.CA2, record.getSegment());
            assertEquals("BBB", record.getOwnerAddress());
            assertEquals(EMPTY_NAME, record.getOwnerName());
            assertEquals("BBB", record.getAssetAgentAddress());
            ZonedDateTime updateTime
                    = ZonedDateTime.ofInstant(record.getUpdateTime().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-04-17T00:00Z[UTC]", updateTime.toString());

        } catch (NoAssetEntityFoundException e) {
            fail();
        }
    }

    @Test
    public void whenNewAssetWithHistory_thenAssetIsCreatedToTheLatestHistory() {
        //given
        records = recordHelper.createRawRecordsForNewAssetAndDistinctNewRecords();
        assetRawHistoryDao.createRawHistoryEntry(records);
        //when
        processorService.syncRawRecordsWithExisting();
        //then
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertEquals("8740827", asset.getUvi());
            assertEquals("OPER", asset.getIRCS());
            assertEquals("T", asset.getIrcsIndicator());
            assertEquals("MMSIMMSI9", asset.getMMSI());
            assertEquals(null, asset.getIMO());
            assertEquals(null, asset.getIccat());
            assertEquals(null, asset.getGfcm());
            assertEquals("1985", asset.getConstructionYear());
            assertEquals("A good place", asset.getConstructionPlace());
            assertEquals("1985", asset.getVessYearofcommissioning());
            assertEquals("05", asset.getCommissionMonth());
            assertEquals("31", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.METAL, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());

            List<AssetHistory> records = asset.getHistories();
            AssetHistory record = records.stream()
                    .filter(ah -> "6FB734930B9CAB5DB275F9A8AA38BB29".equals(ah.getHashKey()))
                    .reduce((ah1, ah2) -> {
                        throw new IllegalStateException("Multiple elements with same hash key.");
                    }).get();

            assertEquals("BBB944", record.getName());
            assertEquals("BEL", record.getCountryOfRegistration());
            assertEquals("6FB734930B9CAB5DB275F9A8AA38BB29", record.getHashKey());
            assertEquals(BigDecimal.valueOf(22100, 2), record.getPowerOfMainEngine());
            assertEquals(BigDecimal.valueOf(0, 2), record.getPowerOfAuxEngine());
            assertEquals(BigDecimal.valueOf(2108, 2), record.getLengthOverAll());
            assertEquals(BigDecimal.valueOf(1800, 2), record.getLengthBetweenPerpendiculars());
            assertEquals(UnitTonnage.LONDON, record.getGrossTonnageUnit());
            assertEquals(BigDecimal.valueOf(6700, 2), record.getGrossTonnage());
            assertEquals(BigDecimal.valueOf(4767, 2), record.getOtherTonnage());
            assertEquals(BigDecimal.valueOf(0, 2), record.getSafteyGrossTonnage());
            FishingGear mainGear = record.getMainFishingGear();
            assertEquals("TBB", mainGear.getCode());
            FishingGear auxGear = record.getSubFishingGear();
            assertEquals("NO", auxGear.getCode());
            assertEquals(SegmentFUP.CA2, record.getSegment());
            assertEquals("BBB", record.getOwnerAddress());
            assertEquals(EMPTY_NAME, record.getOwnerName());
            assertEquals("BBB", record.getAssetAgentAddress());
            ZonedDateTime updateTime
                    = ZonedDateTime.ofInstant(record.getUpdateTime().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-04-17T00:00Z[UTC]", updateTime.toString());
            ZonedDateTime dateOfEvent
                    = ZonedDateTime.ofInstant(record.getDateOfEvent().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-04-18T00:00Z[UTC]", dateOfEvent.toString());
        } catch (NoAssetEntityFoundException e) {
            fail();
        }
    }

    @Test
    public void whenAllHistoryRecordsInThePast_thenExistingHistoryRecordIsUpdatedButNotAsset() {
        //given
        //we have an asset with 2 records...
        records = recordHelper.createRawRecordsForNewAssetAndDistinctNewRecords();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //when
        //2 new additional past records (one new, one update) come and sync is again run
        assetRawHistoryDao.cleanUpRawRecordsTable();
        records = recordHelper.createRawRecordsForExistingAssetTargetingRecordUpdate();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //then
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertEquals("8740827", asset.getUvi());
            assertEquals("OPER", asset.getIRCS());
            assertEquals("T", asset.getIrcsIndicator());
            assertEquals("MMSIMMSI9", asset.getMMSI());
            assertEquals(null, asset.getIMO());
            assertEquals(null, asset.getIccat());
            assertEquals(null, asset.getGfcm());
            assertEquals("1985", asset.getConstructionYear());
            assertEquals("A good place", asset.getConstructionPlace());
            assertEquals("1985", asset.getVessYearofcommissioning());
            assertEquals("05", asset.getCommissionMonth());
            assertEquals("31", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.METAL, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());
            assertEquals(3, asset.getHistories().size());

            List<AssetHistory> records = asset.getHistories();
            AssetHistory record = records.stream()
                    .filter(ah -> "6FB734930B9CAB5DB275F9A8AA38BB28".equals(ah.getHashKey()))
                    .reduce((ah1, ah2) -> {
                        throw new IllegalStateException("Multiple elements with same hash key.");
                    }).get();
            assertEquals(false, record.getActive());
            assertEquals("BEL", record.getCountryOfRegistration());
            assertEquals(EventCodeEnum.MOD, record.getEventCode());
            assertEquals("BBB944", record.getName());
            assertEquals("Z.122", record.getExternalMarking());
            assertEquals(null, record.getAssetAgentIsAlsoOwner());
            assertEquals(BigDecimal.valueOf(2108,2), record.getLengthOverAll());
            assertEquals(BigDecimal.valueOf(1800,2), record.getLengthBetweenPerpendiculars());
            assertEquals(BigDecimal.valueOf(0,2), record.getSafteyGrossTonnage());
            assertEquals(BigDecimal.valueOf(4767,2), record.getOtherTonnage());
            assertEquals(BigDecimal.valueOf(6700,2), record.getGrossTonnage());
            assertEquals(UnitTonnage.LONDON, record.getGrossTonnageUnit());
            assertEquals("ZEEBR", record.getPortOfRegistration());
            assertEquals(BigDecimal.valueOf(22100,2), record.getPowerOfMainEngine());
            assertEquals(BigDecimal.valueOf(0,2), record.getPowerOfAuxEngine());
            assertEquals(true, record.getHasLicence());
            //add fishing gear
            assertEquals("No Name", record.getOwnerName());
            assertEquals(false, record.getHasVms());
            assertEquals("BBB", record.getOwnerAddress());
            assertEquals("BBB", record.getAssetAgentAddress());
            assertEquals("BEL", record.getCountryOfImportOrExport());
            assertEquals(SegmentFUP.CA2, record.getSegment());
            assertEquals(PublicAidEnum.AE, record.getPublicAid());
            assertEquals("111", record.getRegistrationNumber());
            assertEquals(TypeOfExportEnum.EX, record.getTypeOfExport());
            assertEquals("fleetsync", record.getUpdatedBy());
            assertEquals("TST031221985", record.getCfr());
            assertEquals("IMOIMO2", record.getImo());
            assertEquals("OPER", record.getIrcs());
            assertEquals("MMSIMMSI2", record.getMmsi());
            //add contact info
            assertEquals("ICCATiccat", record.getIccat());
            assertEquals("8740826", record.getUvi());
            assertEquals("GFCMGFC2", record.getGfcm());
            //assertEquals("CONSTRUCTION_PLACE", record.getPlaceOfConstruction());
            assertEquals(null, record.getIrcsIndicator());
            assertEquals(null, record.getHullMaterial());
            ZonedDateTime dateOfEvent
                    = ZonedDateTime.ofInstant(record.getDateOfEvent().toInstant(), ZoneId.of("UTC"));
            assertEquals("2019-07-27T04:00Z[UTC]", dateOfEvent.toString());
        } catch (NoAssetEntityFoundException e) {
            fail();
        }
    }

    @Test
    public void whenAreAlsoNewHistoryRecords_thenNewRecordsAreAddedAndAssetUpdatedToLatestActive() {
        //given
        //we have an asset with 2 records...
        records = recordHelper.createRawRecordsForNewAssetAndDistinctNewRecords();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //when
        //2 new additional and distinct records come and sync is again run
        assetRawHistoryDao.cleanUpRawRecordsTable();
        records = recordHelper.createRawRecordsForExistingAssetTargetingDistinctNewRecords();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //then
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertEquals("8740826", asset.getUvi());
            assertEquals("OPER", asset.getIRCS());
            assertEquals("T", asset.getIrcsIndicator());
            assertEquals("MMSIMMSII", asset.getMMSI());
            assertEquals("IMOIMOI", asset.getIMO());
            assertEquals("1985", asset.getConstructionYear());
            assertEquals("CONSTRUCTION_PLACE", asset.getConstructionPlace());
            assertEquals("1985", asset.getVessYearofcommissioning());
            assertEquals("05", asset.getCommissionMonth());
            assertEquals("31", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.METAL, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());
            assertEquals("GFCMGFCM", asset.getGfcm());
            assertEquals(4, asset.getHistories().size());
            //add here checks for AssetHistory
        } catch (NoAssetEntityFoundException e) {
            fail();
        }
    }

    @Test
    public void whenIncomingHistoryIsDuplicated_thenIsIgnored() {
        //given
        //we have an asset with 2 records...
        records = recordHelper.createRawRecordsForNewAssetAndDistinctNewRecords();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //when
        //2 new additional past records (one new, one update) come and sync is again run
        assetRawHistoryDao.cleanUpRawRecordsTable();
        records = recordHelper.createOneDuplicatedRawRecordForExistingAsset();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //then
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertEquals("8740827", asset.getUvi());
            assertEquals("OPER", asset.getIRCS());
            assertEquals("T", asset.getIrcsIndicator());
            assertEquals("MMSIMMSI9", asset.getMMSI());
            assertEquals(null, asset.getIMO());
            assertEquals(null, asset.getIccat());
            assertEquals(null, asset.getGfcm());
            assertEquals("1985", asset.getConstructionYear());
            assertEquals("A good place", asset.getConstructionPlace());
            assertEquals("1985", asset.getVessYearofcommissioning());
            assertEquals("05", asset.getCommissionMonth());
            assertEquals("31", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.METAL, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());
            assertEquals(2, asset.getHistories().size());

            //AssetHistoryUpdateHandler mock  = mock(AssetHistoryUpdateHandler.class);
            //would be nice to check method call count to really check if update was ignored.
        } catch (NoAssetEntityFoundException e) {
            fail();
        }
    }

    @Test
    public void whenPastRecordsAndLastActiveRecordUpdatedThenEachRequiredRecordIsUpdatedPlusAsset() {
        //given
        //we have an asset with 4 records, most recent being active...
        records = recordHelper.createFourRawRecordsForNewAsset();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //then -- see if initial conditions are also ok -- TO ADD also for a history record
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertNotNull(asset);
            assertEquals("IRCS", asset.getIRCS());
            assertEquals("T", asset.getIrcsIndicator());
            assertEquals("MMSIMMSAA", asset.getMMSI());
            assertEquals("IMOIMAA", asset.getIMO());
            assertEquals("1998", asset.getConstructionYear());
            assertEquals("AMSTERDAM", asset.getConstructionPlace());
            assertEquals("1998", asset.getVessYearofcommissioning());
            assertEquals("12", asset.getCommissionMonth());
            assertEquals("12", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.METAL, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());
            assertEquals("ICCATiccAA", asset.getIccat());
            assertEquals("8740999", asset.getUvi());
            assertEquals("GFCMGFAA", asset.getGfcm());
            ZonedDateTime updateTime
                    = ZonedDateTime.ofInstant(asset.getUpdateTime().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-05-20T01:11:11Z[UTC]", updateTime.toString());
            List<AssetHistory> records = asset.getHistories();
            assertEquals(4, records.size());

            AssetHistory activeRecord = records.stream()
                    .filter(ah -> "6FB734930B9CAB5DB275F9A8AA38BB72".equals(ah.getHashKey()))
                    .reduce((ah1, ah2) -> {
                        throw new IllegalStateException("Multiple elements with same hash key.");
                    }).get();
            assertEquals(true, activeRecord.getActive());
            assertEquals("ESP", activeRecord.getCountryOfRegistration());
            assertEquals(EventCodeEnum.MOD, activeRecord.getEventCode());
            assertEquals("Vessel Name A", activeRecord.getName());
            assertEquals("ExtMarktA", activeRecord.getExternalMarking());
            assertEquals(false, activeRecord.getAssetAgentIsAlsoOwner());
            assertEquals(BigDecimal.valueOf(2044L, 2), activeRecord.getLengthOverAll());
            assertEquals(BigDecimal.valueOf(2077L, 2), activeRecord.getLengthBetweenPerpendiculars());
            assertEquals(BigDecimal.valueOf(9945, 2), activeRecord.getSafteyGrossTonnage());
            assertEquals(BigDecimal.valueOf(1122L, 2), activeRecord.getOtherTonnage());
            assertEquals(BigDecimal.valueOf(9800, 2), activeRecord.getGrossTonnage());
            assertEquals(UnitTonnage.LONDON, activeRecord.getGrossTonnageUnit());
            assertEquals("PLACEOFREGAA", activeRecord.getPortOfRegistration());
            assertEquals(BigDecimal.valueOf(65400,2), activeRecord.getPowerOfMainEngine());
            assertEquals(BigDecimal.valueOf(21000,2), activeRecord.getPowerOfAuxEngine());
            assertEquals(true, activeRecord.getHasLicence());
            //add fishing gear
            assertEquals("OwnerNameA", activeRecord.getOwnerName());
            assertEquals(false, activeRecord.getHasVms());
            assertEquals("Owner Address A", activeRecord.getOwnerAddress());
            assertEquals("Agent address A", activeRecord.getAssetAgentAddress());
            assertEquals("SWE", activeRecord.getCountryOfImportOrExport());
            assertEquals(SegmentFUP.CA3, activeRecord.getSegment());
            assertEquals(PublicAidEnum.EG, activeRecord.getPublicAid());
            assertEquals("REGNUMBER11", activeRecord.getRegistrationNumber());
            assertEquals(TypeOfExportEnum.SM, activeRecord.getTypeOfExport());
            assertEquals("fleetsync", activeRecord.getUpdatedBy());
            assertEquals("TST031221985", activeRecord.getCfr());
            assertEquals("IMOIMAA", activeRecord.getImo());
            assertEquals("IRCS", activeRecord.getIrcs());
            assertEquals("MMSIMMSAA", activeRecord.getMmsi());
            //add contact info
            assertEquals("ICCATiccAA", activeRecord.getIccat());
            assertEquals("8740999", activeRecord.getUvi());
            assertEquals("GFCMGFAA", activeRecord.getGfcm());
            ZonedDateTime dateOfEventLatest
                    = ZonedDateTime.ofInstant(activeRecord.getDateOfEvent().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-05-20T02:00Z[UTC]", dateOfEventLatest.toString());
        } catch (NoAssetEntityFoundException e) {
            fail();
        }

        //when
        //2 new records come, two updates, including for the last existing and active, and sync is again run
        assetRawHistoryDao.cleanUpRawRecordsTable();
        records = recordHelper.createTwoUpdateRecordsIncludingTheActiveRecord();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //then
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertNotNull(asset);
            assertEquals("SCRI", asset.getIRCS());
            assertEquals("F", asset.getIrcsIndicator());
            assertEquals("MMSIMMSBB", asset.getMMSI());
            assertEquals("IMOIMBB", asset.getIMO());
            assertEquals("1998", asset.getConstructionYear());
            assertEquals("AMSTERDAMA", asset.getConstructionPlace());
            assertEquals("1999", asset.getVessYearofcommissioning());
            assertEquals("12", asset.getCommissionMonth());
            assertEquals("15", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.WOOD, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());
            assertEquals("ICCATiccBB", asset.getIccat());
            assertEquals("8740111", asset.getUvi());
            assertEquals("GFCMGFBB", asset.getGfcm());
            ZonedDateTime updateTime
                    = ZonedDateTime.ofInstant(asset.getUpdateTime().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-05-20T01:17:17Z[UTC]", updateTime.toString());

            List<AssetHistory> records = asset.getHistories();
            assertEquals(4, records.size());

            AssetHistory olderRecord = records.stream()
                    .filter(ah -> "6FB734930B9CAB5DB275F9A8AA38BB71".equals(ah.getHashKey()))
                    .reduce((ah1, ah2) -> {
                        throw new IllegalStateException("Multiple elements with same hash key.");
                    }).get();
            ZonedDateTime dateOfEvent
                    = ZonedDateTime.ofInstant(olderRecord.getDateOfEvent().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-04-20T11:11:11Z[UTC]", dateOfEvent.toString());
            assertEquals("8000826", olderRecord.getUvi());
            assertEquals("MMSIAASII", olderRecord.getMmsi());
            assertEquals("GFCAAFCM", olderRecord.getGfcm());
            assertEquals("IMAAMOI", olderRecord.getImo());
            assertEquals("GER", olderRecord.getCountryOfImportOrExport());
            assertEquals(TypeOfExportEnum.SM, olderRecord.getTypeOfExport());
            assertEquals(false, olderRecord.getActive());

            AssetHistory activeRecord = records.stream()
                    .filter(ah -> "6FB734930B9CAB5DB275F9A8AA38BB72".equals(ah.getHashKey()))
                    .reduce((ah1, ah2) -> {
                        throw new IllegalStateException("Multiple elements with same hash key.");
                    }).get();
            assertEquals(true, activeRecord.getActive());
            assertEquals("NOR", activeRecord.getCountryOfRegistration());
            assertEquals(EventCodeEnum.CST, activeRecord.getEventCode());
            assertEquals("Vessel Name B", activeRecord.getName());
            assertEquals("ExtMarktB", activeRecord.getExternalMarking());
            assertEquals(false, activeRecord.getAssetAgentIsAlsoOwner());
            assertEquals(BigDecimal.valueOf(2055L, 2), activeRecord.getLengthOverAll());
            assertEquals(BigDecimal.valueOf(2088L, 2), activeRecord.getLengthBetweenPerpendiculars());
            assertEquals(BigDecimal.valueOf(9989, 2), activeRecord.getSafteyGrossTonnage());
            assertEquals(BigDecimal.valueOf(1182L, 2), activeRecord.getOtherTonnage());
            assertEquals(BigDecimal.valueOf(9700, 2), activeRecord.getGrossTonnage());
            assertEquals(UnitTonnage.OSLO, activeRecord.getGrossTonnageUnit());
            assertEquals("PLACEOFREGBB", activeRecord.getPortOfRegistration());
            assertEquals(BigDecimal.valueOf(65900,2), activeRecord.getPowerOfMainEngine());
            assertEquals(BigDecimal.valueOf(21200,2), activeRecord.getPowerOfAuxEngine());
            assertEquals(true, activeRecord.getHasLicence());
            //add fishing gear
            assertEquals("OwnerNameB", activeRecord.getOwnerName());
            assertEquals(true, activeRecord.getHasVms());
            assertEquals("Owner Address B", activeRecord.getOwnerAddress());
            assertEquals("Agent address B", activeRecord.getAssetAgentAddress());
            assertEquals("POL", activeRecord.getCountryOfImportOrExport());
            //assertEquals(SegmentFUP.MFL, activeRecord.getSegment());
            assertEquals(PublicAidEnum.AE, activeRecord.getPublicAid());
            assertEquals("REGNUMBER22", activeRecord.getRegistrationNumber());
            assertEquals(TypeOfExportEnum.EX, activeRecord.getTypeOfExport());
            assertEquals("fleetsync", activeRecord.getUpdatedBy());
            assertEquals("TST031221985", activeRecord.getCfr());
            assertEquals("IMOIMBB", activeRecord.getImo());
            assertEquals("SCRI", activeRecord.getIrcs());
            assertEquals("MMSIMMSBB", activeRecord.getMmsi());
            //add contact info
            assertEquals("ICCATiccBB", activeRecord.getIccat());
            assertEquals("8740111", activeRecord.getUvi());
            assertEquals("GFCMGFBB", activeRecord.getGfcm());
            ZonedDateTime dateOfEventLatest
                    = ZonedDateTime.ofInstant(activeRecord.getDateOfEvent().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-05-21T22:22:22Z[UTC]", dateOfEventLatest.toString());
        } catch (NoAssetEntityFoundException e) {
            fail();
        }
    }

    @Test
    public void whenUpdatesOnLatestActiveRecord_thenNewRecordIsAddedAndAssetUpdated() {
        //given
        //we have an asset with 2 records...
        records = recordHelper.createRawRecordsForNewAssetAndDistinctNewRecords();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //when
        //1 new record comes, that is an update for the last existing and active, and sync is again run
        assetRawHistoryDao.cleanUpRawRecordsTable();
        records = recordHelper.createRawRecordsForExistingAssetTargetingMostRecentAndActiveExistingRecordUpdate();
        assetRawHistoryDao.createRawHistoryEntry(records);
        processorService.syncRawRecordsWithExisting();
        //then
        try {
            AssetEntity asset = assetDao.getAssetByCfrWithHistory("TST031221985");
            assertEquals("8740828", asset.getUvi());
            assertEquals("OPER", asset.getIRCS());
            assertEquals("T", asset.getIrcsIndicator());
            assertEquals("MMSIMMSI5", asset.getMMSI());
            assertEquals("IMOIMO9", asset.getIMO());
            assertEquals("1985", asset.getConstructionYear());
            assertEquals("CONSTRUCTION_PLACE", asset.getConstructionPlace()); //place of registration == place of construction ??
            assertEquals("1985", asset.getVessYearofcommissioning());
            assertEquals("05", asset.getCommissionMonth());
            assertEquals("31", asset.getCommissionDay());
            assertEquals(HullMaterialEnum.METAL, asset.getHullMaterial());
            assertEquals("fleetsync", asset.getUpdatedBy());
            assertEquals("GFCMGFC4", asset.getGfcm());
            assertEquals(2, asset.getHistories().size());
            //add here checks for AssetHistory

            List<AssetHistory> records = asset.getHistories();

            AssetHistory record = records.stream()
                    .filter(ah -> "6FB734930B9CAB5DB275F9A8AA38BB29".equals(ah.getHashKey()))
                    .reduce((ah1, ah2) -> {
                        throw new IllegalStateException("Multiple elements with same hash key.");
                    }).get();
            assertEquals(true, record.getActive());
            assertEquals("BEL", record.getCountryOfRegistration());
            assertEquals(EventCodeEnum.MOD, record.getEventCode());
            assertEquals("BBB944", record.getName());
            assertEquals("Z.122", record.getExternalMarking());
            assertEquals(null, record.getAssetAgentIsAlsoOwner());
            assertEquals(BigDecimal.valueOf(2108,2), record.getLengthOverAll());
            assertEquals(BigDecimal.valueOf(1800,2), record.getLengthBetweenPerpendiculars());
            assertEquals(BigDecimal.valueOf(0,2), record.getSafteyGrossTonnage());
            assertEquals(BigDecimal.valueOf(4767,2), record.getOtherTonnage());
            assertEquals(BigDecimal.valueOf(6700,2), record.getGrossTonnage());
            assertEquals(UnitTonnage.LONDON, record.getGrossTonnageUnit());
            assertEquals("ZEEBR", record.getPortOfRegistration());
            assertEquals(BigDecimal.valueOf(22100,2), record.getPowerOfMainEngine());
            assertEquals(BigDecimal.valueOf(0,2), record.getPowerOfAuxEngine());
            assertEquals(true, record.getHasLicence());
            //add fishing gear
            assertEquals("No Name", record.getOwnerName());
            assertEquals(false, record.getHasVms());
            assertEquals("BBB", record.getOwnerAddress());
            assertEquals("BBB", record.getAssetAgentAddress());
            assertEquals("BEL", record.getCountryOfImportOrExport());
            assertEquals(SegmentFUP.CA2, record.getSegment());
            assertEquals(PublicAidEnum.AE, record.getPublicAid());
            assertEquals("111", record.getRegistrationNumber());
            assertEquals(TypeOfExportEnum.SM, record.getTypeOfExport());
            assertEquals("fleetsync", record.getUpdatedBy());
            assertEquals("TST031221985", record.getCfr());
            assertEquals("IMOIMO9", record.getImo());
            assertEquals("OPER", record.getIrcs());
            assertEquals("MMSIMMSI5", record.getMmsi());
            //add contact info
            assertEquals("ICCATiccat", record.getIccat());
            assertEquals("8740828", record.getUvi());
            assertEquals("GFCMGFC4", record.getGfcm());
            assertEquals(null, record.getIrcsIndicator());
            assertEquals(null, record.getHullMaterial());
            ZonedDateTime dateOfEvent
                    = ZonedDateTime.ofInstant(record.getDateOfEvent().toInstant(), ZoneId.of("UTC"));
            assertEquals("2020-04-18T00:00Z[UTC]", dateOfEvent.toString());
        } catch (NoAssetEntityFoundException e) {
            fail();
        }
    }
}
