package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Stateless
@LocalBean
public class ListReceivedEventBean {

    final static Logger LOG = LoggerFactory.getLogger(ListReceivedEventBean.class);

    @EJB
    private MobileTerminalServiceBean mobileTerminalService;

    //@Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    //private ConnectionFactory connectionFactory;

    @Inject
    // @ErrorEvent
    Event<EventMessage> errorEvent;

    public void list(EventMessage message) {
        /*
        LOG.info("List Mobile terminals:{}",message);
        try {
            MobileTerminalListRequest request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), MobileTerminalListRequest.class);

            MobileTerminalListResponse mobileTerminalListResponse = mobileTerminalService.getMobileTerminalList(request.getQuery());
            List<MobileTerminalType> mobileTerminalTypes = mobileTerminalListResponse.getMobileTerminal();

            try (Connection connection = connectionFactory.createConnection()) {
                // In a Java EE web or EJB container, when there is an active JTA transaction in progress:
                // Both arguments transacted and acknowledgeMode are ignored.
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                String response = MobileTerminalModuleRequestMapper.mapGetMobileTerminalList(mobileTerminalTypes);
                TextMessage responseMessage = session.createTextMessage(response);
                responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                AssetMessageProducer producer = session.createProducer(message.getJmsMessage().getJMSReplyTo());
                producer.send(responseMessage);
            }
        } catch (MobileTerminalException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get list in MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }
        */
    }
}
