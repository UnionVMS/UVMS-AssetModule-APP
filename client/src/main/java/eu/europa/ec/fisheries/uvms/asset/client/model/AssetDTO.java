/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.client.model;


import java.time.Instant;
import java.util.UUID;

public class AssetDTO {

    private UUID id;
    private UUID historyId;
    private Boolean ircsIndicator;
    private Boolean ersIndicator;
    private Boolean aisIndicator;
    private Boolean vmsIndicator;
    private String hullMaterial;
    private Instant commissionDate;
    private String constructionYear;
    private String constructionPlace;
    private Instant updateTime;
    private String source;
    private String vesselType;
    private Instant vesselDateOfEntry;
    private String cfr;
    private String imo;
    private String ircs;
    private String mmsi;
    private String iccat;
    private String uvi;
    private String gfcm;
    private Boolean active;
    private String flagStateCode;
    private String eventCode;
    private String name;
    private String externalMarking;
    private Boolean agentIsAlsoOwner;
    private Double lengthOverAll;
    private Double lengthBetweenPerpendiculars;
    private Double safteyGrossTonnage;
    private Double otherTonnage;
    private Double grossTonnage;
    private String grossTonnageUnit;
    private String portOfRegistration;
    private Double powerOfAuxEngine;
    private Double powerOfMainEngine;
    private Boolean hasLicence;
    private String licenceType;
    private String mainFishingGearCode;
    private String subFishingGearCode;
    private String gearFishingType;
    private String ownerName;
    private Boolean hasVms;
    private String ownerAddress;
    private String assetAgentAddress;
    private String countryOfImportOrExport;
    private String typeOfExport;
    private Instant administrativeDecisionDate;
    private String segment;
    private String segmentOfAdministrativeDecision;
    private String publicAid;
    private String registrationNumber;
    private String updatedBy;
    private String prodOrgCode;
    private String prodOrgName;
    private String comment;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getHistoryId() {
        return historyId;
    }
    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }
    public Boolean getIrcsIndicator() {
        return ircsIndicator;
    }
    public void setIrcsIndicator(Boolean ircsIndicator) {
        this.ircsIndicator = ircsIndicator;
    }
    public Boolean getErsIndicator() {
        return ersIndicator;
    }
    public void setErsIndicator(Boolean ersIndicator) {
        this.ersIndicator = ersIndicator;
    }
    public Boolean getAisIndicator() {
        return aisIndicator;
    }
    public void setAisIndicator(Boolean aisIndicator) {
        this.aisIndicator = aisIndicator;
    }
    public Boolean getVmsIndicator() {
        return vmsIndicator;
    }
    public void setVmsIndicator(Boolean vmsIndicator) {
        this.vmsIndicator = vmsIndicator;
    }
    public String getHullMaterial() {
        return hullMaterial;
    }
    public void setHullMaterial(String hullMaterial) {
        this.hullMaterial = hullMaterial;
    }
    public Instant getCommissionDate() {
        return commissionDate;
    }
    public void setCommissionDate(Instant commissionDate) {
        this.commissionDate = commissionDate;
    }
    public String getConstructionYear() {
        return constructionYear;
    }
    public void setConstructionYear(String constructionYear) {
        this.constructionYear = constructionYear;
    }
    public String getConstructionPlace() {
        return constructionPlace;
    }
    public void setConstructionPlace(String constructionPlace) {
        this.constructionPlace = constructionPlace;
    }
    public Instant getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getVesselType() {
        return vesselType;
    }
    public void setVesselType(String vesselType) {
        this.vesselType = vesselType;
    }
    public Instant getVesselDateOfEntry() {
        return vesselDateOfEntry;
    }
    public void setVesselDateOfEntry(Instant vesselDateOfEntry) {
        this.vesselDateOfEntry = vesselDateOfEntry;
    }
    public String getCfr() {
        return cfr;
    }
    public void setCfr(String cfr) {
        this.cfr = cfr;
    }
    public String getImo() {
        return imo;
    }
    public void setImo(String imo) {
        this.imo = imo;
    }
    public String getIrcs() {
        return ircs;
    }
    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }
    public String getMmsi() {
        return mmsi;
    }
    public void setMmsi(String mmsi) {
        this.mmsi = mmsi;
    }
    public String getIccat() {
        return iccat;
    }
    public void setIccat(String iccat) {
        this.iccat = iccat;
    }
    public String getUvi() {
        return uvi;
    }
    public void setUvi(String uvi) {
        this.uvi = uvi;
    }
    public String getGfcm() {
        return gfcm;
    }
    public void setGfcm(String gfcm) {
        this.gfcm = gfcm;
    }
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public String getFlagStateCode() {
        return flagStateCode;
    }
    public void setFlagStateCode(String flagStateCode) {
        this.flagStateCode = flagStateCode;
    }
    public String getEventCode() {
        return eventCode;
    }
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getExternalMarking() {
        return externalMarking;
    }
    public void setExternalMarking(String externalMarking) {
        this.externalMarking = externalMarking;
    }
    public Boolean getAgentIsAlsoOwner() {
        return agentIsAlsoOwner;
    }
    public void setAgentIsAlsoOwner(Boolean agentIsAlsoOwner) {
        this.agentIsAlsoOwner = agentIsAlsoOwner;
    }
    public Double getLengthOverAll() {
        return lengthOverAll;
    }
    public void setLengthOverAll(Double lengthOverAll) {
        this.lengthOverAll = lengthOverAll;
    }
    public Double getLengthBetweenPerpendiculars() {
        return lengthBetweenPerpendiculars;
    }
    public void setLengthBetweenPerpendiculars(Double lengthBetweenPerpendiculars) {
        this.lengthBetweenPerpendiculars = lengthBetweenPerpendiculars;
    }
    public Double getSafteyGrossTonnage() {
        return safteyGrossTonnage;
    }
    public void setSafteyGrossTonnage(Double safteyGrossTonnage) {
        this.safteyGrossTonnage = safteyGrossTonnage;
    }
    public Double getOtherTonnage() {
        return otherTonnage;
    }
    public void setOtherTonnage(Double otherTonnage) {
        this.otherTonnage = otherTonnage;
    }
    public Double getGrossTonnage() {
        return grossTonnage;
    }
    public void setGrossTonnage(Double grossTonnage) {
        this.grossTonnage = grossTonnage;
    }
    public String getGrossTonnageUnit() {
        return grossTonnageUnit;
    }
    public void setGrossTonnageUnit(String grossTonnageUnit) {
        this.grossTonnageUnit = grossTonnageUnit;
    }
    public String getPortOfRegistration() {
        return portOfRegistration;
    }
    public void setPortOfRegistration(String portOfRegistration) {
        this.portOfRegistration = portOfRegistration;
    }
    public Double getPowerOfAuxEngine() {
        return powerOfAuxEngine;
    }
    public void setPowerOfAuxEngine(Double powerOfAuxEngine) {
        this.powerOfAuxEngine = powerOfAuxEngine;
    }
    public Double getPowerOfMainEngine() {
        return powerOfMainEngine;
    }
    public void setPowerOfMainEngine(Double powerOfMainEngine) {
        this.powerOfMainEngine = powerOfMainEngine;
    }
    public Boolean getHasLicence() {
        return hasLicence;
    }
    public void setHasLicence(Boolean hasLicence) {
        this.hasLicence = hasLicence;
    }
    public String getLicenceType() {
        return licenceType;
    }
    public void setLicenceType(String licenceType) {
        this.licenceType = licenceType;
    }
    public String getMainFishingGearCode() {
        return mainFishingGearCode;
    }
    public void setMainFishingGearCode(String mainFishingGearCode) {
        this.mainFishingGearCode = mainFishingGearCode;
    }
    public String getSubFishingGearCode() {
        return subFishingGearCode;
    }
    public void setSubFishingGearCode(String subFishingGearCode) {
        this.subFishingGearCode = subFishingGearCode;
    }
    public String getGearFishingType() {
        return gearFishingType;
    }
    public void setGearFishingType(String gearFishingType) {
        this.gearFishingType = gearFishingType;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public Boolean getHasVms() {
        return hasVms;
    }
    public void setHasVms(Boolean hasVms) {
        this.hasVms = hasVms;
    }
    public String getOwnerAddress() {
        return ownerAddress;
    }
    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }
    public String getAssetAgentAddress() {
        return assetAgentAddress;
    }
    public void setAssetAgentAddress(String assetAgentAddress) {
        this.assetAgentAddress = assetAgentAddress;
    }
    public String getCountryOfImportOrExport() {
        return countryOfImportOrExport;
    }
    public void setCountryOfImportOrExport(String countryOfImportOrExport) {
        this.countryOfImportOrExport = countryOfImportOrExport;
    }
    public String getTypeOfExport() {
        return typeOfExport;
    }
    public void setTypeOfExport(String typeOfExport) {
        this.typeOfExport = typeOfExport;
    }
    public Instant getAdministrativeDecisionDate() {
        return administrativeDecisionDate;
    }
    public void setAdministrativeDecisionDate(Instant administrativeDecisionDate) {
        this.administrativeDecisionDate = administrativeDecisionDate;
    }
    public String getSegment() {
        return segment;
    }
    public void setSegment(String segment) {
        this.segment = segment;
    }
    public String getSegmentOfAdministrativeDecision() {
        return segmentOfAdministrativeDecision;
    }
    public void setSegmentOfAdministrativeDecision(String segmentOfAdministrativeDecision) {
        this.segmentOfAdministrativeDecision = segmentOfAdministrativeDecision;
    }
    public String getPublicAid() {
        return publicAid;
    }
    public void setPublicAid(String publicAid) {
        this.publicAid = publicAid;
    }
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    public String getProdOrgCode() {
        return prodOrgCode;
    }
    public void setProdOrgCode(String prodOrgCode) {
        this.prodOrgCode = prodOrgCode;
    }
    public String getProdOrgName() {
        return prodOrgName;
    }
    public void setProdOrgName(String prodOrgName) {
        this.prodOrgName = prodOrgName;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
