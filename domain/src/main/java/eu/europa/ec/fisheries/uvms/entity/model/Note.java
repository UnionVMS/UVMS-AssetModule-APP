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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.annotations.GenericGenerator;
import eu.europa.ec.fisheries.uvms.entity.asset.types.NotesSourceEnum;

@Entity
@Table(name = "Note")
@NamedQueries({
    @NamedQuery(name = Note.NOTE_FIND_BY_ASSET, query = "SELECT n FROM Note n WHERE n.asset = :asset"),
})
public class Note implements Serializable {

    private static final long serialVersionUID = 6790572532903829338L;

    public static final String NOTE_FIND_BY_ASSET = "Note.findByAsset";
    
    @Id
	@GeneratedValue(generator="UUID")
    @GenericGenerator(
            name="UUID",
            strategy="org.hibernate.id.UUIDGenerator"
    )
	@Column(name="id")
	private UUID id;

    @JsonbTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="asset")
	private AssetSE asset;

	@Column(name="date")
	private LocalDateTime date;

	@Column(name="activity")
	private String activity;

	@Column(name="noteuser")
	private String user;

	@Column(name="readydate")
	private LocalDateTime readyDate;

	@Column(name="licenseholder")
	private String licenseHolder;

	@Column(name="contact")
	private String contact;

	@Column(name="sheetnumber")
	private String sheetNumber;

	@Column(name="notes")
	private String notes;

	@Column(name="document")
	private String document;

	@Enumerated(EnumType.STRING)
	@Column(name="source")
	private NotesSourceEnum source;

	@Column(name="updatetime")
	private LocalDateTime updateTime;

	@Size(max=60)
	@Column(name="updatedby")
	private String updatedBy;

	public Note() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public AssetSE getAsset() {
		return asset;
	}

	public void setAsset(AssetSE asset) {
		this.asset = asset;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
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

	public LocalDateTime getReadyDate() {
		return readyDate;
	}

	public void setReadyDate(LocalDateTime readyDate) {
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

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

    @Override
    public String toString() {
        return "Note [id=" + id + ", activity=" + activity + ", user=" + user + ", notes=" + notes + "]";
    }

}