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
package eu.europa.ec.fisheries.uvms.asset.message;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEventBean;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageJSONBean;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.UpsertAssetModuleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = MessageConstants.QUEUE_ASSET_EVENT, activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = MessageConstants.QUEUE_ASSET_EVENT),
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
        TextMessage textMessage = (TextMessage) message;

        try {
            String propertyMethod = textMessage.getStringProperty("METHOD");
            if (propertyMethod != null && propertyMethod.equals("UPSERT_ASSET")) {
                LOG.info("[ Message received in AssetModule with METHOD = {} ]", propertyMethod);
                assetJsonBean.upsertAsset(textMessage);
                return;
            }
            String propertyFunction = textMessage.getStringProperty(MessageConstants.JMS_FUNCTION_PROPERTY);
            if (propertyFunction != null && propertyFunction.equals("ASSET_INFORMATION")) {
                LOG.info("[ Message received in AssetModule with FUNCTION = {} ]", propertyFunction);
                assetJsonBean.assetInformation(textMessage);
                return;
            }

            AssetModuleRequest request = JAXBMarshaller.unmarshallTextMessage(textMessage, AssetModuleRequest.class);
            AssetModuleMethod method = request.getMethod();
            LOG.info("[ Message received in AssetModule with unmarshalled method = {} ]", method);

            switch (method) {
                case GET_ASSET:
                    GetAssetModuleRequest getRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, GetAssetModuleRequest.class);
                    messageEventBean.getAsset(textMessage, getRequest.getId());
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
