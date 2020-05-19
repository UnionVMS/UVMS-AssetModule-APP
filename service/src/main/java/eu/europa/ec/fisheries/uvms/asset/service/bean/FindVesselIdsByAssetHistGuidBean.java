package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.wsdl.asset.module.FindVesselIdsByAssetHistGuidRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Slf4j
public class FindVesselIdsByAssetHistGuidBean {

    private final static Logger LOG = LoggerFactory.getLogger(FindVesselIdsByAssetHistGuidBean.class);

    @Inject
    private AssetMessageProducer messageProducer;

    @Inject
    AssetHistoryService service;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    public void findIdentifiers(TextMessage jmsMessage, FindVesselIdsByAssetHistGuidRequest request) {
        try {
            Asset asset = service.getAssetHistoryByAssetHistGuid(request.getAssetHistoryGuid());
            String response = AssetModuleResponseMapper.createFindVesselIdsByAssetHistGuidResponse(asset);
            messageProducer.sendModuleResponseMessageOv(jmsMessage, response);
            log.info("Response sent back to requestor on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetException | JMSException e) {
            LOG.error("[ Error when getting vessel identifiers by asset history guid. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(jmsMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting vessel identifiers by asset history guid [ " + e.getMessage())));
        }
    }

}
