package eu.europa.ec.fisheries.uvms.asset.service.sync;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.service.bean.ReportingProducerBean;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.FishingGearDao;
import eu.europa.ec.fisheries.uvms.dao.FishingGearTypeDao;
import eu.europa.ec.fisheries.uvms.entity.asset.types.CarrierSourceEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.Carrier;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetHistoryId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Slf4j
public class AssetHistoryRecordHandler {

    private static final String FLEETSYNC = "fleetsync";
    private static final int TTL_IN_MINUTES = 30;
    private static final String FISHING_GEAR_UNKNOWN = "NK";

    private List<FishingGear> allFishingGear = null;
    private Instant lastUpdateOfAllFishingGear = Instant.now().minus(TTL_IN_MINUTES, ChronoUnit.MINUTES);

    @EJB
    private AssetDao assetDao;

    @EJB
    private ReportingProducerBean reportingProducer;

    @EJB
    private FishingGearTypeDao fishingGearTypeDao;

    @EJB
    private FishingGearDao fishingGearDao;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void handleRecord(AssetHistory assetHistoryRecord) {
        String assetGuid = null, assetHistoryGuid = null;
        AssetHistory assetHistory = getAssetHistoryByHashKey(assetHistoryRecord);
        if (assetHistory != null) {
            // asset history found
            updateExistingAssetHistoryEntry(assetHistory, assetHistoryRecord);
            //get info for reporting msg
            if (assetHistory.getAsset() != null) {
                assetGuid = assetHistory.getAsset().getGuid();
            }
            assetHistoryGuid = assetHistory.getGuid();
        } else {
            AssetEntity assetByCfr = getAssetByCfr(assetHistoryRecord);
            if (assetByCfr != null) {
                // asset with CFR exists attach new history to it
                addNewAssetHistoryEntry(assetHistoryRecord, assetByCfr);
                //get info for reporting msg
                if (assetByCfr.getGuid() != null) {
                    assetGuid = assetByCfr.getGuid();
                }
                assetHistoryGuid = assetHistoryRecord.getGuid();
            } else {
                // create new asset entity and attach the history update to it
                AssetEntity assetEntity = createAssetEntityFrom(assetHistoryRecord);
                saveNewAssetEntity(assetHistoryRecord, assetEntity);
                assetGuid = assetEntity.getGuid();
                assetHistoryGuid = assetHistoryRecord.getGuid();
            }
        }
        // send to reporting to keep synced its asset history table
        sendAssetUpdateToReporting(mapFromAssetHistoryEntity(assetHistoryRecord, assetGuid, assetHistoryGuid));
    }


    private void addNewAssetHistoryEntry(AssetHistory assetHistoryRecord, AssetEntity assetByCfr) {
        assetHistoryRecord.setGuid(UUID.randomUUID().toString());
        setMainFishingGear(assetHistoryRecord);
        setSubFishingGear(assetHistoryRecord);
        assetHistoryRecord.getContactInfo().forEach(c -> c.setAsset(assetHistoryRecord));
        assetHistoryRecord.setAsset(assetByCfr);
        Optional<AssetHistory> anyEventMoreRecent = assetByCfr.getHistories().stream()
                .filter(e -> e.getDateOfEvent().toInstant().isAfter(assetHistoryRecord.getDateOfEvent().toInstant()))
                .findAny();
        if (anyEventMoreRecent.isPresent()) {
            assetHistoryRecord.setActive(false);
        } else {
            assetHistoryRecord.setActive(true);
            assetHistoryRecord.getAsset().getHistories().stream()
                    .filter(e -> e.getDateOfEvent().toInstant().isBefore(assetHistoryRecord.getDateOfEvent().toInstant()))
                    .collect(Collectors.toList())
                    .forEach(e -> e.setActive(false));
            // update the main asset fields accordingly
            updateAssetFieldsFromActiveHistory(assetByCfr, assetHistoryRecord);
        }
        assetByCfr.getHistories().add(assetHistoryRecord);
    }

