package eu.europa.ec.fisheries.uvms.asset.dto;

import java.util.UUID;

public class MicroAsset {

    UUID assetId;
    String flagstate;
    String assetName;
    String vesselType;
    String ircs;
    String cfr;
    String externalMarking;
    Double lengthOverAll;
    Boolean hasLicence;

    public MicroAsset(UUID assetId, String flagstate, String assetName, String vesselType, String ircs, String cfr, String externalMarking, Double lengthOverAll, Boolean hasLicence) {
        this.assetId = assetId;
        this.flagstate = flagstate;
        this.assetName = assetName;
        this.vesselType = vesselType;
        this.ircs = ircs;
        this.cfr = cfr;
        this.externalMarking = externalMarking;
        this.lengthOverAll = lengthOverAll;
        this.hasLicence = hasLicence;
    }

    public MicroAsset() {
    }

    public UUID getAssetId() {
        return assetId;
    }

    public void setAssetId(UUID assetId) {
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

    public String getVesselType() {
        return vesselType;
    }

    public void setVesselType(String vesselType) {
        this.vesselType = vesselType;
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

    public Double getLengthOverAll() {
        return lengthOverAll;
    }

    public void setLengthOverAll(Double lengthOverAll) {
        this.lengthOverAll = lengthOverAll;
    }

    public Boolean getHasLicence() {
        return hasLicence;
    }

    public void setHasLicence(Boolean hasLicence) {
        this.hasLicence = hasLicence;
    }
}
