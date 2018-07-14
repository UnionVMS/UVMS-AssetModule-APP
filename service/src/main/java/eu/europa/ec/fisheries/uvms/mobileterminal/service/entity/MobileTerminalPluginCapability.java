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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the landearthstation database table.
 *
 */
@Entity
@Table(name = "plugin_capability")
@NamedQueries({
	@NamedQuery(name = "PluginCapability.findAll", query = "SELECT p FROM MobileTerminalPluginCapability p"),
})
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class, property="id")
public class MobileTerminalPluginCapability implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "MOBILETERMINALPLUGINCAPABILITY_UUID")
    @GenericGenerator(name = "MOBILETERMINALPLUGINCAPABILITY_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Size(max = 25)
    @Column(name = "value")
    private String value;

    @Size(max = 25)
    @Column(name = "capability")
    private String name;

    @Column(name = "updattim")
    private LocalDateTime updateTime;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name="plugin_id")
    private MobileTerminalPlugin plugin;
    
    public MobileTerminalPluginCapability() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public MobileTerminalPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(MobileTerminalPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobileTerminalPluginCapability that = (MobileTerminalPluginCapability) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(value, that.value) &&
                Objects.equals(name, that.name) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(updatedBy, that.updatedBy) &&
                Objects.equals(plugin, that.plugin);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
