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
package eu.europa.ec.fisheries.uvms.entity.assetgroup;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import org.hibernate.annotations.GenericGenerator;


/**
 * The persistent class for the assetgroup database table.
 * 
 */
@Entity
@Table(name="Assetgroup")
@NamedQueries({
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_FIND_ALL, query="SELECT a FROM AssetGroupEntity a WHERE a.archived = false"),
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_BY_USER, query="SELECT a FROM AssetGroupEntity a WHERE a.archived = false AND a.owner = :owner"),
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_BY_GUID, query="SELECT a FROM AssetGroupEntity a WHERE a.id = :guid"),
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_BY_GUID_LIST, query="SELECT a FROM AssetGroupEntity a WHERE a.archived = false AND a.id IN :guidList")
})
public class AssetGroupEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(
			name="UUID",
			strategy="org.hibernate.id.UUIDGenerator"
	)
	@Column(name="id")
	private UUID id;

	@Column(name="archived")
	private Boolean archived = false;

	@Column(name="dynamic")
	private Boolean dynamic = true;

	@Column(name="global")
	private Boolean global = true;

	@Size(max=80)
	@NotNull
	@Column(name="name")
	private String name;

	@Column(name="updattim")
	private LocalDateTime updateTime;

	@Size(max=60)
	@Column(name="upuser")
	private String updatedBy;

	@Size(max=80)
	@NotNull
	@Column(name="user_id")
	private String owner;

	public AssetGroupEntity() {
	}

	public UUID getId() {
		return this.id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Boolean getArchived() {
		return this.archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Boolean getDynamic() {
		return this.dynamic;
	}

	public void setDynamic(Boolean dynamic) {
		this.dynamic = dynamic;
	}

	public Boolean getGlobal() {
		return this.global;
	}

	public void setGlobal(Boolean global) {
		this.global = global;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}