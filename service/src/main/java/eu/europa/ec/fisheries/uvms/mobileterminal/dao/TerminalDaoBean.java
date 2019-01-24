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
package eu.europa.ec.fisheries.uvms.mobileterminal.dao;

import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
public class TerminalDaoBean {

    @PersistenceContext
    private EntityManager em;

    public MobileTerminal getMobileTerminalById(UUID id) {
        try {
            TypedQuery<MobileTerminal> query = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_ID, MobileTerminal.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public MobileTerminal getMobileTerminalBySerialNo(String serialNo) {
        try {
            TypedQuery<MobileTerminal> query = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_SERIAL_NO, MobileTerminal.class);
            query.setParameter("serialNo", serialNo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void removeMobileTerminalAfterTests(String guid) {
        MobileTerminal mobileTerminal = getMobileTerminalById(UUID.fromString(guid));
        em.remove(em.contains(mobileTerminal) ? mobileTerminal : em.merge(mobileTerminal));
    }

    public MobileTerminal createMobileTerminal(MobileTerminal terminal) {
        em.persist(terminal);
        return terminal;
    }

    public MobileTerminal updateMobileTerminal(MobileTerminal terminal) {
        if (terminal == null || terminal.getId() == null)
            throw new IllegalArgumentException();
        return em.merge(terminal);
    }

    @SuppressWarnings("unchecked")
    public List<MobileTerminal> getMobileTerminalsByQuery(String sql) {
            Query query = em.createQuery(sql, MobileTerminal.class);
            return query.getResultList();
    }

    public MobileTerminal findMobileTerminalByAsset(UUID assetId) {
        List<MobileTerminal> ret = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_ASSET_ID, MobileTerminal.class)
                .setParameter("assetId", assetId).getResultList();
        if (ret.size() > 0) return ret.get(0);
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<MobileTerminal> getMobileTerminalRevisionForHistoryId(UUID historyId) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(MobileTerminal.class, true, true);
        return query.add(AuditEntity.property("historyId").eq(historyId)).getResultList();
    }

    public List<MobileTerminal> getMobileTerminalHistoryById(UUID id) {
        AuditReader auditReader = AuditReaderFactory.get(em);
        List<MobileTerminal> resultList = new ArrayList<>();

        List<Number> revisionNumbers = auditReader.getRevisions(MobileTerminal.class, id);
        for (Number rev : revisionNumbers) {
            MobileTerminal audited = auditReader.find(MobileTerminal.class, id, rev);
            resultList.add(audited);
        }
        return resultList;
    }

    public MobileTerminal getMobileTerminalByRequest(AssetMTEnrichmentRequest request) {
        MobileTerminal singleResult = null;
        try {
            singleResult = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_DNID_AND_MEMBER_NR_AND_TYPE, MobileTerminal.class)
                    .setParameter("dnid", request.getDnidValue())
                    .setParameter("memberNumber", request.getMemberNumberValue())
                    .setParameter("mobileTerminalType", MobileTerminalTypeEnum.valueOf(request.getTranspondertypeValue()))
                    .getSingleResult();
        } catch (NoResultException nre) {
            // We want to return null if there is no result instead of throwing exception.
        }
        return singleResult;
    }
}
