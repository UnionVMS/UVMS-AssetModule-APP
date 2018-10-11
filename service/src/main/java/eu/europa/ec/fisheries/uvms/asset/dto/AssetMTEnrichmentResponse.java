package eu.europa.ec.fisheries.uvms.asset.dto;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssetMTEnrichmentResponse implements Serializable {

    private MobileTerminalType mobileTerminalType;
    private Asset asset;
    private List<UUID> assetGroupList = null;
    private Map<String,String > assetId = null;

    public AssetMTEnrichmentResponse(){}

    public MobileTerminalType getMobileTerminalType() {
        return mobileTerminalType;
    }

    public void setMobileTerminalType(MobileTerminalType mobileTerminalType) {
        this.mobileTerminalType = mobileTerminalType;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public List<UUID> getAssetGroupList() {
        return assetGroupList;
    }

    public void setAssetGroupList(List<UUID> assetGroupList) {
        this.assetGroupList = assetGroupList;
    }

    public Map<String, String> getAssetId() {
        return assetId;
    }

    public void setAssetId(Map<String, String> assetId) {
        this.assetId = assetId;
    }
}
