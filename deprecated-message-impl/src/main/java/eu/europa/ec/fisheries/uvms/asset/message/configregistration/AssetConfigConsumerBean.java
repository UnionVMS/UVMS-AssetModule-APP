package eu.europa.ec.fisheries.uvms.asset.message.configregistration;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractConsumer;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigMessageException;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
@LocalBean
public class AssetConfigConsumerBean extends AbstractConsumer implements ConfigMessageConsumer {

    private final static Logger LOG = LoggerFactory.getLogger(AssetConfigConsumerBean.class);

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public <T> T getConfigMessage(String correlationId, Class type) throws ConfigMessageException {
        try {
            return getMessage(correlationId, type);
        } catch (MessageException e) {
            LOG.error("[ Error when getting config message. ] {}", e);
            throw new ConfigMessageException(e.getMessage());
        }
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_ASSET;
    }
}
