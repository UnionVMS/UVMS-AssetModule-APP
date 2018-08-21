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
package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField.ASSETGROUP_FIELD_CLEAR;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField.ASSETGROUP_FIELD_GETBYID;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField.ASSETGROUP_RETRIEVE_FIELDS_FOR_GROUP;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Assetgroupfield", indexes = {@Index(columnList = "assetGroup", name = "assetgroupfield_assetgroup_FK_INX01", unique = false),})
@NamedQueries({
		@NamedQuery(name="Assetgroupfield.findAll", query="SELECT a FROM AssetGroupField a"),
		@NamedQuery(name=ASSETGROUP_FIELD_GETBYID, query="SELECT a FROM AssetGroupField a where a.id=:id"),
		@NamedQuery(name=ASSETGROUP_FIELD_CLEAR, query="DELETE  FROM AssetGroupField a where a.assetGroup=:assetgroup"),
		@NamedQuery(name=ASSETGROUP_RETRIEVE_FIELDS_FOR_GROUP, query="SELECT a  FROM AssetGroupField a where a.assetGroup=:assetgroup"),
})
public class AssetGroupField implements Serializable {

    public static final String ASSETGROUP_FIELD_CLEAR = "Assetgroupfield.clear";
    public static final String ASSETGROUP_FIELD_GETBYID = "Assetgroupfield.getbyid";
    public static final String ASSETGROUP_RETRIEVE_FIELDS_FOR_GROUP = "Assetgroupfield.retrievefieldsforgroup";

    private static final long serialVersionUID = 2806956373362523218L;

    @Id
    @GeneratedValue(generator = "FIELD_UUID")
    @GenericGenerator(name = "FIELD_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Size(max = 80)
    @NotNull
    @Column(name = "field")
    private String field;

    @Column(name = "updatetime")
    private OffsetDateTime updateTime;

    @Size(max = 60)
    @Column(name = "updateuser")
    private String updatedBy;

    @Size(max = 100)
    @NotNull
    @Column(name = "value")
    private String value;

    @Column(name = "assetgroup")
    private UUID assetGroup;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String filterField) {
        this.field = filterField;
    }

    public OffsetDateTime getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UUID getAssetGroup() {
        return this.assetGroup;
    }

    public void setAssetGroup(UUID assetGroup) {
        this.assetGroup = assetGroup;
    }

}
