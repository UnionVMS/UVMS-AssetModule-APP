package eu.europa.ec.fisheries.uvms.asset.service.sync.processor;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.service.bean.ReportingProducerBean;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.FishingGearDao;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import eu.europa.ec.fisheries.uvms.entity.model.*;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class AssetHistoryRawRecordHandler {

    @EJB
    private ReportingProducerBean reportingProducer;
    @EJB
    private FishingGearDao fishingGearDao;

    static final String FLEETSYNC = "fleetsync";
    private static final String FISHING_GEAR_UNKNOWN = "NK";
    private static final int TTL_IN_MINUTES = 30;
    private final Instant lastUpdateOfAllFishingGear =
            Instant.now().minus(TTL_IN_MINUTES, ChronoUnit.MINUTES);
    private ExecutorService executorService = null;
    private TreeMap<String, FishingGear> allExistingFishingGear = null;


    @PostConstruct
    public void init() {
        executorService = Executors.newWorkStealingPool();
        allExistingFishingGear = getAllExistingFishingGear();
    }

    @PreDestroy
    public void cleanUp() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("FLEET SYNC: Process of creating raw history records interrupted.");
        } finally {
            if (!executorService.isTerminated()) {
                log.info("FLEET SYNC: Force shutdown during the raw history records syncing.");
            }
            executorService.shutdownNow();
        }
    }

    List<AssetHistory> mapRawHistoryToHistory(List<AssetRawHistory> rawRecords) {
        List<AssetHistory> records = new ArrayList<>();
        rawRecords.stream().forEachOrdered(rawRecord -> {
            AssetHistory assetHistory = convertRawRecordToAppRecord(rawRecord);
            records.add(assetHistory);
        });
        return records;
    }


    private AssetHistory convertRawRecordToAppRecord(AssetRawHistory rawRecord) {
        AssetHistory record = new AssetHistory();

        record.setHashKey(rawRecord.getHashKey());
        String eventCodeType = rawRecord.getEventCodeType();
        if (EnumUtils.isValidEnum(EventCodeEnum.class, eventCodeType)) {
            record.setEventCode(EventCodeEnum.valueOf(eventCodeType));
        } else {
            record.setEventCode(EventCodeEnum.UNK);
        }
        record.setActive(rawRecord.getEventActive());
        record.setDateOfEvent(rawRecord.getDateOfEvent());
        record.setUpdatedBy(FLEETSYNC);

        record.setIccat(rawRecord.getIccat());
        record.setGfcm(rawRecord.getGfcm());
        record.setCfr(rawRecord.getCfr());
        record.setUvi(rawRecord.getUvi());
        record.setIrcs(rawRecord.getIrcs());
        record.setName(rawRecord.getName());
        record.setExternalMarking(rawRecord.getExternalMarking());
        record.setMmsi(rawRecord.getMmsi());

        String segmentFup = rawRecord.getSegment();
        if(EnumUtils.isValidEnum(SegmentFUP.class, segmentFup)) {
            record.setSegment(SegmentFUP.valueOf(segmentFup));
        } else {
            log.debug("Segment for asset {} does not exist.", record.getCfr());
        }

        record.setCountryOfRegistration(rawRecord.getCountryOfRegistration());
        record.setRegistrationNumber(rawRecord.getRegistrationNumber());
        record.setHasLicence(rawRecord.getHasLicence());
        //TODO check this business logic
        record.setPortOfRegistration(rawRecord.getPlaceOfRegistration());

        String typeOfExport = rawRecord.getTypeOfExport();
        if (EnumUtils.isValidEnum(TypeOfExportEnum.class, typeOfExport)) {
            record.setTypeOfExport(TypeOfExportEnum.valueOf(typeOfExport));
        } else {
            record.setTypeOfExport(TypeOfExportEnum.EX);
        }

        createMainFishingGear(rawRecord, record);
        createSubFishingGear(rawRecord, record);
        record.setType(GearFishingTypeEnum.UNKNOWN);

        record.setLengthOverAll(rawRecord.getLengthOverAll());
        record.setLengthBetweenPerpendiculars(rawRecord.getLengthBetweenPerpendiculars());
        if (rawRecord.getGrossTonnageUnit() != null) {
            try {
                record.setGrossTonnageUnit(UnitTonnage.getType(rawRecord.getGrossTonnageUnit()));
            } catch (AssetModelValidationException e) {
                e.printStackTrace();
                record.setGrossTonnageUnit(UnitTonnage.LONDON);
            }
            record.setGrossTonnage(rawRecord.getGrossTonnage());
        }
        record.setOtherTonnage(rawRecord.getOtherTonnage());
        record.setSafteyGrossTonnage(rawRecord.getSafteyGrossTonnage());
        if (rawRecord.getHullMaterial() != null) {
            record.setHullMaterial(HullMaterialEnum.getType(rawRecord.getHullMaterial()));
        } else {
            record.setHullMaterial(HullMaterialEnum.UNKNOWN);
        }

        String publicAid = rawRecord.getPublicAid();
        if (EnumUtils.isValidEnum(PublicAidEnum.class, publicAid)) {
            record.setPublicAid(PublicAidEnum.valueOf(publicAid));
        } else {
            record.setPublicAid(PublicAidEnum.AE);
        }
        record.setHasVms(rawRecord.getHasVms());
        String hasIrcs = Boolean.TRUE.equals(rawRecord.getHasIrcs()) ? "T" : "F";
        record.setIrcsIndicator(hasIrcs);

        record.setPowerOfMainEngine(rawRecord.getPowerOfMainEngine());
        record.setPowerOfAuxEngine(rawRecord.getPowerOfAuxEngine());

        List<ContactInfo> contacts = new ArrayList<>();
        record.setOwnerAddress(rawRecord.getOwnerAddress());
        record.setOwnerName(rawRecord.getOwnerName());
        record.setAssetAgentAddress(rawRecord.getAgentAddress());
        record.setImo(rawRecord.getImo());
        ContactInfo owner = new ContactInfo();
        owner.setOwner(true);
        owner.setEmail(rawRecord.getOwnerEmailAddress());
        String ownerName = rawRecord.getOwnerName();
        if (ownerName == null || ownerName.isEmpty()) {
            owner.setName("- -");
        } else {
            owner.setName(ownerName);
        }
        owner.setPhoneNumber(rawRecord.getOwnerPhoneNumber());
        owner.setSource(ContactInfoSourceEnum.INTERNAL);
        owner.setUpdatedBy(FLEETSYNC);
        owner.setAsset(record);
        contacts.add(owner);

        ContactInfo agent = new ContactInfo();
        agent.setOwner(false);
        agent.setEmail(rawRecord.getAgentEmailAddress());
        String agentName = rawRecord.getAgentName();
        if (agentName == null || agentName.isEmpty()) {
            agent.setName("- -");
        } else {
            agent.setName(agentName);
        }
        agent.setPhoneNumber(rawRecord.getAgentPhoneNumber());
        agent.setSource(ContactInfoSourceEnum.INTERNAL);
        agent.setUpdatedBy(FLEETSYNC);
        agent.setAsset(record);
        contacts.add(agent);

        record.setContactInfo(contacts);

        record.setPlaceOfConstruction(rawRecord.getConstructionPlace());
        record.setConstructionDate(rawRecord.getDateOfConstruction());
        record.setCommisionDate(rawRecord.getDateOfServiceEntry());
        record.setUpdateTime(rawRecord.getUpdateTime());

        record.setCountryOfImportOrExport(rawRecord.getCountryOfImportOrExport());

        record.setAssetAgentIsAlsoOwner(rawRecord.getAssetAgentIsAlsoOwner());

        return record;
    }

    void createMainFishingGear(AssetRawHistory rawRecord, AssetHistory assetHistoryRecord) {
        allExistingFishingGear = getAllExistingFishingGear();
        String newFishingGearCode = rawRecord.getMainFishingGearType();
        if (newFishingGearCode != null && !allExistingFishingGear.isEmpty()
                && allExistingFishingGear.containsKey(newFishingGearCode)) {
            assetHistoryRecord.setMainFishingGear(allExistingFishingGear.get(newFishingGearCode));
        } else {
            assetHistoryRecord.setMainFishingGear(allExistingFishingGear.get(FISHING_GEAR_UNKNOWN));
        }
    }

    void createSubFishingGear(AssetRawHistory rawRecord, AssetHistory assetHistoryRecord) {
        allExistingFishingGear = getAllExistingFishingGear();
        String newFishingGearCode = rawRecord.getSubFishingGearType();
        if (newFishingGearCode != null && !allExistingFishingGear.isEmpty()
                && allExistingFishingGear.containsKey(newFishingGearCode)) {
            assetHistoryRecord.setSubFishingGear(allExistingFishingGear.get(newFishingGearCode));
        } else {
            assetHistoryRecord.setSubFishingGear(allExistingFishingGear.get(FISHING_GEAR_UNKNOWN));
        }
    }


    void updateMainFishingGear(AssetHistory assetHistoryRecord) {
        allExistingFishingGear = getAllExistingFishingGear();
        FishingGear incomingMainFishingGear = assetHistoryRecord.getMainFishingGear();

        if (incomingMainFishingGear != null) {
            String incomingFishingGearCode = incomingMainFishingGear.getCode();
            if (allExistingFishingGear.containsKey(incomingFishingGearCode)) {
                assetHistoryRecord.setMainFishingGear(allExistingFishingGear.get(incomingFishingGearCode));
            }
        } else if (!allExistingFishingGear.isEmpty()) {
            assetHistoryRecord.setMainFishingGear(allExistingFishingGear.get(FISHING_GEAR_UNKNOWN));
        }
    }

    void updateSubFishingGear(AssetHistory assetHistoryRecord) {
        allExistingFishingGear = getAllExistingFishingGear();
        FishingGear incomingSubFishingGear = assetHistoryRecord.getSubFishingGear();

        if (incomingSubFishingGear != null) {
            String incomingFishingGearCode = incomingSubFishingGear.getCode();
            if (allExistingFishingGear.containsKey(incomingFishingGearCode)) {
                assetHistoryRecord.setSubFishingGear(allExistingFishingGear.get(incomingFishingGearCode));
            }
        } else if (!allExistingFishingGear.isEmpty()) {
            assetHistoryRecord.setSubFishingGear(allExistingFishingGear.get(FISHING_GEAR_UNKNOWN));
        }
    }

    private TreeMap<String, FishingGear> getAllExistingFishingGear() {
        boolean timeFrameExpired = Instant.now()
                .isAfter(lastUpdateOfAllFishingGear.plus(TTL_IN_MINUTES, ChronoUnit.MINUTES));

        if (allExistingFishingGear == null || timeFrameExpired) {
            List<FishingGear> fishingGearList = fishingGearDao.getAllFishingGear();
            allExistingFishingGear = fishingGearList.stream()
                    .collect(Collectors.toMap(FishingGear::getCode,
                            Function.identity(),
                            (fg1, fg2) -> fg1,
                            TreeMap::new));
        }
        return allExistingFishingGear;
    }

    /////////////////////////////////////////////////

    private void sendAssetHistoryUpdateToReporting(Asset asset) {
        try {
            log.info("FLEET SYNC: sent update to reporting for {}", asset.getCfr());
            Map<String, String> params = new HashMap<>();
            params.put("mainTopic", "reporting");
            params.put("subTopic", "asset");
            reportingProducer.sendMessageToSpecificQueueSameTx(
                    AssetModuleRequestMapper.createUpsertAssetModuleResponse(asset),
                    reportingProducer.getDestination(),
                    null,
                    params);
        } catch (MessageException | AssetModelMarshallException e) {
            log.error("Could not send asset update to reporting", e);
        }
    }

    private Asset mapFromAssetHistoryEntity(AssetHistory assetHist, String assetGuid) {
        Asset asset = new Asset();
        asset.setCfr(assetHist.getCfr());
        asset.setIrcs(assetHist.getIrcs());
        asset.setIccat(assetHist.getIccat());
        asset.setUvi(assetHist.getUvi());
        asset.setGfcm(assetHist.getGfcm());
        asset.setExternalMarking(assetHist.getExternalMarking());
        asset.setName(assetHist.getName());
        asset.setCountryCode(assetHist.getCountryOfRegistration());
        asset.setGearType(assetHist.getMainFishingGear().getCode());
        asset.setLengthOverAll(assetHist.getLengthOverAll());

        AssetId assetId = new AssetId();
        assetId.setGuid(assetGuid);
        assetId.setType(AssetIdType.GUID);
        asset.setAssetId(assetId);
        asset.setActive(assetHist.getActive());
        AssetHistoryId assetHistoryId = new AssetHistoryId();
        assetHistoryId.setEventId(assetHist.getGuid());
        asset.setEventHistory(assetHistoryId);
        return asset;
    }

    private void sendUpdatesToReportingForMultipleAssets(List<AssetEntity> assets) {
        assets.stream().forEach(asset -> {
            final String assetGuid = asset.getGuid();
            asset.getHistories().stream().forEach(record -> {
                sendAssetHistoryUpdateToReporting(mapFromAssetHistoryEntity(record, assetGuid));
            });
        });
    }

}