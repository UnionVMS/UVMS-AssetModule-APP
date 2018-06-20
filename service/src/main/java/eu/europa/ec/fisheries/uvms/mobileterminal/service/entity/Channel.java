/*
 Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

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

import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the channel database table.
 * 
 */
@Entity
@Table(name="channel")
@Audited
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "CHANNEL_UUID")
	@GenericGenerator(name = "CHANNEL_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(unique = true, name = "historyid")
	private UUID historyId;

	@Column(name="archived")
	private Boolean archived;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updattime")
	private Date updateTime;

	@Column(name="updateuser")
	private String updateUser;

	@ManyToOne
	@JoinColumn(name="mobterm_id")
	private MobileTerminal mobileTerminal;

	@Column(name="comchanname")
	private String name;

	@Column(name="active")
	private boolean active;

	@Column(name="attributes")
	private String attributes;

	@Enumerated(EnumType.STRING)
	@Column(name="eventtype")
	private EventCodeEnum eventCodeType;

	// ???????? kanske
	@Fetch(FetchMode.JOIN)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mobterm_event_id")
	private MobileTerminalEvent mobileTerminalEvent;

	@Column(name="chan_def")
	private boolean defaultChannel;

	@Column(name="chan_conf")
	private boolean configChannel;

	@Column(name="chan_poll")
	private boolean pollChannel;

	public Channel(){

	}

	@PrePersist
	@PreUpdate
	private void generateNewHistoryId() {
		this.historyId = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getHistoryId() {
		return historyId;
	}

	public void setHistoryId(UUID historyId) {
		this.historyId = historyId;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public MobileTerminal getMobileTerminal() {
		return mobileTerminal;
	}

	public void setMobileTerminal(MobileTerminal mobileTerminal) {
		this.mobileTerminal = mobileTerminal;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public EventCodeEnum getEventCodeType() {
		return eventCodeType;
	}

	public void setEventCodeType(EventCodeEnum eventCodeType) {
		this.eventCodeType = eventCodeType;
	}

	public MobileTerminalEvent getMobileTerminalEvent() {
		return mobileTerminalEvent;
	}

	public void setMobileTerminalEvent(MobileTerminalEvent mobileTerminalEvent) {
		this.mobileTerminalEvent = mobileTerminalEvent;
	}

	public boolean isDefaultChannel() {
		return defaultChannel;
	}

	public void setDefaultChannel(boolean defaultChannel) {
		this.defaultChannel = defaultChannel;
	}

	public boolean isConfigChannel() {
		return configChannel;
	}

	public void setConfigChannel(boolean configChannel) {
		this.configChannel = configChannel;
	}

	public boolean isPollChannel() {
		return pollChannel;
	}

	public void setPollChannel(boolean pollChannel) {
		this.pollChannel = pollChannel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Channel channel = (Channel) o;
		return active == channel.active &&
				defaultChannel == channel.defaultChannel &&
				configChannel == channel.configChannel &&
				pollChannel == channel.pollChannel &&
				Objects.equals(id, channel.id) &&
				Objects.equals(historyId, channel.historyId) &&
				Objects.equals(archived, channel.archived) &&
				Objects.equals(updateTime, channel.updateTime) &&
				Objects.equals(updateUser, channel.updateUser) &&
				Objects.equals(mobileTerminal, channel.mobileTerminal) &&
				Objects.equals(name, channel.name) &&
				Objects.equals(attributes, channel.attributes) &&
				eventCodeType == channel.eventCodeType &&
				Objects.equals(mobileTerminalEvent, channel.mobileTerminalEvent);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, historyId, archived, updateTime, updateUser, mobileTerminal, name, active, attributes, eventCodeType, mobileTerminalEvent, defaultChannel, configChannel, pollChannel);
	}
}
