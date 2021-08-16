package eu.europa.ec.fisheries.uvms.dao.bean;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Stateless
@Slf4j
public class AssetRawHistoryDaoBean extends Dao implements AssetRawHistoryDao {

    @Override
    public AssetRawHistory createRawHistoryEntry(AssetRawHistory assetRawHistory) {
        em.persist(assetRawHistory);
        return assetRawHistory;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createRawHistoryEntry(List<AssetRawHistory> assetHistoryRecords) {
        int recordsListSize = assetHistoryRecords.size();
        IntStream.range(0, recordsListSize).forEach( idx -> {
                em.persist(assetHistoryRecords.get(idx));
                /* if (idx % 1000 == 0) { em.flush(); } */
            });
    }

    @Override
    public void saveAssetWithHistory(AssetEntity asset) {
       log.debug("FLEET SYNC: Saving asset: {}", asset.getCFR());
       em.persist(asset);
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
    public void saveAssets(List<AssetEntity> assets) {
        for(AssetEntity asset : assets) {
            em.persist(asset);
        }
    }

    @Override
    public AssetEntity getAssetByCfrWithHistory(String cfr) throws NoAssetEntityFoundException {
        try {
            TypedQuery<AssetEntity> query =
                    em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_CFR_WITH_HISTORY, AssetEntity.class);
            query.setParameter("cfr", cfr);
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoAssetEntityFoundException("No asset found for " + cfr);
        }
    }

    @Override
    public AssetEntity getAssetByCfr(String cfr) {
        TypedQuery<AssetEntity> query =
                em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_CFR, AssetEntity.class);
        query.setParameter("cfr", cfr);
        return query.getSingleResult();
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
