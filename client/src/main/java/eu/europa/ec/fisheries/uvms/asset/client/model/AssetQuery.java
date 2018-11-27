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
import java.util.List;
import java.util.UUID;

public class AssetQuery {

    private List<UUID> id;
    private List<UUID> historyId;
    private List<String> cfr;
    private List<String> ircs;
    private List<String> mmsi;
    private List<String> imo;
    private List<String> iccat;
    private List<String> uvi;
    private List<String> gfcm;
    private List<String> name;
    private List<String> flagState;
    private List<String> externalMarking;
    private List<String> portOfRegistration;
    private List<String> licenseType;
    private List<String> producerName;
    private Integer gearType;
    private Double minLength;
    private Double maxLength;
    private Double minPower;
    private Double maxPower;
    private Instant date;

    public List<UUID> getId() {
        return id;
    }
    public void setId(List<UUID> id) {
        this.id = id;
    }
    public List<UUID> getHistoryId() {
        return historyId;
    }
    public void setHistoryId(List<UUID> historyId) {
        this.historyId = historyId;
    }
    public List<String> getCfr() {
        return cfr;
    }
    public void setCfr(List<String> cfr) {
        this.cfr = cfr;
    }
    public List<String> getIrcs() {
        return ircs;
    }
    public void setIrcs(List<String> ircs) {
        this.ircs = ircs;
    }
    public List<String> getMmsi() {
        return mmsi;
    }
    public void setMmsi(List<String> mmsi) {
        this.mmsi = mmsi;
    }
    public List<String> getImo() {
        return imo;
    }
    public void setImo(List<String> imo) {
        this.imo = imo;
    }
    public List<String> getIccat() {
        return iccat;
    }
    public void setIccat(List<String> iccat) {
        this.iccat = iccat;
    }
    public List<String> getUvi() {
        return uvi;
    }
    public void setUvi(List<String> uvi) {
        this.uvi = uvi;
    }
    public List<String> getGfcm() {
        return gfcm;
    }
    public void setGfcm(List<String> gfcm) {
        this.gfcm = gfcm;
    }
    public List<String> getName() {
        return name;
    }
    public void setName(List<String> name) {
        this.name = name;
    }
    public List<String> getFlagState() {
        return flagState;
    }
    public void setFlagState(List<String> flagState) {
        this.flagState = flagState;
    }
    public List<String> getExternalMarking() {
        return externalMarking;
    }
    public void setExternalMarking(List<String> externalMarking) {
        this.externalMarking = externalMarking;
    }
    public List<String> getPortOfRegistration() {
        return portOfRegistration;
    }
    public void setPortOfRegistration(List<String> portOfRegistration) {
        this.portOfRegistration = portOfRegistration;
    }
    public List<String> getLicenseType() {
        return licenseType;
    }
    public void setLicenseType(List<String> licenseType) {
        this.licenseType = licenseType;
    }
    public List<String> getProducerName() {
        return producerName;
    }
    public void setProducerName(List<String> producerName) {
        this.producerName = producerName;
    }
    public Integer getGearType() {
        return gearType;
    }
    public void setGearType(Integer gearType) {
        this.gearType = gearType;
    }
    public Double getMinLength() {
        return minLength;
    }
    public void setMinLength(Double minLength) {
        this.minLength = minLength;
    }
    public Double getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(Double maxLength) {
        this.maxLength = maxLength;
    }
    public Double getMinPower() {
        return minPower;
    }
    public void setMinPower(Double minPower) {
        this.minPower = minPower;
    }
    public Double getMaxPower() {
        return maxPower;
    }
    public void setMaxPower(Double maxPower) {
        this.maxPower = maxPower;
    }
    public Instant getDate() {
        return date;
    }
    public void setDate(Instant date) {
        this.date = date;
    }
}
