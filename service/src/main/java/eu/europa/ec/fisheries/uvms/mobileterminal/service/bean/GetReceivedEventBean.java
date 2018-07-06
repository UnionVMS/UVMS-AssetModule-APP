package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.GetMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.ParameterKey;
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

import static eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.ErrorCode.RETRIEVING_BOOL_ERROR;

@Stateless
@LocalBean
public class GetReceivedEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetReceivedEventBean.class);

    @Resource(lookup = MessageConstants.JAVA_MESSAGE_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    // TODO: NOOOOOOOOOO, Config Module is locally deployed in MobileTerminal...
    @EJB
    private ParameterService parameters;

    @EJB
    private MobileTerminalServiceBean service;

    @Inject
    @ErrorEvent
    Event<EventMessage> errorEvent;

    public void get(EventMessage message) {



        try {
            MobileTerminalType mobileTerminal = getMobileTerminal(message);
            try (Connection connection = connectionFactory.createConnection()) {
                // In a Java EE web or EJB container, when there is an active JTA transaction in progress:
                // Both arguments transacted and acknowledgeMode are ignored.
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                String response = MobileTerminalModuleRequestMapper.createMobileTerminalResponse(mobileTerminal);
                TextMessage responseMessage = session.createTextMessage(response);
                responseMessage.setJMSCorrelationID(message.getJmsMessage().getJMSMessageID());
                javax.jms.MessageProducer producer = session.createProducer(message.getJmsMessage().getJMSReplyTo());
                producer.send(responseMessage);
            }

        } catch ( JMSException | MobileTerminalModelException e) {
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get a MobileTerminal: " + e.getMessage()));
            // Propagate error
            throw new EJBException(e);
        }

    }

    // TODO: Go through this logic and error handling
    private MobileTerminalType getMobileTerminal(EventMessage message) {

        GetMobileTerminalRequest request = null;
        MobileTerminalType mobTerm = null;
        DataSourceQueue dataSource = null;
        try {
            request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
        } catch (MobileTerminalModelException ex) {
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
        } catch (MobileTerminalModelException ex) {
            mobTerm = null;
        }
        if (mobTerm == null) {
            LOG.debug("Trying to retrieve MobileTerminal from datasource: {0} as second option", DataSourceQueue.INTERNAL.name());
            try {
                request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
                mobTerm = service.getMobileTerminalById(request.getId(), DataSourceQueue.INTERNAL);
            } catch (MobileTerminalModelException ex) {
                errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when getting vessel from source : " + dataSource.name() + " Error message: " + ex.getMessage()));
            }
        }
        return mobTerm;


    }

    private DataSourceQueue decideDataflow() throws ConfigServiceException {

        Boolean national = parameters.getBooleanValue(ParameterKey.USE_NATIONAL.getKey());
        LOG.debug("Settings for dataflow are: NATIONAL: {}", national.toString());
        if (national) {
            return DataSourceQueue.INTEGRATION;
        }
        return DataSourceQueue.INTERNAL;

    }
}
