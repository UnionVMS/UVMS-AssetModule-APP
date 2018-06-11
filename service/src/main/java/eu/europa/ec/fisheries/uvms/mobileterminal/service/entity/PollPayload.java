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

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Table(name = "pollpayload")
@Entity
public class PollPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "reporting_freq")
    private Integer reportingFrequency;

    @Column(name = "grace_period")
    private Integer gracePeriod;

    @Column(name = "in_port_grace")
    private Integer inPortGrace;

    @Size(max = 60)
    @Column(name = "newdnid")
    private String newDnid;

    @Size(max = 60)
    @Column(name = "newmemberno")
    private String newMemberNumber;

    @Column(name = "startdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "stopdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stopDate;

    @JoinColumn(name = "poll_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Poll poll;

    public PollPayload() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReportingFrequency() {
        return reportingFrequency;
    }

    public void setReportingFrequency(Integer reportingFrequency) {
        this.reportingFrequency = reportingFrequency;
    }

    public Integer getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Integer gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public Integer getInPortGrace() {
        return inPortGrace;
    }

    public void setInPortGrace(Integer inPortGrace) {
        this.inPortGrace = inPortGrace;
    }

    public String getNewDnid() {
        return newDnid;
    }

    public void setNewDnid(String newDnid) {
        this.newDnid = newDnid;
    }

    public String getNewMemberNumber() {
        return newMemberNumber;
    }

    public void setNewMemberNumber(String newMemberNumber) {
        this.newMemberNumber = newMemberNumber;
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

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}
