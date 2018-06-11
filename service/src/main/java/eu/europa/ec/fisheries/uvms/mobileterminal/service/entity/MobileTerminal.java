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

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.EqualsUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The persistent class for the mobileterminal database table.
 * 
 */
@Audited
@Entity
@NamedQueries({
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_ALL, query = "SELECT m FROM MobileTerminal m"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_GUID, query="SELECT m FROM MobileTerminal m WHERE m.guid = :guid"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_SERIAL_NO, query="SELECT m FROM MobileTerminal m WHERE m.serialNo = :serialNo")
})
public class MobileTerminal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Column(unique = true, name = "historyid")
	private UUID historyId;


	@Size(max=36)
	@NotNull
	@Column(name="guid")
	private String guid;
	
	@NotNull
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="plugin_id")
	private MobileTerminalPlugin plugin;
	
	@Column(name="archived")
	private Boolean archived = false;

	@Column(name="inactivated")
	private Boolean inactivated = false;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="source")
	private MobileTerminalSourceEnum source;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="type")
	private MobileTerminalTypeEnum mobileTerminalType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updatetime")
	private Date updatetime;

	@Size(max = 60)
	@Column(name="updateuser")
	private String updateuser;

	@Column(name="serial_no")
	private String serialNo;

	//bi-directional many-to-one association to Mobileterminalevent
	@OneToMany(mappedBy="mobileterminal", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<MobileTerminalEvent> mobileTerminalEvents;

	@OneToMany(mappedBy = "mobileTerminal", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<Channel> channels;

	public MobileTerminal() {
	}

	@PrePersist
	private void atPrePersist() {
		if(guid == null) {
			setGuid(UUID.randomUUID().toString());
		}
		this.historyId = UUID.randomUUID();
	}

	@PreUpdate
	private void generateNewHistoryId() {
		this.historyId = UUID.randomUUID();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getArchived() {
		return this.archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Boolean getInactivated() {
		return this.inactivated;
	}

	public void setInactivated(Boolean inactivated) {
		this.inactivated = inactivated;
	}

	public MobileTerminalSourceEnum getSource() {
		return this.source;
	}

	public void setSource(MobileTerminalSourceEnum source) {
		this.source = source;
	}

	public MobileTerminalTypeEnum getMobileTerminalType() {
		return this.mobileTerminalType;
	}

	public void setMobileTerminalType(MobileTerminalTypeEnum mobileTerminalType) {
		this.mobileTerminalType = mobileTerminalType;
	}

	public Date getUpdateTime() {
		return this.updatetime;
	}

	public void setUpdateTime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdatedBy() {
		return this.updateuser;
	}

	public void setUpdatedBy(String updateuser) {
		this.updateuser = updateuser;
	}

	public Set<MobileTerminalEvent> getMobileTerminalEvents() {
		if (mobileTerminalEvents == null) {
			mobileTerminalEvents = new HashSet<>();
		}
		return this.mobileTerminalEvents;
	}

	public void setMobileTerminalEvents(Set<MobileTerminalEvent> mobileTerminalEvents) {
		this.mobileTerminalEvents = mobileTerminalEvents;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Set<Channel> getChannels() {
		if (channels == null) {
			channels = new HashSet<>();
		}
		return channels;
	}

	public UUID getHistoryId() {
		return historyId;
	}

	public void setHistoryId(UUID historyId) {
		this.historyId = historyId;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

	public void setChannels(Set<Channel> channels) {
		this.channels = channels;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MobileTerminal) {
			MobileTerminal other = (MobileTerminal)obj;
			return EqualsUtil.compare(guid, other.guid);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return EqualsUtil.getHashCode(guid);
	}

	public MobileTerminalPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(MobileTerminalPlugin plugin) {
		this.plugin = plugin;
	}

	public MobileTerminalEvent getCurrentEvent() {
		for (MobileTerminalEvent event : getMobileTerminalEvents()) {
			if (event.isActive()) {
				return event;
			}
		}
		return null;
	}
}
