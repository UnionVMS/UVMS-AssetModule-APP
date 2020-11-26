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
package eu.europa.ec.fisheries.uvms.asset.client.model.mt;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class MobileTerminal {

	private UUID id;

	private UUID historyId;

	private Boolean archived = false;

	private Boolean active = true;

	private String source;

	private String mobileTerminalType;

	private Instant updatetime;

	private Instant createTime;

	private String updateuser;

	private String serialNo;

	private String satelliteNumber;

	private String antenna;

	private String transceiverType;

	private String softwareVersion;

	private Set<Channel> channels;

	private String assetId;

	private String comment;

	private Boolean eastAtlanticOceanRegion = false;

	private Boolean westAtlanticOceanRegion = false;

	private Boolean pacificOceanRegion = false;

	private Boolean indianOceanRegion = false;

    private Instant installDate;

    private Instant uninstallDate;

	private String installedBy;

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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		if(active != null){this.active = active;}
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

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMobileTerminalType() {
		return mobileTerminalType;
	}

	public void setMobileTerminalType(String mobileTerminalType) {
		this.mobileTerminalType = mobileTerminalType;
	}
}
