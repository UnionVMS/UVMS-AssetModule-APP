package eu.europa.ec.fisheries.uvms.asset.dto;

public class MicroAsset {

    String assetId;
    String flagstate;
    String assetName;
    String shipType;
    String ircs;
    String cfr;
    String externalMarking;

    public MicroAsset(String assetId, String flagstate, String assetName, String shipType, String ircs, String cfr, String externalMarking) {
        this.assetId = assetId;
        this.flagstate = flagstate;
        this.assetName = assetName;
        this.shipType = shipType;
        this.ircs = ircs;
        this.cfr = cfr;
        this.externalMarking = externalMarking;
    }

    public MicroAsset() {
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getFlagstate() {
        return flagstate;
    }

    public void setFlagstate(String flagstate) {
        this.flagstate = flagstate;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public String getIrcs() {
        return ircs;
    }

    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }

    public String getCfr() {
        return cfr;
    }

    public void setCfr(String cfr) {
        this.cfr = cfr;
    }

    public String getExternalMarking() {
        return externalMarking;
    }

    public void setExternalMarking(String externalMarking) {
        this.externalMarking = externalMarking;
    }
}
