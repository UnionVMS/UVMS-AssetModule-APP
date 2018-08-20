package eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobileTerminalEntityToModelMapper {
    private static Logger LOG = LoggerFactory.getLogger(MobileTerminalEntityToModelMapper.class);

    public static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity, Channel channel) {
        Set<Channel> channels = new HashSet<>();
        channels.add(channel);
        return mapToMobileTerminalType(entity, channels);
    }

    private static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity, Set<Channel> channels) {
        MobileTerminalType type = mapToMobileTerminalType(entity);

        type.getChannels().clear();
        type.getChannels().addAll(mapChannels(entity.getChannels(), entity.getCurrentEvent()));

        return type;
    }

    public static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity) {
        if (entity == null) {
            throw new NullPointerException("No mobile terminal entity to map");
        }

        MobileTerminalEvent currentEvent = entity.getCurrentEvent();
        if (currentEvent == null) {
            throw new NullPointerException("No mobile terminal event entity to map");
        }

        MobileTerminalType model = new MobileTerminalType();
        model.setMobileTerminalId(mapToMobileTerminalId(entity.getId().toString()));

        Plugin plugin = PluginMapper.mapEntityToModel(entity.getPlugin());
        model.setPlugin(plugin);

        try {
            model.setSource(entity.getSource());
        } catch (RuntimeException e) {
            LOG.error("[ Error when setting mobile terminal source. ] {}", e.getMessage());
            throw new RuntimeException(e);
        }

        model.setConnectId(currentEvent.getConnectId());

        model.setType(entity.getMobileTerminalType().name());
        model.setInactive(entity.getInactivated());
        model.setArchived(entity.getArchived());
        model.setId(new Long(entity.getCreateTime().toEpochSecond()).intValue());

        model.getChannels().addAll(mapChannels(entity.getChannels(), currentEvent));

        model.getAttributes().addAll(AttributeMapper.mapAttributeStringToTerminalAttribute(currentEvent.getAttributes()));

        return model;
    }

    private static List<ComChannelType> mapChannels(Set<Channel> channels, MobileTerminalEvent currentEvent) {
        //TODO: fix later
        // (fix what? the channel history removal is done from this part, is there anything else?)

        if (channels == null || channels.isEmpty()) {
            return new ArrayList<>();
        }
        List<ComChannelType> channelList = new ArrayList<>();
        for (Channel channel : channels) {
            if (channel.getArchived() != null && channel.getArchived()) {
                continue;
            }
            //ChannelHistory current = channel.getCurrentHistory();
            //if (current != null) {
                ComChannelType comChannel = new ComChannelType();
                comChannel.setName(channel.getName());
                comChannel.setGuid(channel.getId().toString());

                comChannel.getAttributes().addAll(AttributeMapper.mapAttributeStringToComChannelAttribute(channel));

                ComChannelCapability pollCapability = new ComChannelCapability();
                pollCapability.setType(MobileTerminalConstants.CAPABILITY_POLLABLE);

                Channel wrkPollChannel = currentEvent.getPollChannel();
                if(wrkPollChannel != null) {
                    pollCapability.setValue(currentEvent.getPollChannel().equals(channel));
                }
                else{
                    pollCapability.setValue(false);
                }
                comChannel.getCapabilities().add(pollCapability);

                ComChannelCapability configCapability = new ComChannelCapability();
                configCapability.setType(MobileTerminalConstants.CAPABILITY_CONFIGURABLE);

                Channel wrkConfigChannel = currentEvent.getConfigChannel();
                if (wrkConfigChannel != null) {
                    configCapability.setValue(currentEvent.getConfigChannel().equals(channel));
                } else {
                    configCapability.setValue(false);
                }

                comChannel.getCapabilities().add(configCapability);

                ComChannelCapability defaultCapability = new ComChannelCapability();
                defaultCapability.setType(MobileTerminalConstants.CAPABILITY_DEFAULT_REPORTING);

                Channel wrkDefaultChannel = currentEvent.getDefaultChannel();
                if(wrkDefaultChannel != null) {
                    defaultCapability.setValue(currentEvent.getDefaultChannel().equals(channel));
                }
                else{
                    defaultCapability.setValue(false);
                }
                comChannel.getCapabilities().add(defaultCapability);

                channelList.add(comChannel);
            //}
        }
        return channelList;
    }

    private static MobileTerminalId mapToMobileTerminalId(String mobtermGuid) {
        if (mobtermGuid == null || mobtermGuid.isEmpty()) {
            throw new NullPointerException("No GUID found");
        }
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(mobtermGuid);
        return terminalId;
    }
}
