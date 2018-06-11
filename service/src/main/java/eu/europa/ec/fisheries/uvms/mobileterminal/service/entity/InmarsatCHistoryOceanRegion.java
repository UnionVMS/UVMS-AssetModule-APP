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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.uvms.mobileterminal.constant.EqualsUtil;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalEvent;

/**
 * The persistent class for the inmarsatc_oceanregion database table.
 * 
 */
@Entity
@Table(name="inmarsatc_oceanregion")
@NamedQuery(name="InmarsatCHistoryOceanRegion.findAll", query="SELECT i FROM InmarsatCHistoryOceanRegion i")
public class InmarsatCHistoryOceanRegion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Column(name="code")
	private Integer code;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="inmarsatc_history_id")
	private MobileTerminalEvent event;

	@Size(max=200)
	@Column(name="name")
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updattim")
	private Date updatetime;

	@Size(max=60)
	@Column(name="upuser")
	private String updateuser;

	public InmarsatCHistoryOceanRegion() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long oceanregId) {
		this.id = oceanregId;
	}

	public Integer getCode() {
		return this.code;
	}

	public void setCode(Integer oceanregCode) {
		this.code = oceanregCode;
	}

	public MobileTerminalEvent getHistory() {
		return this.event;
	}

	public void setHistory(MobileTerminalEvent history) {
		this.event = history;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getUpdateTime() {
		return this.updatetime;
	}

	public void setUpdateTime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdatedBy() {
		return this.updateuser;
	}

	public void setUpdatedBy(String updateuser) {
		this.updateuser = updateuser;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof InmarsatCHistoryOceanRegion) {
			InmarsatCHistoryOceanRegion other = (InmarsatCHistoryOceanRegion)obj;
			if(!EqualsUtil.compare(code, other.code)) return false;
			return EqualsUtil.compare(name, other.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return EqualsUtil.getHashCode(code) + EqualsUtil.getHashCode(name);
	}
}
