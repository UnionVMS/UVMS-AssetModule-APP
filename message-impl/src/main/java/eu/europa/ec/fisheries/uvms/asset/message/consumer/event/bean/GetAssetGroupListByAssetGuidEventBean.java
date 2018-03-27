package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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

@Stateless
public class GetAssetGroupListByAssetGuidEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(GetAssetGroupListByAssetGuidEventBean.class);

    @Inject
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @Inject
    private AssetGroupService assetGroup;

    public void getAssetGroupListByAssetEvent(AssetMessageEvent message) {
        LOG.info("Get asset group by asset guid");
        try {
            List<eu.europa.ec.fisheries.uvms.entity.AssetGroup> assetGroups = assetGroup.getAssetGroupListByAssetId(UUID.fromString(message.getAssetGuid()));
            List<AssetGroup> response = assetGroups.stream().map(AssetModelMapper::toAssetGroupModel).collect(Collectors.toList());
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetGroupListResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetGroupByUserName [ " + e.getMessage())));
        }
    }


}
