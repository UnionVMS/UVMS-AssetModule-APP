package eu.europa.ec.fisheries.uvms.asset.service.sync.processor;

import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.entity.asset.types.CarrierSourceEnum;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import eu.europa.ec.fisheries.uvms.entity.model.Carrier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class AssetHistoryCreationHandler {

    @EJB
    private AssetRawHistoryDao assetRawHistoryDao;
    @EJB
    private AssetDao assetDao;
    @Inject
    private AssetHistoryRawRecordHandler rawRecordHandler;

    //////////////////////////////////
    //  public methods
    //////////////////////////////////

    /**
     * Creates a new AssetEntity together with its asset history of records
     * @param assetCfr The vessel identifier, as CFR, to be created
     * @return The newly created AssetEntity
     */
    public AssetEntity createAssetWithFullHistory(String assetCfr) {
        AssetEntity asset = null;
        List<AssetRawHistory> rawRecordsFull = assetRawHistoryDao.getAssetRawHistoryByCfrSortedByEventDate(assetCfr);
        List<AssetRawHistory> rawRecords = removeDuplicatedHashKeys(rawRecordsFull);
        if (rawRecords.size() > 0) {
            List<AssetHistory> records = rawRecordHandler.mapRawHistoryToHistory(rawRecords);

            asset = createAssetEntityFromHistoryRecord(records.get(0));

            for(AssetHistory record : records) {
                record.setAsset(asset);
                record.setActive(false);
            }
            records.get(0).setActive(true);
            asset.setHistories(records);
        }
        return asset;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createAssets(List<AssetEntity> assetsToCreate) {
        assetDao.saveAssets(assetsToCreate);
        //TODO to be fixed and re-enabled
        //sendUpdatesToReportingForMultipleAssets(assetsToCreate);
    }

    //////////////////////////////////
    //  private methods
    //////////////////////////////////

    private List<AssetRawHistory> removeDuplicatedHashKeys(List<AssetRawHistory> rawRecordsFull) {
        HashSet<Object> existsCurrently = new HashSet<>();
        //removes the duplicates but keeps the newest thanks to descending order in the list
        rawRecordsFull.removeIf(record -> !existsCurrently.add(record.getHashKey()));
        return rawRecordsFull;
    }

    private AssetEntity createAssetEntityFromHistoryRecord(AssetHistory record) {
        AssetEntity asset = new AssetEntity();

        asset.setCFR(record.getCfr());
        asset.setUvi(record.getUvi());
        asset.setIRCS(record.getIrcs());
        asset.setIccat(record.getIccat());
        asset.setGfcm(record.getGfcm());
        asset.setMMSI(record.getMmsi());
        asset.setUpdatedBy(record.getUpdatedBy());
        asset.setIMO(record.getImo());
        Optional.ofNullable(record.getIrcsIndicator()).ifPresent(ircs -> {
            asset.setIrcsIndicator(ircs.substring(0,1));
        });
        asset.setConstructionPlace(record.getPlaceOfConstruction());
        asset.setHullMaterial(record.getHullMaterial());
        asset.setConstructionYear(
                Optional.ofNullable(record.getConstructionDate())
                        .map(v -> String.valueOf(v.getYear() + 1900))
                        .orElse(null));
        asset.setCommissionDay(
                Optional.ofNullable(record.getCommissionDate())
                        .map(v -> StringUtils.leftPad(String.valueOf(v.getDate()), 2, "0"))
                        .orElse(null));
        asset.setCommissionMonth(
                Optional.ofNullable(record.getCommissionDate())
                        .map(v -> StringUtils.leftPad(String.valueOf(v.getMonth() + 1), 2, "0"))
                        .orElse(null));
        asset.setCommissionYear(
                Optional.ofNullable(
                        record.getCommissionDate()).map(v -> String.valueOf(v.getYear() + 1900))
                        .orElse(null));

        Carrier carrier = new Carrier();
        carrier.setUpdatedBy(rawRecordHandler.FLEETSYNC);
        carrier.setSource(CarrierSourceEnum.XEU);
        carrier.setActive(true);

        asset.setCarrier(carrier);
        asset.setNotes(new ArrayList<>());

        if (record.getContactInfo() != null) {
            record.getContactInfo().forEach(c -> c.setAsset(record));
        }

        //rawRecordHandler.updateMainFishingGear(record);
        //rawRecordHandler.updateSubFishingGear(record);
        //record.setType(GearFishingTypeEnum.UNKNOWN);

        return asset;
    }
}
