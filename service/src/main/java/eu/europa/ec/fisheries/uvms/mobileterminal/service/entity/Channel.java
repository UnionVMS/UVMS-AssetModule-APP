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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.OffsetDateTimeDeserializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the channel database table.
 * 
 */
@Entity
@Table(name = "channel", indexes = {@Index(columnList = "mobterm_id", name = "channel_mobterm_FK_INX01", unique = false),
		@Index(columnList = "mobterm_event_id", name = "channel_mobterm_event_FK_INX02", unique = false),
		@Index(columnList = "dnid", name = "channel_INX01", unique = false),},
		uniqueConstraints = @UniqueConstraint(name = "channel_uc_historyid" , columnNames = "historyid"))
@Audited
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class)
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "CHANNEL_UUID")
	@GenericGenerator(name = "CHANNEL_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(name = "historyid")
	private UUID historyId;

	@Column(name="archived")
	private Boolean archived;

	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="updattime")
	private OffsetDateTime updateTime;

	@Column(name="updateuser")
	private String updateUser;

	@ManyToOne
	@JoinColumn(name="mobterm_id")
	private MobileTerminal mobileTerminal;

	@Column(name="com_channel_name")
	private String name;

	@Column(name="active")
	private boolean active;


	@Enumerated(EnumType.STRING)
	@Column(name="eventtype")
	private EventCodeEnum eventCodeType;

	// ???????? kanske
	@Fetch(FetchMode.JOIN)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "mobterm_event_id")
	private MobileTerminalEvent mobileTerminalEvent;

	@Column(name="chan_def")
	private boolean defaultChannel;

	@Column(name="chan_conf")
	private boolean configChannel;

	@Column(name="chan_poll")
	private boolean pollChannel;

	@NotNull
	@Column(name="dnid")
	private String DNID;

	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	@NotNull
	@Column(name="expected_frequency")
	private Duration expectedFrequency;

	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	@NotNull
	@Column(name="expected_frequency_in_port")
	private Duration expectedFrequencyInPort;

	@JsonSerialize(using = DurationSerializer.class)
	@JsonDeserialize(using = DurationDeserializer.class)
	@NotNull
	@Column(name="frequency_grace_period")
	private Duration frequencyGracePeriod;

	@NotNull
	@Column(name="les_description")
	private String lesDescription;

	@NotNull
	@Column(name="member_number")
	private String memberNumber;

	@NotNull
	@Column(name="installed_by")
	private String installedBy;

	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="install_date")
	private OffsetDateTime installDate;

	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="uninstall_date")
	private OffsetDateTime uninstallDate;

	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="start_date")
	private OffsetDateTime startDate;

	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="end_date")
	private OffsetDateTime endDate;

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

	public OffsetDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(OffsetDateTime updateTime) {
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

	public String getDNID() {
		return DNID;
	}

	public void setDNID(String DNID) {
		this.DNID = DNID;
	}

	public Duration getExpectedFrequency() {
		return expectedFrequency;
	}

	public void setExpectedFrequency(Duration expectedFrequency) {
		this.expectedFrequency = expectedFrequency;
	}

	public Duration getExpectedFrequencyInPort() {
		return expectedFrequencyInPort;
	}

	public void setExpectedFrequencyInPort(Duration expectedFrequencyInPort) {
		this.expectedFrequencyInPort = expectedFrequencyInPort;
	}

	public Duration getFrequencyGracePeriod() {
		return frequencyGracePeriod;
	}

	public void setFrequencyGracePeriod(Duration frequencyGracePeriod) {
		this.frequencyGracePeriod = frequencyGracePeriod;
	}

	public String getLesDescription() {
		return lesDescription;
	}

	public void setLesDescription(String lesDescription) {
		this.lesDescription = lesDescription;
	}

	public String getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getInstalledBy() {
		return installedBy;
	}

	public void setInstalledBy(String installedBy) {
		this.installedBy = installedBy;
	}

	public OffsetDateTime getInstallDate() {
		return installDate;
	}

	public void setInstallDate(OffsetDateTime installDate) {
		this.installDate = installDate;
	}

	public OffsetDateTime getUninstallDate() {
		return uninstallDate;
	}

	public void setUninstallDate(OffsetDateTime uninstallDate) {
		this.uninstallDate = uninstallDate;
	}

	public OffsetDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(OffsetDateTime startDate) {
		this.startDate = startDate;
	}

	public OffsetDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(OffsetDateTime endDate) {
		this.endDate = endDate;
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
				eventCodeType == channel.eventCodeType &&
				Objects.equals(mobileTerminalEvent, channel.mobileTerminalEvent) &&
				Objects.equals(DNID, channel.DNID) &&
				Objects.equals(expectedFrequency, channel.expectedFrequency) &&
				Objects.equals(expectedFrequencyInPort, channel.expectedFrequencyInPort) &&
				Objects.equals(frequencyGracePeriod, channel.frequencyGracePeriod) &&
				Objects.equals(lesDescription, channel.lesDescription) &&
				Objects.equals(memberNumber, channel.memberNumber) &&
				Objects.equals(installedBy, channel.installedBy) &&
				Objects.equals(installDate, channel.installDate) &&
				Objects.equals(uninstallDate, channel.uninstallDate) &&
				Objects.equals(startDate, channel.startDate) &&
				Objects.equals(endDate, channel.endDate);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, historyId, archived, updateTime, updateUser, mobileTerminal, name, active, eventCodeType, mobileTerminalEvent, defaultChannel, configChannel, pollChannel, DNID, expectedFrequency, expectedFrequencyInPort, frequencyGracePeriod, lesDescription, memberNumber, installedBy, installDate, uninstallDate, startDate, endDate);
	}
}
