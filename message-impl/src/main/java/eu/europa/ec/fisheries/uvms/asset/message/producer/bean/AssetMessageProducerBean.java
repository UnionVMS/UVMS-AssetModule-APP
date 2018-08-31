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
package eu.europa.ec.fisheries.uvms.asset.message.producer.bean;

import eu.europa.ec.fisheries.uvms.asset.message.AssetConstants;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;

@Stateless
public class AssetMessageProducerBean extends AbstractProducer implements AssetMessageProducer, ConfigMessageProducer {

    final static Logger LOG = LoggerFactory.getLogger(AssetMessageProducerBean.class);

    private Queue nationalSourceQueue;
    private Queue xeuSourceQueue;
    private Queue auditQueue;
    private Queue configQueue;
    private Queue responseQueue;

    @PostConstruct
    public void init() {
        responseQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_ASSET);
        nationalSourceQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_DATASOURCE_NATIONAL);
        xeuSourceQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_DATASOURCE_XEU);
        auditQueue = JMSUtils.lookupQueue(AssetConstants.AUDIT_MODULE_QUEUE);
        configQueue = JMSUtils.lookupQueue(ConfigConstants.CONFIG_MESSAGE_IN_QUEUE);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendDataSourceMessage(String text, AssetDataSourceQueue queue) {
    	try {
            LOG.info("[ Sending datasource message {}  to recipient on queue {} ] ",text, queue.name());
            Queue destination = getDestinationQueue(queue);
            if(destination != null){
                return sendMessageToSpecificQueue(text, destination, responseQueue);
            }
            return null;
        } catch (Exception e) {
            LOG.error("[ Error when sending message {} ] {}",text, e.getMessage());
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws AssetMessageException {
        LOG.info("[ Sending module message to recipient on queue {} ] ", queue.name());
        try {
            Queue destination = getDestinationQueue(queue);
            if(destination != null){
                return sendMessageToSpecificQueue(text, destination, responseQueue);
            }
            return null;
        } catch (Exception e) {
            LOG.error("[ Error when sending message {} ] {}",text, e.getMessage());
            throw new AssetMessageException(e.getMessage());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleResponseMessageOv(TextMessage message, String text) {
    	try {
            LOG.info("Sending message back to recipient from VesselModule with correlationId {} on queue: {}", message.getJMSMessageID(), message.getJMSReplyTo());
            sendResponseMessageToSender(message, text);
        } catch (JMSException | MessageException e) {
            LOG.error("[ Error when returning module asset request. ] {} {}", e.getMessage(), e.getStackTrace());
        }
    }

    @Override
    public void sendModuleErrorResponseMessage(@Observes @AssetMessageErrorEvent AssetMessageEvent message) {
    	try {
            TextMessage jmsMessage = message.getMessage();
            LOG.info("Sending error message back from AssetModule to recipient om JMS Queue [ {} ] with correlationID: [ {} ]", jmsMessage.getJMSReplyTo(), jmsMessage.getJMSMessageID());
            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getFault());
            sendResponseMessageToSender(jmsMessage, data);
        } catch (JMSException | AssetModelMarshallException | MessageException e) {
            LOG.error("[ Error when returning Error message to recipient. ] {} ", e.getMessage());
        } catch (EJBTransactionRolledbackException e) {
            LOG.error("[ Error when returning Error message to recipient. Usual cause is NoAssetEntityFoundException ] {} ", e.getMessage());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendConfigMessage(String text) throws ConfigMessageException {
        try {
            return sendModuleMessage(text, ModuleQueue.CONFIG);
        } catch (AssetMessageException e) {
            LOG.error("[ Error when sending config message. ] {}", e.getMessage());
            throw new ConfigMessageException(e.getMessage());
        }
    }

    private Queue getDestinationQueue(AssetDataSourceQueue queue) {
        Queue destination = null;
        switch (queue) {
            case INTERNAL:
                destination = xeuSourceQueue;
                break;
            case NATIONAL:
                destination = nationalSourceQueue;
                break;
            default:
                break;
        }
        return destination;
    }

    private Queue getDestinationQueue(ModuleQueue queue) {
        Queue destination = null;
        switch (queue) {
            case AUDIT:
                destination = auditQueue;
                break;
            case CONFIG:
                destination = configQueue;
                break;
            default:
                break;
        }
        return destination;
    }

    @Override
    public String getDestinationName() {
        return AssetConstants.QUEUE_ASSET;
    }
}