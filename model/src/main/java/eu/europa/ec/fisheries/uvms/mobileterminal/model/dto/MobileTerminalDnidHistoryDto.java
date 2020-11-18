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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.dto;

import java.time.Instant;
import java.util.UUID;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;

public class MobileTerminalDnidHistoryDto {

    private UUID id;
    private UUID historyId;
    private MobileTerminalTypeEnum mobileTerminalType;
    private String serialNo;
    private String satelliteNumber;
    private String assetId;
    private Long nationalId;
    private Instant installDate;
    private Instant uninstallDate;
    private String installedBy;
    private Instant updateTime;
    private String channelName;
    private boolean defaultChannel;
    private boolean configChannel;
    private boolean pollChannel;
    private Integer dnid;
    private Integer memberNumber;
    private Instant startDate;
    private Instant endDate;

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
    public MobileTerminalTypeEnum getMobileTerminalType() {
        return mobileTerminalType;
    }
    public void setMobileTerminalType(MobileTerminalTypeEnum mobileTerminalType) {
        this.mobileTerminalType = mobileTerminalType;
    }
    public String getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    public String getSatelliteNumber() {
        return satelliteNumber;
    }
    public void setSatelliteNumber(String satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }
    public String getAssetId() {
        return assetId;
    }
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    public Long getNationalId() {
        return nationalId;
    }
    public void setNationalId(Long nationalId) {
        this.nationalId = nationalId;
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
    public Instant getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }
    public String getChannelName() {
        return channelName;
    }
    public void setChannelName(String channelName) {
        this.channelName = channelName;
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
    public Integer getDnid() {
        return dnid;
    }
    public void setDnid(Integer dnid) {
        this.dnid = dnid;
    }
    public Integer getMemberNumber() {
        return memberNumber;
    }
    public void setMemberNumber(Integer memberNumber) {
        this.memberNumber = memberNumber;
    }
    public Instant getStartDate() {
        return startDate;
    }
    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }
    public Instant getEndDate() {
        return endDate;
    }
    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }
}
