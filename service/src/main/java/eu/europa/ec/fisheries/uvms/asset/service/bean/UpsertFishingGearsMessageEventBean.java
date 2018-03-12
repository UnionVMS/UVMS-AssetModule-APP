package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.service.FishingGearService;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGearEntity;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class UpsertFishingGearsMessageEventBean {

    @EJB
    private FishingGearService fishingGearService;

    public FishingGearEntity upsertFishingGears(FishingGearEntity fishingGear, String user){
        return fishingGearService.upsertFishingGear(fishingGear, user);
    }

}
