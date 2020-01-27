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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PollModelToEntityMapper {
    private static Logger LOG = LoggerFactory.getLogger(PollModelToEntityMapper.class);

    public static ProgramPoll mapToProgramPoll(MobileTerminal terminal, String channelGuid, PollRequestType requestType) {
        ProgramPoll programPoll = createPoll(terminal, channelGuid, requestType, ProgramPoll.class);

        programPoll.setPollState(PollStateEnum.STARTED);
        programPoll.setLatestRun(null);

        List<PollAttribute> attributes = requestType.getAttributes();

        if (attributes == null || attributes.isEmpty())
            throw new NullPointerException("No attributes to map to program poll");

        for (PollAttribute attr : attributes) {
            try {
                switch (attr.getKey()) {
                    case FREQUENCY:
                        programPoll.setFrequency(Integer.parseInt(attr.getValue()));
                        break;
                    case START_DATE:
                        programPoll.setStartDate(DateUtils.stringToDate(attr.getValue()));
                        break;
                    case END_DATE:
                        programPoll.setStopDate(DateUtils.stringToDate(attr.getValue()));
                        break;
                    default:
                        LOG.debug("ProgramPoll with attr [ " + attr.getKey() + " ] is non valid to map");
                }
            } catch (UnsupportedOperationException | IllegalArgumentException e) {
                throw new RuntimeException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
            }
        }
        return programPoll;
    }

    public static ConfigurationPoll mapToConfigurationPoll(MobileTerminal terminal, String channelGuid, PollRequestType requestType) {
        ConfigurationPoll configurationPoll = createPoll(terminal, channelGuid, requestType, ConfigurationPoll.class);

        List<PollAttribute> attributes = requestType.getAttributes();

        if (attributes == null || attributes.isEmpty())
            throw new NullPointerException("No attributes to map to configuration poll");

        for (PollAttribute attr : attributes) {
            try {
                switch (attr.getKey()) {
                    case REPORT_FREQUENCY:
                        configurationPoll.setReportingFrequency(Integer.parseInt(attr.getValue()));
                        break;
                    case GRACE_PERIOD:
                        configurationPoll.setGracePeriod(Integer.parseInt(attr.getValue()));
                        break;
                    case IN_PORT_GRACE:
                        configurationPoll.setInPortGrace(Integer.parseInt(attr.getValue()));
                        break;
                }
            } catch (UnsupportedOperationException | IllegalArgumentException e) {
                throw new RuntimeException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
            }
        }
        return configurationPoll;
    }

    public static SamplingPoll mapToSamplingPoll(MobileTerminal terminal, String channelGuid, PollRequestType requestType) {
        SamplingPoll samplingPoll = createPoll(terminal, channelGuid, requestType, SamplingPoll.class);

        List<PollAttribute> attributes = requestType.getAttributes();

        if (attributes == null || attributes.isEmpty())
            throw new NullPointerException("No attributes to map to sampling poll");

        for (PollAttribute attr : attributes) {
            try {
                switch (attr.getKey()) {
                    case START_DATE:
                        samplingPoll.setStartDate(DateUtils.stringToDate(attr.getValue()));
                        break;
                    case END_DATE:
                        samplingPoll.setStopDate(DateUtils.stringToDate(attr.getValue()));
                        break;
                }
            } catch (UnsupportedOperationException | IllegalArgumentException e) {
                throw new RuntimeException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
            }
        }
        return samplingPoll;
    }

    public static <T extends PollBase> T createPoll(MobileTerminal terminal, String channelGuid, PollRequestType requestType, Class<T> clazz) {
        try {
            T poll = clazz.newInstance();
            poll.setChannelId(UUID.fromString(channelGuid));
            poll.setMobileterminal(terminal);
            poll.setTerminalConnect(terminal.getAssetUUID());
            poll.setComment(requestType.getComment());
            poll.setCreator(requestType.getUserName());
            poll.setUpdatedBy(requestType.getUserName());
            poll.setPollTypeEnum(EnumMapper.getPollTypeFromModel(requestType.getPollType()));
            poll.setUpdateTime(Instant.now());
            return poll;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error when creating Poll instance of type: " + clazz.getTypeName(), e);
        }
    }
}
