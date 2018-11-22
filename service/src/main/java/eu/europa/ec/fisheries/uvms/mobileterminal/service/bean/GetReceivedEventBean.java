package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.module.v1.GetMobileTerminalRequest;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.constants.MessageConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.EventMessage;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper.MobileTerminalModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.ParameterKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
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
    private Event<EventMessage> errorEvent;

    public void get(EventMessage message) {
        try {
            LOG.info("Received message to MobileTerminal in Asset_SE. Message id: " + message.getJmsMessage().getJMSMessageID());
        } catch (JMSException e) {
            LOG.warn("Error while getting JMS message ID: " + e);
        }

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
        } catch ( JMSException | AssetException e) {
            LOG.error("Exception when trying to get a MobileTerminal: " + e);
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when trying to get a MobileTerminal: " + e));
            // Propagate error
            throw new EJBException(e);
        }
    }

    // TODO: Go through this logic and error handling
    public MobileTerminalType getMobileTerminal(EventMessage message) {

        GetMobileTerminalRequest request = null;
        MobileTerminalType mobTerm = null;
        DataSourceQueue dataSource = DataSourceQueue.INTERNAL;
        try {
            request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
        } catch (AssetException ex) {
            LOG.error("Error when mapping message: " + ex);
            errorEvent.fire(new EventMessage(message.getJmsMessage(), "Error when mapping message: " + ex));
        }

        try {
            LOG.debug("Got message to MobileTerminalModule, Executing Get MobileTerminal from datasource {}", dataSource.name());
            mobTerm = service.getMobileTerminalByIdFromInternalOrExternalSource(request.getId(), dataSource);
        } catch (AssetException ex) {
            mobTerm = null;
        }
        if (mobTerm == null) {
            LOG.debug("Trying to retrieve MobileTerminal from datasource: {0} as second option", DataSourceQueue.INTERNAL.name());
            try {
                request = JAXBMarshaller.unmarshallTextMessage(message.getJmsMessage(), GetMobileTerminalRequest.class);
                mobTerm = service.getMobileTerminalByIdFromInternalOrExternalSource(request.getId(), DataSourceQueue.INTERNAL);
            } catch (AssetException ex) {
                LOG.error("Exception when getting vessel from source : " + dataSource.name() + " Error message: " + ex);
                errorEvent.fire(new EventMessage(message.getJmsMessage(), "Exception when getting vessel from source : " + dataSource.name() + " Error message: " + ex));
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
