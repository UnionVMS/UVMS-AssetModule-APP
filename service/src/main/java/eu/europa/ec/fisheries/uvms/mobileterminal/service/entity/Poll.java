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

import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollTypeEnum;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "poll", indexes = { @Index(columnList = "id", name = "poll00", unique = true),
        @Index(columnList = "pollbase_id", name = "poll10", unique = false),})
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Poll.findAll", query = "SELECT p FROM Poll p"),
        @NamedQuery(name = "Poll.findById", query = "SELECT p FROM Poll p WHERE p.id = :id"),
        @NamedQuery(name = "Poll.findByPollId", query = "SELECT p FROM Poll p WHERE p.id = :pollId"),
        @NamedQuery(name = "Poll.findByPollComment", query = "SELECT p FROM Poll p WHERE p.pollBase.comment = :pollComment"),
        @NamedQuery(name = "Poll.findByPollCreated", query = "SELECT p FROM Poll p WHERE p.updateTime = :pollCreated"),
        @NamedQuery(name = "Poll.findByPollUserCreator", query = "SELECT p FROM Poll p WHERE p.pollBase.creator = :pollUserCreator"),
        })
public class Poll implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "POLL_UUID")
    @GenericGenerator(name = "POLL_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;


    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @Column(name = "updattim")
    private OffsetDateTime updateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "polltype")
    private PollTypeEnum pollType;

    @JoinColumn(name = "pollbase_id")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PollBase pollBase;

    @OneToMany(mappedBy = "poll", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PollPayload> payloads;

    public Poll() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public PollTypeEnum getPollType() {
        return pollType;
    }

    public void setPollType(PollTypeEnum pollType) {
        this.pollType = pollType;
    }

    public PollBase getPollBase() {
        return pollBase;
    }

    public void setPollBase(PollBase pollBase) {
        this.pollBase = pollBase;
    }

    public List<PollPayload> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<PollPayload> payloads) {
        this.payloads = payloads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return Objects.equals(id, poll.id) &&
                Objects.equals(updatedBy, poll.updatedBy) &&
                Objects.equals(updateTime, poll.updateTime) &&
                pollType == poll.pollType &&
                Objects.equals(pollBase, poll.pollBase) &&
                Objects.equals(payloads, poll.payloads);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, updatedBy, updateTime, pollType, pollBase, payloads);
    }
}
