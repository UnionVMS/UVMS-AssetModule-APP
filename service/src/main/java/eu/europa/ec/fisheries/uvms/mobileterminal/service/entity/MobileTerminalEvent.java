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
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.OffsetDateTimeDeserializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * The persistent class for the mobileterminalevent database table.
 * 
 */
@Entity
@NamedQuery(name="MobileTerminalEvent.findAll", query="SELECT m FROM MobileTerminalEvent m")
@Audited
@Table(name = "mobileterminalevent", indexes = { @Index(columnList = "mobileterminal_id", name = "mobileterminalevent_mobterm_FK_INX10", unique = false),
												 @Index(columnList = "chan_def", name = "mobileterminalevent_channel_FK_INX10", unique = false),
												 @Index(columnList = "chan_conf", name = "mobileterminalevent_channel_FK_INX20", unique = false),
												 @Index(columnList = "chan_poll", name = "mobileterminalevent_channel_FK_INX30", unique = false),
												 @Index(columnList = "asset_id", name = "mobileterminalevent_asset_FK_INX10", unique = false),})
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class /*, property="id"*/)
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

	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="updattime")
	private OffsetDateTime updatetime;

	@Size(max=60)
	@Column(name="upuser")
	private String updateuser;

	@Size(max=1000)
	@Column(name="attributes")
	private String attributes;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="asset_id")
	@Fetch(FetchMode.SELECT)
	private Asset asset;

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


	@OneToMany(mappedBy="mobileTerminalEvent", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<MobileTerminalAttributes> mobileTerminalAttributes;


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


	public OffsetDateTime getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(OffsetDateTime updatetime) {
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

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset assetId) {
		this.asset = assetId;
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

	public Set<MobileTerminalAttributes> getMobileTerminalAttributes() {
		if(mobileTerminalAttributes == null){
			mobileTerminalAttributes = new HashSet<>();
		}
		return mobileTerminalAttributes;
	}

	public void setMobileTerminalAttributes(Set<MobileTerminalAttributes> mobileTerminalAttributes) {
		this.mobileTerminalAttributes = mobileTerminalAttributes;
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
				Objects.equals(asset, that.asset) &&
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
