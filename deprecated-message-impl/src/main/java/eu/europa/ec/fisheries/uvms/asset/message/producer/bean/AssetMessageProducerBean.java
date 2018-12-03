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
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
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
public class AssetMessageProducerBean extends AbstractProducer implements AssetMessageProducer {

    private static final Logger LOG = LoggerFactory.getLogger(AssetMessageProducerBean.class);

    private Queue nationalSourceQueue;
    private Queue xeuSourceQueue;
    private Queue auditQueue;
    private Queue configQueue;
    private Queue responseQueue;
    private Queue exchangeQueue;

    @PostConstruct
    public void init() {
        responseQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_ASSET);
        nationalSourceQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_DATASOURCE_NATIONAL);
        xeuSourceQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_DATASOURCE_XEU);
        auditQueue = JMSUtils.lookupQueue(AssetConstants.AUDIT_MODULE_QUEUE);
        exchangeQueue = JMSUtils.lookupQueue(AssetConstants.EXCHANGE_MODULE_QUEUE);
        configQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_CONFIG);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendDataSourceMessage(String text, AssetDataSourceQueue queue) {
        String corrId = null;
        try {
            LOG.info("[ Sending datasource message {}  to recipient on queue {} ] ", text, queue.name());
            switch (queue) {
                case INTERNAL:
                    break;
                case NATIONAL:
                    corrId = sendMessageToSpecificQueue(text, nationalSourceQueue, responseQueue);
                    break;
                case XEU:
                    corrId = sendMessageToSpecificQueue(text, xeuSourceQueue, responseQueue);
                    break;
                default:
                    break;
            }
            return corrId;
        } catch (Exception e) {
            LOG.error("[ Error when sending message {} ] {}", text, e);
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws AssetMessageException {
        String corrId = null;
        try {
            LOG.info("[ Sending module message to recipient on queue {} ] ", queue.name());
            switch (queue) {
                case AUDIT:
                    corrId = sendMessageToSpecificQueue(text, auditQueue, responseQueue);
                    break;
                case EXCHANGE:
                    corrId = sendMessageToSpecificQueue(text, exchangeQueue, responseQueue);
                    break;
                case CONFIG:
                    corrId = sendMessageToSpecificQueue(text, configQueue, responseQueue);
                    break;
            }
            return corrId;
        } catch (Exception e) {
            LOG.error("[ Error when sending data source message. ] {}", e);
            throw new AssetMessageException(e.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleResponseMessageAss(TextMessage message, String text) {
        try {
            LOG.info("Sending message back to recipient from VesselModule with correlationId {} on queue: {}", message.getJMSMessageID(), message.getJMSReplyTo());
            sendResponseMessageToSender(message, text);
        } catch (JMSException | MessageException e) {
            LOG.error("[ Error when returning module asset request. ] {} {}", e, e.getStackTrace());
        }
    }

    @Override
    public void sendModuleErrorResponseMessage(@Observes @AssetMessageErrorEvent AssetMessageEvent message) {
        try {
            LOG.info("Sending error message back from VesselModule to recipient om JMS Queue with correlationID: {}", message.getMessage().getJMSMessageID());
            sendResponseMessageToSender(message.getMessage(), JAXBMarshaller.marshallJaxBObjectToString(message.getFault()));
        } catch (JMSException | AssetException | MessageException e) {
            LOG.error("[ Error when returning Error message to recipient. ] {} ", e);
        } catch (EJBTransactionRolledbackException e) {
            LOG.error("[ Error when returning Error message to recipient. Usual cause is NoAssetEntityFoundException ] {} ", e);
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_ASSET;
    }

}