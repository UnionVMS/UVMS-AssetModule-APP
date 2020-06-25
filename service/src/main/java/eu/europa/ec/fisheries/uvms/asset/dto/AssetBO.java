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
package eu.europa.ec.fisheries.uvms.asset.dto;

import java.util.List;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.FishingLicence;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Note;

public class AssetBO {

    private Asset asset;
    private List<ContactInfo> contacts;
    private List<Note> notes;
    private FishingLicence fishingLicence;
    private AssetIdentifier defaultIdentifier = AssetIdentifier.CFR;
    
    public Asset getAsset() {
        return asset;
    }
    public void setAsset(Asset asset) {
        this.asset = asset;
    }
    public List<ContactInfo> getContacts() {
        return contacts;
    }
    public void setContacts(List<ContactInfo> contacts) {
        this.contacts = contacts;
    }
    public List<Note> getNotes() {
        return notes;
    }
    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
    public FishingLicence getFishingLicence() {
        return fishingLicence;
    }
    public void setFishingLicence(FishingLicence fishingLicence) {
        this.fishingLicence = fishingLicence;
    }
    public AssetIdentifier getDefaultIdentifier() {
        return defaultIdentifier;
    }
    public void setDefaultIdentifier(AssetIdentifier defaultIdentifier) {
        this.defaultIdentifier = defaultIdentifier;
    }
}
