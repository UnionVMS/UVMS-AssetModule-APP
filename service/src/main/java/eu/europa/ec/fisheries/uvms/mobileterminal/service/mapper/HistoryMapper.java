///*
//﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
//© European Union, 2015-2016.
//
//This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
//redistribute it and/or modify it under the terms of the GNU General Public License as published by the
//Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
//the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
//copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
// */
//package eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper;
//
//import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.EventCode;
//import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
//import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalEvents;
//import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalHistory;
//import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;
//import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
//import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalEvent;
//
//import java.util.Date;
//import java.util.Map;
//
//public class HistoryMapper {
//
//	public static MobileTerminalHistory getHistory(MobileTerminal terminal) {
//		if (terminal == null || terminal.getMobileTerminalEvents() == null) {
//            throw new IllegalArgumentException("No terminal history available");
//        }
//
//		MobileTerminalHistory terminalHistory = new MobileTerminalHistory();
//		for (MobileTerminalEvent event : terminal.getMobileTerminalEvents()) {
//			MobileTerminalEvents eventModel = new MobileTerminalEvents();
//			Date d = Date.from(event.getUpdatetime().toInstant());
//			eventModel.setChangeDate(d);
//			eventModel.setComments(event.getComment());
//			eventModel.setEventCode(EventCode.valueOf(event.getEventCodeType().toString()));
//			eventModel.setConnectId((event.getAsset() == null) ? null : event.getAsset().getId().toString());   //if there is no asset then null otherwise assets id
//			Map<String, String> attributes = AttributeMapper.mapAttributeString(event.getAttributes());
//			for (String key : attributes.keySet()) {
//				if (MobileTerminalConstants.SERIAL_NUMBER.equalsIgnoreCase(key)) {
//					eventModel.setSerialNumber(attributes.get(key));
//				}
//				MobileTerminalAttribute attribute = new MobileTerminalAttribute();
//				attribute.setType(key);
//				attribute.setValue(attributes.get(key));
//				eventModel.getAttributes().add(attribute);
//			}
//			terminalHistory.getEvents().add(eventModel);
//		}
//
//        return terminalHistory;
//	}
//}
