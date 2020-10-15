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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto;

import java.util.List;

public class MTMobileTerminalDeviceConfig {

	private String terminalSystemType;
	private List<String> terminalFieldList;
	private List<String> channelFieldList;
	private List<MTCapabilityDto> capabilityList;
	
	public MTMobileTerminalDeviceConfig() {
	}

	public String getTerminalSystemType() {
		return terminalSystemType;
	}

	public void setTerminalSystemType(String terminalSystemType) {
		this.terminalSystemType = terminalSystemType;
	}

	public List<String> getTerminalFieldList() {
		return terminalFieldList;
	}

	public void setTerminalFieldList(List<String> terminalFieldList) {
		this.terminalFieldList = terminalFieldList;
	}

	public List<String> getChannelFieldList() {
		return channelFieldList;
	}

	public void setChannelFieldList(List<String> channelFieldList) {
		this.channelFieldList = channelFieldList;
	}

	public List<MTCapabilityDto> getCapabilityList() {
		return capabilityList;
	}
	
	public void setCapabilityList(List<MTCapabilityDto> capabilityList) {
		this.capabilityList = capabilityList;
	}
}
