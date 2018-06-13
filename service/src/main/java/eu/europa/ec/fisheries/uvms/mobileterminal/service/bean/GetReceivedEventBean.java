package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
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
public class GetReceivedEventBean {

    final static Logger LOG = LoggerFactory.getLogger(GetReceivedEventBean.class);

    //@Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    //private ConnectionFactory connectionFactory;

    // TODO: NOOOOOOOOOO, Config Module is locally deployed in MobileTerminal...
    @EJB
    private ParameterService parameters;

    @EJB
    private MobileTerminalServiceBean service;

    @Inject
    // OBS ---- >>>  @ErrorEvent
    Event<EventMessage> errorEvent;

    public void get(EventMessage message) {

        /*

        try {
            MobileTerminalType mobileTerminal = getMobileTerminal(message);
            try (Connection connection = connectionFactory.createConnection()) {
                // In a Java EE web or EJB container, when there is an active JTA transaction in progress:
                // Both arguments transacted and acknowledgeMode are ignored.
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                String response = MobileTerminalModuleRequestMapper.createMobileTerminalResponse(mobileTerminal);
                TextMessage responseMessage = session.createTextMessage(response);
                responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                MessageProducer producer = session.createProducer(message.getJmsMessage().getJMSReplyTo());
                producer.send(responseMessage);
            }

        } catch (MobileTerminalModelMapperException | JMSException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get a MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }
        */
    }

    // TODO: Go through this logic and error handling
    private MobileTerminalType getMobileTerminal(EventMessage message) {
        return null;

        /*
        GetMobileTerminalRequest request = null;
        MobileTerminalType mobTerm = null;
        DataSourceQueue dataSource = null;
        try {
            request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
        } catch (MobileTerminalUnmarshallException ex) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Error when mapping message: " + ex.getMessage()));
        }
        try {
            dataSource = decideDataflow();
        } catch (Exception ex) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when deciding Dataflow for : " + dataSource.name() + " Error message: " + ex.getMessage()));
        }
        try {
            LOG.debug("Got message to MobileTerminalModule, Executing Get MobileTerminal from datasource {}", dataSource.name());
            mobTerm = service.getMobileTerminalById(request.getId(), dataSource);
            if (!dataSource.equals(DataSourceQueue.INTERNAL)) {
                service.upsertMobileTerminal(mobTerm, MobileTerminalSource.NATIONAL, dataSource.name());
            }
        } catch (MobileTerminalException ex) {
            mobTerm = null;
        }
        if (mobTerm == null) {
            LOG.debug("Trying to retrieve MobileTerminal from datasource: {0} as second option", DataSourceQueue.INTERNAL.name());
            try {
                request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
                mobTerm = service.getMobileTerminalById(request.getId(), DataSourceQueue.INTERNAL);
            } catch (MobileTerminalException ex) {
                errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when getting vessel from source : " + dataSource.name() + " Error message: " + ex.getMessage()));
            }
        }
        return mobTerm;

        */
    }

//    private DataSourceQueue decideDataflow()  {
        private Object decideDataflow()  {

        return null;

        /*
        try {
            Boolean national = parameters.getBooleanValue(ParameterKey.USE_NATIONAL.getKey());
            LOG.debug("Settings for dataflow are: NATIONAL: {}", national.toString());
            if (national) {
                return DataSourceQueue.INTEGRATION;
            }
            return DataSourceQueue.INTERNAL;
        } catch (ConfigServiceException ex) {
            LOG.error("[ Error when deciding data flow. ] {}", ex.getMessage());
            throw new MobileTerminalServiceException(ex.getMessage());
        }
        */
    }
}
