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

import eu.europa.ec.fisheries.uvms.asset.domain.entity.ContactInfo;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.*;

@Stateless
public class ContactInfoDao {
    
    @PersistenceContext
    private EntityManager em;
    
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

    public List<ContactInfo> getContactInfoByAssetId(UUID assetId) {
        TypedQuery<ContactInfo> query = em.createNamedQuery(ContactInfo.FIND_BY_ASSET, ContactInfo.class);
        query.setParameter("assetId", assetId);
        return query.getResultList();
    }

    public List<ContactInfo> getContactInfoRevisionForAssetHistory(List<ContactInfo> contactInfoList, Instant updateDate) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<ContactInfo> resultList = new ArrayList<>();
        for(ContactInfo contactInfo : contactInfoList) { // An Asset can have multiple ContactInfo and each ContactInfo can have multiple History records.
            List<ContactInfo> revisionList = getSortedContactInfoRevisions(auditReader, contactInfo);
            filterOlderRevisionsByAssetUpdatetime(resultList, updateDate, revisionList);
        }
        return resultList;
    }

    private List<ContactInfo> getSortedContactInfoRevisions(AuditReader auditReader, ContactInfo contactInfo) {
        List<ContactInfo> revisionList = new ArrayList<>();
        List<Number> revisionNumbers = auditReader.getRevisions(ContactInfo.class, contactInfo.getId());
        for (Number rev : revisionNumbers) {
            ContactInfo audited = auditReader.find(ContactInfo.class, contactInfo.getId(), rev);
            revisionList.add(audited);
        }
        revisionList.sort(Comparator.comparing(ContactInfo::getCreateTime));
        Collections.reverse(revisionList);
        return revisionList;
    }

    private void filterOlderRevisionsByAssetUpdatetime(List<ContactInfo> resultList, Instant updateDate, List<ContactInfo> revisionList) {
        for(ContactInfo ci : revisionList) {
            if(ci.getAssetUpdateTime().equals(updateDate)) {
                resultList.add(ci);
            } else if(ci.getAssetUpdateTime().isBefore(updateDate)) {
                if(isUnique(ci.getId(), resultList)) {
                    resultList.add(ci);
                }
            }
        }
    }

    private boolean isUnique(final UUID id, final List<ContactInfo> resultList) {
        Optional<String> first = resultList
                .stream()
                .map(contactInfo -> contactInfo.getId().toString())
                .filter(idValue -> idValue.equals(id.toString()))
                .findFirst();
        return !first.isPresent();
    }
}
