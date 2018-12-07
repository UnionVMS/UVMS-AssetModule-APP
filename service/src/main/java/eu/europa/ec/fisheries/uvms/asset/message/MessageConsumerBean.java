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

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.message.AssetConstants;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean.AssetMessageEventBean;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean.AssetMessageJSONBean;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetListModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetGroupListByAssetGuidRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertAssetModuleRequest;

@MessageDriven(mappedName = AssetConstants.QUEUE_ASSET_EVENT, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = AssetConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = AssetConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = AssetConstants.QUEUE_NAME_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = AssetConstants.QUEUE_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = AssetConstants.CONNECTION_FACTORY)
})
public class MessageConsumerBean implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    private AssetMessageEventBean messageEventBean;
    
    @Inject
    private AssetMessageJSONBean assetJsonBean;
    
    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @Override
    public void onMessage(Message message) {
        LOG.info("Message received in AssetModule");
        TextMessage textMessage = (TextMessage) message;

        try {
            String propertyMethod = textMessage.getStringProperty("METHOD");
            if (propertyMethod != null && propertyMethod.equals("UPSERT_ASSET")) {
                assetJsonBean.upsertAsset(textMessage);
                return;
            }
            
            AssetModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetModuleRequest.class);
            AssetModuleMethod method = request.getMethod();

            switch (method) {
                case GET_ASSET:
                    GetAssetModuleRequest getRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetModuleRequest.class);
                    messageEventBean.getAsset(textMessage, getRequest.getId());
                    break;
                case ASSET_LIST:
                    AssetListModuleRequest listRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetListModuleRequest.class);
                    AssetMessageEvent listEvent = new AssetMessageEvent(textMessage, listRequest.getQuery());
                    messageEventBean.getAssetList(listEvent);
                    break;
                case ASSET_GROUP:
                    AssetGroupListByUserRequest groupListRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetGroupListByUserRequest.class);
                    AssetMessageEvent assetGroupListEvent = new AssetMessageEvent(textMessage, groupListRequest);
                    messageEventBean.getAssetGroupByUserName(assetGroupListEvent);
                    break;
                case ASSET_GROUP_LIST_BY_ASSET_GUID:
                    GetAssetGroupListByAssetGuidRequest getAssetGroupListByAssetGuidRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetGroupListByAssetGuidRequest.class);
                    AssetMessageEvent assetMessageEvent = new AssetMessageEvent(textMessage, getAssetGroupListByAssetGuidRequest.getAssetGuid());
                    messageEventBean.getAssetGroupListByAssetEvent(assetMessageEvent);
                    break;
                case ASSET_LIST_BY_GROUP:
                    GetAssetListByAssetGroupsRequest assetListByGroupListRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetListByAssetGroupsRequest.class);
                    AssetMessageEvent assetListByGroupListEvent = new AssetMessageEvent(textMessage, assetListByGroupListRequest);
                    messageEventBean.getAssetListByAssetGroups(assetListByGroupListEvent);
                    break;
                case PING:
                    messageEventBean.ping(new AssetMessageEvent(textMessage));
                    break;
                case UPSERT_ASSET:
                    UpsertAssetModuleRequest upsertRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, UpsertAssetModuleRequest.class);
                    AssetMessageEvent upsertAssetMessageEvent = new AssetMessageEvent(textMessage, upsertRequest.getAsset(), upsertRequest.getUserName());
                    messageEventBean.upsertAsset(upsertAssetMessageEvent);
                    break;
                default:
                    LOG.error("[ Not implemented method consumed: {} ]", method);
                    assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Method not implemented")));
            }
        } catch (Exception e) {
            LOG.error("[ Error when receiving message in AssetModule. ]", e);
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Method not implemented")));
        }
    }
}
