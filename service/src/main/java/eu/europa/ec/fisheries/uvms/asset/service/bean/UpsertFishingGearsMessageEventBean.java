package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;

@Stateless
@LocalBean
public class UpsertFishingGearsMessageEventBean {

//    @EJB
//    private FishingGearDomainModelBean fishingGearDomainModel;
//
    public void upsertFishingGears(AssetMessageEvent messageEvent){
//        fishingGearDomainModel.upsertFishingGear(messageEvent.getFishingGear(), messageEvent.getUsername());
    }

}
