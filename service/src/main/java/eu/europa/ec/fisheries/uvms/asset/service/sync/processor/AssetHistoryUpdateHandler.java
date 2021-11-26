package eu.europa.ec.fisheries.uvms.asset.service.sync.processor;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@ApplicationScoped
@Slf4j
public class AssetHistoryUpdateHandler {

    @EJB
    private AssetRawHistoryDao assetRawHistoryDao;
    @EJB
    private AssetDao assetDao;
    @Inject
    private AssetHistoryRawRecordHandler rawRecordHandler;


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void updateAssetsHistory(List<String> assetsCfrToUpdate) {
        for (String cfr : assetsCfrToUpdate) {
            //updateAssetToFullHistory(cfr);
             updateHistoryOfAsset(cfr);
            log.debug("FLEET SYNC: Asset {} processed for update.", cfr);
        }
        int size = assetsCfrToUpdate.size();
        log.info("FLEET SYNC: Updates of current batch completed. Size {}. Last updated {} ",
                size, assetsCfrToUpdate.get(size-1));
    }

    //////////////////////////////////
    //  private methods
    //////////////////////////////////

    /**
     * Updates and existing asset regarding its history of records
     * @param cfr Asset's CFR
     * @return The same asset object updated
     */
    private AssetEntity updateAssetToFullHistory(String cfr) {
        AssetEntity asset = null;
        try {
            asset = getAssetByCfr(cfr);
        } catch (NoAssetEntityFoundException e) {
            return null;
        }
        List<AssetRawHistory> rawRecords = assetRawHistoryDao.getAssetRawHistoryByCfrSortedByEventDate(cfr);
        if (rawRecords.size() > 0) {
            List<AssetRawHistory> newRawRecords = removeFullyDuplicatedRecordsForAsset(rawRecords, asset);
            if (newRawRecords.size() > 0) {
                List<AssetHistory> newRecords = rawRecordHandler.mapRawHistoryToHistory(newRawRecords);
                for(AssetHistory record : newRecords) {
                    asset.addHistoryRecord(record);
                    //TODO to be fixed and re-enabled
                    //sendAssetHistoryUpdateToReporting(mapFromAssetHistoryEntity(record, asset.getGuid()));
                }
                AssetHistory mostRecentRecord = getMostRecentHistoryRecordToUpdateAsset(asset, newRecords);
                if (mostRecentRecord != null) {
                    List<AssetHistory> existingAndIncomingRecords = asset.getHistories();
                    for(AssetHistory record : existingAndIncomingRecords) {
                        record.setActive(false);
                    }
                    mostRecentRecord.setActive(true);
                    updateAssetFromMostRecentHistoryRecord(asset, mostRecentRecord);
                }
                assetDao.saveAssetWithHistory(asset); //saved asset contains its history
                log.debug("FLEET SYNC: Update asset {}. History records # {}. Processed # {}",
                        cfr, rawRecords.size(), newRecords.size());
            }
        }
        return asset;
    }


    /**
     * Updates history records for an existing asset
     * @param cfr Asset's CFR
     * @return The same asset object updated
     */
    private AssetEntity updateHistoryOfAsset(String cfr) {
        //Step 1.1 Get the new history for this vessel identifier
        List<AssetRawHistory> rawRecords = assetRawHistoryDao.getAssetRawHistoryByCfrSortedByEventDate(cfr);
        //Step 1.2 Get the existing history for this vessel identifier
        List<AssetHistory> existingHistory = null;
        AssetEntity asset  = null;
        try {
            existingHistory = assetDao.getAssetHistoryByCfr(cfr);
            if (rawRecords.size() > 0) {
                //Step 2.1 Check there are indeed new history records for this particular vessel
                List<AssetRawHistory> latestRawRecords = extractOnlyLatestRecordsForHashKeys(rawRecords);
                asset  = assetDao.getAssetByCfr(cfr);
                List<AssetRawHistory> newRawRecords =
                        processUpdatesForAsset(latestRawRecords, existingHistory, asset);
                if (newRawRecords.size() > 0) {
                    List<AssetHistory> newRecords = rawRecordHandler.mapRawHistoryToHistory(newRawRecords);
                    //Step 2.2 Save the new history records
                    for(AssetHistory record : newRecords) {
                        record.setAsset(asset);
                        //TODO to be fixed when there is time/need and re-enabled
                        //sendAssetHistoryUpdateToReporting(mapFromAssetHistoryEntity(record, asset.getGuid()));
                    }
                    AssetHistory mostRecentRecord = getMostRecentHistoryRecordToUpdateAsset(existingHistory, newRecords);
                    existingHistory.addAll(newRecords);
                    if (mostRecentRecord != null) {
                        for(AssetHistory record : existingHistory) {
                            record.setActive(false);
                        }
                        mostRecentRecord.setActive(true);
                        updateAssetFromMostRecentHistoryRecord(asset, mostRecentRecord);
                    }
                    assetDao.saveHistoryRecords(newRecords);
                    //Step 2.3 Update the asset itself if required
                    assetDao.saveAssetWithHistory(asset);
                    log.info("FLEET SYNC: Update asset {}. History records # {}. Processed # {}",
                            cfr, rawRecords.size(), newRecords.size());
                }
            }
        } catch (AssetDaoException e) {
            e.printStackTrace();
        }

        return asset;
    }

