package eu.europa.ec.fisheries.uvms.asset.dto;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;

import java.util.UUID;

public class AssetMTEnrichmentRequest {


    private String source = null;
    private PluginType pluginType;

    // for mobileTerminal
    private String dnidValue = null;
    private String memberNumberValue = null;
    private String serialNumberValue = null;
    private String lesValue = null;

    // for Asset
    private UUID id;
    private String cfrValue = null;
    private String ircsValue = null;
    private String imoValue = null;
    private String mmsiValue = null;


    public AssetMTEnrichmentRequest(){

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public void  getIdValue(UUID id) {
        this.id = id;
    }

    public PluginType getPluginType() {
        return pluginType;
    }

    public void setPluginType(PluginType pluginType) {
        this.pluginType = pluginType;
    }


}
