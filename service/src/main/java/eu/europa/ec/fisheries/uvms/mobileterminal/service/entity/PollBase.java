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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pollbase")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PollBase implements Serializable {

    @Id
    @GeneratedValue(generator = "POLLBASE_UUID")
    @GenericGenerator(name = "POLLBASE_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Size(max = 400)
    @Column(name = "comment")
    private String comment;

    @Size(max = 60)
    @Column(name = "createuser")
    private String creator;

    //@Size(max = 36)
    @Column(name = "channel_guid")
    @NotNull
    private UUID channelId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updattim")
    private Date updateTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mobileterminal_id")
    @NotNull
    private MobileTerminal mobileterminal;

    @Column(name = "upuser")
    private String updatedBy;

    @Column(name = "connect_id")
    private String terminalConnect;

    private static final long serialVersionUID = 1L;

    public PollBase() {
        super();
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public MobileTerminal getMobileterminal() {
        return mobileterminal;
    }

    public void setMobileterminal(MobileTerminal mobileterminal) {
        this.mobileterminal = mobileterminal;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getTerminalConnect() {
        return terminalConnect;
    }

    public void setTerminalConnect(String terminalConnect) {
        this.terminalConnect = terminalConnect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PollBase pollBase = (PollBase) o;
        return Objects.equals(id, pollBase.id) &&
                Objects.equals(comment, pollBase.comment) &&
                Objects.equals(creator, pollBase.creator) &&
                Objects.equals(channelId, pollBase.channelId) &&
                Objects.equals(updateTime, pollBase.updateTime) &&
                Objects.equals(mobileterminal, pollBase.mobileterminal) &&
                Objects.equals(updatedBy, pollBase.updatedBy) &&
                Objects.equals(terminalConnect, pollBase.terminalConnect);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, comment, creator, channelId, updateTime, mobileterminal, updatedBy, terminalConnect);
    }
}
