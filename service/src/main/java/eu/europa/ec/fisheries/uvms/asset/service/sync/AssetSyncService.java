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
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.service.sync.message.AssetHistorySyncRetrievalMessage;
import eu.europa.ec.fisheries.uvms.asset.service.sync.message.AssetSyncProducerBean;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AssetSyncService {

    private static final int FIRST_PAGE = 0;
    private static final int PAGE_SIZE = 100;

    @Inject
    private AssetSyncClient assetSyncClient;

    @Inject
    private AssetSyncProducerBean assetSyncProducerBean;

    @Inject
    private AssetHistoryRecordHandler assetHistoryRecordHandler;

    @Transactional
    public void syncAssetPage(Integer pageNumber, Integer pageSize) {
        List<AssetHistory> assetHistoryFromPage = assetSyncClient.getAssetsPage(pageNumber, pageSize);
        assetHistoryFromPage.forEach(assetHistoryRecord -> {
            try {
                enrichAssetHistoryRecord(assetHistoryRecord, assetSyncClient.getAssetExtendedData(assetHistoryRecord.getCfr()));
                assetHistoryRecordHandler.handleRecord(assetHistoryRecord);
            } catch (Exception e) {
                // add to a dead letter queue ??
                log.error("Could not perform sync for record with asset hash key {}", assetHistoryRecord.getHashKey(), e);
            }
        });
        addMessageToQueueForNextPage(pageNumber, pageSize, assetHistoryFromPage.size());
    }

    private void enrichAssetHistoryRecord(AssetHistory assetHistoryRecord, AssetHistory iccatAndGfcm) {
        assetHistoryRecord.setIccat(iccatAndGfcm.getIccat());
        assetHistoryRecord.setGfcm(iccatAndGfcm.getGfcm());
    }

    private void addMessageToQueueForNextPage(Integer pageNumber, Integer pageSize, int elementsInLastPage) {
        if (elementsInLastPage == pageSize) {
            try {
                assetSyncProducerBean.sendModuleMessage(AssetHistorySyncRetrievalMessage.encode(new AssetHistorySyncRetrievalMessage(pageNumber + 1, pageSize)), null);
            } catch (MessageException e) {
                log.error("Error sending message for next page to asset history sync queue", e);
            }
        }
    }

    public void triggerSync() {
        try {
            assetSyncProducerBean.sendModuleMessage(AssetHistorySyncRetrievalMessage.encode(new AssetHistorySyncRetrievalMessage(FIRST_PAGE, PAGE_SIZE)), null);
        } catch (MessageException e) {
            log.error("Error sending message to trigger start of asset history sync queue", e);
        }
    }

}
