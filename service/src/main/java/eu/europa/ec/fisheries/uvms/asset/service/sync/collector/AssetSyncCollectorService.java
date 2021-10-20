package eu.europa.ec.fisheries.uvms.asset.service.sync.collector;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetSyncException;
import eu.europa.ec.fisheries.uvms.asset.service.sync.AssetSyncClient;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Singleton
@Slf4j
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AssetSyncCollectorService {

    @Inject
    private AssetSyncClient assetSyncClient;

    @EJB
    private AssetRawHistoryDao assetRawHistoryDao;

    @Resource
    ManagedExecutorService managedExecutorService;

    @Resource
    TimerService timerService;

    private static final long TIME_TO_CANCEL_COLLECTION = 2*60*60*1000;
    private static final long TIME_TO_NEXT_CHECK = 1*60*1000;

    private List<Future<?>> results;
    private boolean activityStarted;
    private boolean activityCompleted;
    private boolean activitySuccessfullyCompleted;
    private ReentrantReadWriteLock lock;

    //////////////////////////////////
    //  initialization and cleanup
    //////////////////////////////////

    @PostConstruct
    private void init() {
        activityStarted = false;
        activityCompleted = false;
        activitySuccessfullyCompleted = true;
        lock = new ReentrantReadWriteLock();
        results = new ArrayList<>();
    }

    //////////////////////////////////
    //  public methods
    //////////////////////////////////
    /**
     * Retrieve the data from Fleet service and store it on Flux FMC side
     * @param startPageIndex Page of history records to start with
     * @param getSinglePage True if just the specified page should be retrieved, or also the next ones
     * @param userPageSize The page size to be retrieved in one go from Fleet service
     * @param defaultPageSize The default page size if user page size is 0 or negative
     */
    public void collectDataFromFleet(Integer startPageIndex, Boolean getSinglePage,
                                     Integer userPageSize, Integer defaultPageSize) {
        checkStartDataCollection();
        prepareFleetStorage();
        results.clear();

        Integer pageSize = userPageSize <= 0 ? defaultPageSize : userPageSize;
        log.info("FLEET SYNC: Asset synchronization collection started with page size {}.", pageSize);
        if (getSinglePage) {
            getSinglePageFromFleet(startPageIndex, pageSize);
        } else {
            collectAndSaveMultipleFleetPages(startPageIndex, pageSize);
        }
        collectSyncActivityResults();
    }

    public boolean checkStartDataCollection() {
        log.info("FLEET SYNC: Checking asset sync state...");

        if (activityStarted) {
            log.info("FLEET SYNC: Asset sync already in progress. Skipping the request.");
            return false;
        } else {
            lock.writeLock().lock();
            try {
                if (!activityStarted) {
                    activityStarted = true;
                    activityCompleted = false;
                    activitySuccessfullyCompleted = false;
                    log.info("FLEET SYNC: Start collecting fleet data.");
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        //create timer to cancel the results if exceeds 2 hours
        timerService.createTimer(TIME_TO_CANCEL_COLLECTION, "FLEET SYNC: Cancel-sync-collector task timer.");

        return true;
    }

    public boolean isCollectingActivityStarted() {
        return activityStarted;
    }

    public boolean isCollectingActivityCompleted() {
        return activityCompleted;
    }

    public boolean isCollectingActivitySuccessfullyCompleted() {
        return activitySuccessfullyCompleted;
    }

    public void resetSyncCollectorState() {
        for (Future<?> result : results) {
            result.cancel(true);
        }
        activityCompleted = false;
        activitySuccessfullyCompleted = false;
        activityStarted = false;
    }


    //////////////////////////////////
    //  private methods
    //////////////////////////////////

    @Timeout
    private void cancelAssetCollector(Timer timer) {
        resetSyncCollectorState();
        activityCompleted = true;
    }

    private void collectSyncActivityResults() {

        //blocking check for results
        while(!areTasksDone(results)) {
            try {
                Thread.sleep(TIME_TO_NEXT_CHECK);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        activityCompleted = true;
        activitySuccessfullyCompleted = true;
        activityStarted = false;
        log.info("FLEET SYNC: Sync collecting steps completed.");
    }

    private boolean areTasksDone(List<Future<?>> results) {
        boolean allDone = true;
        for (Future<?> result : results) {
            allDone = result.isDone() && allDone;
        }
        return  allDone;
    }

    private void prepareFleetStorage() {
        assetRawHistoryDao.cleanUpRawRecordsTable();
        log.info("FLEET SYNC: Raw records sync table prepared.");
    }


    private Integer getSinglePageFromFleet(Integer pageIndex, Integer pageSize) {
        List<AssetRawHistory> currentAssetsPageAsList = getRawAssetsPageSafe(pageIndex, pageSize);
        Future<?> result = saveRawRecords(currentAssetsPageAsList);
        if (results != null) {
            results.add(result);
        }
        return currentAssetsPageAsList.size();
    }

    private Future<?> saveRawRecords(List<AssetRawHistory> historyRecords) {
        return managedExecutorService.submit(() -> {
            assetRawHistoryDao.createRawHistoryEntry(historyRecords);
        });
    }

    private void collectAndSaveMultipleFleetPages(Integer startPage, Integer pageSize) {
        Integer startPageIndex = new Integer(startPage);
        boolean moreAssetsExist = true;
        log.info("FLEET SYNC: Start collecting from page {} with batch size {}", startPage, pageSize);
        while (moreAssetsExist) {
            Integer retrievedRecordsCount = getSinglePageFromFleet(startPageIndex, pageSize);
            moreAssetsExist = pageSize.equals(retrievedRecordsCount);
            log.info("FLEET SYNC: Collected page {} with {} records", startPageIndex, retrievedRecordsCount);
            startPageIndex++;
        }
    }

    private List<AssetRawHistory> getRawAssetsPageSafe(Integer pageNumber, Integer pageSize) {
        try {
            return assetSyncClient.getRawAssetsPage(pageNumber, pageSize);
        } catch (AssetSyncException ase) {
            log.error("FLEET SYNC: Error syncing raw assets page " + pageNumber + " with page size " + pageSize, ase);
            return Collections.emptyList();
        }
    }
}
