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
package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.MobileTerminalDnidHistoryDto;

public class MobileTerminalDnidHistoryMapper {

    private MobileTerminalDnidHistoryMapper() {}
    
    public static MobileTerminalDnidHistoryDto mapToMobileTerminalDnidHistory(MobileTerminal mobileTerminal, Channel channel) {
        MobileTerminalDnidHistoryDto dnidHistoryDto = new MobileTerminalDnidHistoryDto();
        dnidHistoryDto.setId(mobileTerminal.getId());
        dnidHistoryDto.setHistoryId(mobileTerminal.getHistoryId());
        dnidHistoryDto.setMobileTerminalType(mobileTerminal.getMobileTerminalType());
        dnidHistoryDto.setSerialNo(mobileTerminal.getSerialNo());
        dnidHistoryDto.setSatelliteNumber(mobileTerminal.getSatelliteNumber());
        dnidHistoryDto.setAssetId(mobileTerminal.getAsset().getId());
        dnidHistoryDto.setNationalId(mobileTerminal.getAsset().getNationalId());
        dnidHistoryDto.setInstallDate(mobileTerminal.getInstallDate());
        dnidHistoryDto.setUninstallDate(mobileTerminal.getUninstallDate());
        dnidHistoryDto.setInstalledBy(mobileTerminal.getInstalledBy());
        dnidHistoryDto.setUpdateTime(mobileTerminal.getUpdatetime());
        dnidHistoryDto.setChannelName(channel.getName());
        dnidHistoryDto.setDefaultChannel(channel.isDefaultChannel());
        dnidHistoryDto.setConfigChannel(channel.isConfigChannel());
        dnidHistoryDto.setPollChannel(channel.isPollChannel());
        dnidHistoryDto.setDnid(channel.getDnid());
        dnidHistoryDto.setMemberNumber(channel.getMemberNumber());
        dnidHistoryDto.setStartDate(channel.getStartDate());
        dnidHistoryDto.setEndDate(channel.getEndDate());
        return dnidHistoryDto;
    }
}
