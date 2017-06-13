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
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Stateless
public class AssetQueueConsumerBean implements AssetQueueConsumer, ConfigMessageConsumer {

    final static Logger LOG = LoggerFactory.getLogger(AssetQueueConsumerBean.class);

    private final static long TIMEOUT = 30000;

    private Queue responseAssetQueue;

    private ConnectionFactory connectionFactory;

    private Connection connection = null;
    private Session session = null;

    @PostConstruct
    private void init() {
        LOG.debug("Open connection to JMS broker");
        InitialContext ctx;
        try {
            ctx = new InitialContext();
        } catch (Exception e) {
            LOG.error("Failed to get InitialContext",e);
            throw new RuntimeException(e);
        }
        try {
            connectionFactory = (QueueConnectionFactory) ctx.lookup(AssetConstants.CONNECTION_FACTORY);
        } catch (NamingException ne) {
            //if we did not find the connection factory we might need to add java:/ at the start
            LOG.debug("Connection Factory lookup failed for " + AssetConstants.CONNECTION_FACTORY);
            String wfName = "java:/" + AssetConstants.CONNECTION_FACTORY;
            try {
                LOG.debug("trying "+wfName);
                connectionFactory = (QueueConnectionFactory) ctx.lookup(wfName);
            } catch (Exception e) {
                LOG.error("Connection Factory lookup failed for both " + AssetConstants.CONNECTION_FACTORY + " and " + wfName);
                throw new RuntimeException(e);
            }
        }
        responseAssetQueue = JMSUtils.lookupQueue(ctx, AssetConstants.QUEUE_ASSET);
    }

    @Override
    public <T> T getMessage(String correlationId, Class type) throws AssetMessageException {
        try {

            if (correlationId == null || correlationId.isEmpty()) {
                throw new AssetMessageException("No CorrelationID provided!");
            }

            connectToQueue();

            T response = (T) session.createConsumer(responseAssetQueue, "JMSCorrelationID='" + correlationId + "'").receive(TIMEOUT);
            if (response == null) {
                throw new AssetMessageException("[ Timeout reached or message null in AssetQueueConsumerBean. ]");
            }

            return response;
        } catch (Exception e) {
            LOG.error("[ Error when retrieving message. ] {}", e.getMessage());
            throw new AssetMessageException("Error when retrieving message: " + e.getMessage());
        } finally {
            disconnectQueue();
        }
    }

    @Override
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessage(correlationId, type);
        }
        catch (AssetMessageException e) {
            LOG.error("[ Error when getting config message. ] {}", e.getMessage());
            throw new ConfigMessageException(e.getMessage());
        }
    }

    private void connectToQueue() throws JMSException {
        connection = connectionFactory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();
    }

    private void disconnectQueue() {
        try {
            if (connection != null) {
                connection.stop();
                connection.close();
            }
        } catch (JMSException e) {
            LOG.error("[ Error when closing JMS connection ] {}", e.getMessage());
        }
    }

}

