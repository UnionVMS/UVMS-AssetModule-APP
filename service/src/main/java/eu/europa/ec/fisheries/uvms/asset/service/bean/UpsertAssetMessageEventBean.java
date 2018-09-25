package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageErrorEvent;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class UpsertAssetMessageEventBean {

    private final static Logger LOG = LoggerFactory.getLogger(GetAssetGroupListByAssetGuidEventBean.class);

    @Inject
    @AssetMessageErrorEvent
    private  Event<AssetMessageEvent> assetErrorEvent;

    @EJB
    private AssetService service;

    public void upsertAsset(AssetMessageEvent message){
        try {
            service.upsertAsset(message.getAsset(), AssetDataSourceQueue.INTERNAL.name());
        } catch (AssetException e) {
            LOG.error("Could not update asset in the local database");
        }
    }

}