    /**
     * For each hash key, extract the newest entries from the list based on event date.
     * If the hash  key is unique collect that one.
     * @param allIncomingRecords List of history records for one asset, ordered by date, newest first in the list
     * @return the initial list filtered by above rule
     */
    private List<AssetRawHistory> extractOnlyLatestRecordsForHashKeys(List<AssetRawHistory> allIncomingRecords) {
        Set<String> hashKeysToKeep = new HashSet<>();
        allIncomingRecords.removeIf(r -> !hashKeysToKeep.add(r.getHashKey()));
        return allIncomingRecords;
    }

    private AssetEntity getAssetByCfr(String assetCfr) throws NoAssetEntityFoundException {
        return assetDao.getAssetByCfrWithHistory(assetCfr);
    }

    private List<AssetRawHistory> removeFullyDuplicatedRecordsForAsset(List<AssetRawHistory> incomingRawRecords,
                                                                       AssetEntity asset) {
        List<AssetHistory> currentRecords = asset.getHistories();
        List<AssetRawHistory> newRawRecords = new ArrayList<>();
        for (AssetRawHistory rawRecord : incomingRawRecords) {
            RawRecordStatus rawRecordStatus = getCurrentRawRecordStatus(currentRecords, rawRecord);
            if (rawRecordStatus.isNew) {
                newRawRecords.add(rawRecord);
            } else if (rawRecordStatus.isUpdate) {
                newRawRecords.add(rawRecord);
                asset.removeHistoryRecord(rawRecordStatus.recordToBeUpdated);
                assetRawHistoryDao.flushCurrentChanges();
            }
        }
        return newRawRecords;
    }

    /**
     * Process the updates for the existing records using incoming records regarding the asset
     * @param incomingRawRecords all incoming records for the asset
     * @param currentRecords existing records for the asset
     * @param asset whose history records will be processed
     * @return the remaining incoming records that are not updates, but new
     */
    private List<AssetRawHistory> processUpdatesForAsset(List<AssetRawHistory> incomingRawRecords,
                                                         List<AssetHistory> currentRecords,
                                                         AssetEntity asset) {
        List<AssetRawHistory> newRawRecords = new ArrayList<>();
        List<RawRecordStatus> duplicatesToBeUpdated = new ArrayList<>();
        for (AssetRawHistory rawRecord : incomingRawRecords) {
            RawRecordStatus rawRecordStatus = getCurrentRawRecordStatus(currentRecords, rawRecord);
            if (rawRecordStatus.isNew) {
                newRawRecords.add(rawRecord);
            } else if (rawRecordStatus.isUpdate) {
                duplicatesToBeUpdated.add(rawRecordStatus);
            }
        }
        List<AssetHistory> updatableRecords = prepareUpdatesForAssetHistoryAndAssset(duplicatesToBeUpdated, asset);
        assetDao.saveHistoryRecords(updatableRecords);
        assetDao.saveAssetWithHistory(asset);
        return newRawRecords;
    }

