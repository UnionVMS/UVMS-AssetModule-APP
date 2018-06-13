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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.AttributeDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceMapperException;

import java.util.ArrayList;
import java.util.List;

public class PollMapper {

    public static List<PollDto> mapPolls(List<PollResponseType> pollResponses) throws MobileTerminalServiceMapperException {
        List<PollDto> dtoList = new ArrayList<>();
        for (PollResponseType response : pollResponses) {
            dtoList.add(mapPoll(response));
        }
        return dtoList;
    }
    
    public static PollDto mapPoll(PollResponseType response) throws MobileTerminalServiceMapperException {
        checkInputParams(response.getMobileTerminal());
        return createPollDto(response);
    }

    private static void checkInputParams(MobileTerminalType terminal) throws MobileTerminalServiceMapperException {
        if (terminal == null) {
            throw new MobileTerminalServiceMapperException("MobileTerminal is null");
        }
    }

    private static PollDto createPollDto(PollResponseType response) {
        MobileTerminalType terminal = response.getMobileTerminal();
        List<PollAttribute> attributes = response.getAttributes();

        PollDto dto = new PollDto();
        dto.addValue(PollKey.CONNECTION_ID, response.getMobileTerminal().getConnectId());
        dto.addValue(PollKey.TRANSPONDER, terminal.getType());
        dto.addValue(PollKey.POLL_ID, response.getPollId().getGuid());
        dto.addValue(PollKey.POLL_TYPE, response.getPollType().name());
        dto.addValue(PollKey.POLL_COMMENT, response.getComment());
        
        String startDate = getPollAttribute(PollAttributeType.START_DATE, attributes);
        if (startDate != null) {
            dto.addValue(PollKey.START_DATE, startDate);
        }
        String endDate = getPollAttribute(PollAttributeType.END_DATE, attributes);
        if (endDate != null) {
            dto.addValue(PollKey.END_DATE, endDate);
        }
        String frequency = getPollAttribute(PollAttributeType.FREQUENCY, attributes);
        if (frequency != null) {
            dto.addValue(PollKey.FREQUENCY, frequency);
        }
        String programRunning = getPollAttribute(PollAttributeType.PROGRAM_RUNNING, attributes);
        if (programRunning != null) {
            dto.addValue(PollKey.PROGRAM_RUNNING, programRunning);
        }

        String creator = getPollAttribute(PollAttributeType.USER, attributes);
        if(creator != null) {
        	dto.addValue(PollKey.USER, creator);
        }
        return dto;
    }

    private static String getPollAttribute(PollAttributeType type, List<PollAttribute> attributes) {
        for (PollAttribute attribute : attributes) {
            if (attribute.getKey().equals(type)) {
                return attribute.getValue();
            }
        }
        return null;
    }

    public static PollChannelDto mapPollChannel(MobileTerminalType mobileTerminal) throws MobileTerminalServiceMapperException {
        checkInputParams(mobileTerminal);

        PollChannelDto pollChannel = new PollChannelDto();

        if(mobileTerminal.getChannels().get(0) != null) {
            pollChannel.setComChannelId(mobileTerminal.getChannels().get(0).getGuid());
        }
        pollChannel.setMobileTerminalId(mobileTerminal.getMobileTerminalId().getGuid());
        pollChannel.setMobileTerminalType(mobileTerminal.getType());
        pollChannel.setConnectId(mobileTerminal.getConnectId());

        List<AttributeDto> attributes = new ArrayList<>();
        for(MobileTerminalAttribute attr : mobileTerminal.getAttributes()) {
        	AttributeDto dto = new AttributeDto();
        	dto.setType(attr.getType());
        	dto.setValue(attr.getValue());
        	attributes.add(dto);
        }

        //Only first channel
        if(mobileTerminal.getChannels().get(0) != null) {
        	for(ComChannelAttribute channel : mobileTerminal.getChannels().get(0).getAttributes()) {
        		AttributeDto cDto = new AttributeDto();
        		cDto.setType(channel.getType());
        		cDto.setValue(channel.getValue());
        		attributes.add(cDto);
        	}
        }

        pollChannel.setMobileTerminalAttributes(attributes);
        return pollChannel;
    }
}
