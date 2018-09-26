package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.wsdl.asset.module.GetFlagStateByGuidAndDateRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.FlagStateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Date;

@Stateless
@LocalBean
public class GetFlagstateByIdAndDateBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetFlagstateByIdAndDateBean.class);

    @EJB
    private AssetMessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    @EJB
    private AssetHistoryService service;


    public void execute(AssetMessageEvent event) {
        GetFlagStateByGuidAndDateRequest getFlagStateByGuidAndDateRequest = event.getGetFlagStateByGuidAndDateRequest();
        if (getFlagStateByGuidAndDateRequest == null) {
            LOG.error("AssetMessageEvent does not contain a message");
            return;
        }
        if (getFlagStateByGuidAndDateRequest.getAssetGuid() == null) {
            LOG.error("AssetMessageEvent does not contain a GUID");
            return;

        }
        if (getFlagStateByGuidAndDateRequest.getDate() == null) {
            LOG.error("AssetMessageEvent does not contain a DATE");
            return;
        }
        String guid = getFlagStateByGuidAndDateRequest.getAssetGuid();
        String dateStr = getFlagStateByGuidAndDateRequest.getDate();
        Date date = DateUtils.parseToUTCDate(dateStr, DateUtils.FORMAT);
        try {
            FlagStateType response = service.getFlagStateByIdAndDate(guid, date);
            // put it on response queue
            messageProducer.sendModuleResponseMessageOv(event.getMessage(), AssetModuleResponseMapper.mapFlagStateModuleResponse(response));
        } catch (NumberFormatException | AssetException | ClassCastException e) {
            LOG.error("[ Error when getting FlagSate. ] ");
            assetErrorEvent.fire(new AssetMessageEvent(event.getMessage(), AssetModuleResponseMapper.createFaultMessage(FaultCode.ASSET_MESSAGE, "Exception when getting FlagSate [ " + e.getMessage())));
        }



    }
}
