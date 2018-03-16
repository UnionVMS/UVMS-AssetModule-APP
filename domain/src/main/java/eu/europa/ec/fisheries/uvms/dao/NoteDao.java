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
package eu.europa.ec.fisheries.uvms.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import eu.europa.ec.fisheries.uvms.entity.model.Note;

@Stateless
public class NoteDao {

    @PersistenceContext
    EntityManager em;
    
    public Note createNote(Note note) {
        em.persist(note);
        return note;
    }
    
    public List<Note> createNotes(AssetSE asset) {
        List<Note> notes = new ArrayList<>();
        for (Note note : asset.getNotes()) {
            note.setAsset(asset);
            note.setUpdatedBy(asset.getUpdatedBy());
            note.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
            updateNote(note);
            notes.add(note);
        }
        return notes;
    }

    public Note updateNote(Note note) {
        return em.merge(note);
    }

    public void deleteNote(Note note) {
        em.remove(note);
    }
    
    public List<Note> findNotesByAsset(AssetSE asset) {
        TypedQuery<Note> query = em.createNamedQuery(Note.NOTE_FIND_BY_ASSET, Note.class);
        query.setParameter("asset", asset);
        return query.getResultList();
    }
    
    public void findNotesByAssets(List<AssetSE> assets) {
        for (AssetSE asset : assets) {
            asset.setNotes(findNotesByAsset(asset));
        }
    }
}
