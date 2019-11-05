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
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PollModelToEntityMapper {
    private static Logger LOG = LoggerFactory.getLogger(PollModelToEntityMapper.class);

    public static PollProgram mapToProgramPoll(MobileTerminal terminal, String channelGuid, PollRequestType requestType) {
        PollProgram poll = new PollProgram();
        PollBase pollBase = createNewPollBase(terminal, channelGuid, requestType);
        poll.setPollBase(pollBase);
        poll.setPollState(PollStateEnum.STARTED);

        poll.setLatestRun(null);
        poll.setUpdatedBy(requestType.getUserName());
        poll.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));

        List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
            throw new NullPointerException("No attributes to map to program poll");
        for (PollAttribute attr : attributes) {
            try {
                switch (attr.getKey()) {
                case FREQUENCY:
                    poll.setFrequency(Integer.parseInt(attr.getValue()));
                    break;
                case START_DATE:
                    poll.setStartDate(OffsetDateTime.parse(attr.getValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))); // Not sure if it will work without a format pattern
                    break;
                case END_DATE:
                    poll.setStopDate(OffsetDateTime.parse(attr.getValue(),  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))); // Not sure if it will work without a format pattern
                    break;
                default:
                    LOG.debug("ProgramPoll with attr [ " + attr.getKey() + " ] is non valid to map");
                }
            } catch (UnsupportedOperationException | IllegalArgumentException e) {
                throw new RuntimeException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
            }
        }
        return poll;
    }

    private static PollBase createNewPollBase(MobileTerminal terminal, String channelGuid, PollRequestType requestType) {
        PollBase pollBase = new PollBase();
        pollBase.setChannelId(UUID.fromString(channelGuid));
        pollBase.setMobileterminal(terminal);
        pollBase.setTerminalConnect(terminal.getAssetId());
        pollBase.setComment(requestType.getComment());
        pollBase.setCreator(requestType.getUserName());

        pollBase.setUpdatedBy(requestType.getUserName());
        pollBase.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return pollBase;
    }

    public static Poll mapToPoll(MobileTerminal mobileTerminal, String channelGuid, PollRequestType requestType) {
    	switch (requestType.getPollType()) {
        case CONFIGURATION_POLL:
        	return mapToConfigurationPoll(mobileTerminal, channelGuid, requestType);
        case SAMPLING_POLL:
        	return mapToSamplingPoll(mobileTerminal, channelGuid, requestType);
        case AUTOMATIC_POLL:
        case MANUAL_POLL:
        	return createPollBase(mobileTerminal, channelGuid, requestType);
        default:
        	throw new IllegalArgumentException("Non valid poll type");
    	}
    }
    
    private static Poll createPollBase(MobileTerminal comchannel, String channelGuid, PollRequestType requestType) {
        Poll poll = new Poll();
        PollBase pollBase = createNewPollBase(comchannel, channelGuid, requestType);
        poll.setPollBase(pollBase);
        try {
        	poll.setPollType(EnumMapper.getPollTypeFromModel(requestType.getPollType()));
        } catch (RuntimeException e) {
            LOG.error("Couldn't map type of poll " + e);
        	throw new RuntimeException(e);
        }
        poll.setUpdatedBy(requestType.getUserName());
        poll.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        return poll;
    }

    private static Poll mapToConfigurationPoll(MobileTerminal comchannel, String channelGuid, PollRequestType requestType) {
    	Poll poll = createPollBase(comchannel, channelGuid, requestType);
        List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
        	throw new NullPointerException("No attributes to map to configuration poll");
        Set<PollPayload> payloadList = new HashSet<>();
        PollPayload payload = new PollPayload();
        for (PollAttribute attr : attributes) {
        	try {
        		switch (attr.getKey()) {
                case REPORT_FREQUENCY:
                	payload.setReportingFrequency(Integer.parseInt(attr.getValue()));
                    break;
                case GRACE_PERIOD:
                	payload.setGracePeriod(Integer.parseInt(attr.getValue()));
                    break;
                case IN_PORT_GRACE:
                	payload.setInPortGrace(Integer.parseInt(attr.getValue()));
                    break;
                case DNID:
                	payload.setNewDnid(attr.getValue());
                    break;
                case MEMBER_NUMBER:
                	payload.setNewMemberNumber(attr.getValue());
                    break;
        		}
        	} catch (UnsupportedOperationException | IllegalArgumentException e) {
        		throw new RuntimeException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
        	}
        }
        payload.setPoll(poll);
        payloadList.add(payload);
        poll.setPayloads(payloadList);
        return poll;
    }

    private static Poll mapToSamplingPoll(MobileTerminal mobileTerminal, String channelGuid, PollRequestType requestType) {
    	Poll poll = createPollBase(mobileTerminal, channelGuid, requestType);
        List<PollAttribute> attributes = requestType.getAttributes();
        if (attributes == null || attributes.isEmpty())
        	throw new NullPointerException("No attributes to map to sampling poll");
        Set<PollPayload> payloadList = new HashSet<>();
        PollPayload payload = new PollPayload();
        for (PollAttribute attr : attributes) {
        	try {
        		switch (attr.getKey()) {
        		case START_DATE:
                    payload.setStartDate(OffsetDateTime.parse(attr.getValue())); // Not sure if it will work without a format pattern
                	break;
        		case END_DATE:
                    payload.setStopDate(OffsetDateTime.parse(attr.getValue())); // Not sure if it will work without a format pattern
                    break;
        		}
        	} catch (UnsupportedOperationException | IllegalArgumentException e) {
        		throw new RuntimeException("Poll attribute [ " + attr.getKey() + " ] could not be parsed");
        	}
        }
        payload.setPoll(poll);
        payloadList.add(payload);
        poll.setPayloads(payloadList);
        return poll;
    }
}
