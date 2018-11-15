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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * The persistent class for the mobileterminal database table.
 *
 */
@Audited
@Entity
@Table(name = "mobileterminal", indexes = {
		@Index(columnList = "plugin_id", name = "mobileterminal_plugin_FK_INX01", unique = false),
		@Index(columnList = "serial_no", name = "mobileterminal_INX01", unique = false),
		@Index(columnList = "chan_def", name = "mobileterminal_channel_FK_INX10", unique = false),
		@Index(columnList = "chan_conf", name = "mobileterminal_channel_FK_INX20", unique = false),
		@Index(columnList = "chan_poll", name = "mobileterminal_channel_FK_INX30", unique = false),
		@Index(columnList = "asset_id", name = "mobileterminal_asset_FK_INX10", unique = false)
		},
		uniqueConstraints = {@UniqueConstraint(name = "mobileterminal_uc_historyid" , columnNames = "historyid"),
				             @UniqueConstraint(name = "mobileterminal_uc_serialnumber" , columnNames = "serial_no")})
@NamedQueries({
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_ALL, query = "SELECT m FROM MobileTerminal m"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_ID, query="SELECT m FROM MobileTerminal m WHERE m.id = :id"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_SERIAL_NO, query="SELECT m FROM MobileTerminal m WHERE m.serialNo = :serialNo"),
	@NamedQuery(name=MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_ASSET_ID, query="SELECT m FROM MobileTerminal m WHERE m.asset.id = :assetId")
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
	@ManyToOne(fetch=FetchType.EAGER,  cascade=CascadeType.ALL)
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

	@OneToMany(mappedBy = "mobileTerminal", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<Channel> channels;

	@OneToMany(mappedBy="mobileTerminal", cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	private Set<MobileTerminalAttributes> mobileTerminalAttributes;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_def", foreignKey = @ForeignKey(name = "MobileTerminal_Channel_FK10"))
	private Channel defaultChannel;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_conf", foreignKey = @ForeignKey(name = "MobileTerminal_Channel_FK20"))
	private Channel configChannel;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="chan_poll", foreignKey = @ForeignKey(name = "MobileTerminal_Channel_FK30"))
	private Channel pollChannel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="asset_id", foreignKey = @ForeignKey(name = "MobileTerminal_Asset_FK"))
	@Fetch(FetchMode.SELECT)
	private Asset asset;

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

	public Set<MobileTerminalAttributes> getMobileTerminalAttributes() {
		if(mobileTerminalAttributes == null){
			mobileTerminalAttributes = new HashSet<>();
		}
		return mobileTerminalAttributes;
	}

	public void setMobileTerminalAttributes(Set<MobileTerminalAttributes> mobileTerminalAttributes) {
		this.mobileTerminalAttributes = mobileTerminalAttributes;
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

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
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
				Objects.equals(channels, that.channels);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
