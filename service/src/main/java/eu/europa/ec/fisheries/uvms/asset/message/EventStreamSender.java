package eu.europa.ec.fisheries.uvms.asset.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMergeInfo;
import eu.europa.ec.fisheries.uvms.asset.dto.MicroAsset;
import eu.europa.ec.fisheries.uvms.asset.message.event.UpdatedAssetEvent;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.jms.*;

@Stateless
public class EventStreamSender {

    private final static Logger LOG = LoggerFactory.getLogger(EventStreamSender.class);

    @Resource(mappedName = "java:/" + MessageConstants.EVENT_STREAM_TOPIC)
    private Destination destination;

    @Inject
    @JMSConnectionFactory("java:/ConnectionFactory")
    JMSContext context;

    private ObjectMapper om = new ObjectMapper();

    @PostConstruct
    public void init() {
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public void updatedAsset(@Observes(during = TransactionPhase.AFTER_SUCCESS) @UpdatedAssetEvent Asset asset){
        try {
            if (asset != null) {
                MicroAsset micro = new MicroAsset(asset.getId(), asset.getFlagStateCode(), asset.getName(), asset.getVesselType(), asset.getIrcs(), asset.getCfr(), asset.getExternalMarking(), asset.getLengthOverAll());
                String outgoingJson = om.writeValueAsString(micro);
                sendMessageOnEventStream(outgoingJson, "Updated Asset");
            }
        }catch (Exception e){
            LOG.error("Error while sending message on event stream: ", e);
            throw new RuntimeException(e);
        }
    }

    public void mergeAsset(@Observes(during = TransactionPhase.AFTER_SUCCESS) @UpdatedAssetEvent AssetMergeInfo mergeInfo){
        try {
            if (mergeInfo != null) {
                String outgoingJson = om.writeValueAsString(mergeInfo);
                sendMessageOnEventStream(outgoingJson, "Merged Asset");
            }
        }catch (Exception e){
            LOG.error("Error while sending message on event stream: ", e);
            throw new RuntimeException(e);
        }
    }

    public void sendMessageOnEventStream(String outgoingJson, String eventName) throws JMSException {
        TextMessage message = this.context.createTextMessage(outgoingJson);
        message.setStringProperty(MessageConstants.EVENT_STREAM_EVENT, eventName);
        message.setStringProperty(MessageConstants.EVENT_STREAM_SUBSCRIBER_LIST, null);
        MappedDiagnosticContext.addThreadMappedDiagnosticContextToMessageProperties(message);

        context.createProducer().setDeliveryMode(1).setTimeToLive(5000L).send(destination, message);
    }
}
