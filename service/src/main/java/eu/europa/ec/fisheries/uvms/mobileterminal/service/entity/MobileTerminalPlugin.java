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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.util.OffsetDateTimeDeserializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * The persistent class for the landearthstation database table.
 */
@Entity
@Table(name = "plugin", indexes = {@Index(columnList = "service_name", name = "plugin_INX10", unique = false),},
        uniqueConstraints = @UniqueConstraint(name = "plugin_uc_service_name", columnNames = "service_name"))
@NamedQueries({
        @NamedQuery(name = MobileTerminalConstants.PLUGIN_FIND_ALL, query = "SELECT p FROM MobileTerminalPlugin p WHERE p.pluginInactive = false"),
        @NamedQuery(name = MobileTerminalConstants.PLUGIN_FIND_BY_SERVICE_NAME, query = "SELECT p FROM MobileTerminalPlugin p WHERE p.pluginServiceName = :serviceName")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class/*, property="id"*/)
public class MobileTerminalPlugin implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "MOBILETERMINALPLUGIN_UUID")
    @GenericGenerator(name = "MOBILETERMINALPLUGIN_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Size(max = 80)
    @Column(name = "description")
    private String description;

    @Size(max = 40)
    @Column(name = "name")
    private String name;

    @Size(max = 500)
    @Column(name = "service_name")
    private String pluginServiceName;

    @Size(max = 50)
    @Column(name = "satellite_type")
    private String pluginSatelliteType;

    @Column(name = "inactive")
    private Boolean pluginInactive;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "updattim")
    private OffsetDateTime updateTime;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @OneToMany(mappedBy = "plugin", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MobileTerminalPluginCapability> capabilities;

    public MobileTerminalPlugin() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluginServiceName() {
        return pluginServiceName;
    }

    public void setPluginServiceName(String pluginServiceName) {
        this.pluginServiceName = pluginServiceName;
    }

    public String getPluginSatelliteType() {
        return pluginSatelliteType;
    }

    public void setPluginSatelliteType(String pluginSatelliteType) {
        this.pluginSatelliteType = pluginSatelliteType;
    }

    public Boolean getPluginInactive() {
        return pluginInactive;
    }

    public void setPluginInactive(Boolean pluginInactive) {
        this.pluginInactive = pluginInactive;
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

    public Set<MobileTerminalPluginCapability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<MobileTerminalPluginCapability> capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileTerminalPlugin that = (MobileTerminalPlugin) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(name, that.name) &&
                Objects.equals(pluginServiceName, that.pluginServiceName) &&
                Objects.equals(pluginSatelliteType, that.pluginSatelliteType) &&
                Objects.equals(pluginInactive, that.pluginInactive) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updatedBy, that.updatedBy) &&
                Objects.equals(capabilities, that.capabilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
