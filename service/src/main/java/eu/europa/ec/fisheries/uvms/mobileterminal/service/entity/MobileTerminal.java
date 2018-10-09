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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.OffsetDateTimeDeserializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * The persistent class for the mobileterminal database table.
 *
 */
@Audited
@Entity
@Table(name = "mobileterminal", indexes = { @Index(columnList = "plugin_id", name = "mobileterminal_plugin_FK_INX01", unique = false),
		@Index(columnList = "serial_no", name = "mobileterminal_INX01", unique = false),},
		uniqueConstraints = @UniqueConstraint(name = "mobileterminal_uc_historyid" , columnNames = "historyid"))
@NamedQueries({
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_ALL, query = "SELECT m FROM MobileTerminal m"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_ID, query="SELECT m FROM MobileTerminal m WHERE m.id = :id"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_SERIAL_NO, query="SELECT m FROM MobileTerminal m WHERE m.serialNo = :serialNo")
})
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class/*, property="id"*/)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileTerminal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "MOBILETERMINAL_UUID")
	@GenericGenerator(name = "MOBILETERMINAL_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(name = "historyid")
	private UUID historyId;

	@NotNull
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="plugin_id", foreignKey = @ForeignKey(name = "MobileTerminal_Plugin_FK"))
	private MobileTerminalPlugin plugin;
	
	@Column(name="archived")
	private Boolean archived = false;

	@Column(name="inactivated")
	private Boolean inactivated = false;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="source")
	private MobileTerminalSource source;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="type")
	private MobileTerminalTypeEnum mobileTerminalType;


	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="updatetime")
	private OffsetDateTime updatetime;

	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	@JsonDeserialize(using = OffsetDateTimeDeserializer.class)
	@Column(name="createtime")
	private OffsetDateTime createTime;

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
		this.historyId = UUID.randomUUID();
		this.createTime = OffsetDateTime.now(ZoneOffset.UTC);
	}

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

	public MobileTerminalPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(MobileTerminalPlugin plugin) {
		this.plugin = plugin;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Boolean getInactivated() {
		return inactivated;
	}

	public void setInactivated(Boolean inactivated) {
		this.inactivated = inactivated;
	}

	public MobileTerminalSource getSource() {
		return source;
	}

	public void setSource(MobileTerminalSource source) {
		this.source = source;
	}

	public MobileTerminalTypeEnum getMobileTerminalType() {
		return mobileTerminalType;
	}

	public void setMobileTerminalType(MobileTerminalTypeEnum mobileTerminalType) {
		this.mobileTerminalType = mobileTerminalType;
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

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public Set<MobileTerminalEvent> getMobileTerminalEvents() {
		if(mobileTerminalEvents == null)
			mobileTerminalEvents = new HashSet<>();
		return mobileTerminalEvents;
	}

	public void setMobileTerminalEvents(Set<MobileTerminalEvent> mobileTerminalEvents) {
		this.mobileTerminalEvents = mobileTerminalEvents;
	}

	public Set<Channel> getChannels() {
		if(channels == null)
			channels = new HashSet<>();
		return channels;
	}

	public void setChannels(Set<Channel> channels) {
		this.channels = channels;
	}

	public OffsetDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(OffsetDateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MobileTerminal that = (MobileTerminal) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(historyId, that.historyId) &&
				Objects.equals(plugin, that.plugin) &&
				Objects.equals(archived, that.archived) &&
				Objects.equals(inactivated, that.inactivated) &&
				source == that.source &&
				mobileTerminalType == that.mobileTerminalType &&
				Objects.equals(updatetime, that.updatetime) &&
				Objects.equals(updateuser, that.updateuser) &&
				Objects.equals(serialNo, that.serialNo) &&
				Objects.equals(mobileTerminalEvents, that.mobileTerminalEvents) &&
				Objects.equals(channels, that.channels);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}

	@JsonIgnore
	public MobileTerminalEvent getCurrentEvent() {
		for (MobileTerminalEvent event : getMobileTerminalEvents()) {
			if (event.isActive()) {
				return event;
			}
		}
		return null;
	}


}
