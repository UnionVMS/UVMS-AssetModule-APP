package eu.europa.ec.fisheries.uvms.entity.model;

import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static eu.europa.ec.fisheries.uvms.entity.model.AssetSE.*;

@Audited
@Entity
@Table(name = "AssetSE")
@NamedQueries({
          @NamedQuery(name = ASSET_FIND_ALL, query = "SELECT v FROM AssetSE v"),
          @NamedQuery(name = ASSET_FIND_BY_CFR, query = "SELECT v FROM AssetSE v WHERE v.cfr = :cfr"),
          @NamedQuery(name = ASSET_FIND_BY_IRCS, query = "SELECT v FROM AssetSE v WHERE v.ircs = :ircs"),
          @NamedQuery(name = ASSET_FIND_BY_IMO, query = "SELECT v FROM AssetSE v WHERE v.imo = :imo"),
          @NamedQuery(name = ASSET_FIND_BY_MMSI, query = "SELECT v FROM AssetSE v WHERE v.mmsi = :mmsi"),
          @NamedQuery(name = ASSET_FIND_BY_ICCAT, query = "SELECT v FROM AssetSE v WHERE v.iccat = :iccat"),
          @NamedQuery(name = ASSET_FIND_BY_UVI, query = "SELECT v FROM AssetSE v WHERE v.uvi = :uvi"),
          @NamedQuery(name = ASSET_FIND_BY_GFCM, query = "SELECT v FROM AssetSE v WHERE v.gfcm = :gfcm"),
          @NamedQuery(name = ASSET_FIND_BY_IDS, query = "SELECT v FROM AssetSE v WHERE v.id in :idList"),

})

public class AssetSE implements Serializable{

    public static final String ASSET_FIND_BY_CFR = "Asset.findByCfr";
    public static final String ASSET_FIND_BY_IRCS = "Asset.findByIrcs";
    public static final String ASSET_FIND_BY_IMO = "Asset.findByImo";
    public static final String ASSET_FIND_BY_MMSI = "Asset.findByMMSI";
    public static final String ASSET_FIND_BY_ICCAT = "Asset.findByIccat";
    public static final String ASSET_FIND_BY_UVI = "Asset.findByUvi";
    public static final String ASSET_FIND_BY_GFCM = "Asset.findByGfcm";
    public static final String ASSET_FIND_ALL = "Asset.findAll";
    public static final String ASSET_FIND_BY_IDS = "Asset.findByIds";


    /*
    public static final String ASSET_FIND_BY_ID = "Asset.findById";
    public static final String ASSET_FIND_BY_GUID = "Asset.findByGuid";

    public static final String ASSETHISTORY_FIND_BY_GUID = "Assethistory.findByGuid";

    public static final String GROUP_ASSET_FIND_ALL = "AssetGroup.findAll";
    public static final String GROUP_ASSET_BY_USER = "AssetGroup.findByUser";
    public static final String GROUP_ASSET_BY_GUID = "AssetGroup.findByGuid";
    public static final String GROUP_ASSET_BY_GUID_LIST = "AssetGroup.findByGuidList";

    public static final String LICENSE_TYPE_LIST = "LicenseType.findAll";
    public static final String FLAG_STATE_LIST = "FlagState.findAll";
    public static final String SETTING_LIST = "Setting.findAll";
    public static final String SETTING_BY_FIELD = "Setting.findByField";

    public static final String QUEUE_DOMAIN_MODEL = "jms/queue/UVMSAssetModel";
    public static final String QUEUE_NAME_DOMAIN_MODEL = "UVMSAssetModel";

    public static final String VESSEL_CONNECTION_FACTORY = "java:jboss/DefaultJMSConnectionFactory";
    public static final String CONNECTION_TYPE = "javax.jms.MessageListener";
    public static final String DESTINATION_TYPE_QUEUE = "javax.jms.Queue";
    public static final String CONNECTION_FACTORY = "ConnectionFactory";

    public static final String ASSET_FIND_BY_CFR_EXCLUDE_ARCHIVED = "Asset.findByCfrExcludeArchived";
    public static final String ASSET_FIND_BY_IRCS_EXCLUDE_ARCHIVED = "Asset.findByIrcsExcludeArchived";
    public static final String ASSET_FIND_BY_IMO_EXCLUDE_ARCHIVED = "Asset.findByImoExcludeArchived";
    public static final String ASSET_FIND_BY_MMSI_EXCLUDE_ARCHIVED = "Asset.findByMMSIExcludeArchived";
    */



    private static final long serialVersionUID = -320627625723663100L;

