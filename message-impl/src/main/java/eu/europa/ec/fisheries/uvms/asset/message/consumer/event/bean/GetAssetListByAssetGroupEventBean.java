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
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.entity.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetListByAssetGroupsRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

@Stateless
public class GetAssetListByAssetGroupEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(GetAssetGroupEventBean.class);

    @EJB
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @EJB
    private AssetService service;

    public void getAssetListByAssetGroups(AssetMessageEvent message) {
        try {
            GetAssetListByAssetGroupsRequest request = message.getAssetListByGroup();

            if (request == null) {
                assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetListByVesselGroups [ Request is null ]")));
                return;
            }
            List<AssetGroup> assetGroupModels = request.getGroups().stream().map(AssetModelMapper::toAssetGroupEntity).collect(Collectors.toList());
            List<eu.europa.ec.fisheries.uvms.entity.Asset> assets = service.getAssetListByAssetGroups(assetGroupModels);

            List<Asset> assetModels = assets.stream().map(AssetModelMapper::toAssetModel).collect(Collectors.toList());
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapToAssetListByAssetGroupResponse(assetModels));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting AssetListByVesselGroups [ " + e.getMessage())));
        }
    }
}
