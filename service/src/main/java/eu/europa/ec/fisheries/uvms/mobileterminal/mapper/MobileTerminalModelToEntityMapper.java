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
package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalAttributes;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobileTerminalModelToEntityMapper {
    private static Logger LOG = LoggerFactory.getLogger(MobileTerminalModelToEntityMapper.class);

    //DO NOT USE WITH AN EMPTY ENTITY UNLESS YOU REALLY KNOW WHAT YOU ARE DOING
    public static MobileTerminal mapMobileTerminalEntity(MobileTerminal entity, MobileTerminalType model,
                                                         String serialNumber, MobileTerminalPlugin plugin, String username,
                                                         EventCodeEnum event) {
        if (model == null)
            throw new NullPointerException("No mobile terminal to map");
        entity.setArchived(model.isArchived());
        entity.setInactivated(model.isInactive());
        entity.setPlugin(plugin);
        entity.setSerialNo(serialNumber);
        entity.setSource(model.getSource());

        MobileTerminalTypeEnum type = MobileTerminalTypeEnum.getType(model.getType());
        if (type == null)
            throw new NullPointerException("Non valid mobile terminal type when mapping");
        entity.setMobileTerminalType(type);

        if (event == EventCodeEnum.MODIFY || event == EventCodeEnum.CREATE) {
            mapChannels(entity, model, username);
        }
        entity.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
        entity.setUpdateuser(username);
        List<MobileTerminalAttributes> attrList = AttributeMapper.mapModelAttributesToEntityAttributes(entity, model.getAttributes());
        entity.getMobileTerminalAttributes().addAll(attrList);

        return entity;
    }

    public static MobileTerminal mapNewMobileTerminalEntity(MobileTerminalType model, String serialNumber, MobileTerminalPlugin plugin, String username) {
        if (model == null)
            throw new NullPointerException("No mobile terminal to map");
        return mapMobileTerminalEntity(new MobileTerminal(), model, serialNumber, plugin, username, EventCodeEnum.CREATE);
    }

    private static void mapChannels(MobileTerminal entity, MobileTerminalType model, String username) {
        List<ComChannelType> modelChannels = model.getChannels();
        Set<Channel> channels = new HashSet<>();
        for (ComChannelType channelType : modelChannels) {
            Channel channel = null;
            if (entity != null) {
                for (Channel c : entity.getChannels()) {
                    if (c.getId().toString().equals(channelType.getGuid())) {
                        channel = c;
                        break;
                    }
                }
            }

            if (channel == null) {
                channel = new Channel();
            }
            channel.setMobileTerminal(entity);
            channel.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
            channel.setUpdateUser(username);
            channel.setArchived(false);

            channel.setName(channelType.getName());
            AttributeMapper.mapComChannelAttributes(channel, channelType.getAttributes());

            for (ComChannelCapability capability : channelType.getCapabilities()) {
                if (MobileTerminalConstants.CAPABILITY_CONFIGURABLE.equalsIgnoreCase(capability.getType()) && capability.isValue()) {
                    entity.setConfigChannel(channel);
                    channel.setConfigChannel(capability.isValue());
                }
                if (MobileTerminalConstants.CAPABILITY_DEFAULT_REPORTING.equalsIgnoreCase(capability.getType()) && capability.isValue()) {
                    entity.setDefaultChannel(channel);
                    channel.setDefaultChannel(capability.isValue());
                }
                if (MobileTerminalConstants.CAPABILITY_POLLABLE.equalsIgnoreCase(capability.getType()) && capability.isValue()) {
                    entity.setPollChannel(channel);
                    channel.setPollChannel(capability.isValue());
                }
            }
            channels.add(channel);
        }

        for (Channel channel : entity.getChannels()) {
            if (!channels.contains(channel)) {
                channel.setArchived(true);
            }
        }
        entity.setChannels(channels);
    }
}
