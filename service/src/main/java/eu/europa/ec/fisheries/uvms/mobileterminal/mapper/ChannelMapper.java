/*
 Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChannelMapper {

    static List<ComChannelType> mapChannels(MobileTerminal entity) {

        Set<Channel> channels = entity.getChannels();
        if (channels == null || channels.isEmpty()) {
            return new ArrayList<>();
        }
        List<ComChannelType> channelList = new ArrayList<>();
        for (Channel channel : channels) {

            if (channel.getArchived() != null && channel.getArchived()) continue;
            if(!channel.isPollChannel()) continue;

            ComChannelType comChannel = new ComChannelType();
            comChannel.setName(channel.getName());
            comChannel.setGuid(channel.getId().toString());

            comChannel.getAttributes().addAll(ChannelMapper.mapAttributes(channel));
            ChannelMapper.mapCapabilities(comChannel, channel);

            channelList.add(comChannel);
        }
        return channelList;
    }

    private static List<ComChannelAttribute> mapAttributes(Channel channel) {
        List<ComChannelAttribute> attributeList = new ArrayList<>();
        attributeList.add(mapAttr("DNID", channel.getDnid()));
        attributeList.add(mapAttr("FREQUENCY_EXPECTED", String.valueOf(channel.getExpectedFrequency().getSeconds())));
        attributeList.add(mapAttr("FREQUENCY_IN_PORT", String.valueOf(channel.getExpectedFrequencyInPort().getSeconds())));
        attributeList.add(mapAttr("LES_DESCRIPTION", channel.getLesDescription()));
        attributeList.add(mapAttr("FREQUENCY_GRACE_PERIOD", String.valueOf(channel.getFrequencyGracePeriod().getSeconds())));
        attributeList.add(mapAttr("MEMBER_NUMBER", channel.getMemberNumber()));
        attributeList.add(mapAttr("START_DATE", DateUtils.dateToEpochMilliseconds(channel.getStartDate())));
        attributeList.add(mapAttr("END_DATE", DateUtils.dateToEpochMilliseconds(channel.getEndDate())));
        return attributeList;
    }

    private static void mapCapabilities(ComChannelType comChannel, Channel channel) {
        ComChannelCapability pollCapability = new ComChannelCapability();
        pollCapability.setType(MobileTerminalConstants.CAPABILITY_POLLABLE);

        if (channel.isPollChannel()) {
            pollCapability.setValue(true);
        } else {
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

        if (channel.isDefaultChannel()) {
            defaultCapability.setValue(true);
        } else {
            defaultCapability.setValue(false);
        }
        comChannel.getCapabilities().add(defaultCapability);
    }

    private static ComChannelAttribute mapAttr(String key, String value) {
        ComChannelAttribute attr = new ComChannelAttribute();
        attr.setType(key);
        attr.setValue(value);
        return attr;
    }
}
