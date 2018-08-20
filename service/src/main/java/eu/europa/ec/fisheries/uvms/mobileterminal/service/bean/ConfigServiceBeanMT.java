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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.CapabilityConfiguration;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemConfiguration;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMapperException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageConsumer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.ConfigService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConfigType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.DNIDListDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.OceanRegionDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.DNIDList;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPluginCapability;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.OceanRegion;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.ExchangeModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.PluginMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.jms.TextMessage;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Stateless
@LocalBean
public class ConfigServiceBeanMT implements ConfigService {

    private final static Logger LOG = LoggerFactory.getLogger(ConfigServiceBeanMT.class);

    @EJB
    private MTMessageProducer MTMessageProducer;

    @EJB
    private OceanRegionDaoBean oceanRegionDao;

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @EJB
    private ChannelDaoBean channelDao;

    @EJB
    private DNIDListDaoBean dnidListDao;

    @EJB
    private MTMessageConsumer MTMessageConsumer;

    public List<TerminalSystemType> getTerminalSystems() {
        return getAllTerminalSystems();
    }

    public List<ConfigList> getConfig() {
        return getConfigValues();
    }

    public List<Plugin> upsertPlugins(List<PluginService> plugins, String username) {
        return upsertPlugins(plugins);
    }

    public List<ServiceResponseType> getRegisteredMobileTerminalPlugins() {
        try {
            List<PluginType> pluginTypes = new ArrayList<>();
            pluginTypes.add(PluginType.SATELLITE_RECEIVER);
            String data = ExchangeModuleRequestMapper.createGetServiceListRequest(pluginTypes);
            String messageId = MTMessageProducer.sendModuleMessage(data, ModuleQueue.EXCHANGE);
            TextMessage response = MTMessageConsumer.getMessage(messageId, TextMessage.class);
            if(response == null){
                throw new NullPointerException("No response from exchange");
            }
            return ExchangeModuleResponseMapper.mapServiceListResponse(response, messageId);
        } catch (ExchangeModelMapperException | RuntimeException e) {
            LOG.error("Failed to map to exchange get service list request due tue: " + e.getMessage());
            throw new RuntimeException("Failed to map to exchange get service list request", e);
        }
    }

    public List<TerminalSystemType> getAllTerminalSystems() {


        Map<MobileTerminalTypeEnum, List<MobileTerminalPlugin>> pluginsByType = getPlugins();
        List<TerminalSystemType> terminalSystemList = new ArrayList<>();

        for (MobileTerminalTypeEnum type : pluginsByType.keySet()) {

            TerminalSystemConfiguration terminalFieldConfiguration = PluginMapper.mapTerminalFieldConfiguration(type);
            TerminalSystemConfiguration comchannelFieldConfiguration = PluginMapper.mapComchannelFieldConfiguration(type);
            List<OceanRegion> oceanRegionList = oceanRegionDao.getOceanRegionList();
            CapabilityConfiguration capabilityConfiguration = PluginMapper.mapCapabilityConfiguration(type, pluginsByType.get(type), oceanRegionList);

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


    public List<Plugin> upsertPlugins(List<PluginService> pluginList) {
        if (pluginList == null) {
            throw new IllegalArgumentException("No pluginList to upsert");
        }

        Map<String, PluginService> map = new HashMap<>();
        List<Plugin> responseList = new ArrayList<>();
        for (PluginService plugin : pluginList) {
            if (plugin.getLabelName() == null || plugin.getLabelName().isEmpty()) {
                throw new IllegalArgumentException("No plugin name");
            }
            if (plugin.getServiceName() == null || plugin.getServiceName().isEmpty()) {
                throw new IllegalArgumentException("No service name");
            }
            if (plugin.getSatelliteType() == null || plugin.getSatelliteType().isEmpty()) {
                throw new IllegalArgumentException("No satellite type");
            }

            MobileTerminalPlugin entity = updatePlugin(plugin);
            if (entity == null) {
                entity = PluginMapper.mapModelToEntity(plugin);
                entity = mobileTerminalPluginDao.createMobileTerminalPlugin(entity);
            }
            map.put(plugin.getServiceName(), plugin);
            responseList.add(PluginMapper.mapEntityToModel(entity));
        }

        responseList.addAll(inactivatePlugins(map));

        return responseList;

    }

    public List<Plugin> inactivatePlugins(Map<String, PluginService> map) {


        List<Plugin> responseList = new ArrayList<>();

        List<MobileTerminalPlugin> availablePlugins = mobileTerminalPluginDao.getPluginList();
        for (MobileTerminalPlugin plugin : availablePlugins) {
            PluginService pluginService = map.get(plugin.getPluginServiceName());
            if (pluginService == null && !plugin.getPluginInactive()) {
                LOG.debug("inactivate no longer available plugin");
                plugin.setPluginInactive(true);
                responseList.add(PluginMapper.mapEntityToModel(plugin));
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

    public boolean checkDNIDListChange(String pluginName) {
        //TODO fix sql query:

        List<String> activeDnidList = channelDao.getActiveDNID(pluginName);
        List<DNIDList> dnidList = dnidListDao.getDNIDList(pluginName);
        if (changed(activeDnidList, dnidList)) {
            dnidListDao.removeByPluginName(pluginName);
            for (String terminalDnid : activeDnidList) {
                DNIDList dnid = new DNIDList();
                dnid.setDnid(terminalDnid);
                dnid.setPluginName(pluginName);
                dnid.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
                dnid.setUpdatedBy(MobileTerminalConstants.UPDATE_USER);
                dnidListDao.create(dnid);
            }
            return true;
        }
        return false;
    }

    private boolean changed(List<String> activeDnidList, List<DNIDList> existingDNIDList) {
        if (activeDnidList.isEmpty() && existingDNIDList.isEmpty()) {
            return false;
        }
        Set<String> activeDnidSet = new HashSet<>(activeDnidList);
        Set<String> entityDnidSet = new HashSet<>();
        for (DNIDList entity : existingDNIDList) {
            entityDnidSet.add(entity.getDnid());
        }
        if (activeDnidSet.size() != entityDnidSet.size()) return true;

        for (String activeDnid : activeDnidSet) {
            if (!entityDnidSet.contains(activeDnid)) {
                return true;
            }
        }

        for (String entityDnid : entityDnidSet) {
            if (!activeDnidSet.contains(entityDnid)) {
                return true;
            }
        }
        return false;
    }

    public List<String> updatedDNIDList(String pluginName) {
        List<String> dnids = new ArrayList<>();
        List<DNIDList> dnidList = dnidListDao.getDNIDList(pluginName);
        for (DNIDList entity : dnidList) {
            dnids.add(entity.getDnid());
        }
        return dnids;
    }
}
