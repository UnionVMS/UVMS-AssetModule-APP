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
package eu.europa.ec.fisheries.uvms.entity.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.uvms.entity.asset.types.CarrierSourceEnum;


/**
 * The persistent class for the carrier database table.
 * 
 */
@Entity
@NamedQuery(name="Carrier.findAll", query="SELECT c FROM Carrier c")
public class Carrier implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="carr_id")
	private Long id;

	@Column(name="carr_active")
	private Boolean active;

	@Enumerated(EnumType.STRING)
	@Column(name="carr_carriersource")
	private CarrierSourceEnum source;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="carr_updattim")
	private Date updateTime;

	@Size(max=60)
	@Column(name="carr_upuser")
	private String updatedBy;

	public Carrier() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public CarrierSourceEnum getSource() {
		return source;
	}

	public void setSource(CarrierSourceEnum source) {
		this.source = source;
	}

	public Date getUpdatetime() {
		return this.updateTime;
	}

	public void setUpdatetime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
}