package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.MobileTerminalListRequest;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.EventMessage;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.MTListResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.*;
import java.util.List;

@Stateless
@LocalBean
public class ListReceivedEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(ListReceivedEventBean.class);

    @EJB
    private MobileTerminalServiceBean mobileTerminalService;

    @Resource(lookup = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Inject
    @AssetMessageErrorEvent
    private Event<EventMessage> errorEvent;

    public void list(EventMessage message) {
        LOG.info("List Mobile terminals:{}",message);
        try {
            MobileTerminalListRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), MobileTerminalListRequest.class);

            MTListResponse mobileTerminalListResponse = mobileTerminalService.getMobileTerminalList(request.getQuery());
            List<MobileTerminal> mobileTerminalList = mobileTerminalListResponse.getMobileTerminalList();

            try (Connection connection = connectionFactory.createConnection()) {
                // In a Java EE web or EJB container, when there is an active JTA transaction in progress:
                // Both arguments transacted and acknowledgeMode are ignored.
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                String response = JAXBMarshaller.marshallJaxBObjectToString(mobileTerminalList);
                TextMessage responseMessage = session.createTextMessage(response);
                responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                javax.jms.MessageProducer producer = session.createProducer(message.getJmsMessage().getJMSReplyTo());
                producer.send(responseMessage);
            }
        } catch (JMSException | AssetException e) {
            LOG.error("Exception when trying to get list in MobileTerminal: ", e);
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get list in MobileTerminal: " + e));
            // Propagate error
            throw new EJBException(e);
        }
    }
}
