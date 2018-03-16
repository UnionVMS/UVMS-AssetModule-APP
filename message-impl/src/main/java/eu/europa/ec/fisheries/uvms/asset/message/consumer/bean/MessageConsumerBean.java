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
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.service.bean.*;
import eu.europa.ec.fisheries.uvms.asset.types.AssetFault;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.message.AssetConstants;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;

import java.io.IOException;

@MessageDriven(mappedName = AssetConstants.QUEUE_ASSET_EVENT, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = AssetConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = AssetConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = AssetConstants.QUEUE_NAME_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "destinationJndiName", propertyValue = AssetConstants.QUEUE_ASSET_EVENT),
    @ActivationConfigProperty(propertyName = "connectionFactoryJndiName", propertyValue = AssetConstants.CONNECTION_FACTORY)
})
public class MessageConsumerBean implements MessageListener {

    final static Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    private static ObjectMapper MAPPER = new ObjectMapper();

    @EJB
    private GetAssetEventBean getAssetEventBean;

    @EJB
    private UpsertAssetMessageEventBean upsertAssetMessageEventBean;


    @EJB
    private PingEventBean pingEventBean;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void onMessage(Message message) {

        if(message == null){
            return;
        }
        if(!(message instanceof TextMessage)){
            return;
        }

        TextMessage textMessage = (TextMessage) message;

        try {

            String command = message.getStringProperty("COMMAND");  // JmsException
            String json = textMessage.getText();

            switch (command) {

                case "UPSERT_ASSET":
                    AssetSE asset = MAPPER.readValue(json, AssetSE.class);
                    upsertAssetMessageEventBean.upsertAsset(asset);
                    break;

                case "PING":
                    pingEventBean.ping(textMessage);
                    break;

                default:
                    LOG.error("[ Not implemented method consumed: {} ]", command);
                    assetErrorEvent.fire(new AssetMessageEvent(textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "Command not implemented")));
            }

        } catch (IllegalArgumentException e) {
            LOG.error("Could not interpret command");
            assetErrorEvent.fire(new AssetMessageEvent( textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "Could not interpret command")));
        } catch (JMSException e) {
            LOG.error("[ Error when receiving message in AssetModule. ]");
            assetErrorEvent.fire(new AssetMessageEvent( textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, e.toString())));
        } catch (JsonParseException e) {
            LOG.error("JsonParseException");
            assetErrorEvent.fire(new AssetMessageEvent( textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "JsonParseException")));
        } catch (JsonMappingException e) {
            LOG.error("JsonMappingException");
            assetErrorEvent.fire(new AssetMessageEvent( textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "JsonMappingException")));
        } catch (IOException e) {
            LOG.error("IOException");
            assetErrorEvent.fire(new AssetMessageEvent( textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "IOException")));
        }

    }


    public AssetFault createFaultMessage(FaultCode code, String message) {
        AssetFault fault = new AssetFault();
        fault.setCode(code.getCode());
        fault.setFault(message);
        return fault;
    }


}
