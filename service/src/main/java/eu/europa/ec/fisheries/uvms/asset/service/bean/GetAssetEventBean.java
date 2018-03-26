package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.constants.ParameterKey;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetFault;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;

@Stateless
@LocalBean
public class GetAssetEventBean {

    final static Logger LOG = LoggerFactory.getLogger(GetAssetEventBean.class);

    @EJB
    private AssetService assetService;

    @EJB
    private ParameterService parameters;

    @EJB
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    public void getAsset(TextMessage textMessage, AssetId assetId) {

        AssetDataSourceQueue dataSource = null;
        Asset asset = null;
        boolean messageSent = false;

        try {
            dataSource = decideDataflow(assetId);
            // TODO
//            asset = assetService.getAssetById(assetId, dataSource);
        } catch (AssetException e) {
            LOG.error("[ Error when getting asset from source {}. ] ", dataSource.name());
            //assetErrorEvent.fire(new AssetMessageEvent(textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting asset from source : " + dataSource.name() + " Error message: " + e.getMessage())));
            messageSent = true;
            asset = null;
        }

        /*
        if (asset != null && !dataSource.equals(AssetDataSourceQueue.INTERNAL)) {
            try {
                AssetDTO upsertedAsset = service.upsertAsset(asset, dataSource.name());
                asset.getAssetId().setGuid(upsertedAsset.getAssetId().getGuid());
            } catch (AssetException e) {
                LOG.error("[ Couldn't upsert asset in internal ]");
                assetErrorEvent.fire(new AssetMessageEvent(textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, e.getMessage())));
                messageSent = true;
            }
        }
        */

        if (!messageSent) {
            try {
                messageProducer.sendModuleResponseMessage(textMessage, mapAssetModuleResponse(asset));
            } catch (AssetModelMapperException e) {
                LOG.error("[ Error when mapping asset ] ");
                //assetErrorEvent.fire(new AssetMessageEvent(textMessage, createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when mapping asset" + e.getMessage())));
            }
        }

    }

    private AssetDataSourceQueue decideDataflow(AssetId assetId) throws AssetServiceException {

        try {
            // If search is made by guid, no other source is relevant
//            if (AssetIdTypeEnum.GUID.equals(assetId.getType())) {
//                return AssetDataSourceQueue.INTERNAL;
//            }

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

    public AssetFault createFaultMessage(FaultCode code, String message) {
        AssetFault fault = new AssetFault();
        fault.setCode(code.getCode());
        fault.setFault(message);
        return fault;
    }

    public  String mapAssetModuleResponse(Asset asset) throws AssetModelMapperException {

        String json = null;
//        try {
//            json = MAPPER.writeValueAsString(asset);
            return json;
//        } catch (JsonProcessingException e) {
//            throw new AssetModelMapperException(e.toString(), e);
//        }
    }



}