    private List<AssetHistory> prepareUpdatesForAssetHistoryAndAssset(List<RawRecordStatus> items, AssetEntity asset) {
        List<AssetHistory> updatableRecords = new ArrayList<>();
        for (RawRecordStatus item : items ) {
            AssetRawHistory rawRecord = item.recordToUpdateFrom;
            AssetHistory existingRecord = item.recordToBeUpdated;

            Date dateOfEvent = rawRecord.getDateOfEvent();
            String ownerName = rawRecord.getOwnerName();
            String ownerAddress = rawRecord.getOwnerAddress();
            String agentAddress = rawRecord.getAgentAddress();
            String name = rawRecord.getName();
            String uvi = rawRecord.getUvi();
            String ircs = rawRecord.getIrcs();
            String externalMarking = rawRecord.getExternalMarking();
            String cfr = rawRecord.getCfr();
            String iccat = rawRecord.getIccat();
            BigDecimal loa = rawRecord.getLengthOverAll();
            BigDecimal lbp = rawRecord.getLengthBetweenPerpendiculars();
            BigDecimal powerOfMain = rawRecord.getPowerOfMainEngine();
            BigDecimal powerOfAux = rawRecord.getPowerOfAuxEngine();
            BigDecimal grossTonnage = rawRecord.getGrossTonnage();
            BigDecimal otherTonnage = rawRecord.getOtherTonnage();
            Boolean hasLicence = rawRecord.getHasLicence();
            String countryOfReg = rawRecord.getCountryOfRegistration();
            String placeOfReg = rawRecord.getPlaceOfRegistration();
            String countryOfImpOrExp = rawRecord.getCountryOfImportOrExport();
            String registrationNumber = rawRecord.getRegistrationNumber();
            String publicAid = rawRecord.getPublicAid();
            String mmsi = rawRecord.getMmsi();
            String imo = rawRecord.getImo();
            String typeOfExport = rawRecord.getTypeOfExport();
            String gfcm = rawRecord.getGfcm();
            String segment = rawRecord.getSegment();
            String eventCodeType = rawRecord.getEventCodeType();
            Date updateTime = rawRecord.getUpdateTime();
            BigDecimal safteyGrossTonnage = rawRecord.getSafteyGrossTonnage();
            String grossTonnageUnit = rawRecord.getGrossTonnageUnit();
            String mainFishingGearType = rawRecord.getMainFishingGearType();
            String subFishingGearType = rawRecord.getSubFishingGearType();
            Date dateOfConstruction  = rawRecord.getDateOfConstruction();
            String placeOfConstruction = rawRecord.getConstructionPlace();
            Date dateOfCommission = rawRecord.getDateOfServiceEntry();
            Long hullMaterial = rawRecord.getHullMaterial();
            Boolean agentIsAlsoOnwer = rawRecord.getAssetAgentIsAlsoOwner();
            Boolean hasIrcs = rawRecord.getHasIrcs();
            Boolean hasVms = rawRecord.getHasVms();

            PublicAidEnum publicAidEnumValCurrent = existingRecord.getPublicAid();
            TypeOfExportEnum typeOfExportEnumValCurrent = existingRecord.getTypeOfExport();
            SegmentFUP segmentEnumValCurrent = existingRecord.getSegment();
            EventCodeEnum eventCodeEnumValCurrent = existingRecord.getEventCode();
            UnitTonnage grossTonnageUnitEnumValCurrent = existingRecord.getGrossTonnageUnit();
            FishingGear mainFishingGearCurrent = existingRecord.getMainFishingGear();
            FishingGear subFishingGearCurrent = existingRecord.getSubFishingGear();
            HullMaterialEnum hullMaterialEnumCurrent = existingRecord.getHullMaterial();

            //if this is the current active history record for this asset then update also the asset itself
            boolean isCurrentRecordActive = existingRecord.getActive();

            if (dateOfEvent != null && !dateOfEvent.equals(existingRecord.getDateOfEvent())) {
                existingRecord.setDateOfEvent(dateOfEvent);
            }
            if (ownerName != null && !ownerName.equals(existingRecord.getOwnerName())) {
                existingRecord.setOwnerName(ownerName);
            }
            if (ownerAddress != null && !ownerAddress.equals(existingRecord.getOwnerAddress())) {
                existingRecord.setOwnerAddress(ownerAddress);
            }
            if (agentAddress != null && !agentAddress.equals(existingRecord.getAssetAgentAddress())) {
                existingRecord.setAssetAgentAddress(agentAddress);
            }
            if (name != null && !name.equals(existingRecord.getName())) {
                existingRecord.setName(name);
            }
            if (uvi != null && !uvi.equals(existingRecord.getUvi())) {
                existingRecord.setUvi(uvi);
                if (isCurrentRecordActive) {
                    asset.setUvi(uvi);
                }
            }
            if (ircs != null && !ircs.equals(existingRecord.getIrcs())) {
                existingRecord.setIrcs(ircs);
                if (isCurrentRecordActive) {
                    asset.setIRCS(ircs);
                }
            }
            if (externalMarking != null && !externalMarking.equals(existingRecord.getExternalMarking())) {
                existingRecord.setExternalMarking(externalMarking);
            }
            if (cfr != null && !cfr.equals(existingRecord.getCfr())) {
                existingRecord.setCfr(cfr);
                if (isCurrentRecordActive) {
                    asset.setCFR(cfr);
                }
            }
            if (iccat != null && !iccat.equals(existingRecord.getIccat())) {
                existingRecord.setIccat(iccat);
                if (isCurrentRecordActive) {
                    asset.setIccat(iccat);
                }
            }
            if (loa != null && !loa.equals(existingRecord.getLengthOverAll())) {
                existingRecord.setLengthOverAll(loa);
            }
            if (lbp != null && !lbp.equals(existingRecord.getLengthBetweenPerpendiculars())) {
                existingRecord.setLengthBetweenPerpendiculars(lbp);
            }
            if (powerOfMain != null && !powerOfMain.equals(existingRecord.getPowerOfMainEngine())) {
                existingRecord.setPowerOfMainEngine(powerOfMain);
            }
            if (powerOfAux != null && !powerOfAux.equals(existingRecord.getPowerOfAuxEngine())) {
                existingRecord.setPowerOfAuxEngine(powerOfAux);
            }
            if (grossTonnage != null && !grossTonnage.equals(existingRecord.getGrossTonnage())) {
                existingRecord.setGrossTonnage(grossTonnage);
            }
            if (otherTonnage != null && !otherTonnage.equals(existingRecord.getOtherTonnage())) {
                existingRecord.setOtherTonnage(otherTonnage);
            }
            if (hasLicence != null && !hasLicence.equals(existingRecord.getHasLicence())) {
                existingRecord.setHasLicence(hasLicence);
            }
            if (countryOfReg != null && !countryOfReg.equals(existingRecord.getCountryOfRegistration())) {
                existingRecord.setCountryOfRegistration(countryOfReg);
            }
            if (placeOfReg != null && !placeOfReg.equals(existingRecord.getPortOfRegistration())) {
                existingRecord.setPortOfRegistration(placeOfReg);
            }
            if (countryOfImpOrExp != null && !countryOfImpOrExp.equals(existingRecord.getCountryOfImportOrExport())) {
                existingRecord.setCountryOfImportOrExport(countryOfImpOrExp);
            }
            if (registrationNumber != null && !registrationNumber.equals(existingRecord.getRegistrationNumber())) {
                existingRecord.setRegistrationNumber(registrationNumber);
            }
            if ((publicAid != null && publicAidEnumValCurrent == null) ||
                    (publicAid != null && publicAidEnumValCurrent != null &&
                            !publicAid.equals(publicAidEnumValCurrent.name()))) {
                try {
                    existingRecord.setPublicAid(PublicAidEnum.valueOf(publicAid));
                } catch (IllegalArgumentException e) {
                    log.error("FLEET SYNC: PublicAidEnum value {} does not exist in Flux FMC. No update will be made.",
                            publicAid);
                    log.error(e.getMessage());
                }
            }
            if (mmsi != null && !mmsi.equals(existingRecord.getMmsi())) {
                existingRecord.setMmsi(mmsi);
                if (isCurrentRecordActive) {
                    asset.setMMSI(mmsi);
                }
            }
            if (imo != null && !imo.equals(existingRecord.getImo())) {
                existingRecord.setImo(imo);
                if (isCurrentRecordActive) {
                    asset.setIMO(imo);
                }
            }
            if ((typeOfExport != null && typeOfExportEnumValCurrent == null) ||
                    (typeOfExport != null && typeOfExportEnumValCurrent != null &&
                            !typeOfExport.equals(typeOfExportEnumValCurrent.name()))) {
                try {
                    existingRecord.setTypeOfExport(TypeOfExportEnum.valueOf(typeOfExport));
                } catch (IllegalArgumentException e) {
                    log.error("FLEET SYNC: TypeOfExportEnum value {} does not exist in FluxFmc. {} will not be updated.",
                            typeOfExport, existingRecord.getCfr());
                    log.error(e.getMessage());
                }
            }
            if (gfcm != null && !gfcm.equals(existingRecord.getGfcm())) {
                existingRecord.setGfcm(gfcm);
                if (isCurrentRecordActive) {
                    asset.setGfcm(gfcm);
                }
            }
            if ((segment != null && segmentEnumValCurrent == null) ||
                    (segment != null && segmentEnumValCurrent != null &&
                            !segment.equals(segmentEnumValCurrent.name()))) {
                try {
                    existingRecord.setSegment(SegmentFUP.valueOf(segment));
                } catch (IllegalArgumentException e) {
                    log.error("FLEET SYNC: SegmentFUP enum value {} does not exist in FluxFmc. {} will not be updated.",
                            segment, existingRecord.getCfr());
                    log.error(e.getMessage());
                }
            }
            if ((eventCodeType != null && eventCodeEnumValCurrent == null) ||
                    (eventCodeType != null && eventCodeEnumValCurrent != null &&
                            !eventCodeType.equals(eventCodeEnumValCurrent.name()))) {
                try {
                    existingRecord.setEventCode(EventCodeEnum.valueOf(eventCodeType));
                } catch (IllegalArgumentException e) {
                    log.error("FLEET SYNC: EventCodeEnum value {} does not exist in FluxFmc. {} will not be updated.",
                            eventCodeType, existingRecord.getCfr());
                    log.error(e.getMessage());
                }
            }
            if (updateTime != null && !updateTime.equals(existingRecord.getUpdateTime())) {
                existingRecord.setUpdateTime(updateTime);
                if (isCurrentRecordActive) {
                    asset.setUpdateTime(updateTime);
                }
            }
            if (safteyGrossTonnage != null && !safteyGrossTonnage.equals(existingRecord.getSafteyGrossTonnage())) {
                existingRecord.setSafteyGrossTonnage(safteyGrossTonnage);
            }
            if ((grossTonnageUnit != null && grossTonnageUnitEnumValCurrent == null) ||
                    (grossTonnageUnit != null && grossTonnageUnitEnumValCurrent != null &&
                            !grossTonnageUnit.equals(grossTonnageUnitEnumValCurrent.name()))) {
                try {
                    existingRecord.setGrossTonnageUnit(UnitTonnage.valueOf(grossTonnageUnit));
                } catch (IllegalArgumentException e) {
                    log.error("FLEET SYNC: UnitTonnage enum value {} does not exist in FluxFmc. {} will not be updated.",
                            grossTonnageUnit, existingRecord.getCfr());
                    log.error(e.getMessage());
                }
            }
            if (mainFishingGearType != null && mainFishingGearCurrent != null &&
                    !mainFishingGearType.equals(mainFishingGearCurrent.getCode())) {
                existingRecord.getMainFishingGear().setCode(mainFishingGearType); //TODO test this; other fields need update?
            }
            if (subFishingGearType != null && subFishingGearCurrent != null &&
                    !subFishingGearType.equals(subFishingGearCurrent.getCode())) {
                existingRecord.getSubFishingGear().setCode(subFishingGearType); //TODO test this; other fields need update?
            }
            if (dateOfConstruction != null && !dateOfConstruction.equals(existingRecord.getConstructionDate())) {
                existingRecord.setConstructionDate(dateOfConstruction);
                if (isCurrentRecordActive) {
                    asset.setConstructionYear(String.valueOf(dateOfConstruction.getYear()+1900));
                }
            }
            if (placeOfConstruction != null && !placeOfConstruction.equals(existingRecord.getPlaceOfConstruction())) {
                existingRecord.setPlaceOfConstruction(placeOfConstruction);
                if (isCurrentRecordActive) {
                    asset.setConstructionPlace(placeOfConstruction);
                }
            }
            if (dateOfCommission != null && !dateOfCommission.equals(existingRecord.getCommissionDate())) {
                existingRecord.setCommisionDate(dateOfCommission);
                if (isCurrentRecordActive) {
                    ZonedDateTime dateTime = ZonedDateTime.ofInstant(dateOfCommission.toInstant(), ZoneOffset.systemDefault());
                    asset.setCommissionDay(StringUtils.leftPad(Integer.toString(dateTime.getDayOfMonth()),2, '0'));
                    asset.setCommissionMonth(StringUtils.leftPad(Integer.toString(dateTime.getMonthValue()), 2,'0'));
                    asset.setCommissionYear(Integer.toString(dateTime.getYear()));
                }
            }
            if ((hullMaterial != null && hullMaterialEnumCurrent == null) ||
                    (hullMaterial != null && hullMaterialEnumCurrent != null &&
                            !hullMaterial.equals(hullMaterialEnumCurrent.getId()))) {
                HullMaterialEnum hullMaterialEnum = HullMaterialEnum.getType(hullMaterial);
                existingRecord.setHullMaterial(hullMaterialEnum);
                if (isCurrentRecordActive) {
                    asset.setHullMaterial(hullMaterialEnum);
                }
            }
            if (agentIsAlsoOnwer != null && !agentIsAlsoOnwer.equals(existingRecord.getAssetAgentIsAlsoOwner())) {
                existingRecord.setAssetAgentIsAlsoOwner(agentIsAlsoOnwer);
            }
            if (isCurrentRecordActive) {
                String ircsIndicator = Boolean.TRUE.equals(hasIrcs) ? "T" : "F";
                asset.setIrcsIndicator(ircsIndicator);
            }
            if (hasVms != null && isCurrentRecordActive) {
                existingRecord.setHasVms(hasVms);
            }

            updatableRecords.add(existingRecord);
        }
        return updatableRecords;
    }

