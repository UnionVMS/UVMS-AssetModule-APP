
/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.service.sync.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.asset.service.sync.AssetSyncService;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Asset facility that accepts messages related to asset history sync of fleet
 */
@MessageDriven(mappedName = "jms/queue/UVMSAssetSyncData", activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.MESSAGING_TYPE_STR, propertyValue = MessageConstants.CONNECTION_TYPE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = "UVMSAssetSyncData")
})
@Slf4j
public class AssetSyncMessageConsumerBean implements MessageListener {

    @Inject
    private AssetSyncService assetSyncService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = null;
        try {
            textMessage = (TextMessage) message;
            AssetHistorySyncRetrievalMessage data = AssetHistorySyncRetrievalMessage.decode(textMessage.getText());
            Integer pageNumber = data.getPageNumber();
            Integer pageSize = data.getPageSize();
            log.info("FLEET SYNC: message received for page {} of page size {}.", pageNumber, pageSize);
            //assetSyncService.syncAssetPage(pageNumber, pageSize);
            if (pageNumber == 0) {
                new Thread(() -> {
                    assetSyncService.syncFleet(pageSize);
                }).start();
            } else {
                new Thread(() -> {
                    assetSyncService.syncFleet(pageNumber, pageSize);
                }).start();
            }
        } catch (JMSException e) {
            log.error("error while handling asset sync data message", e);
        }
    }
}
