package eu.europa.ec.fisheries.uvms.asset.dto;

public class AssetMergeInfo {
    String oldAssetId;
    String newAssetId;

    public AssetMergeInfo(String oldAssetId, String newAssetId) {
        this.oldAssetId = oldAssetId;
        this.newAssetId = newAssetId;
    }

    public String getOldAssetId() {
        return oldAssetId;
    }

    public void setOldAssetId(String oldAssetId) {
        this.oldAssetId = oldAssetId;
    }

    public String getNewAssetId() {
        return newAssetId;
    }

    public void setNewAssetId(String newAssetId) {
        this.newAssetId = newAssetId;
    }
}