    private void updateAssetFieldsFromActiveHistory(AssetEntity assetByCfr, AssetHistory assetHistoryRecord) {
        assetByCfr.setCFR(assetHistoryRecord.getCfr());
        assetByCfr.setUvi(assetHistoryRecord.getUvi());
        assetByCfr.setIRCS(assetHistoryRecord.getIrcs());
        assetByCfr.setIccat(assetHistoryRecord.getIccat());
        assetByCfr.setGfcm(assetHistoryRecord.getGfcm());
        assetByCfr.setMMSI(assetHistoryRecord.getMmsi());
        assetByCfr.setUpdatedBy(assetHistoryRecord.getUpdatedBy());

        assetByCfr.setIMO(assetHistoryRecord.getImo());
        assetByCfr.setIrcsIndicator(assetHistoryRecord.getIrcsIndicator());
        assetByCfr.setConstructionPlace(assetHistoryRecord.getPortOfRegistration());
        assetByCfr.setHullMaterial(assetHistoryRecord.getHullMaterial());
        // check dates +1 or year etc
        assetByCfr.setConstructionYear(Optional.ofNullable(assetHistoryRecord.getConstructionDate()).map(v -> String.valueOf(v.getYear() + 1900)).orElse(null));
        assetByCfr.setCommissionDay(Optional.ofNullable(assetHistoryRecord.getCommissionDate()).map(v -> StringUtils.leftPad(String.valueOf(v.getDate()), 2, "0")).orElse(null));
        assetByCfr.setCommissionMonth(Optional.ofNullable(assetHistoryRecord.getCommissionDate()).map(v -> StringUtils.leftPad(String.valueOf(v.getMonth() + 1), 2, "0")).orElse(null));
        assetByCfr.setCommissionYear(Optional.ofNullable(assetHistoryRecord.getCommissionDate()).map(v -> String.valueOf(v.getYear() + 1900)).orElse(null));
//        asset.setUpdateTime();
    }

    private AssetEntity getAssetByCfr(AssetHistory assetHistoryRecord) {
        AssetEntity assetByCfr;
        try {
            assetByCfr = assetDao.getAssetByCfr(assetHistoryRecord.getCfr());
        } catch (AssetDaoException ex) {
            assetByCfr = null;
        }
        return assetByCfr;
    }

    private AssetHistory getAssetHistoryByHashKey(AssetHistory assetHistoryRecord) {
        AssetHistory assetHistory;
        try {
            assetHistory = assetDao.getAssetHistoryByHashKey(assetHistoryRecord.getHashKey());
        } catch (AssetDaoException e) {
            assetHistory = null;
        }
        return assetHistory;
    }

    private void saveNewAssetEntity(AssetHistory assetHistoryRecord, AssetEntity assetEntity) {
        try {
            assetDao.createAsset(assetEntity);
        } catch (AssetDaoException e) {
            log.error("Could not create asset for new history entry with hashKey {}", assetHistoryRecord.getHashKey());
        }
    }

    private AssetEntity createAssetEntityFrom(AssetHistory assetHistoryRecord) {
        AssetEntity asset = new AssetEntity();
        asset.setCFR(assetHistoryRecord.getCfr());
        asset.setUvi(assetHistoryRecord.getUvi());
        asset.setIRCS(assetHistoryRecord.getIrcs());
        asset.setIccat(assetHistoryRecord.getIccat());
        asset.setGfcm(assetHistoryRecord.getGfcm());
        asset.setMMSI(assetHistoryRecord.getMmsi());
        asset.setUpdatedBy(assetHistoryRecord.getUpdatedBy());


        asset.setIMO(assetHistoryRecord.getImo());
        asset.setIrcsIndicator(assetHistoryRecord.getIrcsIndicator());
        asset.setConstructionPlace(assetHistoryRecord.getPortOfRegistration());
        asset.setHullMaterial(assetHistoryRecord.getHullMaterial());
        // check dates +1 or year etc
        asset.setConstructionYear(Optional.ofNullable(assetHistoryRecord.getConstructionDate()).map(v -> String.valueOf(v.getYear() + 1900)).orElse(null));
        asset.setCommissionDay(Optional.ofNullable(assetHistoryRecord.getCommissionDate()).map(v -> StringUtils.leftPad(String.valueOf(v.getDate()), 2, "0")).orElse(null));
        asset.setCommissionMonth(Optional.ofNullable(assetHistoryRecord.getCommissionDate()).map(v -> StringUtils.leftPad(String.valueOf(v.getMonth() + 1), 2, "0")).orElse(null));
        asset.setCommissionYear(Optional.ofNullable(assetHistoryRecord.getCommissionDate()).map(v -> String.valueOf(v.getYear() + 1900)).orElse(null));
//        asset.setUpdateTime();

        Carrier carrier = new Carrier();
        carrier.setUpdatedBy(FLEETSYNC);
        carrier.setSource(CarrierSourceEnum.XEU); // from where this value?
        carrier.setActive(true); // from where this value?
//        carrier.setUpdatetime();
        asset.setCarrier(carrier);
        asset.setNotes(new ArrayList<>());

        List<AssetHistory> assetHistoryList = new ArrayList<>();
        assetHistoryRecord.setActive(true); // since it is the only one
        assetHistoryList.add(assetHistoryRecord);
        asset.setHistories(assetHistoryList);

        if (assetHistoryRecord.getContactInfo() != null) {
            assetHistoryRecord.getContactInfo().forEach(c -> c.setAsset(assetHistoryRecord));
        }

        setMainFishingGear(assetHistoryRecord);
        setSubFishingGear(assetHistoryRecord);
        assetHistoryRecord.setType(GearFishingTypeEnum.UNKNOWN);
        assetHistoryRecord.setAsset(asset);

        return asset;
    }

