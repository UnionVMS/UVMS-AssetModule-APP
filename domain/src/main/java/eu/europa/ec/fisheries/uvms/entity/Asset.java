package eu.europa.ec.fisheries.uvms.entity;

import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_ALL;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_CFR;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_GFCM;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_ICCAT;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_IDS;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_IMO;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_IRCS;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_MMSI;
import static eu.europa.ec.fisheries.uvms.entity.Asset.ASSET_FIND_BY_UVI;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;

@Audited
@Entity
@Table(name = "Asset")
@NamedQueries({
          @NamedQuery(name = ASSET_FIND_ALL, query = "SELECT v FROM Asset v"),
          @NamedQuery(name = ASSET_FIND_BY_CFR, query = "SELECT v FROM Asset v WHERE v.cfr = :cfr"),
          @NamedQuery(name = ASSET_FIND_BY_IRCS, query = "SELECT v FROM Asset v WHERE v.ircs = :ircs"),
          @NamedQuery(name = ASSET_FIND_BY_IMO, query = "SELECT v FROM Asset v WHERE v.imo = :imo"),
          @NamedQuery(name = ASSET_FIND_BY_MMSI, query = "SELECT v FROM Asset v WHERE v.mmsi = :mmsi"),
          @NamedQuery(name = ASSET_FIND_BY_ICCAT, query = "SELECT v FROM Asset v WHERE v.iccat = :iccat"),
          @NamedQuery(name = ASSET_FIND_BY_UVI, query = "SELECT v FROM Asset v WHERE v.uvi = :uvi"),
          @NamedQuery(name = ASSET_FIND_BY_GFCM, query = "SELECT v FROM Asset v WHERE v.gfcm = :gfcm"),
          @NamedQuery(name = ASSET_FIND_BY_IDS, query = "SELECT v FROM Asset v WHERE v.id in :idList"),
})
public class Asset implements Serializable {

    public static final String ASSET_FIND_BY_CFR = "Asset.findByCfr";
    public static final String ASSET_FIND_BY_IRCS = "Asset.findByIrcs";
    public static final String ASSET_FIND_BY_IMO = "Asset.findByImo";
    public static final String ASSET_FIND_BY_MMSI = "Asset.findByMMSI";
    public static final String ASSET_FIND_BY_ICCAT = "Asset.findByIccat";
    public static final String ASSET_FIND_BY_UVI = "Asset.findByUvi";
    public static final String ASSET_FIND_BY_GFCM = "Asset.findByGfcm";
    public static final String ASSET_FIND_ALL = "Asset.findAll";
    public static final String ASSET_FIND_BY_IDS = "Asset.findByIds";

    private static final long serialVersionUID = -320627625723663100L;

