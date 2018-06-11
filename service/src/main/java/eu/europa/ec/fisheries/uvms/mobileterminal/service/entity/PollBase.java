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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "pollbase")
public class PollBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Size(max = 400)
    @Column(name = "comment")
    private String comment;

    @Size(max = 60)
    @Column(name = "createuser")
    private String creator;

    @Size(max = 36)
    @Column(name = "channel_guid")
    @NotNull
    private String channelGuid;
    
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser() {
        return creator;
    }

    public void setUser(String user) {
        this.creator = user;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public MobileTerminal getMobileTerminal() {
        return mobileterminal;
    }

    public void setMobileTerminal(MobileTerminal mobileterminal) {
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

	public String getChannelGuid() {
		return channelGuid;
	}

	public void setChannelGuid(String channelGuid) {
		this.channelGuid = channelGuid;
	}
}
