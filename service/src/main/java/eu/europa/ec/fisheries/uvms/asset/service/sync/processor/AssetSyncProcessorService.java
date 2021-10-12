package eu.europa.ec.fisheries.uvms.asset.service.sync.processor;

import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Singleton
@Slf4j
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AssetSyncProcessorService {

    @EJB
    private AssetRawHistoryDao assetRawHistoryDao;

    @EJB
    private AssetDao assetDao;

    @Inject
    AssetHistoryUpdateHandler updateHandler;

    @Inject
    AssetHistoryCreationHandler creationHandler;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Resource
    TimerService timerService;


    private static int processedAssetsCount = 0;
    private static int toUpdateAssetsCount = 0;
    private static int toCreateAssetsCount = 0;
    private static final int BATCH_PROC_SIZE = 100;
    private static final long WAITING_TIME = 240;
    private static final long TIME_TO_CANCEL_COLLECTION = 4*60*60*1000;
    private static final long TIME_TO_NEXT_CHECK = 2*60*1000;

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
    }

    //////////////////////////////////
    //  public methods
    //////////////////////////////////

    public void syncRawRecordsWithExisting() {
        checkStartDataProcessing();
        assetRawHistoryDao.truncateAddressForRawRecordsEntries();

        results = new ArrayList<>();
        processedAssetsCount = 0;
        final List<String> existingCfrs = assetDao.getAllCfrsSorted();
        final List<String> incomingCfrs = assetRawHistoryDao.getAllDistinctRawCfrs();
        log.info("FLEET SYNC: Asset synchronization processing started with batches of {}. Existing CFRs: {}. Incoming CFRs {}",
                BATCH_PROC_SIZE, existingCfrs.size(), incomingCfrs.size());

        Arrays.stream(partition(incomingCfrs, BATCH_PROC_SIZE))
                .forEach(cfrList -> {
                    toUpdateAssetsCount = 0;
                    toCreateAssetsCount = 0;
                    log.debug("FLEET SYNC: Start processing a new batch of {}.", BATCH_PROC_SIZE);
                    List<AssetEntity> assetsToCreate = new ArrayList<>();
                    List<String> assetsToUpdate = new ArrayList<>();
                    cfrList.stream().forEach(cfr -> {
                        processedAssetsCount++;
                        if (existingCfrs.contains(cfr)) {
                            assetsToUpdate.add(cfr);
                            toUpdateAssetsCount++;
                            log.debug("FLEET SYNC: {} will be updated.", cfr);
                        } else {
                            assetsToCreate.add( creationHandler.createAssetWithFullHistory(cfr) );
                            toCreateAssetsCount++;
                            log.debug("FLEET SYNC: {} will be created.", cfr);
                        }
                    });
                    if (assetsToCreate.size() > 0) {
                        Future<?> result = managedExecutorService.submit(()-> {
                            creationHandler.createAssets(assetsToCreate);
                        });
                        if (result != null) {
                            results.add(result);
                        }
                    }
                    if (assetsToUpdate.size() > 0) {
                        Future<?> result = managedExecutorService.submit(() -> {
                            updateHandler.updateAssetsHistory(assetsToUpdate);
                        });
                        if (result != null) {
                            results.add(result);
                        }
                    }
                    log.info("FLEET SYNC: Processed: {} assets. Update candidates {}. Create candidates {}",
                            processedAssetsCount, toUpdateAssetsCount, toCreateAssetsCount);
                });
        log.info("FLEET SYNC: All history records sent to processing. CFRs: {}. Collecting results...", processedAssetsCount);

        collectSyncActivityResults();
        if (activitySuccessfullyCompleted) {
            log.info("FLEET SYNC: Success: {} assets with their history records processed.", processedAssetsCount);
        } else {
            log.error("FLEET SYNC: Processing {} assets interrupted.", processedAssetsCount);
        }
    }

    public boolean checkStartDataProcessing() {
        log.info("FLEET SYNC: Checking asset sync processing state...");

        if (activityStarted) {
            log.info("FLEET SYNC: Asset processing already in progress. Skipping the request.");
            return false;
        } else {
            lock.writeLock().lock();
            try {
                if (!activityStarted) {
                    activityStarted = true;
                    activityCompleted = false;
                    activitySuccessfullyCompleted = false;
                    log.info("FLEET SYNC: Start processing fleet data.");
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        //create timer to cancel the results if exceeds 4 hours
        timerService.createTimer(TIME_TO_CANCEL_COLLECTION, "FLEET SYNC: Cancel-sync-processor task timer.");

        return true;
    }

    public boolean isProcessingActivityStarted() {
        return activityStarted;
    }

    public boolean isProcessingActivityCompleted() {
        return activityCompleted;
    }

    public boolean isProcessingActivitySuccessfullyCompleted() {
        return activitySuccessfullyCompleted;
    }

    public void resetSyncProcessorState() {
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

    private <T> List<T>[] partition(List<T> list, int batchSize) {
        int listSize = list.size();
        int batchCount = listSize / batchSize;
        if (batchCount == 0 || listSize % batchSize != 0) {
            batchCount++;
        }
        List<T>[] partition = new ArrayList[batchCount];
        for (int i = 0; i < batchCount; i++) {
            int fromIndex = i * batchSize;
            int toIndex = Math.min(fromIndex + batchSize, listSize);
            partition[i] = new ArrayList(list.subList(fromIndex, toIndex));
        }
        return partition;
    }

    @Timeout
    private void cancelAssetProcessor(Timer timer) {
        resetSyncProcessorState();
        activityCompleted = true;
    }

    private void collectSyncActivityResults() {

        //blocking check for results
        while(!areTasksDone(results)) {
            try {
                Thread.sleep(TIME_TO_NEXT_CHECK);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        activityCompleted = true;
        activitySuccessfullyCompleted = true;
        activityStarted = false;
        log.info("FLEET SYNC: Sync processing steps completed.");
    }

    private boolean areTasksDone(List<Future<?>> results) {
        boolean allDone = true;
        for (Future<?> result : results) {
            allDone = result.isDone() && allDone;
        }
        return  allDone;
    }
}
