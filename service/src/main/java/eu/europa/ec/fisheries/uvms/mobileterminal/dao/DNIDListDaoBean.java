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

import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.DNIDList;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class DNIDListDaoBean {

	@PersistenceContext
	private EntityManager em;


	public List<DNIDList> getAllDNIDList()  {
            TypedQuery<DNIDList> query = em.createNamedQuery(MobileTerminalConstants.DNID_LIST, DNIDList.class);
            return query.getResultList();
	}

	public List<DNIDList> getDNIDList(String pluginName)  {
            TypedQuery<DNIDList> query = em.createNamedQuery(MobileTerminalConstants.DNID_LIST_BY_PLUGIN, DNIDList.class);
            query.setParameter("pluginName", pluginName);
			return query.getResultList();
	}

	public void removeByPluginName(String pluginName)  {
		List<DNIDList> dnidList = getDNIDList(pluginName);
		for(DNIDList entity : dnidList) {
			em.remove(entity);
		}
	}

	public DNIDList create(DNIDList entity)  {
            em.persist(entity);
            return entity;
	}
}
