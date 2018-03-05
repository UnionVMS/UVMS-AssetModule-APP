package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.List;

@Stateless
@LocalBean
public class GetAssetGroupEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetAssetGroupEventBean.class);

    @EJB
    private MessageProducer messageProducer;

    @EJB
    private AssetGroupService assetGroup;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    public void getAssetGroupByUserName(AssetMessageEvent message) {
        LOG.info("Get asset group");
        try {
            AssetGroupListByUserRequest request = message.getRequest();
            List<AssetGroupEntity> response = assetGroup.getAssetGroupList(request.getUser());

            LOG.debug("Send back assetGroupList response.");
//            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
          //  messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }

}
