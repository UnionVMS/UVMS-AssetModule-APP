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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.entity.asset.types.HullMaterialEnum;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the asset database table.
 *
 */
@Entity
@Table(name = "Asset")
public class AssetEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "asset_id")
    private Long id;

    @Size(max = 12)
    @Column(name = "asset_cfr", unique = true)
    private String cfr;

    @Size(min = 36, max = 36)
    @Column(name = "asset_guid", unique = true)
    private String guid;

    @Size(max = 7)
    @Column(name = "asset_imo")
    private String imo;

    @Size(max = 8)
    @Column(name = "asset_ircs")
    private String ircs;

    @Size(max = 9)
    @Column(name = "asset_mmsi", unique = true)
    private String mmsi;

    @Size(min = 1, max = 1)
    @Column(name = "asset_ircsindicator")
    private String assetIrcsindicator;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_hullmaterial")
    private HullMaterialEnum hullMaterial;

    @Size(min = 2, max = 2)
    @Column(name = "asset_dayofcommissioning")
    private String commissionDay;

    @Size(min = 2, max = 2)
    @Column(name = "asset_monthofcommissioning")
    private String commissionMonth;

    @Size(min = 4, max = 4)
    @Column(name = "asset_yearofcommissioning")
    private String commissionYear;

    @Size(min = 4, max = 4)
    @Column(name = "asset_yearofconstruction")
    private String constructionYear;

    @Size(max = 100)
    @Column(name = "asset_placeofconstruction")
    private String constructionPlace;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "asset_updattim")
    private Date updateTime;

    @Size(max = 60)
    @Column(name = "asset_upuser")
    private String updatedBy;

    @Fetch(FetchMode.JOIN)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "asset_carr_id")
    private Carrier carrier;

    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private List<AssetHistory> histories;

    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private List<Notes> notes;

    @Size(max = 50)
    @Column(name = "asset_iccat")
    private String iccat;

    @Size(max = 50)
    @Column(name = "asset_uvi")
    private String uvi;

    @Size(max = 50)
    @Column(name = "asset_gfcm")
    private String gfcm;


    public AssetEntity() {
    }

    @PrePersist
    private void prepersist() {
        setGuid(UUID.randomUUID().toString());
    }

    public List<Notes> getNotes() {
        return notes;
    }

    public void setNotes(List<Notes> notes) {
        this.notes = notes;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCFR() {
        return this.cfr;
    }

    public void setCFR(String cfr) {
        this.cfr = cfr;
    }

    public String getCommissionDay() {
        return this.commissionDay;
    }

    public void setCommissionDay(String commissionDay) {
        this.commissionDay = commissionDay;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public HullMaterialEnum getHullMaterial() {
        return this.hullMaterial;
    }

    public void setHullMaterial(HullMaterialEnum hullMaterial) {
        this.hullMaterial = hullMaterial;
    }

    public String getIMO() {
        return this.imo;
    }

    public void setIMO(String imo) {
        this.imo = imo;
    }

    public String getIRCS() {
        return this.ircs;
    }

    public void setIRCS(String ircs) {
        this.ircs = ircs;
    }

    public String getIrcsIndicator() {
        return this.assetIrcsindicator;
    }

    public void setIrcsIndicator(String ircsIndicator) {
        this.assetIrcsindicator = ircsIndicator;
    }

    public String getMMSI() {
        return this.mmsi;
    }

    public void setMMSI(String mmsi) {
        this.mmsi = mmsi;
    }

    public String getCommissionMonth() {
        return this.commissionMonth;
    }

    public void setCommissionMonth(String commissionMonth) {
        this.commissionMonth = commissionMonth;
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

    public String getVessYearofcommissioning() {
        return this.commissionYear;
    }

    public void setCommissionYear(String commissionyear) {
        this.commissionYear = commissionyear;
    }

    public String getConstructionYear() {
        return this.constructionYear;
    }

    public void setConstructionYear(String constructionyear) {
        this.constructionYear = constructionyear;
    }

    public Carrier getCarrier() {
        return this.carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public String getConstructionPlace() {
        return constructionPlace;
    }

    public void setConstructionPlace(String constructionPlace) {
        this.constructionPlace = constructionPlace;
    }

    public List<AssetHistory> getHistories() {
        return histories;
    }

    public void setHistories(List<AssetHistory> histories) {
        this.histories = histories;
    }


    public String getIccat() { return iccat; }

    public void setIccat(String iccat) { this.iccat = iccat; }

    public String getUvi() {return uvi; }

    public void setUvi(String uvi) {this.uvi = uvi; }

    public String getGfcm() { return gfcm; }

    public void setGfcm(String gfcm) { this.gfcm = gfcm; }






}