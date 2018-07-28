package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetFromAssetIdAndDateRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;
import java.util.Date;

@Stateless
@LocalBean
public class GetAssetFromAssetIdAndDateBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetAssetFromAssetIdAndDateBean.class);
    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;
    @EJB
    private MessageProducer messageProducer;
    @EJB
    private AssetHistoryService service;


    public void execute(AssetMessageEvent event) {


        GetAssetFromAssetIdAndDateRequest getAssetFromAssetIdAndDate = event.getGetAssetFromAssetIdAndDateRequest();


        if (getAssetFromAssetIdAndDate == null) {
            LOG.error("AssetMessageEvent does not contain a message");
            return;
        }

        if (getAssetFromAssetIdAndDate.getAssetId() == null) {
            LOG.error("AssetMessageEvent does not contain an assetId");
            return;
        }
        if (getAssetFromAssetIdAndDate.getDate() == null) {
            LOG.error("AssetMessageEvent does not contain a date");
            return;
        }
        if (getAssetFromAssetIdAndDate.getAssetId().getType() == null) {
            LOG.error("AssetMessageEvent assetId does not contain a valid transportMeans id type");
            return;
        }
        if (getAssetFromAssetIdAndDate.getAssetId().getValue() == null) {
            LOG.error("AssetMessageEvent assetId does not contain a valid valuue");
            return;
        }


        AssetId assetId = getAssetFromAssetIdAndDate.getAssetId();
        String typ = assetId.getType().value();
        String val = assetId.getValue();
        Date date = getAssetFromAssetIdAndDate.getDate();
        try {
            Asset response = service.getAssetByIdAndDate(typ, val, date);
            messageProducer.sendModuleResponseMessage(event.getMessage(), AssetModuleResponseMapper.mapAssetModuleResponse(response));

        } catch (AssetException e) {
            LOG.error("[ Error when getting assetGroupList from source. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(event.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting GetAssetFromAssetIdAndDate [ " + e.getMessage())));
        }
    }
}
