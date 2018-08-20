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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.OffsetDateTimeDeserializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * WHERE p.startDate < :currentDate AND p.stopDate > :currentDate AND p.state <>
 * 'ARCHIVED'
 **/
@Table(name = "pollprogram")
@Entity
@DiscriminatorValue("true")
@NamedQueries({
	@NamedQuery(name = "PollProgram.findAll", query = "SELECT p FROM PollProgram p"),
	@NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_BY_ID, query = "SELECT p FROM PollProgram p WHERE p.id = :id"),
	@NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_ALIVE, query = "SELECT p FROM PollProgram  p WHERE p.stopDate > :currentDate " +
	"AND p.pollState <> eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum.ARCHIVED"),
    @NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_RUNNING_AND_STARTED,
    query = "SELECT p FROM PollProgram  p WHERE p.startDate < :currentDate AND p.pollState = eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum.STARTED") })
public class PollProgram implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "POLLPROGRAM_UUID")
    @GenericGenerator(name = "POLLPROGRAM_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;


    @Column(name = "frequency")
    private Integer frequency; // this is probably in seconds


    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "startdate")
    private OffsetDateTime startDate;


    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "stopdate")
    private OffsetDateTime stopDate;

    @Column(name = "latestruntime")
    private OffsetDateTime latestRun;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "updattim")
    private OffsetDateTime updateTime;

    @JoinColumn(name = "pollbase_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PollBase pollBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "pollstate")
    private PollStateEnum pollState;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getStopDate() {
        return stopDate;
    }

    public void setStopDate(OffsetDateTime stopDate) {
        this.stopDate = stopDate;
    }

    public OffsetDateTime getLatestRun() {
        return latestRun;
    }

    public void setLatestRun(OffsetDateTime latestRun) {
        this.latestRun = latestRun;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public OffsetDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public PollBase getPollBase() {
        return pollBase;
    }

    public void setPollBase(PollBase pollBase) {
        this.pollBase = pollBase;
    }

    public PollStateEnum getPollState() {
        return pollState;
    }

    public void setPollState(PollStateEnum pollState) {
        this.pollState = pollState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollProgram that = (PollProgram) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(frequency, that.frequency) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(stopDate, that.stopDate) &&
                Objects.equals(latestRun, that.latestRun) &&
                Objects.equals(updatedBy, that.updatedBy) &&
                Objects.equals(updateTime, that.updateTime) &&
                Objects.equals(pollBase, that.pollBase) &&
                pollState == that.pollState;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, frequency, startDate, stopDate, latestRun, updatedBy, updateTime, pollBase, pollState);
    }
}
