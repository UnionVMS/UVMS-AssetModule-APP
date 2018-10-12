package eu.europa.ec.fisheries.uvms.asset.client.model;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AssetMTEnrichmentResponse implements Serializable {

    private UUID mobileTerminalConnectId = null;
    private String mobileTerminalType = null;
    private String channelGuid = null;



    // private Asset asset;
    private String assetName;
    private Map<String,String > assetId = null;
    private List<UUID> assetGroupList = null;
    private UUID assetUUID = null;
    private UUID assetHistoryId = null;
    private String flagstate = null;

    public AssetMTEnrichmentResponse(){}

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Map<String, String> getAssetId() {
        return assetId;
    }

    public void setAssetId(Map<String, String> assetId) {
        this.assetId = assetId;
    }

    public List<UUID> getAssetGroupList() {
        return assetGroupList;
    }

    public void setAssetGroupList(List<UUID> assetGroupList) {
        this.assetGroupList = assetGroupList;
    }

    public String getFlagstate() {
        return flagstate;
    }

    public void setFlagstate(String flagstate) {
        this.flagstate = flagstate;
    }

    public UUID getMobileTerminalConnectId() {
        return mobileTerminalConnectId;
    }

    public void setMobileTerminalConnectId(UUID mobileTerminalConnectId) {
        this.mobileTerminalConnectId = mobileTerminalConnectId;
    }

    public String getMobileTerminalType() {
        return mobileTerminalType;
    }

    public void setMobileTerminalType(String mobileTerminalType) {
        this.mobileTerminalType = mobileTerminalType;
    }

    public UUID getAssetUUID() {
        return assetUUID;
    }

    public void setAssetUUID(UUID assetUUID) {
        this.assetUUID = assetUUID;
    }

    public String getChannelGuid() {
        return channelGuid;
    }

    public void setChannelGuid(String channelGuid) {
        this.channelGuid = channelGuid;
    }

    public UUID getAssetHistoryId() {
        return assetHistoryId;
    }

    public void setAssetHistoryId(UUID assetHistoryId) {
        this.assetHistoryId = assetHistoryId;
    }
}
