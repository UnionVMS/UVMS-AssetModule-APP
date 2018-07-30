package eu.europa.ec.fisheries.uvms.asset.service.bean;


import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class GetAssetGroupListByAssetGuidEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetAssetGroupListByAssetGuidEventBean.class);

    @EJB
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @EJB
    private AssetGroupService assetGroup;

    public void getAssetGroupListByAssetEvent(AssetMessageEvent message) {
        LOG.info("Get asset group by asset guid");
        try {
            List<AssetGroup> response = assetGroup.getAssetGroupListByAssetGuid(message.getAssetGuid());
            LOG.debug("Send back assetGroupList response.");
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }


}
