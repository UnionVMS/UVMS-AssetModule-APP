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
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class MobileTerminalPluginDaoBean  {

	@PersistenceContext
	private EntityManager em;

	private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalPluginDaoBean.class);

	public List<MobileTerminalPlugin> getPluginList()  {
            TypedQuery<MobileTerminalPlugin> query = em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_ALL, MobileTerminalPlugin.class);
            return query.getResultList();
	}

	public MobileTerminalPlugin createMobileTerminalPlugin(MobileTerminalPlugin plugin)  {
			em.persist(plugin);
			return plugin;
	}

	public MobileTerminalPlugin getPluginByServiceName(String serviceName) throws NoEntityFoundException {
		try {
            TypedQuery<MobileTerminalPlugin> query = em.createNamedQuery(MobileTerminalConstants.PLUGIN_FIND_BY_SERVICE_NAME, MobileTerminalPlugin.class);
            query.setParameter("serviceName", serviceName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting plugin by service name. ] {}", e.getMessage());
            throw new NoEntityFoundException("No entities found when retrieving mobile terminal plugin by service name");
        }
	}

	public MobileTerminalPlugin updateMobileTerminalPlugin(MobileTerminalPlugin entity)  {
			entity = em.merge(entity);
			em.flush();
			return entity;
	}
}
