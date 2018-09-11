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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIdentityInfo(generator= ObjectIdGenerators.UUIDGenerator.class /*, resolver = EntityIdResolver.class*/)
public class AssetGroup {

    private UUID id;
    private Boolean archived = false;
    private Boolean dynamic = true;
    private Boolean global = true;
    private String name;
    private OffsetDateTime updateTime;
    private String updatedBy;
    private String owner;
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public Boolean getArchived() {
        return archived;
    }
    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
    public Boolean getDynamic() {
        return dynamic;
    }
    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }
    public Boolean getGlobal() {
        return global;
    }
    public void setGlobal(Boolean global) {
        this.global = global;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
}
