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
package eu.europa.ec.fisheries.uvms.mobileterminal.message;

import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalMessageException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.*;

@Stateless
public class MTResponseMessageConsumer implements MTMessageConsumer{

    private final static Logger LOG = LoggerFactory.getLogger(MTResponseMessageConsumer.class);

    private final static long TIMEOUT = 30000; //TODO timeout

    private ConnectionFactory connectionFactory;

    private Queue responseMobileTerminalQueue;

    @PostConstruct
    private void init() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
        responseMobileTerminalQueue = JMSUtils.lookupQueue(MessageConstants.COMPONENT_RESPONSE_QUEUE);
    }

    @Override
    public <T> T getMessage(String correlationId, Class type) throws MobileTerminalMessageException {
        if (correlationId == null || correlationId.isEmpty()) {
            throw new MobileTerminalMessageException("No CorrelationID provided!");
        }

        Connection connection=null;
        try {

            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            System.out.println("looking for message " + correlationId + " in " + MessageConstants.COMPONENT_RESPONSE_QUEUE + " with " + responseMobileTerminalQueue);
            T response = (T) session.createConsumer(responseMobileTerminalQueue, "JMSCorrelationID='" + correlationId + "'").receive(TIMEOUT);

            if (response == null) {
                throw new MobileTerminalMessageException("[ Timeout reached or message null in MobileTerminalMessageConsumer. ]" + correlationId);
            }

            return response;
        } catch (Exception e) {
            LOG.error("[ Error when consuming message. ] {}", e.getMessage());
            throw new MobileTerminalMessageException("Error when retrieving message: " + e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }
    }

}