    private void setMainFishingGear(AssetHistory assetHistoryRecord) {
        List<FishingGear> allFishingGear = getAllFishingGear(); // need to cache this
        Optional<FishingGear> fishingGearMain = allFishingGear.stream()
                .filter(g -> g.getCode().equals(assetHistoryRecord.getMainFishingGear().getCode())).findFirst();
        if (fishingGearMain.isPresent()) {
            assetHistoryRecord.setMainFishingGear(fishingGearMain.get());
        } else if (allFishingGear.size() > 0) {
            //set UNKNOWN fishing gear
            FishingGear fishingGearUnknown = allFishingGear.stream()
                    .filter((g->g.getCode().equalsIgnoreCase(FISHING_GEAR_UNKNOWN)))
                    .findFirst().get();
            assetHistoryRecord.setMainFishingGear(fishingGearUnknown);
        }
    }

    private void setSubFishingGear(AssetHistory assetHistoryRecord) {
        List<FishingGear> allFishingGear = getAllFishingGear(); // need to cache this
        FishingGear subFishingGear  = assetHistoryRecord.getSubFishingGear();
        if (subFishingGear != null) {
            Optional<FishingGear> fishingGearSub = allFishingGear.stream()
                    .filter(g -> g.getCode().equals(subFishingGear.getCode())).findFirst();
            if (fishingGearSub.isPresent()) {
                assetHistoryRecord.setSubFishingGear(fishingGearSub.get());
            } else if (allFishingGear.size() > 0) {
                //set UNKNOWN fishing gear
                FishingGear fishingGearUnknown = allFishingGear.stream()
                        .filter((g->g.getCode().equalsIgnoreCase(FISHING_GEAR_UNKNOWN)))
                        .findFirst().get();
                assetHistoryRecord.setSubFishingGear(fishingGearUnknown);
            }
        } else {
            log.error("Data received from Fleet App is inconsistent. Missing subsidiary fishing gear for {}.",
                    assetHistoryRecord.getCfr());
        }
    }

    private void updateExistingAssetHistoryEntry(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        updateEventInfoIfDiff(assetHistory, assetHistoryRecord);
        updateContactInfoIfDiff(assetHistory, assetHistoryRecord);
        updateVesselIdentifiersIfDiff(assetHistory, assetHistoryRecord);
        updatePhysicalCharacteristicsIfDiff(assetHistory, assetHistoryRecord);
        updateLicenseInfoIfDiff(assetHistory, assetHistoryRecord);
        updateIndicatorsIfDiff(assetHistory, assetHistoryRecord);
        updateFishingGearIfDiff(assetHistory, assetHistoryRecord);
        updateInfoIfDiff(assetHistory, assetHistoryRecord);
    }

    private void updateEventInfoIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        if (assetHistoryRecord.getDateOfEvent() != null && !assetHistoryRecord.getDateOfEvent().equals(assetHistory.getDateOfEvent())) {
            assetHistory.setDateOfEvent(assetHistoryRecord.getDateOfEvent());
        }
        if (assetHistoryRecord.getEventCode() != null && !assetHistoryRecord.getEventCode().equals(assetHistory.getEventCode())) {
            assetHistory.setEventCode(assetHistoryRecord.getEventCode());
        }

        Optional<AssetHistory> anyEventMoreRecent = assetHistory.getAsset().getHistories().stream()
                .filter(e -> e.getDateOfEvent().toInstant().isAfter(assetHistoryRecord.getDateOfEvent().toInstant()))
                .findAny();

