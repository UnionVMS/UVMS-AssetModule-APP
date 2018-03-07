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
import eu.europa.ec.fisheries.uvms.entity.asset.types.NotesSourceEnum;
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
public class Notes implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="notes_id")
	private Long id;

	//@Fetch(FetchMode.JOIN)
	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "notes_asset_id")
	//private AssetEntity asset;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="notes_date")
	private Date date;

	@Column(name="notes_activity")
	private String activity;

	@Column(name="notes_user")
	private String user;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="notes_ready_date")
	private Date readyDate;

	@Column(name="notes_license_holder")
	private String licenseHolder;

	@Column(name="notes_contact")
	private String contact;

	@Column(name="notes_sheet_number")
	private String sheetNumber;

	@Column(name="notes_notes")
	private String notes;

	@Column(name="notes_document")
	private String document;

	@Enumerated(EnumType.STRING)
	@Column(name="notes_source")
	private NotesSourceEnum source;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="notes_updattim")
	private Date updateTime;

	@Size(max=60)
	@Column(name="notes_upuser")
	private String updatedBy;

	public Notes() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	//public AssetEntity getAsset() {
//		return asset;
//	}

	//public void setAsset(AssetEntity asset) {
	//	this.asset = asset;
	//}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getReadyDate() {
		return readyDate;
	}

	public void setReadyDate(Date readyDate) {
		this.readyDate = readyDate;
	}

	public String getLicenseHolder() {
		return licenseHolder;
	}

	public void setLicenseHolder(String licenseHolder) {
		this.licenseHolder = licenseHolder;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getSheetNumber() {
		return sheetNumber;
	}

	public void setSheetNumber(String sheetNumber) {
		this.sheetNumber = sheetNumber;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public NotesSourceEnum getSource() {
		return source;
	}

	public void setSource(NotesSourceEnum source) {
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
}