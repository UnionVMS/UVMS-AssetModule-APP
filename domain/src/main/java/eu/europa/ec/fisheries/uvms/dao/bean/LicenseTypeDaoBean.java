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
package eu.europa.ec.fisheries.uvms.dao.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.LicenseTypeDao;
import eu.europa.ec.fisheries.uvms.entity.model.LicenseType;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;

@Stateless
public class LicenseTypeDaoBean extends Dao implements LicenseTypeDao {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseTypeDaoBean.class);

	@Override
	public List<LicenseType> getAllLicenseType() throws AssetDaoException {
		try {
			TypedQuery<LicenseType> query = em.createNamedQuery(UvmsConstants.LICENSE_TYPE_LIST, LicenseType.class);
			return query.getResultList();
		} catch (Exception e) {
			LOG.error("[ get license types ]", e.getMessage());
			throw new AssetDaoException("[ get license types ] " + e.getMessage());
		}
	}

}