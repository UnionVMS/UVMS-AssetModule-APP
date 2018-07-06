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

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Table(name = "pollpayload")
@Entity
public class PollPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "POLLPAYLOAD_UUID")
    @GenericGenerator(name = "POLLPAYLOAD_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

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
    private LocalDateTime startDate;

    @Column(name = "stopdate")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime stopDate;

    @JoinColumn(name = "poll_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Poll poll;

    public PollPayload() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getStopDate() {
        return stopDate;
    }

    public void setStopDate(LocalDateTime stopDate) {
        this.stopDate = stopDate;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollPayload that = (PollPayload) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(reportingFrequency, that.reportingFrequency) &&
                Objects.equals(gracePeriod, that.gracePeriod) &&
                Objects.equals(inPortGrace, that.inPortGrace) &&
                Objects.equals(newDnid, that.newDnid) &&
                Objects.equals(newMemberNumber, that.newMemberNumber) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(stopDate, that.stopDate) &&
                Objects.equals(poll, that.poll);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, reportingFrequency, gracePeriod, inPortGrace, newDnid, newMemberNumber, startDate, stopDate, poll);
    }
}
