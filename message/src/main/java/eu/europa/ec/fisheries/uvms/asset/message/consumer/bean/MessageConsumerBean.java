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
package eu.europa.ec.fisheries.uvms.asset.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.asset.message.AssetConstants;
import eu.europa.ec.fisheries.uvms.asset.message.event.*;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.module.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = AssetConstants.QUEUE_ASSET_EVENT, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = AssetConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = AssetConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = AssetConstants.QUEUE_NAME_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = AssetConstants.QUEUE_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = AssetConstants.CONNECTION_FACTORY)
})
public class MessageConsumerBean implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    @GetAssetMessageEvent
    Event<AssetMessageEvent> assetEvent;

    @Inject
    @GetAssetListMessageEvent
    Event<AssetMessageEvent> assetListEvent;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @Inject
    @GetAssetGroupEvent
    Event<AssetMessageEvent> assetGroupEvent;

    @Inject
    @GetAssetListByAssetGroupEvent
    Event<AssetMessageEvent> assetListByGroupEvent;

    @Inject
    @PingEvent
    Event<AssetMessageEvent> pingEvent;

    @Inject
    @GetAssetGroupListByAssetGuidEvent
    Event<AssetMessageEvent> assetGroupByAssetEvent;

    @Inject
    @UpsertAssetMessageEvent
    Event<AssetMessageEvent> upsertAssetEvent;

    @Inject
    @UpsertFishingGearsMessageEvent
    Event<AssetMessageEvent> upsertFisMessageEvent;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {
        LOG.info("Message received in AssetModule");
        TextMessage textMessage = (TextMessage) message;

        try {

            AssetModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetModuleRequest.class);
            AssetModuleMethod method = request.getMethod();

            switch (method) {
                case GET_ASSET:
                    GetAssetModuleRequest getRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetModuleRequest.class);
                    AssetMessageEvent event = new AssetMessageEvent(textMessage, getRequest.getId());
                    assetEvent.fire(event);
                    break;
                case ASSET_LIST:
                    AssetListModuleRequest listRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetListModuleRequest.class);
                    AssetMessageEvent listEvent = new AssetMessageEvent(textMessage, listRequest.getQuery());
                    assetListEvent.fire(listEvent);
                    break;
                case ASSET_GROUP:
                    AssetGroupListByUserRequest groupListRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetGroupListByUserRequest.class);
                    AssetMessageEvent assetGroupListEvent = new AssetMessageEvent(textMessage, groupListRequest);
                    assetGroupEvent.fire(assetGroupListEvent);
                    break;
                case ASSET_GROUP_LIST_BY_ASSET_GUID:
                    GetAssetGroupListByAssetGuidRequest getAssetGroupListByAssetGuidRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetGroupListByAssetGuidRequest.class);
                    AssetMessageEvent assetMessageEvent = new AssetMessageEvent(textMessage, getAssetGroupListByAssetGuidRequest.getAssetGuid());
                    assetGroupByAssetEvent.fire(assetMessageEvent);
                    break;
                case ASSET_LIST_BY_GROUP:
                    GetAssetListByAssetGroupsRequest assetListByGroupListRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetListByAssetGroupsRequest.class);
                    AssetMessageEvent assetListByGroupListEvent = new AssetMessageEvent(textMessage, assetListByGroupListRequest);
                    assetListByGroupEvent.fire(assetListByGroupListEvent);
                    break;
                case PING:
                    pingEvent.fire(new AssetMessageEvent(textMessage));
                    break;
                case UPSERT_ASSET:
                    UpsertAssetModuleRequest upsertRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, UpsertAssetModuleRequest.class);
                    AssetMessageEvent upsertAssetMessageEvent = new AssetMessageEvent(textMessage, upsertRequest.getAsset(), upsertRequest.getUserName());
                    upsertAssetEvent.fire(upsertAssetMessageEvent);
                    break;
                case FISHING_GEAR_UPSERT:
                    UpsertFishingGearModuleRequest upsertFishingGearListModuleRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, UpsertFishingGearModuleRequest.class);
                    AssetMessageEvent fishingGearMessageEvent = new AssetMessageEvent(textMessage, upsertFishingGearListModuleRequest.getFishingGear(), upsertFishingGearListModuleRequest.getUsername());
                    upsertFisMessageEvent.fire(fishingGearMessageEvent);
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ]", method);
                    assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Method not implemented")));
            }

        } catch (AssetModelMarshallException e) {
            LOG.error("[ Error when receiving message in AssetModule. ]");
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Method not implemented")));
        }
    }
}
