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

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.ConfigModelException;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ConfigurationDto;
import eu.europa.ec.fisheries.uvms.constant.UnitLength;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.dao.FlagStateDao;
import eu.europa.ec.fisheries.uvms.dao.LicenseTypeDao;
import eu.europa.ec.fisheries.uvms.dao.SettingDao;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.model.FlagState;
import eu.europa.ec.fisheries.uvms.entity.model.LicenseType;
import eu.europa.ec.fisheries.uvms.entity.model.Setting;
import eu.europa.ec.fisheries.wsdl.asset.config.Config;
import eu.europa.ec.fisheries.wsdl.asset.config.ConfigField;
import eu.europa.ec.fisheries.wsdl.asset.config.ConfigValue;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@LocalBean
public class ConfigDomainModelBean  {

    @EJB
    private LicenseTypeDao licenseDao;

    @EJB
    private FlagStateDao flagStateDao;

    @EJB
    private SettingDao settingDao;

    public List<String> getLicenseType() throws ConfigModelException {
        try {
            List<String> licenseTypes = new ArrayList<>();
            List<LicenseType> list = licenseDao.getAllLicenseType();
            for (LicenseType type : list) {
                licenseTypes.add(type.getName());
            }
            return licenseTypes;
        } catch (AssetDaoException e) {
            throw new ConfigModelException("Couldn't fetch license types " + e.getMessage());
        }
    }

    public List<String> getFlagState() throws ConfigModelException {
        try {
            List<String> flagStateList = new ArrayList<>();
            List<FlagState> list = flagStateDao.getAllFlagState();
            for (FlagState flagState : list) {
                flagStateList.add(flagState.getCode());
            }
            return flagStateList;
        } catch (AssetDaoException e) {
            throw new ConfigModelException("Couldn't fetch flag states " + e.getMessage());
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

    public ConfigurationDto getConfiguration(ConfigField config) throws ConfigModelException {
        //TODO fix if config != ALL
        ConfigurationDto dto = new ConfigurationDto();
        Map<String, List<String>> settings = getSettings();

        switch (config) {
            case ALL:
            case ASSET_TYPE:
                dto.addConfig(createConfigFromList(ConfigField.ASSET_TYPE, settings.get(ConfigField.ASSET_TYPE.name())));
            case FLAG_STATE:
                dto.addConfig(createConfigFromList(ConfigField.FLAG_STATE, getFlagState()));
            case GEAR_TYPE:
                dto.addConfig(createConfigFromList(ConfigField.GEAR_TYPE, getGearTypes()));
            case LICENSE_TYPE:
                dto.addConfig(createConfigFromList(ConfigField.LICENSE_TYPE, getLicenseType()));
            case SPAN_LENGTH_LOA:
                dto.addConfig(createConfigFromList(ConfigField.SPAN_LENGTH_LOA, settings.get(ConfigField.SPAN_LENGTH_LOA.name())));
            case SPAN_POWER_MAIN:
                dto.addConfig(createConfigFromList(ConfigField.SPAN_POWER_MAIN, settings.get(ConfigField.SPAN_POWER_MAIN.name())));
            case UNIT_LENGTH:
                dto.addConfig(createConfigFromList(ConfigField.UNIT_LENGTH, getLengthUnit()));
            case UNIT_TONNAGE:
                dto.addConfig(createConfigFromList(ConfigField.UNIT_TONNAGE, getTonnageUnit()));
        }
        return dto;
    }

    private static List<String> getGearTypes() {
        List<String> values = new ArrayList<>();
        for (GearFishingTypeEnum gearType : GearFishingTypeEnum.values()) {
            values.add(gearType.name());
        }
        return values;
    }

    private static List<String> getLengthUnit() {
        List<String> values = new ArrayList<>();
        for (UnitLength unit : UnitLength.values()) {
            values.add(unit.name());
        }
        return values;
    }

    private static List<String> getTonnageUnit() {
        List<String> values = new ArrayList<>();
        for (UnitTonnage unit : UnitTonnage.values()) {
            values.add(unit.name());
        }
        return values;
    }

    private Config createConfigFromList(ConfigField field, List<String> values) {
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
