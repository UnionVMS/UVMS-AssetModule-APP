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
package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the channel database table.
 * 
 */
@Entity
@Table(name = "channel",
		indexes = {
			@Index(columnList = "mobterm_id", name = "channel_mobterm_FK_INX01", unique = false),
			@Index(columnList = "dnid", name = "channel_INX01", unique = false)
		},
		uniqueConstraints = {
			@UniqueConstraint(name = "channel_uc_historyid" , columnNames = "historyid"),
			@UniqueConstraint(name = "channel_uc_dnid_member_number" , columnNames = {"dnid", "member_number"})
		})
@Audited
@NamedNativeQueries({
		@NamedNativeQuery(name=Channel.LOWEST_UNUSED_MEMBER_NUMBER_FOR_DNID_NATIV_SQL, query = "SELECT MIN(a.member_number) + 1 AS firstFree \n" +
				"FROM (SELECT member_number FROM asset.channel where dnid = :dnid UNION SELECT 0) a\n" +
				"LEFT JOIN asset.channel b ON b.dnid = :dnid AND b.member_number = a.member_number + 1\n" +
				"WHERE b.member_number IS NULL AND a.member_number < 255"),
})
public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String LOWEST_UNUSED_MEMBER_NUMBER_FOR_DNID_NATIV_SQL = "Channel.LowestUnusedMemberNumberForDnidNativeSql";

	@Id
	@GeneratedValue(generator = "CHANNEL_UUID")
	@GenericGenerator(name = "CHANNEL_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(name = "historyid")
	private UUID historyId;

	@Column(name="archived")
	private Boolean archived = false;

	@Column(name="updattime")
	private Instant updateTime;

	@Column(name="updateuser")
	private String updateUser;

	@JsonbTransient
	@ManyToOne
	@JoinColumn(name="mobterm_id", foreignKey = @ForeignKey(name = "Channel_MobileTerminal_FK"))
	private MobileTerminal mobileTerminal;

	@NotNull
	@Column(name="com_channel_name")
	private String name;

	@Column(name="active")
	private boolean active = true;

	@Column(name="chan_def")
	private boolean defaultChannel;

	@Column(name="chan_conf")
	private boolean configChannel;

	@Column(name="chan_poll")
	private boolean pollChannel;

	@NotNull
	@Column(name="dnid")
	private Integer dnid;

	@NotNull
	@Column(name="expected_frequency")
	private Duration expectedFrequency;

	@NotNull
	@Column(name="expected_frequency_in_port")
	private Duration expectedFrequencyInPort;

	@NotNull
	@Column(name="frequency_grace_period")
	private Duration frequencyGracePeriod;

	@Column(name="les_description")
	private String lesDescription;

	@NotNull
	@Column(name="member_number")
	private Integer memberNumber;

	@Audited(withModifiedFlag=true)
	@Column(name="start_date")
	private Instant startDate;

	@Audited(withModifiedFlag=true)
	@Column(name="end_date")
	private Instant endDate;

	public Channel(){

	}

	@PrePersist
	@PreUpdate
	private void generateNewHistoryId() {
		this.historyId = UUID.randomUUID();
		this.updateTime = Instant.now();
		if(this.startDate == null){
            this.startDate = Instant.now();
        }
		if(this.mobileTerminal != null){
			this.updateUser = mobileTerminal.getUpdateuser();
		}
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Channel channel = (Channel) o;
		return active == channel.active &&
				defaultChannel == channel.defaultChannel &&
				configChannel == channel.configChannel &&
				pollChannel == channel.pollChannel &&
				Objects.equals(id, channel.id) &&
				Objects.equals(historyId, channel.historyId) &&
				Objects.equals(archived, channel.archived) &&
				Objects.equals(updateTime, channel.updateTime) &&
				Objects.equals(updateUser, channel.updateUser) &&
				Objects.equals(name, channel.name) &&
				Objects.equals(dnid, channel.dnid) &&
				Objects.equals(expectedFrequency, channel.expectedFrequency) &&
				Objects.equals(expectedFrequencyInPort, channel.expectedFrequencyInPort) &&
				Objects.equals(frequencyGracePeriod, channel.frequencyGracePeriod) &&
				Objects.equals(lesDescription, channel.lesDescription) &&
				Objects.equals(memberNumber, channel.memberNumber) &&
				Objects.equals(startDate, channel.startDate) &&
				Objects.equals(endDate, channel.endDate);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id, historyId, archived, updateTime, updateUser, mobileTerminal, name, active, defaultChannel, configChannel, pollChannel, dnid, expectedFrequency, expectedFrequencyInPort, frequencyGracePeriod, lesDescription, memberNumber, startDate, endDate);
	}
}
