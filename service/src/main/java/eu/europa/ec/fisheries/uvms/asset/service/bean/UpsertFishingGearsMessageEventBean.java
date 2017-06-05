package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.bean.FishingGearDomainModelBean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class UpsertFishingGearsMessageEventBean {

    @EJB
    private FishingGearDomainModelBean fishingGearDomainModel;

    public void upsertFishingGears(AssetMessageEvent messageEvent){
        fishingGearDomainModel.upsertFishingGear(messageEvent.getFishingGear(), messageEvent.getUsername());
    }

}