    private AssetHistory getMostRecentHistoryRecordToUpdateAsset(AssetEntity asset,
                                                                 List<AssetHistory> incomingRecords) {
        AssetHistory mostRecentIncomingRecord = incomingRecords.get(0);
        Instant mostRecentIncomingEventDate = mostRecentIncomingRecord.getDateOfEvent().toInstant();

        List<AssetHistory> currentRecords = asset.getHistories();
        boolean recentIncomingIsMostRecent = true;
        for (AssetHistory record : currentRecords) {
            if (mostRecentIncomingEventDate.isBefore(record.getDateOfEvent().toInstant())) {
                recentIncomingIsMostRecent = false;
                break;
            }
        }
        if (recentIncomingIsMostRecent && mostRecentIncomingRecord.getActive()) {
            return mostRecentIncomingRecord;
        }
        return null;
    }

    private AssetHistory getMostRecentHistoryRecordToUpdateAsset(List<AssetHistory> currentRecords,
                                                                 List<AssetHistory> incomingRecords) {
        AssetHistory mostRecentIncomingRecord = incomingRecords.get(0);
        Instant mostRecentIncomingEventDate = mostRecentIncomingRecord.getDateOfEvent().toInstant();

        boolean recentIncomingIsMostRecent = true;
        for (AssetHistory record : currentRecords) {
            if (mostRecentIncomingEventDate.isBefore(record.getDateOfEvent().toInstant())) {
                recentIncomingIsMostRecent = false;
                break;
            }
        }
        if (recentIncomingIsMostRecent && mostRecentIncomingRecord.getActive()) {
            return mostRecentIncomingRecord;
        }
        return null;
    }

