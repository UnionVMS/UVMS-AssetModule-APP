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
package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the oceanregion database table.
 *
 */
@Entity
@Table(name = "dnid_list", indexes = { @Index(columnList = "dnid", name = "dnid_list_INX_01", unique = false),})
@NamedQueries({
	@NamedQuery(name = MobileTerminalConstants.DNID_LIST, query = "SELECT dnid FROM DNIDList dnid"),
	@NamedQuery(name = MobileTerminalConstants.DNID_LIST_BY_PLUGIN, query = "SELECT dnid FROM DNIDList dnid where dnid.pluginName = :pluginName")
})
public class DNIDList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DNID_UUID")
    @GenericGenerator(name = "DNID_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Size(max = 100)
    @Column(name = "dnid")
    private String dnid;

    @Size(max = 500)
    @Column(name = "plugin_service_name")
    private String pluginName;

    @Column(name = "updattim")
    private OffsetDateTime updateTime;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    public DNIDList() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDnid() {
        return dnid;
    }

    public void setDnid(String dnid) {
        this.dnid = dnid;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DNIDList dnidList = (DNIDList) o;
        return Objects.equals(id, dnidList.id) &&
                Objects.equals(dnid, dnidList.dnid) &&
                Objects.equals(pluginName, dnidList.pluginName) &&
                Objects.equals(updateTime, dnidList.updateTime) &&
                Objects.equals(updatedBy, dnidList.updatedBy);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, dnid, pluginName, updateTime, updatedBy);
    }
}
