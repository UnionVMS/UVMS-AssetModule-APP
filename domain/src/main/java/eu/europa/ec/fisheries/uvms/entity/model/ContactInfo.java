/*
 Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

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

import eu.europa.ec.fisheries.uvms.entity.asset.types.ContactInfoSourceEnum;
import eu.europa.ec.fisheries.wsdl.asset.types.ContactType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the carrier database table.
 * 
 */
@Entity
public class ContactInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="contactinfo_id")
	private Long id;

	@Fetch(FetchMode.JOIN)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contactinfo_assethist_id")
	private AssetHistory assetHistory;

	@Column(name="contactinfo_name")
	private String name;

	@Column(name="contactinfo_email")
	private String email;

	@Column(name="contactinfo_phone")
	private String phoneNumber;

	@Column(name="contactinfo_owner")
	private Boolean owner;

	@Enumerated(EnumType.STRING)
	@Column(name="contactinfo_source")
	private ContactInfoSourceEnum source;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="contactinfo_updattim")
	private Date updateTime;

	@Size(max=60)
	@Column(name="contactinfo_upuser")
	private String updatedBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "contactinfo_type")
	private ContactType type;

    @Size(max = 100)
    @Column(name = "contactinfo_nationality")
    private String nationality;

	@Size(max = 100)
	@Column(name = "contactinfo_city_name")
	private String cityName;

	@Size(max = 3)
	@Column(name = "contactinfo_country")
	private String countryCode;

	@Size(max = 100)
	@Column(name = "contactinfo_post_office_box")
	private String postOfficeBox;

	@Size(max = 100)
	@Column(name = "contactinfo_postal_area")
	private String postalCode;

	@Size(max = 100)
	@Column(name = "contactinfo_street_name")
	private String streetName;

	@Size(max = 100)
	@Column(name = "contactinfo_fax_number")
	private String faxNumber;


	public ContactInfo() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AssetHistory getAsset() {
		return assetHistory;
	}

	public void setAsset(AssetHistory assetHistory) {
		this.assetHistory = assetHistory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Boolean getOwner() {
		return owner;
	}

	public void setOwner(Boolean owner) {
		this.owner = owner;
	}

	public ContactInfoSourceEnum getSource() {
		return source;
	}

	public void setSource(ContactInfoSourceEnum source) {
		this.source = source;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType contactType) {
        this.type = contactType;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPostOfficeBox() {
		return postOfficeBox;
	}

	public void setPostOfficeBox(String postOfficeBox) {
		this.postOfficeBox = postOfficeBox;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
}