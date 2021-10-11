package eu.europa.ec.fisheries.uvms.dao;

import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;

import javax.ejb.Local;
import java.util.List;

@Local
public interface AssetRawHistoryDao {

    AssetRawHistory createRawHistoryEntry(AssetRawHistory assetRawHistory);

    void createRawHistoryEntry(List<AssetRawHistory> assetHistoryRecords);

    int cleanUpRawRecordsTable();

    int deleteOldDuplicatedHistoryRecords(List<AssetHistory> duplicatedRecords);

    void flushCurrentChanges();

    List<String> getAllCfrsSorted();

    List<String> getAllDistinctRawCfrs();

    List<AssetRawHistory> getAssetRawHistoryByCfrSortedByEventDate(String cfr);

}
