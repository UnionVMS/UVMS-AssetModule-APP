package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;

import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AssetModelMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetGroupListByUserRequest;

@Stateless
public class GetAssetGroupEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(GetAssetGroupEventBean.class);

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
            List<eu.europa.ec.fisheries.uvms.entity.AssetGroup> assetGroups = assetGroup.getAssetGroupList(request.getUser());
            List<AssetGroup> response = assetGroups.stream().map(AssetModelMapper::toAssetGroupModel).collect(Collectors.toList());

            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }

}
