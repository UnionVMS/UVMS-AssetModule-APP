package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    public void truncateLongFieldsInRawRecordsEntries() {
        Long nano = System.nanoTime();
         Query query = em.createNativeQuery("UPDATE asset.assetrawhistoryrecords " +
                                 "SET assetrawhist_addressowner = LEFT(assetrawhist_addressowner, ?) " +
                                 "WHERE LENGTH(assetrawhist_addressowner) > ?") ;
         query.setParameter(1, 100);
         query.setParameter(2, 100);
         query.executeUpdate();

         query = em.createNativeQuery("UPDATE asset.assetrawhistoryrecords " +
                                 "SET assetrawhist_addressagent = LEFT(assetrawhist_addressagent, ?) " +
                                 "WHERE LENGTH(assetrawhist_addressagent) > ?") ;
        query.setParameter(1, 100);
        query.setParameter(2, 100);
        query.executeUpdate();

        query = em.createNativeQuery("UPDATE asset.assetrawhistoryrecords " +
                "SET assetrawhist_emailowner = LEFT(assetrawhist_emailowner, ?) " +
                "WHERE LENGTH(assetrawhist_emailowner) > ?") ;
        query.setParameter(1, 40);
        query.setParameter(2, 40);
        query.executeUpdate();

        query = em.createNativeQuery("UPDATE asset.assetrawhistoryrecords " +
                "SET assetrawhist_emailagent = LEFT(assetrawhist_emailagent, ?) " +
                "WHERE LENGTH(assetrawhist_emailagent) > ?") ;
        query.setParameter(1, 40);
        query.setParameter(2, 40);
        query.executeUpdate();
        Long timeA = System.nanoTime();
        log.info("FLEET SYNC: Truncate columns took {} ms", TimeUnit.NANOSECONDS.toMillis(timeA-nano));
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
