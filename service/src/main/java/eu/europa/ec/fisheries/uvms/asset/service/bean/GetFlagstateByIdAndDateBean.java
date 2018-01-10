package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.wsdl.asset.module.GetFlagStateByGuidAndDateRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

@Stateless
@LocalBean
public class GetFlagstateByIdAndDateBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetFlagstateByIdAndDateBean.class);

    @EJB
    private MessageProducer messageProducer;

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @EJB
    private AssetHistoryService service;


    public void execute(AssetMessageEvent event) {


        GetFlagStateByGuidAndDateRequest getFlagStateByGuidAndDateRequest = event.getGetFlagStateByGuidAndDateRequest();


        if (getFlagStateByGuidAndDateRequest == null) {

        }
        if (getFlagStateByGuidAndDateRequest.getAssetGuid() == null) {

        }
        if (getFlagStateByGuidAndDateRequest.getDate() == null) {

        }


        String guid = getFlagStateByGuidAndDateRequest.getAssetGuid();
        Date date = getFlagStateByGuidAndDateRequest.getDate();
        try {
            Map<String, Object> response = service.getFlagStateByIdAndDate(guid, date);
            FlagState flagState = new FlagState();
            flagState.setName(String.valueOf(response.get("name")));
            flagState.setCode(String.valueOf(response.get("code")));
            flagState.setUpdatedBy(String.valueOf(response.get("updatedBy")));
            Long id = Long.parseLong(String.valueOf(response.get("id")));
            flagState.setId(id);
            Date  updTime = (Date)response.get("updateTime");
            flagState.setUpdateTime(updTime);

            // put it on rersponse queue


        } catch (NumberFormatException | AssetException | ClassCastException e) {
            e.printStackTrace();
        }



    }
}
