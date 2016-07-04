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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;

/***/
@Stateless
public class AssetHistoryServiceBean implements AssetHistoryService {

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetQueueConsumer reciever;

    final static Logger LOG = LoggerFactory.getLogger(AssetHistoryServiceBean.class);


    @Override
    public List<Asset> getAssetHistoryListByAssetId(String assetId, Integer maxNbr) throws AssetException {
        LOG.info("Getting AssetHistoryList by AssetId: {}.", assetId);

        String data = AssetDataSourceRequestMapper.mapGetAssetHistoryListByAssetId(assetId, maxNbr);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        return AssetDataSourceResponseMapper.mapToAssetListFromResponse(response, messageId);
    }

    @Override
    public Asset getAssetHistoryByAssetHistGuid(String assetHistId) throws AssetException {
        LOG.info("Getting AssetHistory by AssetHistoryGuid: {}.", assetHistId);

        String data = AssetDataSourceRequestMapper.mapGetAssetHistoryByGuid(assetHistId);
        String messageId = messageProducer.sendDataSourceMessage(data, AssetDataSourceQueue.INTERNAL);
        TextMessage response = reciever.getMessage(messageId, TextMessage.class);
        return AssetDataSourceResponseMapper.mapToAssetFromResponse(response, messageId);
    }

}