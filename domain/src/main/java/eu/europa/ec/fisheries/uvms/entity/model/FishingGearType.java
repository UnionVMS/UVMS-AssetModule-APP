package eu.europa.ec.fisheries.uvms.entity.model;
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

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Entity
@org.hibernate.annotations.NamedQueries({
    @org.hibernate.annotations.NamedQuery(name= UvmsConstants.FISHING_GEAR_TYPE_FIND_ALL, query="SELECT f FROM FishingGearType f"),
    @org.hibernate.annotations.NamedQuery(name= UvmsConstants.FISHING_GEAR_TYPE_FIND_BY_CODE, query="SELECT f FROM FishingGearType f where f.code = :code")
})
@Table(name = "fishinggeartype")
public class FishingGearType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "fishgtyp_id")
    private Long id;

    @Column(name="fishgtyp_code")
    private Long code;

    @Column(name="fishgtyp_name")
    private String name;

    @Column(name="fishgtyp_desc")
    private String description;

    @Column(name="fishgtyp_updattim")
    private Date updateDateTime;

    @Column(name="fishgtyp_upuser")
    private String updateUser;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fishingGearType")
    private List<FishingGear> fishingGears;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public List<FishingGear> getFishingGears() {
        return fishingGears;
    }

    public void setFishingGears(List<FishingGear> fishingGears) {
        this.fishingGears = fishingGears;
    }
}
