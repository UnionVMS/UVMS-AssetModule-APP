package eu.europa.ec.fisheries.uvms.asset.dto;

import java.io.Serializable;
import java.util.List;

public class AssetMTEnrichmentResponse implements Serializable {

    private String mobileTerminalConnectId = null;
    private String mobileTerminalType = null;
    private String channelGuid = null;

    private String assetName;
    private List<String> assetFilterList = null;
    private String assetUUID = null;
    private String assetHistoryId = null;
    private String flagstate = null;
    private String vesselType = null;

    private String externalMarking = null;
    private String gearType = null;
    private String cfr = null;
    private String ircs = null;
    private String assetStatus = null;
    private String mmsi = null;
    private String imo = null;
    private String dnid = null;
    private String mobileTerminalGuid = null;
    private String memberNumber = null;
    private String serialNumber = null;
    private Boolean mobileTerminalIsInactive = null;

    public AssetMTEnrichmentResponse(){}

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public List<String> getAssetFilterList() {
        return assetFilterList;
    }

    public void setAssetFilterList(List<String> assetFilterList) {
        this.assetFilterList = assetFilterList;
    }

    public String getFlagstate() {
        return flagstate;
    }

    public void setFlagstate(String flagstate) {
        this.flagstate = flagstate;
    }

    public String getVesselType() {
        return vesselType;
    }

    public void setVesselType(String vesselType) {
        this.vesselType = vesselType;
    }

    public String getMobileTerminalConnectId() {
        return mobileTerminalConnectId;
    }

    public void setMobileTerminalConnectId(String mobileTerminalConnectId) {
        this.mobileTerminalConnectId = mobileTerminalConnectId;
    }

    public String getMobileTerminalType() {
        return mobileTerminalType;
    }

    public void setMobileTerminalType(String mobileTerminalType) {
        this.mobileTerminalType = mobileTerminalType;
    }

    public String getAssetUUID() {
        return assetUUID;
    }

    public void setAssetUUID(String assetUUID) {
        this.assetUUID = assetUUID;
    }

    public String getChannelGuid() {
        return channelGuid;
    }

    public void setChannelGuid(String channelGuid) {
        this.channelGuid = channelGuid;
    }

    public String getAssetHistoryId() {
        return assetHistoryId;
    }

    public void setAssetHistoryId(String assetHistoryId) {
        this.assetHistoryId = assetHistoryId;
    }

    public String getExternalMarking() {
        return externalMarking;
    }

    public void setExternalMarking(String externalMarking) {
        this.externalMarking = externalMarking;
    }

    public String getGearType() {
        return gearType;
    }

    public void setGearType(String gearType) {
        this.gearType = gearType;
    }

    public String getCfr() {
        return cfr;
    }

    public void setCfr(String cfr) {
        this.cfr = cfr;
    }

    public String getIrcs() {
        return ircs;
    }

    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public String getMmsi() {
        return mmsi;
    }

    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }

    public String getImo() {
        return imo;
    }

    public void setImo(String imo) {
        this.imo = imo;
    }

    public String getMobileTerminalGuid() {
        return mobileTerminalGuid;
    }

    public void setMobileTerminalGuid(String mobileTerminalGuid) {
        this.mobileTerminalGuid = mobileTerminalGuid;
    }

    public String getDNID() {
        return dnid;
    }

    public void setDNID(String dnid) {
        this.dnid = dnid;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Boolean getMobileTerminalIsInactive() {
        return mobileTerminalIsInactive;
    }

    public void setMobileTerminalIsInactive(Boolean mobileTerminalIsInactive) {
        this.mobileTerminalIsInactive = mobileTerminalIsInactive;
    }
}
