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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeType;
import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.MTMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.jms.*;

@Stateless
public class MTMessageConsumerBean implements MTMessageConsumer{

    private final static Logger LOG = LoggerFactory.getLogger(MTMessageConsumer.class);

    private ConnectionFactory connectionFactory;

    @PostConstruct
    private void init() {
        connectionFactory = JMSUtils.lookupConnectionFactory();
    }

    @Override
    public <T> T getMessage(String correlationId, Class type) {

        Message message = null;
        Connection connection=null;

        try {
            connection = connectionFactory.createConnection();
            final Session session = JMSUtils.connectToQueue(connection);

            message = session.createTextMessage(createResponse());
            message.setJMSCorrelationID(correlationId);
            message.setJMSMessageID(correlationId);

        } catch (ExchangeModelMarshallException | JMSException   e) {
            LOG.warn("Problem getMessage",e);
        } finally{
            JMSUtils.disconnectQueue(connection);
        }

        return (T) message;
    }

    private String createResponse() throws ExchangeModelMarshallException {

        AcknowledgeType acknowledgeType = new AcknowledgeType();
        acknowledgeType.setType(AcknowledgeTypeType.OK);
        acknowledgeType.setMessage("MESSAGE");
        return ExchangeModuleResponseMapper.mapSetCommandResponse(acknowledgeType);
    }

}
