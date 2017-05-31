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

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.entity.asset.types.FishingGearMobilityEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the fishinggear database table.
 * 
 */
@Entity
@NamedQueries({
	@NamedQuery(name= UvmsConstants.FISHING_GEAR_FIND_ALL, query="SELECT f FROM FishingGear f"),
	@NamedQuery(name=UvmsConstants.FISHING_GEAR_FIND_BY_ID, query="SELECT f FROM FishingGear f where id = :id"),
	@NamedQuery(name=UvmsConstants.FISHING_GEAR_FIND_BY_EXT_ID, query="SELECT f FROM FishingGear f where externalId = :externalId")
})

public class FishingGear implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name="fishg_id")
	private Long id;

	@Size(min=2, max=3)
	@Column(name="fishg_code")
	private String code;

	@Size(max=200)
	@Column(name="fishg_desc")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fishg_updattim")
	private Date updateTime;

	@Size(max=60)
	@Column(name="fishg_upuser")
	private String updatedBy;

	@Column(name="fishg_fishgm_id")
	private long mobility;

	@JoinColumn(name = "fishg_fishgtyp_id")
	@ManyToOne(cascade = javax.persistence.CascadeType.ALL )
	private FishingGearType fishingGearType;

	@NotNull
	@Column(name="fishg_fishtyp_ext_id")
	private Long externalId;

	public FishingGear() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public FishingGearMobilityEnum getMobility() {
		return FishingGearMobilityEnum.getType(this.mobility);
	}

	public void setMobility(FishingGearMobilityEnum mobility) {
		if(mobility != null) {
			this.mobility = mobility.getId();
		}
	}

	public FishingGearType getFishingGearType() {
		return fishingGearType;
	}

	public void setFishingGearType(FishingGearType fishingGearType) {
		this.fishingGearType = fishingGearType;
	}

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}
}