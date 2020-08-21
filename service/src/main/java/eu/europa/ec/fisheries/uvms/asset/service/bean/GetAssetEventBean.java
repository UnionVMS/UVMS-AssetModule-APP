package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.constants.ParameterKey;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class GetAssetEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetAssetEventBean.class);

    @EJB
    private AssetService service;

    @EJB
    private ParameterService parameters;

    @EJB
    private AssetMessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    public void getAsset(TextMessage textMessage, AssetId assetId) {
        LOG.info("Getting asset.");
        AssetDataSourceQueue dataSource = null;
        Asset asset;
        boolean messageSent = false;
        try {
            dataSource = decideDataflow(assetId);
            LOG.debug("Got message to AssetModule, Executing Get asset from datasource {}", dataSource);
            asset = service.getAssetById(assetId, dataSource);
        } catch (AssetException e) {
            LOG.error("Error when getting asset from source " + dataSource ,e);
            assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting asset from source : " + dataSource + " Error message: " + e.getMessage())));
            messageSent = true;
            asset = null;
        }

        if (asset != null && !dataSource.equals(AssetDataSourceQueue.INTERNAL)) {
            try {
                Asset upsertedAsset = service.upsertAsset(asset, dataSource.name());
                asset.getAssetId().setGuid(upsertedAsset.getAssetId().getGuid());
            } catch (AssetException e) {
                LOG.error("Couldn't upsert asset in internal ",e);
                assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, e.getMessage())));
                messageSent = true;
            }
        }

        if (!messageSent) {
            try {
                messageProducer.sendModuleResponseMessageOv(textMessage, AssetModuleResponseMapper.mapAssetModuleResponse(asset));
                LOG.info("Response sent back to requestor on queue [ {} ]", textMessage!= null ? textMessage.getJMSReplyTo() : "Null!!!");
            } catch (AssetModelMapperException | JMSException e) {
                LOG.error(" Error when mapping asset ",e);
                assetErrorEvent.fire(new AssetMessageEvent(textMessage, AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when mapping asset" + e.getMessage())));
            }
        }
    }

    private AssetDataSourceQueue decideDataflow(AssetId assetId) throws AssetServiceException {
        try { // If search is made by guid, no other source is relevant
            if (AssetIdType.GUID.equals(assetId.getType())) {
                return AssetDataSourceQueue.INTERNAL;
            }
            Boolean national = parameters.getBooleanValue(ParameterKey.NATIONAL_USE.getKey());
            if (national) {
                return AssetDataSourceQueue.NATIONAL;
            }
            Boolean xeu = parameters.getBooleanValue(ParameterKey.EU_USE.getKey());
            if (xeu) {
                return AssetDataSourceQueue.XEU;
            }
            return AssetDataSourceQueue.INTERNAL;
        } catch (ConfigServiceException e) {
            LOG.error("Error when deciding data flow. ",e);
            throw new AssetServiceException(e.getMessage(),e);
        }
    }
}
