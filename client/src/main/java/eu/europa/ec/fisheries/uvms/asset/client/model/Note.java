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

import java.time.OffsetDateTime;
import java.util.UUID;

public class Note {

    private UUID id;
    private UUID assetId;
    private OffsetDateTime date;
    private String activityCode;
    private String user;
    private OffsetDateTime readyDate;
    private String licenseHolder;
    private String contact;
    private String sheetNumber;
    private String notes;
    private String document;
    private String source;
    private OffsetDateTime updateTime;
    private String updatedBy;
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
    public OffsetDateTime getDate() {
        return date;
    }
    public void setDate(OffsetDateTime date) {
        this.date = date;
    }
    public String getActivityCode() {
        return activityCode;
    }
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public OffsetDateTime getReadyDate() {
        return readyDate;
    }
    public void setReadyDate(OffsetDateTime readyDate) {
        this.readyDate = readyDate;
    }
    public String getLicenseHolder() {
        return licenseHolder;
    }
    public void setLicenseHolder(String licenseHolder) {
        this.licenseHolder = licenseHolder;
    }
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getSheetNumber() {
        return sheetNumber;
    }
    public void setSheetNumber(String sheetNumber) {
        this.sheetNumber = sheetNumber;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getDocument() {
        return document;
    }
    public void setDocument(String document) {
        this.document = document;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public OffsetDateTime getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(OffsetDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