        if (anyEventMoreRecent.isPresent()) {
            assetHistory.setActive(false);
            assetHistoryRecord.setActive(false);
        } else {
            assetHistory.setActive(true);
            assetHistoryRecord.setActive(true);
            assetHistory.getAsset().getHistories().stream()
                    .filter(e -> e.getDateOfEvent().toInstant().isBefore(assetHistoryRecord.getDateOfEvent().toInstant()))
                    .collect(Collectors.toList())
                    .forEach(e -> e.setActive(false));
            // update the main asset fields accordingly
            updateAssetFieldsFromActiveHistory(assetHistory.getAsset(), assetHistoryRecord);

        }
        if (assetHistoryRecord.getGuid() != null && !assetHistoryRecord.getGuid().equals(assetHistory.getGuid())) {
            assetHistory.setGuid(assetHistoryRecord.getGuid());
        }
        if (assetHistoryRecord.getUpdateTime() != null && !assetHistoryRecord.getUpdateTime().equals(assetHistory.getUpdateTime())) {
            assetHistory.setUpdateTime(assetHistoryRecord.getUpdateTime());
        }
        if (assetHistoryRecord.getUpdatedBy() != null && !assetHistoryRecord.getUpdatedBy().equals(assetHistory.getUpdatedBy())) {
            assetHistory.setUpdatedBy(assetHistoryRecord.getUpdatedBy());
        }
    }

    private void updateInfoIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        if (assetHistoryRecord.getTypeOfExport() != null && !assetHistoryRecord.getTypeOfExport().equals(assetHistory.getTypeOfExport())) {
            assetHistory.setTypeOfExport(assetHistoryRecord.getTypeOfExport());
        }
        if (assetHistoryRecord.getGfcm() != null && !assetHistoryRecord.getGfcm().equals(assetHistory.getGfcm())) {
            assetHistory.setGfcm(assetHistoryRecord.getGfcm());
        }

        if (assetHistoryRecord.getSegment() != null && !assetHistoryRecord.getSegment().equals(assetHistory.getSegment())) {
            assetHistory.setSegment(assetHistoryRecord.getSegment());
        }
