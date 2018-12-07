package eu.europa.ec.fisheries.uvms.asset.dto;

import java.util.UUID;

public class AssetMTEnrichmentRequest {

    private String transpondertypeValue = null;  // the server only looks for TRANSPONDER_TYPE
    private String pluginType = null;
    private String user= null;

    // for mobileTerminal
    private String dnidValue = null;
    private String memberNumberValue = null;
    private String serialNumberValue = null;
    private String lesValue = null;

    // for Asset
    private UUID id = null;
    private String cfrValue = null;
    private String ircsValue = null;
    private String imoValue = null;
    private String mmsiValue = null;
    private String gfcmValue = null;
    private String uviValue = null;
    private String iccatValue = null;

    public AssetMTEnrichmentRequest(){
    }

    public String getTranspondertypeValue() {
        return transpondertypeValue;
    }

    public void setTranspondertypeValue(String transpondertypeValue) {
        this.transpondertypeValue = transpondertypeValue;
    }

    public String getDnidValue() {
        return dnidValue;
    }

    public void setDnidValue(String dnidValue) {
        this.dnidValue = dnidValue;
    }

    public String getMemberNumberValue() {
        return memberNumberValue;
    }

    public void setMemberNumberValue(String memberNumberValue) {
        this.memberNumberValue = memberNumberValue;
    }

    public String getSerialNumberValue() {
        return serialNumberValue;
    }

    public void setSerialNumberValue(String serialNumberValue) {
        this.serialNumberValue = serialNumberValue;
    }

    public String getLesValue() {
        return lesValue;
    }

    public void setLesValue(String lesValue) {
        this.lesValue = lesValue;
    }

    public String getCfrValue() {
        return cfrValue;
    }

    public void setCfrValue(String cfrValue) {
        this.cfrValue = cfrValue;
    }

    public String getIrcsValue() {
        return ircsValue;
    }

    public void setIrcsValue(String ircsValue) {
        this.ircsValue = ircsValue;
    }

    public String getImoValue() {
        return imoValue;
    }

    public void setImoValue(String imoValue) {
        this.imoValue = imoValue;
    }

    public String getMmsiValue() {
        return mmsiValue;
    }

    public void setMmsiValue(String mmsiValue) {
        this.mmsiValue = mmsiValue;
    }

    public UUID getIdValue() {
        return id;
    }

    public void  setIdValue(UUID id) {
        this.id = id;
    }

    public String getGfcmValue() {
        return gfcmValue;
    }

    public void setGfcmValue(String gfcmValue) {
        this.gfcmValue = gfcmValue;
    }

    public String getUviValue() {
        return uviValue;
    }

    public void setUviValue(String uviValue) {
        this.uviValue = uviValue;
    }

    public String getIccatValue() {
        return iccatValue;
    }

    public void setIccatValue(String iccatValue) {
        this.iccatValue = iccatValue;
    }

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
