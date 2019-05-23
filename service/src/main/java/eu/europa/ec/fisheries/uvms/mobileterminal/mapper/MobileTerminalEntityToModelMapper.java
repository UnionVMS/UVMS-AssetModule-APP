package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.asset.mapper.PollToCommandRequestMapper.PollReceiverInmarsatC;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MobileTerminalEntityToModelMapper {
    private static Logger LOG = LoggerFactory.getLogger(MobileTerminalEntityToModelMapper.class);

    public static MobileTerminalType mapToMobileTerminalType(MobileTerminal entity) {
        if (entity == null) {
            throw new NullPointerException("No mobile terminal entity to map");
        }

        MobileTerminalType model = new MobileTerminalType();
        model.setMobileTerminalId(mapToMobileTerminalId(entity.getId().toString()));

        Plugin plugin = PluginMapper.mapEntityToModel(entity.getPlugin());
        model.setPlugin(plugin);

        try {
            String value = entity.getSource().value();
            model.setSource(MobileTerminalSource.valueOf(value));
        } catch (RuntimeException e) {
            LOG.error("[ Error when setting mobile terminal source. ] {}", e);
            throw new RuntimeException(e);
        }

        if(entity.getAsset() != null){
            model.setConnectId(entity.getAsset().getId().toString());
        }

        model.setType(entity.getMobileTerminalType().name());
        model.setInactive(entity.getInactivated());
        model.setArchived(entity.getArchived());
        model.setId(new Long(entity.getCreateTime().toEpochSecond()).intValue());

        model.getChannels().addAll(mapChannels(entity));

        
        MobileTerminalAttribute serialNumber = new MobileTerminalAttribute();
        serialNumber.setType(PollReceiverInmarsatC.SERIAL_NUMBER.toString());
        serialNumber.setValue(entity.getSerialNo());
        model.getAttributes().add(serialNumber);
        
        MobileTerminalAttribute satelliteNumber = new MobileTerminalAttribute();
        serialNumber.setType(PollReceiverInmarsatC.SATELLITE_NUMBER.toString());
        serialNumber.setValue(entity.getSatelliteNumber());
        model.getAttributes().add(satelliteNumber);
        
        return model;
    }

    private static List<ComChannelType> mapChannels(MobileTerminal entity) {

        Set<Channel> channels = entity.getChannels();
        if (channels == null || channels.isEmpty()) {
            return new ArrayList<>();
        }
        List<ComChannelType> channelList = new ArrayList<>();
        for (Channel channel : channels) {
            if (channel.getArchived() != null && channel.getArchived()) {
                continue;
            }
            ComChannelType comChannel = new ComChannelType();
            comChannel.setName(channel.getName());
            comChannel.setGuid(channel.getId().toString());

            comChannel.getAttributes().addAll(AttributeMapper.mapAttributeStringToComChannelAttribute(channel));

            ComChannelCapability pollCapability = new ComChannelCapability();
            pollCapability.setType(MobileTerminalConstants.CAPABILITY_POLLABLE);

            if(channel.isPollChannel()) {
                pollCapability.setValue(true);
            }
            else{
                pollCapability.setValue(false);
            }
            comChannel.getCapabilities().add(pollCapability);

            ComChannelCapability configCapability = new ComChannelCapability();
            configCapability.setType(MobileTerminalConstants.CAPABILITY_CONFIGURABLE);

            if (channel.isConfigChannel()) {
                configCapability.setValue(true);
            } else {
                configCapability.setValue(false);
            }

            comChannel.getCapabilities().add(configCapability);

            ComChannelCapability defaultCapability = new ComChannelCapability();
            defaultCapability.setType(MobileTerminalConstants.CAPABILITY_DEFAULT_REPORTING);

            if(channel.isDefaultChannel()) {
                defaultCapability.setValue(true);
            }
            else{
                defaultCapability.setValue(false);
            }
            comChannel.getCapabilities().add(defaultCapability);

            channelList.add(comChannel);
        }
        return channelList;
    }

    private static MobileTerminalId mapToMobileTerminalId(String mobTermGuid) {
        if (mobTermGuid == null || mobTermGuid.isEmpty()) {
            throw new NullPointerException("No GUID found");
        }
        MobileTerminalId terminalId = new MobileTerminalId();
        terminalId.setGuid(mobTermGuid);
        return terminalId;
    }
}
