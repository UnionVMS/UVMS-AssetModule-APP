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

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalFault;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.*;

@Stateless
@LocalBean
public class MobileTerminalEventServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalEventServiceBean.class);

    @Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Inject
    @ErrorEvent
    private Event<EventMessage> errorEvent;

    public void returnError(@Observes @ErrorEvent EventMessage message) {
        try (Connection connection = connectionFactory.createConnection()) {
            LOG.debug("Sending error message back from Mobile Terminal module to recipient om JMS Queue with correlationID: {} ",
                    message.getJmsMessage().getJMSMessageID());
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MobileTerminalFault request = new MobileTerminalFault();
            request.setMessage(message.getErrorMessage());
            String data = JAXBMarshaller.marshallJaxBObjectToString(request);

            TextMessage response = session.createTextMessage(data);
            response.setJMSCorrelationID(message.getJmsMessage().getJMSCorrelationID());
            MessageProducer producer = session.createProducer(message.getJmsMessage().getJMSReplyTo());
            producer.send(response);
        } catch (Exception ex) {
            LOG.error("Error when returning Error message to recipient", ex);
        }
    }
}
