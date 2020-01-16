package eu.europa.ec.fisheries.uvms.mobileterminal.timer;

import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetRemapMapping;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMergeInfo;
import eu.europa.ec.fisheries.uvms.asset.message.event.UpdatedAssetEvent;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AssetRemapTask {

    @Inject
    private AssetDao assetDao;

    @Inject
    private AssetServiceBean assetService;

    @Inject
    @UpdatedAssetEvent
    private Event<AssetMergeInfo> updatedAssetEvent;

    public void remap(){
        List<AssetRemapMapping> mappings = assetDao.getAllAssetRemappings();
        List<AssetRemapMapping> deleteMappings = new ArrayList<>();

        for (AssetRemapMapping mapping : mappings) {
            assetService.remapAssetsInMovement(mapping.getOldAssetId().toString(), mapping.getNewAssetId().toString());
            if(Instant.now().isAfter(mapping.getCreatedDate().plus(3, ChronoUnit.HOURS))){
                deleteMappings.add(mapping);
            }
        }
        for (AssetRemapMapping mappingToBeDeleted : deleteMappings) {
            assetService.removeMovementConnectInMovement(mappingToBeDeleted.getOldAssetId().toString());
            assetDao.deleteAssetMapping(mappingToBeDeleted);
            Asset asset = assetDao.getAssetById(mappingToBeDeleted.getOldAssetId());
            if(asset != null) {
                assetDao.deleteAsset(asset);
                updatedAssetEvent.fire(new AssetMergeInfo(mappingToBeDeleted.getOldAssetId().toString(), mappingToBeDeleted.getNewAssetId().toString()));
            }
        }
    }
}
