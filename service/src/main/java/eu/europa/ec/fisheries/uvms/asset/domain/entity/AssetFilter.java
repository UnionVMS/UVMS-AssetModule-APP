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
package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_FIND_ALL;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_BY_USER;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_BY_GUID;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter.ASSETFILTER_GUID_LIST;

@Entity
@Table(name = "assetfilter")
@NamedQueries({
	@NamedQuery(name=ASSETFILTER_FIND_ALL, query="SELECT a FROM AssetFilter a"),
	@NamedQuery(name=ASSETFILTER_BY_USER, query="SELECT a FROM AssetFilter a WHERE a.owner = :owner"),
	@NamedQuery(name=ASSETFILTER_BY_GUID, query="SELECT a FROM AssetFilter a WHERE a.id = :guid"),
	@NamedQuery(name=ASSETFILTER_GUID_LIST, query="SELECT a FROM AssetFilter a WHERE a.id IN :guidList")
})
public class AssetFilter implements Serializable {

	private static final long serialVersionUID = -1218306334950687248L;
    
    public static final String ASSETFILTER_FIND_ALL = "AssetFilter.findAll";
    public static final String ASSETFILTER_BY_USER = "AssetFilter.findByUser";
    public static final String ASSETFILTER_BY_GUID = "AssetFilter.findByGuid";
    public static final String ASSETFILTER_GUID_LIST = "AssetFilter.findByGuidList";
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "updatetime")
    private Instant updateTime;

    @Size(max = 255)
    @Column(name = "updatedby")
    private String updatedBy;
    
    @Size(max = 255)
    @Column(name = "owner")
    private String owner;
    
    @OneToMany(mappedBy="assetFilter", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    @Column(name="queries")
    private Set<AssetFilterQuery> queries;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Instant getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Instant updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Set<AssetFilterQuery> getQueries() {
        return queries;
    }

    public void setQueries(Set<AssetFilterQuery> queries) {
        this.queries = queries;
    }

}
