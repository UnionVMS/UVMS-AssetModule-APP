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

import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import eu.europa.ec.fisheries.uvms.entity.Asset;
import eu.europa.ec.fisheries.uvms.entity.Note;
import eu.europa.ec.fisheries.uvms.entity.NotesActivityCode;

@Stateless
public class NoteDao {

    @PersistenceContext
    EntityManager em;
    
    public Note findNote(UUID id) {
        return em.find(Note.class, id);
    }
    
    public Note createNote(Note note) {
        em.persist(note);
        return note;
    }

    public Note updateNote(Note note) {
        return em.merge(note);
    }

    public void deleteNote(Note note) {
        em.remove(note);
    }
    
    public List<Note> getNotesByAsset(Asset asset) {
        TypedQuery<Note> query = em.createNamedQuery(Note.NOTE_FIND_BY_ASSET, Note.class);
        query.setParameter("asset", asset);
        return query.getResultList();
    }
    
    public List<NotesActivityCode> getNoteActivityCodes() {
        throw new IllegalStateException("Not implemented yet!");
    }

}
