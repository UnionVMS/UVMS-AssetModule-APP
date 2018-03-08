package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.service.FishingGearService;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class UpsertFishingGearsMessageEventBean {

    @EJB
    private FishingGearService fishingGearService;

    public void upsertFishingGears(AssetMessageEvent messageEvent){
        //fishingGearService.upsertFishingGear(messageEvent.getFishingGear(), messageEvent.getUsername());
    }

}
