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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.SanePollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.ConfigurationPoll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.PollBase;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.ProgramPoll;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PollEntityToModelMapper {

    public static PollResponseType mapToPollResponseType(ProgramPoll program, MobileTerminal mobileTerminal) {
        PollResponseType response = mapEntityToPollResponseType(program, mobileTerminal);
        response.setPollType(PollType.PROGRAM_POLL);
        PollId pollId = new PollId();
        pollId.setGuid(program.getId().toString());
        response.setPollId(pollId);

        response.getAttributes().addAll(getProgramPollAttributes(program));
        return response;
    }

    public static PollResponseType mapToPollResponseType(PollBase poll, MobileTerminal mobileTerminal) {
        PollResponseType response = mapEntityToPollResponseType(poll, mobileTerminal);

        PollId pollId = new PollId();
        pollId.setGuid(poll.getId().toString());
        response.setPollId(pollId);

        PollTypeEnum pollTypeEnum = poll.getPollTypeEnum();
        PollType pollType = EnumMapper.getPollModelFromType(pollTypeEnum);

        response.setPollType(pollType);
        if (pollType == PollType.CONFIGURATION_POLL) {
            List<PollAttribute> pollAttributes = response.getAttributes();

            if(((ConfigurationPoll) poll).getGracePeriod() != null) {
                pollAttributes.add(createPollAttribute(PollAttributeType.GRACE_PERIOD, ((ConfigurationPoll) poll).getGracePeriod()));
            }
            if(((ConfigurationPoll) poll).getInPortGrace() != null){
                pollAttributes.add(createPollAttribute(PollAttributeType.IN_PORT_GRACE, ((ConfigurationPoll) poll).getInPortGrace()));
            }
            if(((ConfigurationPoll) poll).getReportingFrequency() != null){
                pollAttributes.add(createPollAttribute(PollAttributeType.REPORT_FREQUENCY, ((ConfigurationPoll) poll).getReportingFrequency()));
            }
        }

        if (pollType == PollType.SAMPLING_POLL) {
            List<PollAttribute> pollAttributes = response.getAttributes();

            if(((ConfigurationPoll) poll).getGracePeriod() != null) {
                pollAttributes.add(createPollAttribute(PollAttributeType.START_DATE, ((ConfigurationPoll) poll).getGracePeriod()));
            }
            if(((ConfigurationPoll) poll).getInPortGrace() != null){
                pollAttributes.add(createPollAttribute(PollAttributeType.END_DATE, ((ConfigurationPoll) poll).getInPortGrace()));
            }
        }
        return response;
    }

    private static <T extends PollBase> PollResponseType mapEntityToPollResponseType(T pollBase, MobileTerminal mobileTerminal) {
        PollResponseType response = new PollResponseType();
        response.setComment(pollBase.getComment());
        response.setUserName(pollBase.getUpdatedBy());
        // TODO created time?
        response.setMobileTerminal(MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal));
        response.getAttributes().add(createPollAttribute(PollAttributeType.USER, pollBase.getUpdatedBy()));
        return response;
    }

    private static PollAttribute createPollAttribute(PollAttributeType key, Integer value){
        PollAttribute pollAttribute = new PollAttribute();
        pollAttribute.setKey(key);
        pollAttribute.setValue(String.valueOf(value));
        return pollAttribute;
    }

    private static List<PollAttribute> getProgramPollAttributes(ProgramPoll program) {
        List<PollAttribute> attributes = new ArrayList<>();
        attributes.add(createPollAttribute(PollAttributeType.FREQUENCY, program.getFrequency().toString()));
        attributes.add(createPollAttribute(PollAttributeType.START_DATE, DateUtils.dateToEpochMilliseconds(program.getStartDate())));
        attributes.add(createPollAttribute(PollAttributeType.END_DATE, DateUtils.dateToEpochMilliseconds(program.getStopDate())));

        switch (program.getPollState()) {
            case STARTED:
                attributes.add(createPollAttribute(PollAttributeType.PROGRAM_RUNNING, MobileTerminalConstants.TRUE));
                break;
            case STOPPED:
            case ARCHIVED:
                attributes.add(createPollAttribute(PollAttributeType.PROGRAM_RUNNING, MobileTerminalConstants.FALSE));
                break;
        }
        return attributes;
    }

    private static PollAttribute createPollAttribute(PollAttributeType key, String value) {
        PollAttribute attr = new PollAttribute();
        attr.setKey(key);
        attr.setValue(value);
        return attr;
    }

    public static  List<SanePollDto> toSanePollDto(List<PollBase> polls){
        return polls.stream().map(poll -> toSanePollDto(poll)).collect(Collectors.toList());
    }

    public static SanePollDto toSanePollDto(PollBase poll){
        SanePollDto dto = new SanePollDto();
        dto.setAssetId(poll.getAssetId());
        dto.setChannelId(poll.getChannelId());
        dto.setComment(poll.getComment());
        dto.setCreator(poll.getCreator());
        dto.setId(poll.getId());
        dto.setMobileterminalId(poll.getMobileterminal().getId());
        dto.setPollTypeEnum(poll.getPollTypeEnum());
        dto.setUpdatedBy(poll.getUpdatedBy());
        dto.setCreateTime(poll.getCreateTime());

        return dto;
    }
}
