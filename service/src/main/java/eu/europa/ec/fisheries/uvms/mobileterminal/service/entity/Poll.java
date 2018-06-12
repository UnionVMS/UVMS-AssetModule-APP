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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "poll")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "Poll.findAll", query = "SELECT p FROM Poll p"),
        @NamedQuery(name = "Poll.findByPollId", query = "SELECT p FROM Poll p WHERE p.id = :pollId"),
        @NamedQuery(name = "Poll.findByPollGUID", query = "SELECT p FROM Poll p WHERE p.guid = :guid"),
        @NamedQuery(name = "Poll.findByPollComment", query = "SELECT p FROM Poll p WHERE p.pollBase.comment = :pollComment"),
        @NamedQuery(name = "Poll.findByPollCreated", query = "SELECT p FROM Poll p WHERE p.updateTime = :pollCreated"),
        @NamedQuery(name = "Poll.findByPollUserCreator", query = "SELECT p FROM Poll p WHERE p.pollBase.creator = :pollUserCreator"),
        @NamedQuery(name = "Poll.findByPolltrackId", query = "SELECT p FROM Poll p WHERE p.guid = :polltrackId") })
public class Poll implements Serializable {
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

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updattim")
    private Date updateTime;

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

    @PrePersist
    public void atPrePersist() {
        if(guid == null)
        setGuid(UUID.randomUUID().toString());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PollTypeEnum getPollType() {
        return pollType;
    }

    public void setPollType(PollTypeEnum pollType) {
        this.pollType = pollType;
    }

    public List<PollPayload> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<PollPayload> payloads) {
        this.payloads = payloads;
    }

    public PollBase getPollBase() {
        return pollBase;
    }

    public void setPollBase(PollBase pollBase) {
        this.pollBase = pollBase;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
