package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class GetAssetGroupEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetAssetGroupEventBean.class);

    @EJB
    private AssetMessageProducer messageProducer;

    @EJB
    private AssetGroupService assetGroup;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    public void getAssetGroupByUserName(AssetMessageEvent message) {
        TextMessage jmsMessage = message.getMessage();
        try {
            AssetGroupListByUserRequest request = message.getRequest();
            List<AssetGroup> response = assetGroup.getAssetGroupList(request.getUser());
            messageProducer.sendModuleResponseMessageOv(jmsMessage, AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
            LOG.info("Response sent back to requestor on queue [ {} ]", jmsMessage!= null ? jmsMessage.getJMSReplyTo() : "Null!!!");
        } catch (AssetException  | JMSException e) {
            LOG.error(" Error when getting assetGroupList from source.",e);
            assetErrorEvent.fire(new AssetMessageEvent(jmsMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }

}
