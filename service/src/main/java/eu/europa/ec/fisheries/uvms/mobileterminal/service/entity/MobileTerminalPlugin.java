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


import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The persistent class for the landearthstation database table.
 *
 */
@Entity
@Table(name = "plugin")
@NamedQueries({
	@NamedQuery(name = MobileTerminalConstants.PLUGIN_FIND_ALL, query = "SELECT p FROM MobileTerminalPlugin p WHERE p.pluginInactive = false"),
	@NamedQuery(name = MobileTerminalConstants.PLUGIN_FIND_BY_SERVICE_NAME, query = "SELECT p FROM MobileTerminalPlugin p WHERE p.pluginServiceName = :serviceName")
})
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class MobileTerminalPlugin implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

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
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updattim")
    private Date updateTime;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @OneToMany(mappedBy="plugin", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    private List<MobileTerminal> mobileTerminals;
    
    @OneToMany(mappedBy="plugin", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<MobileTerminalPluginCapability> capabilities;
    
    public MobileTerminalPlugin() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

	public void setPluginInactive(Boolean isInactive) {
		this.pluginInactive = isInactive;
	}

	public List<MobileTerminal> getMobileTerminals() {
		return mobileTerminals;
	}

	public void setMobileTerminals(List<MobileTerminal> mobileTerminals) {
		this.mobileTerminals = mobileTerminals;
	}

	public Set<MobileTerminalPluginCapability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(Set<MobileTerminalPluginCapability> capabilities) {
		this.capabilities = capabilities;
	}
}
