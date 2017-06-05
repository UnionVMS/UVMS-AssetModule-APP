package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.property.ParameterKey;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Stateless
@LocalBean
public class GetAssetEventBean {

    final static Logger LOG = LoggerFactory.getLogger(GetAssetEventBean.class);

    @EJB
    private AssetService service;

    @EJB
    private ParameterService parameters;

    @EJB
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    public void getAsset(AssetMessageEvent message) {
        LOG.info("Getting asset.");
        AssetDataSourceQueue dataSource = null;
        Asset asset = null;
        boolean messageSent = false;

        try {
            dataSource = decideDataflow(message.getAssetId());
            LOG.debug("Got message to AssetModule, Executing Get asset from datasource {}", dataSource.name());
            asset = service.getAssetById(message.getAssetId(), dataSource);
        } catch (AssetException e) {
            LOG.error("[ Error when getting asset from source {}. ] ", dataSource.name());
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting asset from source : " + dataSource.name() + " Error message: " + e.getMessage())));
            messageSent = true;
            asset = null;
        }

        if (asset != null && !dataSource.equals(AssetDataSourceQueue.INTERNAL)) {
            try {
                Asset upsertedAsset = service.upsertAsset(asset, dataSource.name());
                asset.getAssetId().setGuid(upsertedAsset.getAssetId().getGuid());
            } catch (AssetException e) {
                LOG.error("[ Couldn't upsert asset in internal ]");
                assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, e.getMessage())));
                messageSent = true;
            }
        }

        if (!messageSent) {
            try {
                messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapAssetModuleResponse(asset));
            } catch (AssetModelMapperException e) {
                LOG.error("[ Error when mapping asset ] ");
                assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when mapping asset" + e.getMessage())));
            }
        }
    }

    private AssetDataSourceQueue decideDataflow(AssetId assetId) throws AssetServiceException {

        try {
            // If search is made by guid, no other source is relevant
            if (AssetIdType.GUID.equals(assetId.getType())) {
                return AssetDataSourceQueue.INTERNAL;
            }

            Boolean xeu = parameters.getBooleanValue(ParameterKey.EU_USE.getKey());
            Boolean national = parameters.getBooleanValue(ParameterKey.NATIONAL_USE.getKey());
            LOG.debug("Settings for dataflow are: XEU: {0} NATIONAL: {1}", new Object[]{xeu, national});
            if (xeu && national) {
                return AssetDataSourceQueue.NATIONAL;
            }
            if (national) {
                return AssetDataSourceQueue.NATIONAL;
            } else if (xeu) {
                return AssetDataSourceQueue.XEU;
            } else {
                return AssetDataSourceQueue.INTERNAL;
            }
        } catch (ConfigServiceException e) {
            LOG.error("[ Error when deciding data flow. ] ");
            throw new AssetServiceException(e.getMessage());
        }

    }


}