//        if (assetHistoryRecord.getSegmentOfAdministrativeDecision() != null && !assetHistoryRecord.getSegmentOfAdministrativeDecision().equals(assetHistory.getSegmentOfAdministrativeDecision())) {
//            assetHistory.setSegmentOfAdministrativeDecision(assetHistoryRecord.getSegmentOfAdministrativeDecision());
//        }
    }

    private void updateFishingGearIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        assetHistory.setType(GearFishingTypeEnum.UNKNOWN);
        FishingGear mainFishingGear = assetHistoryRecord.getMainFishingGear();
        if (mainFishingGear != null
                && !mainFishingGear.getCode().equals(assetHistory.getMainFishingGear().getCode())) {
            setMainFishingGear(assetHistoryRecord);
            assetHistory.setMainFishingGear(mainFishingGear);
        }
        if (assetHistoryRecord.getSubFishingGear() != null && !assetHistoryRecord.getSubFishingGear().getCode().equals(assetHistory.getSubFishingGear().getCode())) {
            setSubFishingGear(assetHistoryRecord);
            assetHistory.setSubFishingGear(assetHistoryRecord.getSubFishingGear());
        }
    }

    private List<FishingGear> getAllFishingGear() {
        if (allFishingGear == null || Instant.now().isAfter(lastUpdateOfAllFishingGear.plus(TTL_IN_MINUTES, ChronoUnit.MINUTES))) {
            allFishingGear = fishingGearDao.getAllFishingGear();
        }
        return allFishingGear;
    }

    private void updateIndicatorsIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        if (assetHistoryRecord.getHasVms() != null && !assetHistoryRecord.getHasVms().equals(assetHistory.getHasVms())) {
            assetHistory.setHasVms(assetHistoryRecord.getHasVms());
        }
        if (assetHistoryRecord.getPublicAid() != null && !assetHistoryRecord.getPublicAid().equals(assetHistory.getPublicAid())) {
            assetHistory.setPublicAid(assetHistoryRecord.getPublicAid());
        }
        if (assetHistoryRecord.getMmsi() != null && !assetHistoryRecord.getMmsi().equals(assetHistory.getMmsi())) {
            assetHistory.setMmsi(assetHistoryRecord.getMmsi());
        }
        if (assetHistoryRecord.getImo() != null && !assetHistoryRecord.getImo().equals(assetHistory.getImo())) {
            assetHistory.setImo(assetHistoryRecord.getImo());
        }
    }

    private void updateLicenseInfoIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        if (assetHistoryRecord.getHasLicence() != null && !assetHistoryRecord.getHasLicence().equals(assetHistory.getHasLicence())) {
            assetHistory.setHasLicence(assetHistoryRecord.getHasLicence());
        }
        if (assetHistoryRecord.getCountryOfRegistration() != null && !assetHistoryRecord.getCountryOfRegistration().equals(assetHistory.getCountryOfRegistration())) {
            assetHistory.setCountryOfRegistration(assetHistoryRecord.getCountryOfRegistration());
        }
        if (assetHistoryRecord.getPortOfRegistration() != null && !assetHistoryRecord.getPortOfRegistration().equals(assetHistory.getPortOfRegistration())) {
            assetHistory.setPortOfRegistration(assetHistoryRecord.getPortOfRegistration());
        }
        if (assetHistoryRecord.getCountryOfImportOrExport() != null && !assetHistoryRecord.getCountryOfImportOrExport().equals(assetHistory.getCountryOfImportOrExport())) {
            assetHistory.setCountryOfImportOrExport(assetHistoryRecord.getCountryOfImportOrExport());
        }
        if (assetHistoryRecord.getLicenceType() != null && !assetHistoryRecord.getLicenceType().equals(assetHistory.getLicenceType())) {
            assetHistory.setLicenceType(assetHistoryRecord.getLicenceType());
        }

        if (assetHistoryRecord.getRegistrationNumber() != null && !assetHistoryRecord.getRegistrationNumber().equals(assetHistory.getRegistrationNumber())) {
            assetHistory.setLicenceType(assetHistoryRecord.getRegistrationNumber());
        }
    }

    private void updatePhysicalCharacteristicsIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        if (assetHistoryRecord.getLengthOverAll() != null && !assetHistoryRecord.getLengthOverAll().equals(assetHistory.getLengthOverAll())) {
            assetHistory.setLengthOverAll(assetHistoryRecord.getLengthOverAll());
        }
        if (assetHistoryRecord.getLengthBetweenPerpendiculars() != null && !assetHistoryRecord.getLengthBetweenPerpendiculars().equals(assetHistory.getLengthBetweenPerpendiculars())) {
            assetHistory.setLengthBetweenPerpendiculars(assetHistoryRecord.getLengthBetweenPerpendiculars());
        }
        if (assetHistoryRecord.getPowerOfMainEngine() != null && !assetHistoryRecord.getPowerOfMainEngine().equals(assetHistory.getPowerOfMainEngine())) {
            assetHistory.setPowerOfMainEngine(assetHistoryRecord.getPowerOfMainEngine());
        }
        if (assetHistoryRecord.getPowerOfAuxEngine() != null && !assetHistoryRecord.getPowerOfAuxEngine().equals(assetHistory.getPowerOfAuxEngine())) {
            assetHistory.setPowerOfAuxEngine(assetHistoryRecord.getPowerOfAuxEngine());
        }
        if (assetHistoryRecord.getGrossTonnage() != null && !assetHistoryRecord.getGrossTonnage().equals(assetHistory.getGrossTonnage())) {
            assetHistory.setGrossTonnage(assetHistoryRecord.getGrossTonnage());
        }
        if (assetHistoryRecord.getOtherTonnage() != null && !assetHistoryRecord.getOtherTonnage().equals(assetHistory.getOtherTonnage())) {
            assetHistory.setOtherTonnage(assetHistoryRecord.getOtherTonnage());
        }
        if (assetHistoryRecord.getGrossTonnageUnit() != null && !assetHistoryRecord.getGrossTonnageUnit().equals(assetHistory.getGrossTonnageUnit())) {
            assetHistory.setGrossTonnageUnit(assetHistoryRecord.getGrossTonnageUnit());
        }
        if (assetHistoryRecord.getSafteyGrossTonnage() != null && !assetHistoryRecord.getSafteyGrossTonnage().equals(assetHistory.getSafteyGrossTonnage())) {
            assetHistory.setSafteyGrossTonnage(assetHistoryRecord.getSafteyGrossTonnage());
        }
    }

    private void updateContactInfoIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        if (assetHistoryRecord.getOwnerName() != null && !assetHistoryRecord.getOwnerName().equals(assetHistory.getOwnerName())) {
            assetHistory.setOwnerName(assetHistoryRecord.getOwnerName());
        }
        if (assetHistoryRecord.getOwnerAddress() != null && !assetHistoryRecord.getOwnerAddress().equals(assetHistory.getOwnerAddress())) {
            assetHistory.setOwnerAddress(assetHistoryRecord.getOwnerAddress());
        }
        if (assetHistoryRecord.getAssetAgentAddress() != null && !assetHistoryRecord.getAssetAgentAddress().equals(assetHistory.getAssetAgentAddress())) {
            assetHistory.setAssetAgentAddress(assetHistoryRecord.getAssetAgentAddress());
        }