    private AssetEntity updateAssetFromMostRecentHistoryRecord(AssetEntity asset,
                                                               AssetHistory mostRecentRecord) {
        asset.setCFR(mostRecentRecord.getCfr());
        asset.setUvi(mostRecentRecord.getUvi());
        asset.setIRCS(mostRecentRecord.getIrcs());
        asset.setIccat(mostRecentRecord.getIccat());
        asset.setGfcm(mostRecentRecord.getGfcm());
        asset.setMMSI(mostRecentRecord.getMmsi());

        asset.setUpdatedBy(mostRecentRecord.getUpdatedBy());

        asset.setIMO(mostRecentRecord.getImo());
        asset.setIrcsIndicator(mostRecentRecord.getIrcsIndicator());
        asset.setConstructionPlace(mostRecentRecord.getPlaceOfConstruction());
        asset.setHullMaterial(mostRecentRecord.getHullMaterial());
        Optional.ofNullable(mostRecentRecord.getConstructionDate())
                .ifPresent(date -> {
                    int year = ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault()).getYear();
                    asset.setConstructionYear(Integer.toString(year));
                });
        Optional.ofNullable(mostRecentRecord.getCommissionDate()).ifPresent(date -> {
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
            asset.setCommissionDay(StringUtils.leftPad(Integer.toString(dateTime.getDayOfMonth()),2, '0'));
            asset.setCommissionMonth(StringUtils.leftPad(Integer.toString(dateTime.getMonthValue()), 2,'0'));
            asset.setCommissionYear(Integer.toString(dateTime.getYear()));
        });
        return asset;
    }

    private RawRecordStatus getCurrentRawRecordStatus(List<AssetHistory> records, AssetRawHistory rawRecord) {
        String targetHashKey = rawRecord.getHashKey();
        if (targetHashKey == null) {
            return new RawRecordStatus(false, false); //due to the lack of a hash key disregard the new raw record
        }
        for (AssetHistory record : records) {
            if (targetHashKey.equals( record.getHashKey() )) {
                boolean isUpdate = isCurrentRawRecordAnUpdate(record, rawRecord);
                return new RawRecordStatus(false, isUpdate, record, rawRecord);
            }
        }
        return new RawRecordStatus(true, false);
    }

    private boolean isCurrentRawRecordAnUpdate(AssetHistory duplicatedRecord, AssetRawHistory rawRecord) {
        Date dateOfEvent = rawRecord.getDateOfEvent();
        String ownerName = rawRecord.getOwnerName();
        String ownerAddress = rawRecord.getOwnerAddress();
        String agentAddress = rawRecord.getAgentAddress();
        String name = rawRecord.getName();
        String uvi = rawRecord.getUvi();
        String ircs = rawRecord.getIrcs();
        String externalMarking = rawRecord.getExternalMarking();
        String cfr = rawRecord.getCfr();
        String iccat = rawRecord.getIccat();
        BigDecimal loa = rawRecord.getLengthOverAll();
        BigDecimal lbp = rawRecord.getLengthBetweenPerpendiculars();
        BigDecimal powerOfMain = rawRecord.getPowerOfMainEngine();
        BigDecimal powerOfAux = rawRecord.getPowerOfAuxEngine();
        BigDecimal grossTonnage = rawRecord.getGrossTonnage();
        BigDecimal otherTonnage = rawRecord.getOtherTonnage();
        Boolean hasLicence = rawRecord.getHasLicence();
        String countryOfReg = rawRecord.getCountryOfRegistration();
        String placeOfReg = rawRecord.getPlaceOfRegistration();
        String countryOfImpOrExp = rawRecord.getCountryOfImportOrExport();
        String registrationNumber = rawRecord.getRegistrationNumber();
        String publicAid = rawRecord.getPublicAid();
        PublicAidEnum publicAidEnumVal = duplicatedRecord.getPublicAid();
        String mmsi = rawRecord.getMmsi();
        String imo = rawRecord.getImo();
        String typeOfExport = rawRecord.getTypeOfExport();
        TypeOfExportEnum typeOfExportEnumVal = duplicatedRecord.getTypeOfExport();
        String gfcm = rawRecord.getGfcm();
        String segment = rawRecord.getSegment();
        SegmentFUP segmentEnumVal = duplicatedRecord.getSegment();
        String eventCodeType = rawRecord.getEventCodeType();
        EventCodeEnum eventCodeEnumVal = duplicatedRecord.getEventCode();
        Date updateTime = rawRecord.getUpdateTime();
        BigDecimal safteyGrossTonnage = rawRecord.getSafteyGrossTonnage();
        String grossTonnageUnit = rawRecord.getGrossTonnageUnit();
        UnitTonnage grossTonnageUnitEnumVal = duplicatedRecord.getGrossTonnageUnit();
        String mainFishingGearType = rawRecord.getMainFishingGearType();
        FishingGear mainFishingGear = duplicatedRecord.getMainFishingGear();
        String subFishingGearType = rawRecord.getSubFishingGearType();
        FishingGear subFishingGear = duplicatedRecord.getSubFishingGear();

        boolean status =
                !(dateOfEvent == null || dateOfEvent.equals(duplicatedRecord.getDateOfEvent())) ||
                !(ownerName == null || ownerName.equals(duplicatedRecord.getOwnerName())) ||
                !(ownerAddress == null || ownerAddress.equals(duplicatedRecord.getOwnerAddress())) ||
                !(agentAddress == null || agentAddress.equals(duplicatedRecord.getAssetAgentAddress())) ||
                !(name == null || name.equals(duplicatedRecord.getName())) ||
                !(uvi == null || uvi.equals(duplicatedRecord.getUvi())) ||
                !(ircs == null || ircs.equals(duplicatedRecord.getIrcs())) ||
                !(externalMarking == null || externalMarking.equals(duplicatedRecord.getExternalMarking())) ||
                !(cfr == null || cfr.equals(duplicatedRecord.getCfr())) ||
                !(iccat == null || iccat.equals(duplicatedRecord.getIccat())) ||
                !(loa == null || loa.equals(duplicatedRecord.getLengthOverAll())) ||
                !(lbp == null || lbp.equals(duplicatedRecord.getLengthBetweenPerpendiculars())) ||
                !(powerOfMain == null || powerOfMain.equals(duplicatedRecord.getPowerOfMainEngine())) ||
                !(powerOfAux == null || powerOfAux.equals(duplicatedRecord.getPowerOfAuxEngine())) ||
                !(grossTonnage == null || grossTonnage.equals(duplicatedRecord.getGrossTonnage())) ||
                !(otherTonnage == null || otherTonnage.equals(duplicatedRecord.getOtherTonnage())) ||
                !(hasLicence == null || hasLicence.equals(duplicatedRecord.getHasLicence())) ||
                !(countryOfReg == null || countryOfReg.equals(duplicatedRecord.getCountryOfRegistration())) ||
                !(placeOfReg == null || placeOfReg.equals(duplicatedRecord.getPortOfRegistration())) ||
                !(countryOfImpOrExp == null ||
                        countryOfImpOrExp.equals(duplicatedRecord.getCountryOfImportOrExport())) ||
                !(registrationNumber == null ||
                        registrationNumber.equals(duplicatedRecord.getRegistrationNumber())) ||
                !(publicAid == null || publicAidEnumVal == null ||
                        publicAid.equals(publicAidEnumVal.name())) ||
                !(mmsi == null || mmsi.equals(duplicatedRecord.getMmsi())) ||
                !(imo == null || imo.equals(duplicatedRecord.getImo())) ||
                !(typeOfExport == null || typeOfExportEnumVal == null ||
                        typeOfExport.equals(typeOfExportEnumVal.name())) ||
                !(gfcm == null || gfcm.equals(duplicatedRecord.getGfcm())) ||
                !(segment == null || segmentEnumVal == null || segment.equals(segmentEnumVal.name())) ||
                !(eventCodeType == null || eventCodeEnumVal == null ||
                        eventCodeType.equals(eventCodeEnumVal.name())) ||
//                !(updateTime == null || updateTime.equals(duplicatedRecord.getUpdateTime())) ||
                !(safteyGrossTonnage == null ||
                        safteyGrossTonnage.equals(duplicatedRecord.getSafteyGrossTonnage())) ||
                !(grossTonnageUnit == null || grossTonnageUnitEnumVal == null ||
                        grossTonnageUnit.equals(grossTonnageUnitEnumVal.name())) ||
                !(mainFishingGearType == null || mainFishingGear == null ||
                        mainFishingGearType.equals(mainFishingGear.getCode())) ||
                !(subFishingGearType == null || subFishingGear == null ||
                        subFishingGearType.equals(subFishingGear.getCode()));
        return status;
    }

    @AllArgsConstructor
    @RequiredArgsConstructor
    private final static class RawRecordStatus {
        private final boolean isNew;
        private final boolean isUpdate;
        private AssetHistory recordToBeUpdated = null;
        private AssetRawHistory recordToUpdateFrom = null;
    }
}
