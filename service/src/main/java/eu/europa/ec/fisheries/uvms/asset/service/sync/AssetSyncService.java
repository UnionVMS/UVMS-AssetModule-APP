/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.service.sync;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetSyncException;
import eu.europa.ec.fisheries.uvms.asset.service.sync.collector.AssetSyncCollectorService;
import eu.europa.ec.fisheries.uvms.asset.service.sync.message.AssetHistorySyncRetrievalMessage;
import eu.europa.ec.fisheries.uvms.asset.service.sync.message.AssetSyncProducerBean;
import eu.europa.ec.fisheries.uvms.asset.service.sync.processor.AssetSyncProcessorService;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AssetSyncService {

    private static final int FIRST_PAGE = 0;
    private static final int PAGE_SIZE = 5000;

    @Inject
    private AssetSyncClient assetSyncClient;

    @Inject
    private AssetSyncProducerBean assetSyncProducerBean;

    @Inject
    private AssetHistoryRecordHandler assetHistoryRecordHandler;

    @Inject
    private AssetSyncCollectorService collectorService;

    @Inject
    private AssetSyncProcessorService processorService;

    //////////////////////////////////
    //  public methods
    //////////////////////////////////

    @Transactional
    public void syncAssetPage(Integer pageNumber, Integer pageSize) {
        List<AssetHistory> assetHistoryFromPage = getAssetsPageSafe(pageNumber, pageSize);
        assetHistoryFromPage.forEach(assetHistoryRecord -> {
            assetHistoryRecordHandler.handleRecord(assetHistoryRecord);
        });
        addMessageToQueueForNextPage(pageNumber, pageSize, assetHistoryFromPage.size());
    }


    public void resetSync() {
        collectorService.resetSyncCollectorState();
        processorService.resetSyncProcessorState();
    }

    public void triggerSync() {
        try {
            assetSyncProducerBean.sendModuleMessage(
                    AssetHistorySyncRetrievalMessage.encode(
                            new AssetHistorySyncRetrievalMessage(FIRST_PAGE, PAGE_SIZE)), null);
        } catch (MessageException e) {
            log.error("FLEET SYNC: Error sending message to trigger start of asset history sync queue", e);
        }
    }

    public void syncFleet(Integer pageSize) {
        if (pageSize >= 0) {
            if( canStartAssetSync()) {
                collectorService.collectDataFromFleet(0, false, pageSize, PAGE_SIZE);
            } else {
                log.info("FLEET SYNC: Sync already started. Request to start skipped.");
                return;
            }
            if (collectorService.isCollectingActivitySuccessfullyCompleted()) {
                processorService.syncRawRecordsWithExisting();
            }
        } else {
            if (!processorService.isProcessingActivityStarted()) {
                processorService.syncRawRecordsWithExisting();
            }
        }
    }

    public void syncFleet(Integer pageNumber, Integer pageSize) {
        if (pageNumber > 0) {
            if( canStartAssetSync() ) {
                collectorService.collectDataFromFleet(pageNumber, true, pageSize, PAGE_SIZE);
            } else {
                log.info("FLEET SYNC: Collection step already started. Request to start skipped.");
                return;
            }
            if (collectorService.isCollectingActivitySuccessfullyCompleted()) {
                processorService.syncRawRecordsWithExisting();
            } else {
                log.warn("FLEET SYNC: Collection activity completed unsuccessfully. " +
                        "Collected data will not be synced into app tables.");
            }
        } else {
            if (pageNumber == -1000) {
                if (!processorService.isProcessingActivityStarted()) {
                    processorService.syncRawRecordsWithExisting();
                }
            } else if (pageNumber == -100) {
                if (!collectorService.isCollectingActivityStarted()) {
                    collectorService.collectDataFromFleet(0, false, pageSize, PAGE_SIZE);
                }
            } else {
                log.info("FLEET SYNC: Use 0 as page number to retrieve all, or a positive # to retrieve just that.");
            }
        }
    }

    //////////////////////////////////
    //  private methods
    //////////////////////////////////

    private List<AssetHistory> getAssetsPageSafe(Integer pageNumber, Integer pageSize) {
        try {
            return assetSyncClient.getAssetsPage(pageNumber, pageSize);
        } catch (AssetSyncException ase) {
            log.error("FLEET SYNC: Error syncing assets page " + pageNumber + " with page size " + pageSize, ase);
            return Collections.emptyList();
        }
    }

    private void addMessageToQueueForNextPage(Integer pageNumber, Integer pageSize, int elementsInLastPage) {
        if (elementsInLastPage == pageSize) {
            try {
                assetSyncProducerBean.sendModuleMessage(
                        AssetHistorySyncRetrievalMessage.encode(
                                new AssetHistorySyncRetrievalMessage(pageNumber + 1, pageSize)), null);
            } catch (MessageException e) {
                log.error("FLEET SYNC: Error sending message for next page to asset history sync queue", e);
            }
        }
    }

    private boolean canStartAssetSync() {
        return !(collectorService.isCollectingActivityStarted() ||
                processorService.isProcessingActivityStarted());
    }
}