    public AssetSE() {
        // json serialization
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name="UUID",
            strategy="org.hibernate.id.UUIDGenerator"
    )
    @Column(name="id")
    private UUID id;

    @Column(name="historyid")
    private UUID historyid;

    @Size(min = 1, max = 1)
    @Column(name="ircsindicator")
    private String ircsIndicator;

    @Enumerated(EnumType.STRING)
    @Column(name="hullmaterial")
    private HullMaterialEnum hullMaterial;

    @Column(name="commissiondate")
    private LocalDateTime commissionDate;

    @Size(min = 4, max = 4)
    @Column(name="constructionyear")
    private String constructionYear;

    @Size(max = 100)
    @Column(name="constructionplace")
    private String constructionPlace;

    @Column(name="updatetime")
    private LocalDateTime updateTime;


    @Enumerated(EnumType.STRING)
    @Column(name="source")
    private CarrierSourceEnum source;

    /**********************************
     *   FROM HISTORY                 *
     **********************************/

    @Size(max = 12)
    @Column(unique = true, name="cfr")
    private String cfr;

    @Size(max = 7)
    @Column(name="imo")
    private String imo;

    @Size(max = 8)
    @Column(name="ircs")
    private String ircs;

    @Size(max = 9)
    @Column(unique = true, name="mmsi")
    private String mmsi;

    @Size(max = 50)
    @Column(name="iccat")
    private String iccat;

    @Size(max = 50)
    @Column(name="uvi")
    private String uvi;

    @Size(max = 50)
    @Column(name="gfcm")
    private String gfcm;

    @Column(name="active")
    private Boolean active;

    @Size(min = 3, max = 3)
    @Column(name="flagstatecode")
    private String flagStateCode;

    @Column(name="eventcodeid")
    private Long eventCodeId;

    @Size(max = 40)
    @Column(name="name")
    private String name;

    @Size(max = 14)
    @Column(name="externalmarking")
    private String externalMarking;

    @Column(name="agentisalsoowner")
    private Boolean agentIsAlsoOwner;

    @Digits(integer = 6, fraction = 2)
    @Column(name="lengthoverall")
    private BigDecimal lengthOverAll;

    @Digits(integer = 6, fraction = 2)
    @Column(name="lengthbetweenperpendiculars")
    private BigDecimal lengthBetweenPerpendiculars;

    @Digits(integer = 7, fraction = 2)
    @Column(name="safetygrosstonnage")
    private BigDecimal safteyGrossTonnage;

    @Digits(integer = 8, fraction = 2)
    @Column(name="othertonnage")
    private BigDecimal otherTonnage;

    @Digits(integer = 8, fraction = 2)
    @Column(name="grosstonnage")
    private BigDecimal grossTonnage;

    @Enumerated(EnumType.STRING)
    @Column(name="grosstonnageunit")
    private UnitTonnage grossTonnageUnit = UnitTonnage.LONDON;

    @Size(max = 30)
    @Column(name="portofregistration")
    private String portOfRegistration;

    @Digits(integer = 8, fraction = 2)
    @Column(name="powerofauxengine")
    private BigDecimal powerOfAuxEngine;

    @Digits(integer = 8, fraction = 2)
    @Column(name="powerofmainengine")
    private BigDecimal powerOfMainEngine;

    @Column(name="haslicense")
    private Boolean hasLicence;

