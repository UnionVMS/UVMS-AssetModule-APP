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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.OffsetDateTimeDeserializer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pollbase", indexes = { @Index(columnList = "channel_guid", name = "pollbase_channel_FK_INX10", unique = false),
        @Index(columnList = "mobileterminal_id", name = "pollbase_mobterm_FK_INX10", unique = false),})
@JsonIgnoreProperties(ignoreUnknown = true)
@Inheritance(strategy = InheritanceType.JOINED)
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = MobileTerminalConstants.POLL_FIND_ALL, query = "SELECT p FROM PollBase p"),
        @NamedQuery(name = MobileTerminalConstants.POLL_FIND_BY_ID, query = "SELECT p FROM PollBase p WHERE p.id = :id"),
        @NamedQuery(name = MobileTerminalConstants.POLL_FIND_BY_TYPE, query = "SELECT p FROM PollBase p WHERE p.pollTypeEnum = :pollTypeEnum"),
        @NamedQuery(name = MobileTerminalConstants.POLL_FIND_BY_COMMENT, query = "SELECT p FROM PollBase p WHERE p.comment = :pollComment"),
        @NamedQuery(name = MobileTerminalConstants.POLL_FIND_BY_CREATE_DATE, query = "SELECT p FROM PollBase p WHERE p.updateTime = :pollCreated"),
        @NamedQuery(name = MobileTerminalConstants.POLL_FIND_BY_USER, query = "SELECT p FROM PollBase p WHERE p.creator = :pollUserCreator"),
})
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

    @Column(name = "channel_guid")
    @NotNull
    private UUID channelId;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "updattim")
    private OffsetDateTime updateTime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mobileterminal_id", foreignKey = @ForeignKey(name = "PollBase_MobileTerminal_FK"))
    @NotNull
    private MobileTerminal mobileterminal;

    @Size(max = 60)
    @Column(name = "upuser")
    private String updatedBy;

    @Column(name = "connect_id")
    private String terminalConnect;

    @Column(name = "poll_type")
    @Enumerated(EnumType.STRING)
    private PollTypeEnum pollTypeEnum;

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

    public OffsetDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(OffsetDateTime updateTime) {
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

    public PollTypeEnum getPollTypeEnum() {
        return pollTypeEnum;
    }

    public void setPollTypeEnum(PollTypeEnum pollTypeEnum) {
        this.pollTypeEnum = pollTypeEnum;
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
                Objects.equals(terminalConnect, pollBase.terminalConnect) &&
                Objects.equals(pollTypeEnum, pollBase.pollTypeEnum);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, comment, creator, channelId, updateTime, mobileterminal, updatedBy, terminalConnect, pollTypeEnum);
    }
}
