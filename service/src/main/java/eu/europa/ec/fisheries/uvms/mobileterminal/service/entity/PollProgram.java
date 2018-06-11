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
package eu.europa.ec.fisheries.uvms.mobileterminal.entity2;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;

/**
 * WHERE p.startDate < :currentDate AND p.stopDate > :currentDate AND p.state <>
 * 'ARCHIVED'
 **/
@Table(name = "pollprogram")
@Entity
@DiscriminatorValue("true")
@NamedQueries({
	@NamedQuery(name = "PollProgram.findAll", query = "SELECT p FROM PollProgram p"),
	@NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_BY_ID, query = "SELECT p FROM PollProgram p WHERE p.guid = :guid"),
	@NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_ALIVE, query = "SELECT p FROM PollProgram  p WHERE p.stopDate > :currentDate " +
	"AND p.pollState <> eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum.ARCHIVED"),
    @NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_RUNNING_AND_STARTED,
    query = "SELECT p FROM PollProgram  p WHERE p.startDate < :currentDate AND p.pollState = eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum.STARTED") })
public class PollProgram implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "guid", unique=true)
    private String guid;

    @Column(name = "frequency")
    private Integer frequency;

    @Column(name = "startdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "stopdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopDate;

    @Column(name = "latestruntime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date latestRun;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updattim")
    private Date updateTime;

    @JoinColumn(name = "pollbase_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PollBase pollBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "pollstate")
    private PollStateEnum pollState;

    public PollProgram() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PrePersist
    public void atPrePersist() {
        if(guid == null)
        setGuid(UUID.randomUUID().toString());
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public Date getLatestRun() {
        return latestRun;
    }

    public void setLatestRun(Date latestRun) {
        this.latestRun = latestRun;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public PollStateEnum getPollState() {
        return pollState;
    }

    public void setPollState(PollStateEnum pollState) {
        this.pollState = pollState;
    }

    public PollBase getPollBase() {
        return pollBase;
    }

    public void setPollBase(PollBase pollBase) {
        this.pollBase = pollBase;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
