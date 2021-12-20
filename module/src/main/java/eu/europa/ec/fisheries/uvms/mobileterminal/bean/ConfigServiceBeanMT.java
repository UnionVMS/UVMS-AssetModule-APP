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
package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.CapabilityConfiguration;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemConfiguration;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.client.ExchangeRestClient;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConfigType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPluginCapability;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PluginMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.ServiceToPluginMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;

@Stateless
public class ConfigServiceBeanMT {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigServiceBeanMT.class);

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Inject
    private ExchangeRestClient exchangeClient;

    public List<TerminalSystemType> getTerminalSystems() {
        return getAllTerminalSystems();
    }

    public List<TerminalSystemType> getAllTerminalSystems() {
        Map<MobileTerminalTypeEnum, List<MobileTerminalPlugin>> pluginsByType = getPlugins();
        List<TerminalSystemType> terminalSystemList = new ArrayList<>();

        for (MobileTerminalTypeEnum type : pluginsByType.keySet()) {
            TerminalSystemConfiguration terminalFieldConfiguration = PluginMapper.mapTerminalFieldConfiguration(type);
            TerminalSystemConfiguration comchannelFieldConfiguration = PluginMapper.mapComchannelFieldConfiguration(type);
            CapabilityConfiguration capabilityConfiguration = PluginMapper.mapCapabilityConfiguration(type, pluginsByType.get(type));

            TerminalSystemType systemType = new TerminalSystemType();
            systemType.setType(type.name());
            systemType.setTerminalConfiguration(terminalFieldConfiguration);
            systemType.setComchannelConfiguration(comchannelFieldConfiguration);
            systemType.setCapabilityConfiguration(capabilityConfiguration);

            terminalSystemList.add(systemType);
        }
        return terminalSystemList;
    }

    private Map<MobileTerminalTypeEnum, List<MobileTerminalPlugin>> getPlugins() {
        Map<MobileTerminalTypeEnum, List<MobileTerminalPlugin>> plugins = new HashMap<>();
        for (MobileTerminalPlugin plugin : mobileTerminalPluginDao.getPluginList()) {
            MobileTerminalTypeEnum mobileTerminalType = MobileTerminalTypeEnum.getType(plugin.getPluginSatelliteType());
            if (mobileTerminalType == null) {
                continue;
            }
            List<MobileTerminalPlugin> typePlugins = plugins.get(mobileTerminalType);
            if (typePlugins == null) {
                typePlugins = new ArrayList<>();
                plugins.put(mobileTerminalType, typePlugins);
            }
            typePlugins.add(plugin);
        }
        return plugins;
    }

    public List<ConfigList> getConfigValues() {
        List<ConfigList> configValues = new ArrayList<>();
        for (MobileTerminalConfigType config : MobileTerminalConfigType.values()) {
            ConfigList list = new ConfigList();
            list.setName(config.name());
            switch (config) {
                case POLL_TYPE:
                    list.getValue().addAll(getPollTypes());
                    break;
                case TRANSPONDERS:
                    list.getValue().addAll(getTransponders());
                    break;
                case POLL_TIME_SPAN:
                    list.getValue().addAll(getPollTimeSpan());
                    break;
            }
            configValues.add(list);
        }
        return configValues;
    }

    private List<String> getPollTimeSpan() {
        List<String> list = new ArrayList<>();
        list.add("Today");
        return list;
    }

    private List<String> getTransponders() {
        List<String> list = new ArrayList<>();
        for (MobileTerminalTypeEnum transponder : MobileTerminalTypeEnum.values()) {
            list.add(transponder.name());
        }
        return list;
    }

    private List<String> getPollTypes() {
        List<String> list = new ArrayList<>();
        for (PollTypeEnum type : PollTypeEnum.values()) {
            list.add(type.name());
        }
        return list;
    }

    public void syncInitialPlugins() {
        List<PluginType> pluginTypes = new ArrayList<>();
        pluginTypes.add(PluginType.SATELLITE_RECEIVER);
        GetServiceListRequest request = new GetServiceListRequest();
        request.getType().addAll(pluginTypes);

        GetServiceListResponse response = exchangeClient.getServiceList(request);

        if(response == null){
            throw new NullPointerException("No response from exchange");
        }

        response.getService().stream()
            .map(ServiceToPluginMapper::mapToPlugin)
            .forEach(this::upsertPlugin);
    }

    public List<MobileTerminalPlugin> inactivatePlugins(Map<String, PluginService> map) {
        List<MobileTerminalPlugin> responseList = new ArrayList<>();
        List<MobileTerminalPlugin> availablePlugins = mobileTerminalPluginDao.getPluginList();
        for (MobileTerminalPlugin plugin : availablePlugins) {
            PluginService pluginService = map.get(plugin.getPluginServiceName());
            if (pluginService == null && !plugin.getPluginInactive()) {
                LOG.debug("inactivate no longer available plugin");
                plugin.setPluginInactive(true);
                responseList.add(plugin);
            }
        }
        return responseList;
    }

    public MobileTerminalPlugin updatePlugin(PluginService plugin) {
        MobileTerminalPlugin entity = mobileTerminalPluginDao.getPluginByServiceName(plugin.getServiceName());
        if(entity == null){
            return null;
        }
        if (PluginMapper.equals(entity, plugin)) {
            return entity;
        } else {
            for (MobileTerminalPluginCapability capability : entity.getCapabilities()) {
                capability.setPlugin(null);
            }
            entity.getCapabilities().clear();
            entity = PluginMapper.mapModelToEntity(entity, plugin);
            mobileTerminalPluginDao.updateMobileTerminalPlugin(entity);
            return entity;
        }
    }

    public List<MobileTerminalPlugin> getMobileTerminalPlugins() {
        return mobileTerminalPluginDao.getPluginList();
    }

    public MobileTerminalPlugin upsertPlugin(PluginService plugin) {
        validatePluginService(plugin);
        MobileTerminalPlugin entity = updatePlugin(plugin);
        if (entity == null) {
            entity = PluginMapper.mapModelToEntity(plugin);
            mobileTerminalPluginDao.createMobileTerminalPlugin(entity);
        }
        return entity;
    }

    public MobileTerminalPlugin inactivatePlugin(PluginService plugin) {
        MobileTerminalPlugin pluginEntity = mobileTerminalPluginDao.getPluginByServiceName(plugin.getServiceName());
        pluginEntity.setPluginInactive(true);
        return pluginEntity;
    }

    private void validatePluginService(PluginService plugin) {
        if (plugin.getLabelName() == null || plugin.getLabelName().isEmpty()) {
            throw new IllegalArgumentException("No plugin name for plugin: " + plugin);
        }
        if (plugin.getServiceName() == null || plugin.getServiceName().isEmpty()) {
            throw new IllegalArgumentException("No service name for plugin: " + plugin.getLabelName());
        }
        if (plugin.getSatelliteType() == null || plugin.getSatelliteType().isEmpty()) {
            throw new IllegalArgumentException("No satellite type for plugin: " + plugin.getServiceName());
        }
    }
}