//        if (assetHistoryRecord.getAssetAgentIsAlsoOwner() != null && !assetHistoryRecord.getAssetAgentIsAlsoOwner().equals(assetHistory.getAssetAgentIsAlsoOwner())) {
//            assetHistory.setAssetAgentIsAlsoOwner(assetHistoryRecord.getAssetAgentIsAlsoOwner());
//        }

        if (assetHistoryRecord.getContactInfo() != null) {
            assetHistory.getContactInfo().clear();
            assetHistoryRecord.getContactInfo().forEach(e -> {
                assetHistory.getContactInfo().add(e);
                e.setAsset(assetHistory);
            });
        }
    }

    private void updateVesselIdentifiersIfDiff(AssetHistory assetHistory, AssetHistory assetHistoryRecord) {
        if (assetHistoryRecord.getName() != null && !assetHistoryRecord.getName().equals(assetHistory.getName())) {
            assetHistory.setName(assetHistoryRecord.getName());
        }
        if (assetHistoryRecord.getUvi() != null && !assetHistoryRecord.getUvi().equals(assetHistory.getUvi())) {
            assetHistory.setUvi(assetHistoryRecord.getUvi());
        }
        if (assetHistoryRecord.getIrcs() != null && !assetHistoryRecord.getIrcs().equals(assetHistory.getIrcs())) {
            assetHistory.setIrcs(assetHistoryRecord.getIrcs());
        }
        if (assetHistoryRecord.getExternalMarking() != null && !assetHistoryRecord.getExternalMarking().equals(assetHistory.getExternalMarking())) {
            assetHistory.setExternalMarking(assetHistoryRecord.getExternalMarking());
        }
        if (assetHistoryRecord.getCfr() != null && !assetHistoryRecord.getCfr().equals(assetHistory.getCfr())) {
            assetHistory.setCfr(assetHistoryRecord.getCfr());
        }
        if (assetHistoryRecord.getIccat() != null && !assetHistoryRecord.getIccat().equals(assetHistory.getIccat())) {
            assetHistory.setIccat(assetHistoryRecord.getIccat());
        }
    }

    private Asset mapFromAssetHistoryEntity(AssetHistory a, String assetGuid, String assetHistoryGuid) {
        Asset asset = new Asset();
        asset.setCfr(a.getCfr());
        asset.setIrcs(a.getIrcs());
        asset.setIccat(a.getIccat());
        asset.setUvi(a.getUvi());
        asset.setGfcm(a.getGfcm());
        asset.setExternalMarking(a.getExternalMarking());
        asset.setName(a.getName());
        asset.setCountryCode(a.getCountryOfRegistration());
        asset.setGearType(a.getMainFishingGear().getCode());
        asset.setLengthOverAll(a.getLengthOverAll());

        AssetId assetId = new AssetId();
        assetId.setGuid(assetGuid);
        assetId.setType(AssetIdType.GUID);
        asset.setAssetId(assetId);
        asset.setActive(a.getActive());
        AssetHistoryId assetHistoryId = new AssetHistoryId();
        assetHistoryId.setEventId(assetHistoryGuid);
        asset.setEventHistory(assetHistoryId);
        return asset;
    }

    private void sendAssetUpdateToReporting(Asset asset) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("mainTopic", "reporting");
            params.put("subTopic", "asset");
            reportingProducer.sendMessageToSpecificQueueSameTx(AssetModuleRequestMapper.createUpsertAssetModuleResponse(asset), reportingProducer.getDestination(), null, params);
        } catch (MessageException | AssetModelMarshallException e) {
            log.error("Could not send asset update to reporting", e);
        }
    }
}
