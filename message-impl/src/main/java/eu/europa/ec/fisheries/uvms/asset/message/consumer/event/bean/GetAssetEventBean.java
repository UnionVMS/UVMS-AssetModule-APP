package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AssetModelMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.constant.AssetIdentity;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;

@Stateless
public class GetAssetEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(GetAssetEventBean.class);

    @Inject
    private AssetService assetService;

    @Inject
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    public void getAsset(TextMessage textMessage, AssetId assetId) {

        Asset asset = null;
        boolean messageSent = false;

        try {
            AssetIdentity assetIdentity = AssetModelMapper.mapToAssetIdentity(assetId.getType());
            asset = assetService.getAssetById(assetIdentity, assetId.getValue());
        } catch (AssetException e) {
            LOG.error("Error when getting asset by id", assetId.getValue(), e);
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting asset by id : " + assetId.getValue() + " Error message: " + e.getMessage())));
            messageSent = true;
            asset = null;
        }

        if (!messageSent) {
            try {
                String response = AssetModuleResponseMapper.mapAssetModuleResponse(AssetModelMapper.toAssetModel(asset));
                messageProducer.sendModuleResponseMessage(textMessage, response);
            } catch (AssetModelMapperException e) {
                LOG.error("[ Error when mapping asset ] ");
                assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when mapping asset" + e.getMessage())));
            }
        }

    }
}
