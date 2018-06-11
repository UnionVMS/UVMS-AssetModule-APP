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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.dao;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class TerminalDaoBean  {

    @PersistenceContext
    private EntityManager em;

    private final static Logger LOG = LoggerFactory.getLogger(TerminalDaoBean.class);

	public MobileTerminal getMobileTerminalByGuid(String guid)  {
		try {
            TypedQuery<MobileTerminal> query = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_GUID, MobileTerminal.class);
            query.setParameter("guid", guid);
            return query.getSingleResult();
        } catch (NoResultException e) {
		    return null;
        }
	}

    public MobileTerminal getMobileTerminalBySerialNo(String serialNo) throws NoEntityFoundException {
        try {
            TypedQuery<MobileTerminal> query = em.createNamedQuery(MobileTerminalConstants.MOBILE_TERMINAL_FIND_BY_SERIAL_NO, MobileTerminal.class);
            query.setParameter("serialNo", serialNo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting terminal by GUID. ] {}", e.getMessage());
            throw new NoEntityFoundException("No entity found with serial no " + serialNo);
        }
    }

    public MobileTerminal createMobileTerminal(MobileTerminal terminal) throws TerminalDaoException {
        try {
            em.persist(terminal);
            return terminal;
        } catch (Exception e) {
            LOG.error("[ Error when creating. ] {}", e.getMessage());
            throw new TerminalDaoException("[ Error when creating. ]" + e.getMessage());
        }
    }

    public void updateMobileTerminal(MobileTerminal terminal) throws TerminalDaoException {
        if(terminal == null || terminal.getId() == null) {
            // It's a defensive decision to prevent clients from using merge excessively instead of persist.
            throw new TerminalDaoException(" [ There is no such persisted entity to update ] ");
        }
        try {
            em.merge(terminal);
            em.flush();
        } catch (Exception e) {
            LOG.error("[ Error when updating. ] {}", e.getMessage());
            throw new TerminalDaoException("[ Error when updating. ]");
        }
    }

    public List<MobileTerminal> getMobileTerminalsByQuery(String sql) {
        Session session = em.unwrap(Session.class);
        Query query = session.createQuery(sql);
        return query.list();
    }
}
