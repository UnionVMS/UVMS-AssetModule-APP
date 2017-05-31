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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;


/**
 * The persistent class for the settings database table.
 * 
 */
@Entity
@Table(name="settings")
@NamedQueries({
	@NamedQuery(name=UvmsConstants.SETTING_LIST, query="SELECT s FROM Setting s"),
	@NamedQuery(name=UvmsConstants.SETTING_BY_FIELD, query="SELECT s FROM Setting s WHERE s.field = :field")
})
public class Setting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="setting_id")
	private Long id;

	@Size(max=80)
	@Column(name="setting_desc")
	private String description;

	@Size(max=25)
	@NotNull
	@Column(name="setting_field")
	private String field;

	@Size(max=25)
	@NotNull
	@Column(name="setting_label")
	private String label;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="setting_updattim")
	private Date updateTime;

	@Size(max=60)
	@Column(name="setting_upuser")
	private String updatedBy;

	public Setting() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getField() {
		return this.field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTIme(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

}