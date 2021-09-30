package eu.europa.ec.fisheries.uvms.asset.service.sync.collector;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetSyncException;
import eu.europa.ec.fisheries.uvms.asset.service.sync.AssetSyncClient;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Slf4j
public class AssetSyncCollectorService {

    @Inject
    private AssetSyncClient assetSyncClient;

    @EJB
    private AssetRawHistoryDao assetRawHistoryDao;

    private static final long WAITING_TIME = 300;

    private static List<Future<?>> results = null;
    private ExecutorService executorService = null;
    private static boolean activityStarted;
    private static boolean activityCompleted;
    private static boolean activitySuccessfullyCompleted;

    //////////////////////////////////
    //  initialization and cleanup
    //////////////////////////////////

    @PostConstruct
    private void init() {
        activityStarted = false;
        activityCompleted = false;
        activitySuccessfullyCompleted = true;
        executorService = Executors.newWorkStealingPool();
    }

    @PreDestroy
    private void cleanUp() {
        if (executorService != null) {
            try {
                executorService.shutdown();
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.info("FLEET SYNC: Process of creating raw history records interrupted.");
            } finally {
                if (!executorService.isTerminated()) {
                    log.info("FLEET SYNC: Force shutdown during the raw history records saving.");
                }
                executorService.shutdownNow();
            }
        }
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
        log.info("FLEET SYNC: Start collecting fleet data.");

        activityStarted = true;
        activityCompleted = false;
        activitySuccessfullyCompleted = false;
        executorService = Executors.newWorkStealingPool();

        prepareFleetStorage();

        results = new ArrayList<>();
        Integer pageSize = userPageSize <=0 ? defaultPageSize : userPageSize;
        log.info("FLEET SYNC: Asset synchronization collection started with page size {}.", pageSize);
        if (getSinglePage) {
            getSinglePageFromFleet(startPageIndex, pageSize);
        } else {
            collectAndSaveMultipleFleetPages(startPageIndex, pageSize);
        }
        collectSyncActivityResults();
    }

    private void collectSyncActivityResults() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                activitySuccessfullyCompleted =
                        executorService.awaitTermination(WAITING_TIME, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("FLEET SYNC: Collecting records from FLEET interrupted.");
                e.printStackTrace();
            }
            //redundancy
            if (executorService.isTerminated()) {
                log.info("FLEET SYNC: Collecting records from FLEET completed successfully.");
                activitySuccessfullyCompleted = true;
            }
        } else {
            activitySuccessfullyCompleted = false;
        }
        activityStarted = false;
        activityCompleted = true;
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

    //////////////////////////////////
    //  private methods
    //////////////////////////////////

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
        Future<?> result = executorService.submit(() -> {
            assetRawHistoryDao.createRawHistoryEntry(historyRecords);
        });
        return result;
    }

    private void collectAndSaveMultipleFleetPages(Integer startPage, Integer pageSize) {
        Integer startPageIndex = new Integer(startPage);
        boolean moreAssetsExist = true;
        while (moreAssetsExist) {
            Integer retrievedRecordsCount = getSinglePageFromFleet(startPageIndex, pageSize);
            moreAssetsExist = pageSize.equals(retrievedRecordsCount);
            log.info("FLEET SYNC: Collected page {}", startPageIndex);
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
