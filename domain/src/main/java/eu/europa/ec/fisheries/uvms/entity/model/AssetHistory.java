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
package eu.europa.ec.fisheries.uvms.entity.model;

import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.entity.asset.types.*;
import eu.europa.ec.fisheries.wsdl.asset.types.ContactType;
import eu.europa.ec.fisheries.wsdl.asset.types.HullMaterial;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The persistent class for the assethistory database table.
 *
 */
@Entity
@Table(name = "Assethistory")
@NamedQueries({
    @NamedQuery(name = UvmsConstants.ASSETHISTORY_FIND_BY_GUID, query = "SELECT v FROM AssetHistory v WHERE v.guid = :guid"),
    @NamedQuery(name = UvmsConstants.ASSETHISTORY_FIND_BY_GUIDS, query = " SELECT DISTINCT vh FROM AssetHistory vh  INNER JOIN FETCH vh.asset v INNER JOIN FETCH v.carrier c WHERE c.active = '1' AND vh.active = '1' AND v.guid  IN :guids")
})

public class AssetHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "assethist_id")
    private Long id;

    @Size(min = 36, max = 36)
    @Column(name = "assethist_guid")
    private String guid;

    @Column(name = "assethist_active")
    private Boolean active;

    @Size(min = 3, max = 3)
    @Column(name = "assethist_countryregistration")
    private String countryOfRegistration;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assethist_dateofevent")
    private Date dateOfEvent;

    @Column(name = "assethist_eventcode_id")
    private long eventCodeId;

    @Size(max = 40)
    @Column(name = "assethist_nameofasset")
    private String name;

    @Size(max = 14)
    @Column(name = "assethist_externalmarking")
    private String externalMarking;

    @Column(name = "assethist_indicatorowner")
    private Boolean assetAgentIsAlsoOwner;

    @Digits(integer = 6, fraction = 2)
    @Column(name = "assethist_loa")
    private BigDecimal lengthOverAll;

    @Digits(integer = 6, fraction = 2)
    @Column(name = "assethist_lbp")
    private BigDecimal lengthBetweenPerpendiculars;

    @Digits(integer = 7, fraction = 2)
    @Column(name = "assethist_gts")
    private BigDecimal safteyGrossTonnage;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "assethist_othertonnage")
    private BigDecimal otherTonnage;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "assethist_tonnagegt")
    private BigDecimal grossTonnage;

    @Enumerated(EnumType.STRING)
    @Column(name = "assethist_tonnagegt_unit")
    private UnitTonnage grossTonnageUnit = UnitTonnage.LONDON;

    @Size(max = 30)
    @Column(name = "assethist_portofregistration")
    private String portOfRegistration;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "assethist_powerofauxengine")
    private BigDecimal powerOfAuxEngine;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "assethist_powerofmainengine")
    private BigDecimal powerOfMainEngine;

    @Column(name = "assethist_licenceindicator")
    private Boolean hasLicence;

    @Size(max = 25)
    @Column(name = "assethist_licencetype")
    private String licenceType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "assethist_main_fishgear_id")
    private FishingGear mainFishingGear;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "assethist_sub_fishgear_id")
    private FishingGear subFishingGear;

    @Column(name = "assethist_fishingtype")
    private int gearFishingType;

    @Size(max = 100)
    @Column(name = "assethist_nameowner")
    private String ownerName;

    @Fetch(FetchMode.JOIN)
    @OneToOne(fetch = FetchType.LAZY,  cascade = CascadeType.PERSIST)
    @JoinColumn(name = "assethist_assetpo_id")
    private AssetProdOrg assetProdOrg;

    /*
    @Size(max = 100)
    @Column(name = "assethist_nameassetagent")
    private String contactName;

    @Size(max = 100)
    @Column(name = "assethist_contactnumber")
    private String contactNumber;

    @Size(max = 100)
    @Column(name = "assethist_contactemail")
    private String contactEmail;

    @Size(max = 2048)
    @Column(name = "assethist_notes")
    private String notes;
    */

    @Fetch(FetchMode.JOIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assethist_asset_id")
    private AssetEntity asset;

    @Column(name = "assethist_vmsindicator")
    private Boolean hasVms;

    @Size(max = 100)
    @Column(name = "assethist_addressowner")
    private String ownerAddress;

    @Size(max = 100)
    @Column(name = "assethist_addressassetagent")
    private String assetAgentAddress;

    @Size(min = 3, max = 3)
    @Column(name = "assethist_countryofimpexp")
    private String countryOfImportOrExport;

    @Size(min = 8, max = 8)
    @Column(name = "assethist_dateadmindecision")
    private String administrativeDecisionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "assethist_segment")
    private SegmentFUP segment;

    @Enumerated(EnumType.STRING)
    @Column(name = "assethist_decisionadmin_seg")
    private SegmentFUP segmentOfAdministrativeDecision;

    @Enumerated(EnumType.STRING)
    @Column(name = "assethist_publicaid")
    private PublicAidEnum publicAid;

    @Size(max = 14)
    @Column(name = "assethist_registrationnumber")
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "assethist_typeofexport")
    private TypeOfExportEnum typeOfExport;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assethist_updattim")
    private Date updateTime;

    @Size(max = 60)
    @Column(name = "assethist_upuser")
    private String updatedBy;

    @Size(max = 12)
    @Column(name = "assethist_cfr", unique = true)
    private String cfr;

    @Size(max = 7)
    @Column(name = "assethist_imo")
    private String imo;

    @Size(max = 8)
    @Column(name = "assethist_ircs")
    private String ircs;

    @Size(min = 9, max = 9)
    @Column(name = "assethist_mmsi", unique = true)
    private String mmsi;


    @OneToMany(mappedBy = "assetHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private List<ContactInfo> contactInfo;

    @Size(max = 50)
    @Column(name = "assethist_iccat")
    private String iccat;

    @Size(max = 50)
    @Column(name = "assethist_uvi")
    private String uvi;

    @Size(max = 50)
    @Column(name = "assethist_gfcm")
    private String gfcm;

    @Column(name = "assethist_ers_indicator")
    private Boolean ersIndicator;

    @Column(name = "assethist_ais_indicator")
    private Boolean aisIndicator;

    @Size(max = 100)
    @Column(name = "assethist_vessel_type")
    private String vesselType;

    @Column(name = "assethist_vessel_date_of_entry")
    private Date vesselDateOfEntry;

    @Size(min = 1, max = 1)
    @Column(name = "assethist_ircs_indicator")
    private String assetIrcsindicator;

    @Enumerated(EnumType.STRING)
    @Column(name = "assethist_hull_material")
    private HullMaterial hullMaterial;

    @Column(name = "assethist_year_of_construction")
    private String constructionYear;

    public AssetHistory() {
    }

    @PrePersist
    private void prepersist() {
        setGuid(UUID.randomUUID().toString());
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerAddress() {
        return this.ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getAssetAgentAddress() {
        return this.assetAgentAddress;
    }

    public void setAssetAgentAddress(String agentAddress) {
        this.assetAgentAddress = agentAddress;
    }

    /*
    public String getContactName() {
        return this.contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    */
    public String getCountryOfImportOrExport() {
        return this.countryOfImportOrExport;
    }

    public void setCountryOfImportOrExport(String countryOfImportOrExport) {
        this.countryOfImportOrExport = countryOfImportOrExport;
    }

    public String getCountryOfRegistration() {
        return this.countryOfRegistration;
    }

    public void setCountryOfRegistration(String countryOfRegistration) {
        this.countryOfRegistration = countryOfRegistration;
    }

    public String getAdminstrativeDecisionDate() {
        return this.administrativeDecisionDate;
    }

    public void setAdministrativeDecisionDate(String administrativeDecisionDate) {
        this.administrativeDecisionDate = administrativeDecisionDate;
    }

    public Date getDateOfEvent() {
        return this.dateOfEvent;
    }

    public void setDateOfEvent(Date dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    public SegmentFUP getSegmentOfAdministrativeDecision() {
        return this.segmentOfAdministrativeDecision;
    }

    public void setSegmentOfAdministrativeDecision(SegmentFUP segment) {
        this.segmentOfAdministrativeDecision = segment;
    }

    public EventCodeEnum getEventCode() {
        return EventCodeEnum.getType(this.eventCodeId);
    }

    public void setEventCode(EventCodeEnum eventCodeEnum) {
        if (eventCodeEnum != null) {
            this.eventCodeId = eventCodeEnum.getId();
        }
    }

    public String getExternalMarking() {
        return this.externalMarking;
    }

    public void setExternalMarking(String externalMarking) {
        this.externalMarking = externalMarking;
    }

    public GearFishingTypeEnum getType() {
        return GearFishingTypeEnum.getType(this.gearFishingType);
    }

    public void setType(GearFishingTypeEnum fishingType) {
        if (fishingType != null) {
            this.gearFishingType = fishingType.getId();
        }
    }

    public BigDecimal getSafteyGrossTonnage() {
        return this.safteyGrossTonnage;
    }

    public void setSafteyGrossTonnage(BigDecimal gts) {
        this.safteyGrossTonnage = gts;
    }

    public Boolean getAssetAgentIsAlsoOwner() {
        return this.assetAgentIsAlsoOwner;
    }

    public void setAssetAgentIsAlsoOwner(Boolean agentIsAlsoOwner) {
        this.assetAgentIsAlsoOwner = agentIsAlsoOwner;
    }

    public BigDecimal getLengthBetweenPerpendiculars() {
        return this.lengthBetweenPerpendiculars;
    }

    public void setLengthBetweenPerpendiculars(BigDecimal lbp) {
        this.lengthBetweenPerpendiculars = lbp;
    }

    public Boolean getHasLicence() {
        return this.hasLicence;
    }

    public void setHasLicence(Boolean hasLicence) {
        this.hasLicence = hasLicence;
    }

    public String getLicenceType() {
        return this.licenceType;
    }

    public void setLicenceType(String licenceType) {
        this.licenceType = licenceType;
    }

    public BigDecimal getLengthOverAll() {
        return this.lengthOverAll;
    }

    public void setLengthOverAll(BigDecimal loa) {
        this.lengthOverAll = loa;
    }

    public FishingGear getMainFishingGear() {
        return this.mainFishingGear;
    }

    public void setMainFishingGear(FishingGear fishingGear) {
        this.mainFishingGear = fishingGear;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /*
    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    */
    public BigDecimal getOtherTonnage() {
        return this.otherTonnage;
    }

    public void setOtherTonnage(BigDecimal otherTonnage) {
        this.otherTonnage = otherTonnage;
    }

    public String getPortOfRegistration() {
        return this.portOfRegistration;
    }

    public void setPortOfRegistration(String portOfRegistration) {
        this.portOfRegistration = portOfRegistration;
    }

    public BigDecimal getPowerOfAuxEngine() {
        return this.powerOfAuxEngine;
    }

    public void setPowerOfAuxEngine(BigDecimal powerOfAuxEngine) {
        this.powerOfAuxEngine = powerOfAuxEngine;
    }

    public BigDecimal getPowerOfMainEngine() {
        return this.powerOfMainEngine;
    }

    public void setPowerOfMainEngine(BigDecimal powerOfMainEngine) {
        this.powerOfMainEngine = powerOfMainEngine;
    }

    public PublicAidEnum getPublicAid() {
        return this.publicAid;
    }

    public void setPublicAid(PublicAidEnum publicAid) {
        this.publicAid = publicAid;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public SegmentFUP getSegment() {
        return this.segment;
    }

    public void setSegment(SegmentFUP segment) {
        this.segment = segment;
    }

    public FishingGear getSubFishingGear() {
        return this.subFishingGear;
    }

    public void setSubFishingGear(FishingGear gear) {
        this.subFishingGear = gear;
    }

    public BigDecimal getGrossTonnage() {
        return this.grossTonnage;
    }

    public void setGrossTonnage(BigDecimal grossTonnage) {
        this.grossTonnage = grossTonnage;
    }

    public TypeOfExportEnum getTypeOfExport() {
        return this.typeOfExport;
    }

    public void setTypeOfExport(TypeOfExportEnum typeOfExport) {
        this.typeOfExport = typeOfExport;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public AssetEntity getAsset() {
        return this.asset;
    }

    public void setAsset(AssetEntity asset) {
        this.asset = asset;
    }

    public Boolean getHasVms() {
        return this.hasVms;
    }

    public void setHasVms(Boolean vms) {
        this.hasVms = vms;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    /*
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }
    */
    public UnitTonnage getGrossTonnageUnit() {
        return grossTonnageUnit;
    }

    public void setGrossTonnageUnit(UnitTonnage grossTonnageUnit) {
        this.grossTonnageUnit = grossTonnageUnit;
    }

    public List<ContactInfo> getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(List<ContactInfo> contactInfo) {
        this.contactInfo = contactInfo;
    }

    public AssetProdOrg getAssetProdOrg() { return assetProdOrg; }

    public void setAssetProdOrg(AssetProdOrg assetProdOrg) { this.assetProdOrg = assetProdOrg; }

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

    public String getIccat() { return iccat; }

    public void setIccat(String iccat) { this.iccat = iccat; }

    public String getUvi() {return uvi; }

    public void setUvi(String uvi) {this.uvi = uvi; }

    public String getGfcm() { return gfcm; }

    public void setGfcm(String gfcm) { this.gfcm = gfcm; }

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

    public String getVesselType() {
        return vesselType;
    }

    public void setVesselType(String vesselType) {
        this.vesselType = vesselType;
    }

    public Date getVesselDateOfEntry() {
        return vesselDateOfEntry;
    }

    public void setVesselDateOfEntry(Date vesselDateOfEntry) {
        this.vesselDateOfEntry = vesselDateOfEntry;
    }

    public String getAssetIrcsindicator() {
        return assetIrcsindicator;
    }

    public void setAssetIrcsindicator(String assetIrcsindicator) {
        this.assetIrcsindicator = assetIrcsindicator;
    }

    public HullMaterial getHullMaterial() {
        return hullMaterial;
    }

    public void setHullMaterial(HullMaterial hullMaterial) {
        this.hullMaterial = hullMaterial;
    }

    public String getConstructionYear() {
        return constructionYear;
    }

    public void setConstructionYear(String constructionYear) {
        this.constructionYear = constructionYear;
    }
}

