package eu.europa.ec.fisheries.uvms.asset.service.sync.processor;

import eu.europa.ec.fisheries.uvms.dao.AssetRawHistoryDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Slf4j
public class AssetSyncProcessorService {

    @EJB
    private AssetRawHistoryDao assetRawHistoryDao;

    @Inject
    AssetHistoryUpdateHandler updateHandler;

    @Inject
    AssetHistoryCreationHandler creationHandler;

    private static int processedAssetsCount = 0;
    private static int toUpdateAssetsCount = 0;
    private static int toCreateAssetsCount = 0;
    private static final int BATCH_PROC_SIZE = 100;
    private static final long WAITING_TIME = 240;

    private ExecutorService executorService = null;
    private boolean activitySuccessfullyCompleted = false;

    //////////////////////////////////
    //  initialization and cleanup
    //////////////////////////////////

    @PostConstruct
    private void init() {
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

    public void syncRawRecordsWithExisting() {
        executorService = Executors.newWorkStealingPool();
        processedAssetsCount = 0;
        final List<String> existingCfrs = assetRawHistoryDao.getAllCfrsSorted();
        final List<String> incomingCfrs = assetRawHistoryDao.getAllDistinctRawCfrs();
        log.info("FLEET SYNC: Start processing raw history records in batches of {}. Existing CFRs: {}. Incoming CFRs {}",
                BATCH_PROC_SIZE, existingCfrs.size(), incomingCfrs.size());

        Arrays.stream(partition(incomingCfrs, BATCH_PROC_SIZE))
                .forEach(cfrList -> {
                    toUpdateAssetsCount = 0;
                    toCreateAssetsCount = 0;
                    log.debug("Start processing a new batch of {}.", BATCH_PROC_SIZE);
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
                        executorService.submit(()-> {
                            creationHandler.createAssets(assetsToCreate);
                        });
                    }
                    if (assetsToUpdate.size() > 0) {
                        executorService.submit(() -> {
                            updateHandler.updateAssetsHistory(assetsToUpdate);
                        });
                    }
                    log.info("FLEET SYNC: Processed: {} assets. Update candidates {}. Create candidates {}",
                            processedAssetsCount, toUpdateAssetsCount, toCreateAssetsCount);
                });
        log.info("FLEET SYNC: All history records sent to processing. CFRs: {}. Collecting results...", processedAssetsCount);

        collectSyncActivityResults();
        if (activitySuccessfullyCompleted) {
            log.info("FLEET SYNC: {} history records processed.", processedAssetsCount);
        } else {
            log.error("FLEET SYNC: Processing {} records interrupted.", processedAssetsCount);
        }
    }


    //////////////////////////////////
    //  private methods
    //////////////////////////////////

    private <T> List<T>[] partition(List<T> list, int batchSize) {
        int listSize = list.size();
        int batchCount = listSize / batchSize;
        if (listSize % batchCount != 0) {
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

    private void collectSyncActivityResults() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                activitySuccessfullyCompleted =
                        executorService.awaitTermination(WAITING_TIME, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("FLEET SYNC: Processing records from FLEET interrupted.");
                e.printStackTrace();
            }
        }
    }
}
