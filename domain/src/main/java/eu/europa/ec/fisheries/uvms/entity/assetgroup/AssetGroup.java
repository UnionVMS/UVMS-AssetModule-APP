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


/**
 * The persistent class for the assetgroup database table.
 * 
 */
@Entity
@Table(name="Assetgroup")
@NamedQueries({
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_FIND_ALL, query="SELECT a FROM AssetGroup a WHERE a.archived = false"),
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_BY_USER, query="SELECT a FROM AssetGroup a WHERE a.archived = false AND a.owner = :owner"),
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_BY_GUID, query="SELECT a FROM AssetGroup a WHERE a.guid = :guid"),
	@NamedQuery(name=UvmsConstants.GROUP_ASSET_BY_GUID_LIST, query="SELECT a FROM AssetGroup a WHERE a.archived = false AND a.guid IN :guidList")
})
public class AssetGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="group_id")
	private Long id;

	@Column(name="group_archived")
	private Boolean archived = false;

	@Column(name="group_dynamic")
	private Boolean dynamic = true;

	@Column(name="group_global")
	private Boolean global = true;

	@Size(max=36)
	@NotNull
	@Column(name="group_guid")
	private String guid;

	@Size(max=80)
	@NotNull
	@Column(name="group_name")
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="group_updattim")
	private Date updateTime;

	@Size(max=60)
	@Column(name="group_upuser")
	private String updatedBy;

	@Size(max=80)
	@NotNull
	@Column(name="group_user_id")
	private String owner;

	@OneToMany(mappedBy="assetgroup", fetch=FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true)
	private List<AssetGroupField> fields;

	public AssetGroup() {
	}

	@PrePersist
	private void prepersist() {
		setGuid(UUID.randomUUID().toString());
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
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

	public String getGuid() {
		return this.guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<AssetGroupField> getFields() {
		if(this.fields == null) {
			this.fields = new ArrayList<>();
		}
		return this.fields;
	}

	public void setFields(List<AssetGroupField> fields) {
		this.fields = fields;
	}
}