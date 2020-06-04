package eu.europa.ec.fisheries.uvms.asset.service.bean;
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

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.FishingGearService;
import eu.europa.ec.fisheries.wsdl.asset.fishinggear.FishingGearResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.FishingGear;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;

@Stateless
public class FishingGearServiceBean implements FishingGearService {

    @EJB
    private AssetMessageProducer messageProducer;

    @EJB
    private AssetQueueConsumer receiver;

    @Override
    public FishingGearResponse upsertFishingGears(FishingGear fishingGear, String username) throws AssetMessageException, AssetModelMapperException {
        String request = AssetDataSourceRequestMapper.mapUpsertFishingGearRequest(fishingGear, username);
        String messageId = messageProducer.sendDataSourceMessage(request, AssetDataSourceQueue.INTERNAL);
        TextMessage response = receiver.getMessageOv(messageId, TextMessage.class);
        FishingGearResponse fishingGearResponse = AssetDataSourceResponseMapper.mapToUpsertFishingGearResponse(response, messageId);
        return fishingGearResponse;
    }
}
