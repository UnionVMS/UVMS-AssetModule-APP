package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.types.BatchAssetListResponseElement;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

@Stateless
@LocalBean
public class GetAssetListEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(GetAssetListEventBean.class);

    @EJB
    private AssetMessageProducer messageProducer;

    @EJB
    private AssetService service;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    public void getAssetList(AssetMessageEvent message) {
        TextMessage jmsMessage = message.getMessage();
        try {
            ListAssetResponse response = service.getAssetList(message.getQuery());
            messageProducer.sendModuleResponseMessageOv(jmsMessage, AssetModuleResponseMapper.mapAssetModuleResponse(response));
            LOG.info("Response sent back to requestor on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetException | JMSException e) {
            assetErrorEvent.fire(new AssetMessageEvent(jmsMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting assetlist [ " + e.getMessage())));
        }
    }
    
    public void getAssetListReporting(AssetMessageEvent message) {
    	TextMessage jmsMessage = message.getMessage();
    	try {
    		ListAssetResponse response = service.getAssetListReporting(message.getQuery());
    		messageProducer.sendModuleResponseMessageOv(jmsMessage, AssetModuleResponseMapper.mapAssetModuleResponse(response));
    		LOG.info("Response sent back to requestor on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
    	} catch (AssetException | JMSException e) {
    		assetErrorEvent.fire(new AssetMessageEvent(jmsMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting assetlist [ " + e.getMessage())));
    	}
    }

    public void getAssetListBatch(AssetMessageEvent message) {
        TextMessage jmsMessage = message.getMessage();
        try {
            List<BatchAssetListResponseElement> batchList = service.getAssetListBatch(message.getBatchQuery());
            messageProducer.sendModuleResponseMessageOv(jmsMessage, AssetModuleResponseMapper.mapToBatchListAssetModuleResponse(batchList));
            LOG.info("Response sent back to requestor on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetException | JMSException e) {
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting assetlist [ " + e.getMessage())));
        }
    }


}
