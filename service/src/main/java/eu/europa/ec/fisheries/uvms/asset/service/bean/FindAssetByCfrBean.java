package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.module.FindAssetHistoriesByCfrModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.TextMessage;
import java.util.List;

@Stateless
@LocalBean
@Slf4j
public class FindAssetByCfrBean {

    @EJB
    private MessageProducer messageProducer;

    @EJB
    private AssetService service;

    @Inject
    @AssetMessageErrorEvent
    private Event<AssetMessageEvent> assetErrorEvent;

    public void findAssetByCfr(FindAssetHistoriesByCfrModuleRequest request, TextMessage textMessage) {
        try {
            AssetId assetId = new AssetId();
            assetId.setType(AssetIdType.CFR);
            assetId.setValue(request.getCfr());

            List<Asset> assetHistories = service.getAssetHistoryListByAssetId(assetId, Integer.MAX_VALUE);

            String findAssetByCfrResponse = AssetModuleRequestMapper.createFindAssetByCfrResponse(assetHistories);
            messageProducer.sendModuleResponseMessage(textMessage, findAssetByCfrResponse);
        } catch (AssetModelException e) {
            log.error("Error when creating createFindAssetByCfrResponse", e);
        }
    }


}
