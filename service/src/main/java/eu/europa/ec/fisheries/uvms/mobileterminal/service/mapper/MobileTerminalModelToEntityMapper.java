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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobileTerminalModelToEntityMapper {
    private static Logger LOG = LoggerFactory.getLogger(MobileTerminalModelToEntityMapper.class);

    public static MobileTerminal mapMobileTerminalEntity(MobileTerminal entity, MobileTerminalType model,
                                                         String serialNumber, MobileTerminalPlugin plugin, String username,
                                                         String comment, EventCodeEnum event) {
        if (model == null)
            throw new NullPointerException("No mobile terminal to map");
//        if (entity.getId() == null) {
//            entity.setId(UUID.randomUUID());
//        }
        entity.setArchived(model.isArchived());
        entity.setInactivated(model.isInactive());
        entity.setPlugin(plugin);
        entity.setSerialNo(serialNumber);

        try {
            entity.setSource(model.getSource());
        } catch (RuntimeException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

        MobileTerminalTypeEnum type = MobileTerminalTypeEnum.getType(model.getType());
        if (type == null)
            throw new NullPointerException("Non valid mobile terminal type when mapping");
        entity.setMobileTerminalType(type);
        /*entity.setUpdatetime(LocalDateTime.now(ZoneOffset.UTC));
        entity.setUpdateuser(username);*/

        mapHistoryAttributes(entity, model, username, comment, event);

        // Channels can only change for these events
        if (event == EventCodeEnum.MODIFY || event == EventCodeEnum.CREATE) {
//            try {
                mapChannels(entity, model, username);
//            } catch (MobileTerminalModelException e) {
//                LOG.error("[ Error when mapping channel field types ]");
//                throw new MobileTerminalModelException(MAP_CHANNEL_FIELD_TYPES_ERROR.getMessage(), e, MAP_CHANNEL_FIELD_TYPES_ERROR.getCode());
//            }
        }
        entity.setUpdatetime(LocalDateTime.now(ZoneOffset.UTC));
        entity.setUpdateuser(username);

        return entity;
    }

    public static MobileTerminal mapNewMobileTerminalEntity(MobileTerminalType model,
                                String serialNumber, MobileTerminalPlugin plugin, String username) {
        if (model == null)
            throw new NullPointerException("No mobile terminal to map");
        return mapMobileTerminalEntity(new MobileTerminal(), model, serialNumber, plugin, username,
                MobileTerminalConstants.CREATE_COMMENT, EventCodeEnum.CREATE);
    }

    private static void mapChannels(MobileTerminal entity, MobileTerminalType model, String username) {
        List<ComChannelType> modelChannels = model.getChannels();
        Set<Channel> channels = new HashSet<>();
        for (ComChannelType channelType : modelChannels) {
            Channel channel = null;
            if (entity != null) {
                for (Channel c : entity.getChannels()) {
                    if (c.getId().equals(channelType.getGuid())) {
                        channel = c;
                        break;
                    }
                }
            }

            if (channel == null) {
                channel = new Channel();
                //channel.setGuid(UUID.randomUUID().toString());
            }
            channel.setMobileTerminal(entity);
            channel.setUpdateTime(LocalDateTime.now(ZoneOffset.UTC));
            channel.setUpdateUser(username);
            channel.setArchived(false);


            /*ChannelHistory history = new ChannelHistory();
            history.setChannel(channel);
            history.setUpdateTime(DateUtils.getNowDateUTC());
            history.setName(channelType.getName());
            history.setActive(true);
            history.setUpdatedBy(username);
            history.setMobileTerminalEvent(entity.getCurrentEvent());*/

            String attributes = mapComChannelAttributes(channelType.getAttributes());
            //history.setAttributes(attributes);
            /*if (channel.getHistories().size() == 0) {
                history.setEventCodeType(EventCodeEnum.CREATE);
            } else {
                history.setEventCodeType(EventCodeEnum.MODIFY);
            }*/

            for (ComChannelCapability capability : channelType.getCapabilities()) {
                if (MobileTerminalConstants.CAPABILITY_CONFIGURABLE.equalsIgnoreCase(capability.getType()) && capability.isValue()) {
                    entity.getCurrentEvent().setConfigChannel(channel);
                    //history.setConfigChannel(capability.isValue());
                }
                if (MobileTerminalConstants.CAPABILITY_DEFAULT_REPORTING.equalsIgnoreCase(capability.getType()) && capability.isValue()) {
                    entity.getCurrentEvent().setDefaultChannel(channel);
                    //history.setDefaultChannel(capability.isValue());
                }
                if (MobileTerminalConstants.CAPABILITY_POLLABLE.equalsIgnoreCase(capability.getType()) && capability.isValue()) {
                    entity.getCurrentEvent().setPollChannel(channel);
                    //history.setPollChannel(capability.isValue());
                }
            }

            // No changes to channel means no new history
            /*ChannelHistory channelHistory = channel.getCurrentHistory();
            if (channelHistory != null) {
                if (channelHistory.equals(history)) {
                    channels.add(channel);
                    continue;
                } else {
                    for (ChannelHistory ch : channel.getHistories()) {
                        ch.setActive(false);
                        channels.add(channel);
                    }
                }
            }

            for (ChannelHistory ch : channel.getHistories()) {
                ch.setActive(false);
            }
            channel.getHistories().add(history);*/
            channels.add(channel);

        }

        for (Channel channel : entity.getChannels()) {
            if (!channels.contains(channel)) {
                channel.setArchived(true);
              /*  ChannelHistory current = channel.getCurrentHistory();
                current.setActive(false);

                ChannelHistory archive = new ChannelHistory();
                archive.setEventCodeType(EventCodeEnum.ARCHIVE);
                archive.setName(current.getName());
                archive.setMobileTerminalEvent(entity.getCurrentEvent());
                archive.setActive(true);
                archive.setAttributes(current.getAttributes());
                archive.setChannel(channel);
                archive.setUpdatedBy(username);
                archive.setUpdateTime(DateUtils.getNowDateUTC());
                channel.getHistories().add(archive);*/
            }
        }
        entity.setChannels(channels);
    }

    private static void mapHistoryAttributes(MobileTerminal entity, MobileTerminalType model, String username, String comment, EventCodeEnum eventCode) {
        List<MobileTerminalAttribute> modelAttributes = model.getAttributes();
        MobileTerminalEvent current = entity.getCurrentEvent();

        MobileTerminalEvent history = new MobileTerminalEvent();
        history.setActive(true);
        history.setUpdatetime(LocalDateTime.now(ZoneOffset.UTC));
        history.setUpdateuser(username);
        history.setMobileterminal(entity);
        history.setComment(comment);
        history.setEventCodeType(eventCode);
        if (current != null && model.getConnectId() == null && eventCode != EventCodeEnum.UNLINK) {
            history.setConnectId(current.getConnectId());
        } else {
            history.setConnectId(model.getConnectId());
        }

        history.setAttributes(mapHistoryAttributes(modelAttributes));
        for (MobileTerminalEvent event : entity.getMobileTerminalEvents()) {
            event.setActive(false);
        }
        entity.getMobileTerminalEvents().add(history);
    }

    private static String mapHistoryAttributes(List<MobileTerminalAttribute> modelAttributes) {
        StringBuilder sb = new StringBuilder();
        for (MobileTerminalAttribute attr : modelAttributes) {
            sb.append(attr.getType()).append("=");
            sb.append(attr.getValue()).append(";");
        }
        return sb.toString();
    }

    private static String mapComChannelAttributes(List<ComChannelAttribute> modelAttributes) {
        StringBuilder sb = new StringBuilder();
        for (ComChannelAttribute attr : modelAttributes) {
            sb.append(attr.getType()).append("=");
            sb.append(attr.getValue()).append(";");
        }
        return sb.toString();
    }

}