    @Id
    @GeneratedValue(generator = "ASSET_UUID")
    @GenericGenerator(name = "ASSET_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Column(unique = true, name = "historyid")
    private UUID historyId;

    @Column(name = "ircsindicator")
    private Boolean ircsIndicator;

    @Column(name = "ersindicator")
    private Boolean ersIndicator;

    @Column(name = "aisindicator")
    private Boolean aisIndicator;

    @Column(name = "vmsindicator")
    private Boolean vmsIndicator;

    @Column(name = "hullmaterial")
    private String hullMaterial;

    @Column(name = "commissiondate")
    private LocalDateTime commissionDate;

    @Size(min = 4, max = 4)
    @Column(name = "constructionyear")
    private String constructionYear;

    @Size(max = 100)
    @Column(name = "constructionplace")
    private String constructionPlace;

    @Column(name = "updatetime")
    private LocalDateTime updateTime;

    @Column(name = "source")
    private String source;

    @Size(max = 100)
    @Column(name = "vesseltype")
    private String vesselType;

    @Column(name = "vesselDateOfEntry")
    private LocalDateTime vesselDateOfEntry;

    @Size(max = 12)
    @Column(unique = true, name = "cfr")
    private String cfr;

    @Size(max = 7)
    @Column(unique = true, name = "imo")
    private String imo;

    @Size(max = 8)
    @Column(unique = true, name = "ircs")
    private String ircs;

    @Size(max = 9)
    @Column(unique = true, name = "mmsi")
    private String mmsi;

    @Size(max = 50)
    @Column(unique = true, name = "iccat")
    private String iccat;

    @Size(max = 50)
    @Column(unique = true, name = "uvi")
    private String uvi;

    @Size(max = 50)
    @Column(unique = true, name = "gfcm")
    private String gfcm;

    @Column(name = "active")
    private Boolean active;

    @Size(min = 3, max = 3)
    @Column(name = "flagstatecode")
    private String flagStateCode;

    @Column(name = "eventcode")
    private String eventCode;

    @Size(max = 40)
    @Column(name = "name")
    private String name;

    @Size(max = 14)
    @Column(name = "externalmarking")
    private String externalMarking;

    @Column(name = "agentisalsoowner")
    private Boolean agentIsAlsoOwner;

    @Digits(integer = 6, fraction = 2)
    @Column(name = "lengthoverall")
    private Double lengthOverAll;

    @Digits(integer = 6, fraction = 2)
    @Column(name = "lengthbetweenperpendiculars")
    private Double lengthBetweenPerpendiculars;

    @Digits(integer = 7, fraction = 2)
    @Column(name = "safetygrosstonnage")
    private Double safteyGrossTonnage;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "othertonnage")
    private Double otherTonnage;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "grosstonnage")
    private Double grossTonnage;

    @Enumerated(EnumType.STRING)
    @Column(name = "grosstonnageunit")
    private UnitTonnage grossTonnageUnit = UnitTonnage.LONDON;

    @Size(max = 30)
    @Column(name = "portofregistration")
    private String portOfRegistration;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "powerofauxengine")
    private Double powerOfAuxEngine;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "powerofmainengine")
    private Double powerOfMainEngine;

    @Column(name = "haslicense")
    private Boolean hasLicence;

    @Size(max = 25)
    @Column(name = "licensetype")
    private String licenceType;

    @Column(name = "mainfishinggearcode")
    private String mainFishingGearCode;

    @Column(name = "subfishinggearcode")
    private String subFishingGearCode;

    @Column(name = "gearfishingtype")
    private Integer gearFishingType;

    @Size(max = 100)
    @Column(name = "ownername")
    private String ownerName;

    @Column(name = "hasvms")
    private Boolean hasVms;

    @Size(max = 100)
    @Column(name = "owneraddress")
    private String ownerAddress;

    @Size(max = 100)
    @Column(name = "assetagentaddress")
    private String assetAgentAddress;

    @Size(min = 3, max = 3)
    @Column(name = "countryofimportorexport")
    private String countryOfImportOrExport;

    @Column(name = "typeofexport")
    private String typeOfExport;

    @Column(name = "administrativedecisiondate")
    private LocalDateTime administrativeDecisionDate;

    @Column(name = "segment")
    private String segment;

    @Column(name = "segmentofadministrativedecision")
    private String segmentOfAdministrativeDecision;

    @Column(name = "publicaid")
    private String publicAid;

    @Size(max = 14)
    @Column(name = "registrationnumber")
    private String registrationNumber;

    @Size(max = 60)
    @Column(name = "updatedby")
    private String updatedBy;

    @Column(name = "prodorgcode")
    private String prodOrgCode;

    @Column(name = "prodorgname")
    private String prodOrgName;

    @PrePersist
    @PreUpdate
    private void generateNewHistoryId() {
        this.historyId = UUID.randomUUID();
    }

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

    public LocalDateTime getCommissionDate() {
        return commissionDate;
    }

    public void setCommissionDate(LocalDateTime commissionDate) {
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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
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

    public LocalDateTime getVesselDateOfEntry() {
        return vesselDateOfEntry;
    }

    public void setVesselDateOfEntry(LocalDateTime vesselDateOfEntry) {
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

    public UnitTonnage getGrossTonnageUnit() {
        return grossTonnageUnit;
    }

    public void setGrossTonnageUnit(UnitTonnage grossTonnageUnit) {
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

    public Integer getGearFishingType() {
        return gearFishingType;
    }

    public void setGearFishingType(Integer gearFishingType) {
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

    public LocalDateTime getAdministrativeDecisionDate() {
        return administrativeDecisionDate;
    }

    public void setAdministrativeDecisionDate(LocalDateTime administrativeDecisionDate) {
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
}
