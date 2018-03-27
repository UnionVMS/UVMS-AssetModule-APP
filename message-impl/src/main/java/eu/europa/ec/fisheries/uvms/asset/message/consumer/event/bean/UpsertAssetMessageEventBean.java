package eu.europa.ec.fisheries.uvms.asset.message.consumer.event.bean;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AssetModelMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.entity.Asset;

@Stateless
public class UpsertAssetMessageEventBean {

    private static final Logger LOG = LoggerFactory.getLogger(GetAssetGroupListByAssetGuidEventBean.class);

    @Inject
    @AssetMessageErrorEvent
    Event<AssetMessageEvent> assetErrorEvent;

    @Inject
    private AssetService service;

    public void upsertAsset(AssetMessageEvent message){
        try {
            Asset assetEntity = AssetModelMapper.toAssetEntity(message.getAsset());
            service.upsertAsset(assetEntity, "");
        } catch (Exception e) {
            LOG.error("Could not update asset in the local database");
        }
    }

}
