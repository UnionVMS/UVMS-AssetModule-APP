package eu.europa.ec.fisheries.uvms.dao;

import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;

import javax.ejb.Local;
import java.util.List;

@Local
public interface AssetRawHistoryDao {

    AssetRawHistory createRawHistoryEntry(AssetRawHistory assetRawHistory);

    void createRawHistoryEntry(List<AssetRawHistory> assetHistoryRecords);

    int cleanUpRawRecordsTable();

    void deleteAsset(AssetEntity asset);

    void deleteAssetByCfr(String assetByCfr);

    int deleteOldDuplicatedHistoryRecords(List<AssetHistory> duplicatedRecords);

    void flushCurrentChanges();

    List<String> getAllCfrsSorted();

    List<String> getAllDistinctRawCfrs();

    AssetEntity getAssetByCfr(String cfr);

    AssetEntity getAssetByCfrWithHistory(String cfr) throws NoAssetEntityFoundException;

    List<AssetRawHistory> getAssetRawHistoryByCfrSortedByEventDate(String cfr);

    void saveAssets(List<AssetEntity> assets);

    void saveAssetWithHistory(AssetEntity assetEntity);

}
