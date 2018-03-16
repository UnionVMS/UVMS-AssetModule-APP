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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import eu.europa.ec.fisheries.uvms.dao.SettingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.ConfigModelException;
import eu.europa.ec.fisheries.uvms.asset.types.Config;
import eu.europa.ec.fisheries.uvms.asset.types.ConfigFieldEnum;
import eu.europa.ec.fisheries.uvms.asset.types.ConfigValue;
import eu.europa.ec.fisheries.uvms.asset.types.ConfigurationDto;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.fisheries.uvms.constant.UnitLength;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.model.Setting;


@Stateless
public class ConfigServiceBean {
	private static final Logger LOG = LoggerFactory.getLogger(ConfigServiceBean.class);

	@EJB
	private ParameterService parameterService;


	@EJB
	SettingDao settingDao;

	public List<Config> getConfiguration() throws AssetException {
		ConfigurationDto configuration = getConfiguration(ConfigFieldEnum.ALL);
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



	public Map<String, List<String>> getSettings() throws ConfigModelException {
		try {
			Map<String, List<String>> settings = new HashMap<>();
			List<Setting> list = settingDao.getAllSettings();
			for (Setting setting : list) {
				List<String> fieldSettings = settings.get(setting.getField());
				if (fieldSettings == null) {
					fieldSettings = new ArrayList<>();
				}
				fieldSettings.add(setting.getLabel());
				settings.put(setting.getField(), fieldSettings);
			}
			return settings;
		} catch (AssetDaoException e) {
			throw new ConfigModelException("Couldn't fetch settings " + e.getMessage());
		}
	}

	public ConfigurationDto getConfiguration(ConfigFieldEnum config) throws ConfigModelException {
		//TODO fix if config != ALL
		ConfigurationDto dto = new ConfigurationDto();
		Map<String, List<String>> settings = getSettings();


		switch (config) {
			case ALL:
			case ASSET_TYPE:
				dto.addConfig(createConfigFromList(ConfigFieldEnum.ASSET_TYPE, settings.get(ConfigFieldEnum.ASSET_TYPE.name())));
			case GEAR_TYPE:
				dto.addConfig(createConfigFromList(ConfigFieldEnum.GEAR_TYPE, getGearTypes()));
			case SPAN_LENGTH_LOA:
				dto.addConfig(createConfigFromList(ConfigFieldEnum.SPAN_LENGTH_LOA, settings.get(ConfigFieldEnum.SPAN_LENGTH_LOA.name())));
			case SPAN_POWER_MAIN:
				dto.addConfig(createConfigFromList(ConfigFieldEnum.SPAN_POWER_MAIN, settings.get(ConfigFieldEnum.SPAN_POWER_MAIN.name())));
			case UNIT_LENGTH:
				dto.addConfig(createConfigFromList(ConfigFieldEnum.UNIT_LENGTH, getLengthUnit()));
			case UNIT_TONNAGE:
				dto.addConfig(createConfigFromList(ConfigFieldEnum.UNIT_TONNAGE, getTonnageUnit()));
		}

		return dto;
	}

	private  List<String> getGearTypes() {
		List<String> values = new ArrayList<>();
		return values;
	}

	private  List<String> getLengthUnit() {
		List<String> values = new ArrayList<>();
		for (UnitLength unit : UnitLength.values()) {
			values.add(unit.name());
		}
		return values;
	}

	private  List<String> getTonnageUnit() {
		List<String> values = new ArrayList<>();
		for (UnitTonnage unit : UnitTonnage.values()) {
			values.add(unit.name());
		}
		return values;
	}

	private Config createConfigFromList(ConfigFieldEnum field, List<String> values) {
		Config config = new Config();
		config.setField(field);
		List<ConfigValue> configValues = new ArrayList<>();
		for (String keyValue : values) {
			ConfigValue value = new ConfigValue();
			value.setKeyValue(keyValue);
			configValues.add(value);
		}
		config.getValues().addAll(configValues);
		return config;
	}



}