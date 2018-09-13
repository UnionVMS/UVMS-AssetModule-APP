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
package eu.europa.ec.fisheries.uvms.asset.domain.dao;

import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;

@Stateless
public class ContactInfoDao {
    
    @PersistenceContext
    EntityManager em;
    
    public ContactInfo findContactInfo(UUID id) {
        return em.find(ContactInfo.class, id);
    }

    public ContactInfo createContactInfo(ContactInfo contactInfo) {
        em.persist(contactInfo);
        return contactInfo;
    }

    public ContactInfo updateContactInfo(ContactInfo contactInfo) {
        return em.merge(contactInfo);
    }

    public void deleteContactInfo(ContactInfo contactInfo) {
        em.remove(contactInfo);
    }

    public List<ContactInfo> getContactInfoByAsset(Asset asset) {
        TypedQuery<ContactInfo> query = em.createNamedQuery(ContactInfo.FIND_BY_ASSET, ContactInfo.class);
        query.setParameter("assetId", asset.getId());
        return query.getResultList();
    }
}
