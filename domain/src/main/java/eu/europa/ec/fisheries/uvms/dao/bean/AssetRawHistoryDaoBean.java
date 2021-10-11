package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Stateless
@Slf4j
public class AssetRawHistoryDaoBean implements AssetRawHistoryDao {

    @PersistenceContext(unitName = "asset-batch")
    protected EntityManager em;

    @Override
    public AssetRawHistory createRawHistoryEntry(AssetRawHistory assetRawHistory) {
        em.persist(assetRawHistory);
        return assetRawHistory;
    }

    @Override
    public void createRawHistoryEntry(List<AssetRawHistory> assetHistoryRecords) {
        int recordsListSize = assetHistoryRecords.size();
        IntStream.range(0, recordsListSize).forEach( idx -> {
                if (idx % 1000 == 0 && idx > 0) {
                    em.flush();
                    em.clear();
                }
                em.persist(assetHistoryRecords.get(idx));
            });
    }

    @Override
    public List<AssetRawHistory> getAssetRawHistoryByCfrSortedByEventDate(String cfr) {
        TypedQuery<AssetRawHistory> query =
                em.createNamedQuery(UvmsConstants.FIND_ASSET_BY_CFR_ORDER_BY_EVENT_DESC, AssetRawHistory.class);
        query.setParameter("cfr", cfr);
        return query.getResultList();
    }

    @Override
    public List<String> getAllDistinctRawCfrs() {
        List<String> cfrs;
        try {
            TypedQuery<String> query =
                    em.createNamedQuery(UvmsConstants.FIND_ALL_DISTINCT_RAW_CFRS, String.class);
             cfrs = query.getResultList();
        } catch (NoResultException e) {
            log.error("No asset found by CFR :(");
            cfrs = new ArrayList<>();
        }
        return cfrs;
    }

    @Override
    public List<String> getAllCfrsSorted() {
        List<String> cfrs;
        try {
            TypedQuery<String> query =
                    em.createNamedQuery("Asset.getAllCfrsSorted", String.class);
            cfrs = query.getResultList();
        } catch (NoResultException e) {
            log.error("No asset found by CFR :(");
            cfrs = new ArrayList<>();
        }
        return cfrs;
    }

    @Override
    public int deleteOldDuplicatedHistoryRecords(List<AssetHistory> incomingPartiallyDuplicatedRecords) {
       for (AssetHistory record : incomingPartiallyDuplicatedRecords) {
           em.remove(record);
       }
       em.flush();
       return incomingPartiallyDuplicatedRecords.size();
    }

    @Override
    public void flushCurrentChanges() {
        em.flush();
    }

    @Override
    public int cleanUpRawRecordsTable() {
        return em.createQuery("DELETE FROM AssetRawHistory AS arh").executeUpdate();
    }
}
