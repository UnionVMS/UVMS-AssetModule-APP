package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Stateless
@LocalBean
public class GetAssetListEventBean {

    final static Logger LOG = LoggerFactory.getLogger(GetAssetListEventBean.class);

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetService service;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    public void getAssetList(AssetMessageEvent message) {
        LOG.info("Get asset list");
        try {
            ListAssetResponse response = service.getAssetList(message.getQuery());

            LOG.debug("Send back assetlist response.");
            messageProducer.sendModuleResponseMessage(message.getMessage(), AssetModuleResponseMapper.mapAssetModuleResponse(response));
        } catch (AssetException e) {
            LOG.error("[ Error when getting assetlist from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(message.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting assetlist [ " + e.getMessage())));
        }
    }


}
