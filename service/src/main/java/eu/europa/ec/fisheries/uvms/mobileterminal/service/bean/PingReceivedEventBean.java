package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;


import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.*;

@Stateless
@LocalBean
public class PingReceivedEventBean {

    final static Logger LOG = LoggerFactory.getLogger(PingReceivedEventBean.class);

    @Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    public void ping(EventMessage message) {
        try {
            try (Connection connection = connectionFactory.createConnection()) {
                // In a Java EE web or EJB container, when there is an active JTA transaction in progress:
                // Both arguments transacted and acknowledgeMode are ignored.
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                String pingResponse = MobileTerminalModuleResponseMapper.createPingResponse("pong");
                TextMessage pingResponseMessage = session.createTextMessage(pingResponse);
                pingResponseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                pingResponseMessage.setJMSDestination(message.getJmsMessage().getJMSReplyTo());
                javax.jms.MessageProducer producer = session.createProducer(pingResponseMessage.getJMSDestination());
                producer.send(pingResponseMessage);
            }
        } catch (MobileTerminalModelMapperException | JMSException e) {
            LOG.error("Ping message went wrong", e);
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to ping MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }
    }
}
