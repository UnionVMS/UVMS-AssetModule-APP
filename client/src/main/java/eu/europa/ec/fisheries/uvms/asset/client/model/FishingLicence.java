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

public class FishingLicence {

    private UUID id;
    private UUID assetId;
    private Long licenceNumber;
    private String civicNumber;
    private String name;
    private Instant fromDate;
    private Instant toDate;
    private Instant decisionDate;
    private String constraints;
    private Instant createdDate;
    
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getAssetId() {
        return assetId;
    }
    public void setAssetId(UUID assetId) {
        this.assetId = assetId;
    }
    public Long getLicenceNumber() {
        return licenceNumber;
    }
    public void setLicenceNumber(Long licenceNumber) {
        this.licenceNumber = licenceNumber;
    }
    public String getCivicNumber() {
        return civicNumber;
    }
    public void setCivicNumber(String civicNumber) {
        this.civicNumber = civicNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Instant getFromDate() {
        return fromDate;
    }
    public void setFromDate(Instant fromDate) {
        this.fromDate = fromDate;
    }
    public Instant getToDate() {
        return toDate;
    }
    public void setToDate(Instant toDate) {
        this.toDate = toDate;
    }
    public Instant getDecisionDate() {
        return decisionDate;
    }
    public void setDecisionDate(Instant decisionDate) {
        this.decisionDate = decisionDate;
    }
    public String getConstraints() {
        return constraints;
    }
    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }
    public Instant getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }
}