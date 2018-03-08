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
package eu.europa.ec.fisheries.uvms.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import eu.europa.ec.fisheries.asset.types.Config;
import eu.europa.ec.fisheries.asset.types.ConfigField;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ConfigurationDto;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Stateless
@LocalBean
public class ConfigServiceBean {
	final static Logger LOG = LoggerFactory.getLogger(ConfigServiceBean.class);


	@EJB
	private ParameterService parameterService;

	@EJB
	private ConfigDomainModelBean configDomainModel;

	@PersistenceContext
    private EntityManager entityManager;

	public List<Config> getConfiguration() throws AssetException {
		ConfigurationDto configuration = configDomainModel.getConfiguration(ConfigField.ALL);
		return configuration.getConfigList();
	}

	public Map<String, String> getParameters() throws AssetException {
		try {
			Map<String, String> parameters = new HashMap<>();
			for (SettingType settingType : parameterService.getAllSettings()) {
				parameters.put(settingType.getKey(), settingType.getValue());
			}

			return parameters;
		} catch (Exception e) {
			LOG.error("[ Error when getting asset parameters from local database. ] {}", e);
			throw new AssetException("Couldn't get parameters");
		}
	}



}