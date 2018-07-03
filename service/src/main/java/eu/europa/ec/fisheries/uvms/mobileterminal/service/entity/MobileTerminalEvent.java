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
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the mobileterminalevent database table.
 * 
 */
@Entity
@NamedQuery(name="MobileTerminalEvent.findAll", query="SELECT m FROM MobileTerminalEvent m")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class, property="id")
public class MobileTerminalEvent implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "MOBILETERMINALEVENT_UUID")
	@GenericGenerator(name = "MOBILETERMINALEVENT_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;


	@Size(max=400)
	@Column(name="comment")
	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updattime")
	private Date updatetime;

	@Size(max=60)
	@Column(name="upuser")
	private String updateuser;

	@Size(max=1000)
	@Column(name="attributes")
	private String attributes;

	@Size(max=400)
	@Column(name="connect_id")
	private String connectId;

	@Enumerated(EnumType.STRING)
	@Column(name="eventtype")
	private EventCodeEnum eventCodeType;

	@Column(name="active")
	private boolean active;

	//bi-directional many-to-one association to Mobileterminal
	@NotNull
	@ManyToOne
	@JoinColumn(name="mobileterminal_id")
	@Fetch(FetchMode.SELECT)
	private MobileTerminal mobileterminal;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_def")
	private Channel defaultChannel;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_conf")
	private Channel configChannel;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_poll")
	private Channel pollChannel;

	public MobileTerminalEvent() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}

	public EventCodeEnum getEventCodeType() {
		return eventCodeType;
	}

	public void setEventCodeType(EventCodeEnum eventCodeType) {
		this.eventCodeType = eventCodeType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public MobileTerminal getMobileterminal() {
		return mobileterminal;
	}

	public void setMobileterminal(MobileTerminal mobileterminal) {
		this.mobileterminal = mobileterminal;
	}

	public Channel getDefaultChannel() {
		return defaultChannel;
	}

	public void setDefaultChannel(Channel defaultChannel) {
		this.defaultChannel = defaultChannel;
	}

	public Channel getConfigChannel() {
		return configChannel;
	}

	public void setConfigChannel(Channel configChannel) {
		this.configChannel = configChannel;
	}

	public Channel getPollChannel() {
		return pollChannel;
	}

	public void setPollChannel(Channel pollChannel) {
		this.pollChannel = pollChannel;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MobileTerminalEvent that = (MobileTerminalEvent) o;
		return active == that.active &&
				Objects.equals(id, that.id) &&
				Objects.equals(comment, that.comment) &&
				Objects.equals(updatetime, that.updatetime) &&
				Objects.equals(updateuser, that.updateuser) &&
				Objects.equals(attributes, that.attributes) &&
				Objects.equals(connectId, that.connectId) &&
				eventCodeType == that.eventCodeType &&
				Objects.equals(mobileterminal, that.mobileterminal) &&
				Objects.equals(defaultChannel, that.defaultChannel) &&
				Objects.equals(configChannel, that.configChannel) &&
				Objects.equals(pollChannel, that.pollChannel);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}
}