    @Size(max = 25)
    @Column(name="licensetype")
    private String licenceType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="mainfishinggear")
    @NotAudited
    private FishingGearEntity mainFishingGear;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="subfishinggear")
    @NotAudited
    private FishingGearEntity subFishingGear;

    @Column(name="gearfishingtype")
    private int gearFishingType;

    @Size(max = 100)
    @Column(name="ownername")
    private String ownerName;

    @Column(name="hasvms")
    private Boolean hasVms;

    @Size(max = 100)
    @Column(name="owneraddress")
    private String ownerAddress;

    @Size(max = 100)
    @Column(name="assetagentaddress")
    private String assetAgentAddress;

    @Size(min = 3, max = 3)
    @Column(name="countryofimportorexport")
    private String countryOfImportOrExport;

    @Column(name="administrativedecisiondate")
    private LocalDateTime administrativeDecisionDate;

    @Enumerated(EnumType.STRING)
    @Column(name="segment")
    private SegmentFUP segment;

    @Enumerated(EnumType.STRING)
    @Column(name="segmentofadministrativedecision")
    private SegmentFUP segmentOfAdministrativeDecision;

    @Enumerated(EnumType.STRING)
    @Column(name="publicaid")
    private PublicAidEnum publicAid;

    @Size(max = 14)
    @Column(name="registrationnumber")
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="typeofexport")
    private TypeOfExportEnum typeOfExport;

    @Size(max = 60)
    @Column(name="updatedby")
    private String updatedBy;


    /**********************************
     *   FROM ProdOrg                 *
     **********************************/

    @Column(name="prodorgcode")
    private String prodOrgCode;

    @Column(name="prodorgname")
    private String prodOrgName;

    @PrePersist
    @PreUpdate
    private void generateNewHistoryId() {
        this.historyid = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIrcsIndicator() {
        return ircsIndicator;
    }

    public void setIrcsIndicator(String ircsIndicator) {
        this.ircsIndicator = ircsIndicator;
    }

    public HullMaterialEnum getHullMaterial() {
        return hullMaterial;
    }

    public void setHullMaterial(HullMaterialEnum hullMaterial) {
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

    public CarrierSourceEnum getSource() {
        return source;
    }

    public void setSource(CarrierSourceEnum source) {
        this.source = source;
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

    public Long getEventCodeId() {
        return eventCodeId;
    }

    public void setEventCodeId(Long eventCodeId) {
        this.eventCodeId = eventCodeId;
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

    public BigDecimal getLengthOverAll() {
        return lengthOverAll;
    }

    public void setLengthOverAll(BigDecimal lengthOverAll) {
        this.lengthOverAll = lengthOverAll;
    }

    public BigDecimal getLengthBetweenPerpendiculars() {
        return lengthBetweenPerpendiculars;
    }

    public void setLengthBetweenPerpendiculars(BigDecimal lengthBetweenPerpendiculars) {
        this.lengthBetweenPerpendiculars = lengthBetweenPerpendiculars;
    }

    public BigDecimal getSafteyGrossTonnage() {
        return safteyGrossTonnage;
    }

    public void setSafteyGrossTonnage(BigDecimal safteyGrossTonnage) {
        this.safteyGrossTonnage = safteyGrossTonnage;
    }

    public BigDecimal getOtherTonnage() {
        return otherTonnage;
    }

    public void setOtherTonnage(BigDecimal otherTonnage) {
        this.otherTonnage = otherTonnage;
    }

    public BigDecimal getGrossTonnage() {
        return grossTonnage;
    }

    public void setGrossTonnage(BigDecimal grossTonnage) {
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

    public BigDecimal getPowerOfAuxEngine() {
        return powerOfAuxEngine;
    }

    public void setPowerOfAuxEngine(BigDecimal powerOfAuxEngine) {
        this.powerOfAuxEngine = powerOfAuxEngine;
    }

    public BigDecimal getPowerOfMainEngine() {
        return powerOfMainEngine;
    }

    public void setPowerOfMainEngine(BigDecimal powerOfMainEngine) {
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

    public FishingGearEntity getMainFishingGear() {
        return mainFishingGear;
    }

    public void setMainFishingGear(FishingGearEntity mainFishingGear) {
        this.mainFishingGear = mainFishingGear;
    }

    public FishingGearEntity getSubFishingGear() {
        return subFishingGear;
    }

    public void setSubFishingGear(FishingGearEntity subFishingGear) {
        this.subFishingGear = subFishingGear;
    }

    public int getGearFishingType() {
        return gearFishingType;
    }

    public void setGearFishingType(int gearFishingType) {
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

    public LocalDateTime getAdministrativeDecisionDate() {
        return administrativeDecisionDate;
    }

    public void setAdministrativeDecisionDate(LocalDateTime administrativeDecisionDate) {
        this.administrativeDecisionDate = administrativeDecisionDate;
    }

    public SegmentFUP getSegment() {
        return segment;
    }

    public void setSegment(SegmentFUP segment) {
        this.segment = segment;
    }

    public SegmentFUP getSegmentOfAdministrativeDecision() {
        return segmentOfAdministrativeDecision;
    }

    public void setSegmentOfAdministrativeDecision(SegmentFUP segmentOfAdministrativeDecision) {
        this.segmentOfAdministrativeDecision = segmentOfAdministrativeDecision;
    }

    public PublicAidEnum getPublicAid() {
        return publicAid;
    }

    public void setPublicAid(PublicAidEnum publicAid) {
        this.publicAid = publicAid;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public TypeOfExportEnum getTypeOfExport() {
        return typeOfExport;
    }

    public void setTypeOfExport(TypeOfExportEnum typeOfExport) {
        this.typeOfExport = typeOfExport;
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

    public UUID getHistoryId() {
        return this.historyid;
    }





}
