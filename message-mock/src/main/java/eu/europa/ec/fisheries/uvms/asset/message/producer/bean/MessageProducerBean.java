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
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.enterprise.event.Observes;
import javax.jms.*;
import javax.naming.InitialContext;

@Stateless
public class MessageProducerBean implements MessageProducer, ConfigMessageProducer {

    private Queue nationalSourceQueue;
    private Queue xeuSourceQueue;
    private Queue auditQueue;
    private Queue configQueue;
    private Queue responseQueue;

    final static Logger LOG = LoggerFactory.getLogger(MessageProducerBean.class);

    private static final int CONFIG_TTL = 30000;

    @EJB
    JMSConnectorBean connector;

    @PostConstruct
    public void init() {
        InitialContext ctx;
        try {
            ctx = new InitialContext();
        } catch (Exception e) {
            LOG.error("Failed to get InitialContext",e);
            throw new RuntimeException(e);
        }
        responseQueue = JMSUtils.lookupQueue(ctx, AssetConstants.QUEUE_ASSET);
        nationalSourceQueue = JMSUtils.lookupQueue(ctx, AssetConstants.QUEUE_DATASOURCE_NATIONAL);
        xeuSourceQueue = JMSUtils.lookupQueue(ctx, AssetConstants.QUEUE_DATASOURCE_XEU);
        auditQueue = JMSUtils.lookupQueue(ctx, AssetConstants.AUDIT_MODULE_QUEUE);
        configQueue = JMSUtils.lookupQueue(ctx, ConfigConstants.CONFIG_MESSAGE_IN_QUEUE);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendDataSourceMessage(String text, AssetDataSourceQueue queue) {
        try {
            LOG.info("[ Sending datasource message to recipient on queue {} ] ", queue.name());

            Session session = connector.getNewSession();
            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            switch (queue) {
                case INTERNAL:
                    break;
                case NATIONAL:
                    getProducer(session, nationalSourceQueue).send(message);
                    break;
                case XEU:
                    getProducer(session, xeuSourceQueue).send(message);
                    break;
                default:
                    break;
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending message. ] {}", e.getMessage(), e.getStackTrace());
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws AssetMessageException {
        try {
            LOG.info("[ Sending module message to recipient on queue {} ] ", queue.name());

            Session session = connector.getNewSession();
            TextMessage message = session.createTextMessage();
            message.setJMSReplyTo(responseQueue);
            message.setText(text);

            switch (queue) {
                case AUDIT:
                    getProducer(session, auditQueue).send(message);
                    break;
                case CONFIG:
                    getProducer(session, configQueue).send(message);
                    break;
            }

            return message.getJMSMessageID();
        } catch (Exception e) {
            LOG.error("[ Error when sending data source message. ] {}", e.getMessage());
            throw new AssetMessageException(e.getMessage());
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleResponseMessage(TextMessage message, String text) {
        try {
            LOG.info("Sending message back to recipient from VesselModule with correlationId {} on queue: {}", message.getJMSMessageID(), message.getJMSReplyTo());
            Session session = connector.getNewSession();
            TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSMessageID());
            getProducer(session, message.getJMSReplyTo()).send(response);
        } catch (JMSException e) {
            LOG.error("[ Error when returning module asset request. ] {} {}", e.getMessage(), e.getStackTrace());
        }
    }

    @Override
    public void sendModuleErrorResponseMessage(@Observes @AssetMessageErrorEvent AssetMessageEvent message) {
        try {
            LOG.info("Sending error message back from VesselModule to recipient om JMS Queue with correlationID: {}", message.getMessage().getJMSMessageID());

            Session session = connector.getNewSession();

            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getFault());
            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getMessage().getJMSMessageID());
            getProducer(session, message.getMessage().getJMSReplyTo()).send(response);

        } catch (JMSException | AssetModelMarshallException e) {
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

    private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }

}