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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.mock;

import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 **/
public class MockData {

    private static Logger LOG = LoggerFactory.getLogger(MockData.class);

    public static MobileTerminalType createMobileTerminalDto(Integer id) {
        MobileTerminalType dto = new MobileTerminalType();
        MobileTerminalId mobId = new MobileTerminalId();
        mobId.setGuid(String.valueOf(id));
        dto.getChannels().addAll(createMobileTerminalChannels(2));
        dto.setMobileTerminalId(mobId);
        return dto;
    }

    public static ComChannelAttribute createComChannelAttribute(String type, String value) {
        ComChannelAttribute channelID = new ComChannelAttribute();
        channelID.setType(type);
        channelID.setValue(value);
        return channelID;
    }

    public static ComChannelType createComChannel(Integer id) {
        ComChannelType channeltype = new ComChannelType();
        channeltype.setName("VMS");

        ComChannelAttribute memberId = createComChannelAttribute("MEMBER_NUMBER", id.toString());
        ComChannelAttribute dnId = createComChannelAttribute("DNID", id.toString());

        channeltype.getAttributes().add(memberId);
        channeltype.getAttributes().add(dnId);
        return channeltype;
    }

    public static List<ComChannelType> createMobileTerminalChannels(Integer amount) {
        List<ComChannelType> dtoList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            dtoList.add(createComChannel(i));
        }
        return dtoList;
    }

    public static List<MobileTerminalType> createMobileTerminalDtoList(Integer amount) {
        List<MobileTerminalType> dtoList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            dtoList.add(createMobileTerminalDto(i));
        }
        return dtoList;
    }

    public static MobileTerminalListResponse createMobileTerminalListResponse() {
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(1);
        response.setTotalNumberOfPages(1);
        response.getMobileTerminal().addAll(createMobileTerminalDtoList(1));
        return response;
    }
}