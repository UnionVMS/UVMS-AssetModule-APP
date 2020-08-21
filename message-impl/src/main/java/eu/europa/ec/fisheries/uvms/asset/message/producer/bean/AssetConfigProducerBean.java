package eu.europa.ec.fisheries.uvms.asset.message.producer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.config.message.ConfigMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Queue;


@Stateless
@Slf4j
public class AssetConfigProducerBean extends AbstractProducer implements ConfigMessageProducer {

    /**
     * Once a message is sent to config, config needs to know where to send the response... This is AssetQueue in case of Asset module..
     */
    private Queue assetInQueue;

    @PostConstruct
    public void initAssetQueue(){
        assetInQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_ASSET);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String sendConfigMessage(String textMsg) {
        try {
            return sendModuleMessage(textMsg, assetInQueue);
        } catch (MessageException e) {
            log.error("[ERROR] Error while trying to send message to Config! Check MdrConfigProducerBeanImpl..",e);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_CONFIG;
    }
}