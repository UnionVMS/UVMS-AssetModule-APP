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
package eu.europa.ec.fisheries.uvms.asset.client.model.mt;

import javax.json.bind.annotation.JsonbTransient;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * The persistent class for the channel database table.
 * 
 */
public class Channel implements Serializable {

	private UUID id;

	private UUID historyId;

	private Boolean archived = false;

	private Instant updateTime;

	private String updateUser;

	@JsonbTransient
	private MobileTerminal mobileTerminal;

	private String name;

	private boolean active = true;

	private boolean defaultChannel;

	private boolean configChannel;

	private boolean pollChannel;

	private Integer dnid;

	private Duration expectedFrequency;

	private Duration expectedFrequencyInPort;

	private Duration frequencyGracePeriod;

	private String lesDescription;

	private Integer memberNumber;

	private Instant startDate;

	private Instant endDate;

	public Channel(){

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

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Instant updateTime) {
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
