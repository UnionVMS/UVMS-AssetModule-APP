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

import javax.annotation.PostConstruct;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.message.AssetConstants;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.constants.ConfigConstants;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;

@Stateless
public class MessageProducerBean implements MessageProducer, ConfigMessageProducer {

    private Queue nationalSourceQueue;
    private Queue xeuSourceQueue;
    private Queue auditQueue;
    private Queue configQueue;
    private Queue responseQueue;
    private ConnectionFactory connectionFactory;
    

    final static Logger LOG = LoggerFactory.getLogger(MessageProducerBean.class);

    @PostConstruct
    public void init() {
    	connectionFactory = JMSUtils.lookupConnectionFactory();
        responseQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_ASSET);
        nationalSourceQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_DATASOURCE_NATIONAL);
        xeuSourceQueue = JMSUtils.lookupQueue(AssetConstants.QUEUE_DATASOURCE_XEU);
        auditQueue = JMSUtils.lookupQueue(AssetConstants.AUDIT_MODULE_QUEUE);
        configQueue = JMSUtils.lookupQueue(ConfigConstants.CONFIG_MESSAGE_IN_QUEUE);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendDataSourceMessage(String text, AssetDataSourceQueue queue) {
    	Connection connection=null;

    	try {
            LOG.info("[ Sending datasource message {}  to recipient on queue {} ] ",text, queue.name());

            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

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
            LOG.error("[ Error when sending message {} ] {}",text, e.getMessage());
            return null;
        } finally {
        	JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendModuleMessage(String text, ModuleQueue queue) throws AssetMessageException {

    	Connection connection=null;
    	try {
            LOG.info("[ Sending module message to recipient on queue {} ] ", queue.name());

            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            
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
        } finally {
        	JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendModuleResponseMessage(TextMessage message, String text) {
    	Connection connection=null;

    	try {
            LOG.info("Sending message back to recipient from VesselModule with correlationId {} on queue: {}", message.getJMSMessageID(), message.getJMSReplyTo());
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);
            TextMessage response = session.createTextMessage(text);
            response.setJMSCorrelationID(message.getJMSMessageID());
            getProducer(session, message.getJMSReplyTo()).send(response);
        } catch (JMSException e) {
            LOG.error("[ Error when returning module transportMeans request. ] {} {}", e.getMessage(), e.getStackTrace());
        } finally {
        	JMSUtils.disconnectQueue(connection);
        }
    }

    @Override
    public void sendModuleErrorResponseMessage(@Observes @AssetMessageErrorEvent AssetMessageEvent message) {
    	Connection connection=null;

    	try {
            LOG.info("Sending error message back from VesselModule to recipient om JMS Queue with correlationID: {}", message.getMessage().getJMSMessageID());

            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            String data = JAXBMarshaller.marshallJaxBObjectToString(message.getFault());
            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getMessage().getJMSMessageID());
            getProducer(session, message.getMessage().getJMSReplyTo()).send(response);

        } catch (JMSException | AssetModelMarshallException e) {
            LOG.error("[ Error when returning Error message to recipient. ] {} ", e.getMessage());
        } catch (EJBTransactionRolledbackException e) {
            LOG.error("[ Error when returning Error message to recipient. Usual cause is NoAssetEntityFoundException ] {} ", e.getMessage());
        } finally {
        	JMSUtils.disconnectQueue(connection);
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