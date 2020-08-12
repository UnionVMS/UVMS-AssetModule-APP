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
package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.util.JsonBAssetIdOnlySerializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
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
		@Index(columnList = "asset_id", name = "mobileterminal_asset_FK_INX10", unique = false)
		},
		uniqueConstraints = {@UniqueConstraint(name = "mobileterminal_uc_historyid" , columnNames = "historyid"),
				             @UniqueConstraint(name = "mobileterminal_uc_serialnumber" , columnNames = "serial_no")})
@NamedQueries({
	@NamedQuery(name= MobileTerminal.FIND_ALL, query = "SELECT m FROM MobileTerminal m"),
	@NamedQuery(name=MobileTerminal.FIND_BY_ID, query="SELECT m FROM MobileTerminal m WHERE m.id = :id"),
	@NamedQuery(name=MobileTerminal.FIND_BY_SERIAL_NO, query="SELECT m FROM MobileTerminal m WHERE m.serialNo = :serialNo"),
	@NamedQuery(name=MobileTerminal.FIND_BY_ASSET_ID, query="SELECT m FROM MobileTerminal m WHERE m.asset.id = :assetId"),
	@NamedQuery(name=MobileTerminal.FIND_BY_UNASSIGNED, query="SELECT m FROM MobileTerminal m WHERE m.asset IS NULL"),
	@NamedQuery(name=MobileTerminal.FIND_BY_DNID_AND_MEMBER_NR_AND_TYPE,
            query="SELECT DISTINCT m FROM MobileTerminal m LEFT OUTER JOIN Channel c ON m.id = c.mobileTerminal.id " +
                    "WHERE m.archived = false AND c.archived = false AND c.dnid = :dnid AND c.memberNumber = :memberNumber AND m.mobileTerminalType = :mobileTerminalType")
})
public class MobileTerminal implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_ALL = "Mobileterminal.findAll";
	public static final String FIND_BY_ID = "Mobileterminal.findById";
	public static final String FIND_BY_SERIAL_NO = "Mobileterminal.findBySerialNo";
	public static final String FIND_BY_UNASSIGNED = "Mobileterminal.findByUnassigned";
	public static final String FIND_BY_ASSET_ID = "Mobileterminal.findByAssetId";
	public static final String FIND_BY_DNID_AND_MEMBER_NR_AND_TYPE = "Mobileterminal.findByDnidAndMemberNumberAndType";


	@Id
	@GeneratedValue(generator = "MOBILETERMINAL_UUID")
	@GenericGenerator(name = "MOBILETERMINAL_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(name = "historyid")
	private UUID historyId;

	@NotNull
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch=FetchType.EAGER,  cascade=CascadeType.ALL)
	@JoinColumn(name="plugin_id", foreignKey = @ForeignKey(name = "MobileTerminal_Plugin_FK"))
	private MobileTerminalPlugin plugin;
	
	@Column(name="archived")
	private Boolean archived = false;

	@Column(name="active")
	private Boolean active = true;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="source")
	private TerminalSourceEnum source;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(name="type")
	private MobileTerminalTypeEnum mobileTerminalType;

	@Column(name="updatetime")
	private Instant updatetime;

	@Column(name="createtime")
	private Instant createTime;

	@Size(max = 60)
	@Column(name="updateuser")
	private String updateuser;

	@NotNull
	@Size(max = 60)
	@Column(name="serial_no")
	private String serialNo;

	@Size(max = 60)
	@Column(name = "satellite_number")
	private String satelliteNumber;

	@Size(max = 60)
	@Column(name = "antenna")
	private String antenna;

	@Size(max = 60)
	@Column(name = "transceiver_type")
	private String transceiverType;

	@Size(max = 60)
	@Column(name = "software_version")
	private String softwareVersion;

	@OneToMany(mappedBy = "mobileTerminal", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Channel> channels;

	@JsonbTypeSerializer(JsonBAssetIdOnlySerializer.class)
	@JsonbProperty("assetId")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="asset_id", foreignKey = @ForeignKey(name = "MobileTerminal_Asset_FK"))
	private Asset asset;

	@Transient
	private String assetUUID;		//renamed to avoid a conflict in yasson

	@Column(name = "comment")
	private String comment;

	@Column(name = "aor_e")
	private Boolean eastAtlanticOceanRegion = false;

	@Column(name = "aor_w")
	private Boolean westAtlanticOceanRegion = false;

	@Column(name = "por")
	private Boolean pacificOceanRegion = false;

	@Column(name = "ior")
	private Boolean indianOceanRegion = false;

    @Column(name="install_date")
    private Instant installDate;

    @Column(name="uninstall_date")
    private Instant uninstallDate;

	@Column(name="installed_by")
	private String installedBy;

	public MobileTerminal() {
	}

	@PrePersist
	private void atPrePersist() {
		this.historyId = UUID.randomUUID();
		this.createTime = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		this.updatetime = Instant.now();
	}

	@PreUpdate
	private void generateNewHistoryId() {
		this.historyId = UUID.randomUUID();
		this.updatetime = Instant.now();
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		if(active != null){this.active = active;}
	}

	public TerminalSourceEnum getSource() {
		return source;
	}

	public void setSource(TerminalSourceEnum source) {
		this.source = source;
	}

	public MobileTerminalTypeEnum getMobileTerminalType() {
		return mobileTerminalType;
	}

	public void setMobileTerminalType(MobileTerminalTypeEnum mobileTerminalType) {
		this.mobileTerminalType = mobileTerminalType;
	}

	public Instant getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Instant updatetime) {
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

	public Instant getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Instant createTime) {
		this.createTime = createTime;
	}

	public Set<Channel> getChannels() {
		if(channels == null)
			channels = new LinkedHashSet<>();
		return channels;
	}

	public void setChannels(Set<Channel> channels) {
		this.channels = channels;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	@JsonbTransient
	public String getAssetUUID() {
		return assetUUID;
	}

	@JsonbProperty("assetId")
	public void setAssetUUID(String assetUUID) {
		this.assetUUID = assetUUID;
	}

	public String getSatelliteNumber() {
		return satelliteNumber;
	}

	public void setSatelliteNumber(String satelliteNumber) {
		this.satelliteNumber = satelliteNumber;
	}

	public String getAntenna() {
		return antenna;
	}

	public void setAntenna(String antenna) {
		this.antenna = antenna;
	}

	public String getTransceiverType() {
		return transceiverType;
	}

	public void setTransceiverType(String transceiverType) {
		this.transceiverType = transceiverType;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getEastAtlanticOceanRegion() {
		return eastAtlanticOceanRegion;
	}

	public void setEastAtlanticOceanRegion(Boolean eastAtlanticOceanRegion) {
		this.eastAtlanticOceanRegion = eastAtlanticOceanRegion;
	}

	public Boolean getWestAtlanticOceanRegion() {
		return westAtlanticOceanRegion;
	}

	public void setWestAtlanticOceanRegion(Boolean westAtlanticOceanRegion) {
		this.westAtlanticOceanRegion = westAtlanticOceanRegion;
	}

	public Boolean getPacificOceanRegion() {
		return pacificOceanRegion;
	}

	public void setPacificOceanRegion(Boolean pacificOceanRegion) {
		this.pacificOceanRegion = pacificOceanRegion;
	}

	public Boolean getIndianOceanRegion() {
		return indianOceanRegion;
	}

	public void setIndianOceanRegion(Boolean indianOceanRegion) {
		this.indianOceanRegion = indianOceanRegion;
	}

    public Instant getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Instant installDate) {
        this.installDate = installDate;
    }

    public Instant getUninstallDate() {
        return uninstallDate;
    }

    public void setUninstallDate(Instant uninstallDate) {
        this.uninstallDate = uninstallDate;
    }

	public String getInstalledBy() {
		return installedBy;
	}

	public void setInstalledBy(String installedBy) {
		this.installedBy = installedBy;
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
				Objects.equals(active, that.active) &&
				source == that.source &&
				mobileTerminalType == that.mobileTerminalType &&
				Objects.equals(updatetime, that.updatetime) &&
				Objects.equals(createTime, that.createTime) &&
				Objects.equals(updateuser, that.updateuser) &&
				Objects.equals(serialNo, that.serialNo) &&
				Objects.equals(satelliteNumber, that.satelliteNumber) &&
				Objects.equals(antenna, that.antenna) &&
				Objects.equals(transceiverType, that.transceiverType) &&
				Objects.equals(softwareVersion, that.softwareVersion) &&
				Objects.equals(channels, that.channels) &&
				Objects.equals(asset, that.asset) &&
				Objects.equals(assetUUID, that.assetUUID) &&
				Objects.equals(comment, that.comment) &&
				Objects.equals(eastAtlanticOceanRegion, that.eastAtlanticOceanRegion) &&
				Objects.equals(westAtlanticOceanRegion, that.westAtlanticOceanRegion) &&
				Objects.equals(pacificOceanRegion, that.pacificOceanRegion) &&
				Objects.equals(indianOceanRegion, that.indianOceanRegion) &&
				Objects.equals(installDate, that.installDate) &&
				Objects.equals(uninstallDate, that.uninstallDate) &&
				Objects.equals(installedBy, that.installedBy);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
