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
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class HistoryMapper {
    
	public static MobileTerminalEvent UPDATED_createMobileterminalevent(MobileTerminal entity, EventCodeEnum eventcode, String comment, String username) {
		MobileTerminalEvent event = new MobileTerminalEvent();
		event.setMobileterminal(entity);
		event.setComment(comment);
        event.setUpdatetime(OffsetDateTime.now(ZoneOffset.UTC));
		event.setUpdateuser(username);
		event.setEventCodeType(eventcode);
		return event;
	}

	public static MobileTerminalHistory getHistory(MobileTerminal terminal) {
		if (terminal == null || terminal.getMobileTerminalEvents() == null) {
            throw new IllegalArgumentException("No terminal history available");
        }

		MobileTerminalHistory terminalHistory = new MobileTerminalHistory();
		for (MobileTerminalEvent event : terminal.getMobileTerminalEvents()) {
			MobileTerminalEvents eventModel = new MobileTerminalEvents();
			eventModel.setChangeDate(event.getUpdatetime());
			eventModel.setComments(event.getComment());
			eventModel.setEventCode(EventCode.valueOf(event.getEventCodeType().toString()));
			eventModel.setConnectId(event.getConnectId());
			Map<String, String> attributes = AttributeMapper.mapAttributeString(event.getAttributes());
			for (String key : attributes.keySet()) {
				if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(key)) {
					eventModel.setSerialNumber(attributes.get(key));
				}
				MobileTerminalAttribute attribute = new MobileTerminalAttribute();
				attribute.setType(key);
				attribute.setValue(attributes.get(key));
				eventModel.getAttributes().add(attribute);
			}
			terminalHistory.getEvents().add(eventModel);
		}

//		for (Channel channel : terminal.getChannels()) {
//			ComChannelHistory channelModel = new ComChannelHistory();
//			for (ChannelHistory history : channel.getHistories()) {
//				ComChannelHistoryAttributes historyModel = new ComChannelHistoryAttributes();
//				historyModel.setName(history.getName());
//				List<ComChannelAttribute> attributeList = AttributeMapper.mapAttributeStringToComChannelAttribute(history.getAttributes());
//				historyModel.getAttributes().addAll(attributeList);
//				historyModel.setChangeDate(history.getUpdateTime());
//				historyModel.setEventCode(EventCode.valueOf(history.getEventCodeType().toString()));
//				channelModel.getChannel().add(historyModel);
//			}
//			terminalHistory.getComChannels().add(channelModel);
//		}
        return terminalHistory;
	}
}
