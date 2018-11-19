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
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * The persistent class for the inmarsatc_oceanregion database table.
 * 
 */
@Entity
@Table(name = "inmarsatc_oceanregion")
@NamedQuery(name="InmarsatCHistoryOceanRegion.findAll", query="SELECT i FROM InmarsatCHistoryOceanRegion i")
public class InmarsatCHistoryOceanRegion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "INMARSAT_UUID")
	@GenericGenerator(name = "INMARSAT_UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(name="code")
	private Integer code;

	@Size(max=200)
	@Column(name="name")
	private String name;

	@Column(name="updattim")
	private OffsetDateTime updatetime;

	@Size(max=60)
	@Column(name="upuser")
	private String updateuser;

	public InmarsatCHistoryOceanRegion() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OffsetDateTime getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(OffsetDateTime updatetime) {
		this.updatetime = updatetime;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InmarsatCHistoryOceanRegion that = (InmarsatCHistoryOceanRegion) o;
		return Objects.equals(id, that.id) &&
				Objects.equals(code, that.code) &&
				Objects.equals(name, that.name) &&
				Objects.equals(updatetime, that.updatetime) &&
				Objects.equals(updateuser, that.updateuser);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, code, name, updatetime, updateuser);
	}
}
