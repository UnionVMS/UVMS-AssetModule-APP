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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceMapperException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.PollMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
@LocalBean
public class MappedPollServiceBean {
    private final static Logger LOG = LoggerFactory.getLogger(MappedPollServiceBean.class);

    @EJB
    private PollServiceBean pollService;

    @EJB
    private MobileTerminalServiceBean mobileTerminalService;

    public CreatePollResultDto createPoll(PollRequestType pollRequest, String username) throws MobileTerminalServiceException {
        LOG.debug("Create poll");

        return pollService.createPoll(pollRequest, username);
    }

    public List<PollDto> getRunningProgramPolls() throws MobileTerminalServiceMapperException {

        List<PollResponseType> pollResponse = pollService.getRunningProgramPolls();
        return PollMapper.mapPolls(pollResponse);
    }

    public PollDto startProgramPoll(String pollId, String username) throws MobileTerminalServiceMapperException, MobileTerminalServiceException {
        PollResponseType pollResponse = pollService.startProgramPoll(pollId, username);
        return PollMapper.mapPoll(pollResponse);
    }

    public PollDto stopProgramPoll(String pollId, String username) throws MobileTerminalServiceMapperException, MobileTerminalServiceException {
        PollResponseType pollResponse = pollService.stopProgramPoll(pollId, username);
        return PollMapper.mapPoll(pollResponse);
    }

    public PollDto inactivateProgramPoll(String pollId, String username) throws MobileTerminalServiceMapperException, MobileTerminalServiceException {
        PollResponseType pollResponse = pollService.inactivateProgramPoll(pollId, username);
        return PollMapper.mapPoll(pollResponse);
    }

    public PollChannelListDto getPollBySearchQuery(PollListQuery query) throws MobileTerminalServiceMapperException, MobileTerminalServiceException {
    	PollChannelListDto channelListDto = new PollChannelListDto();
    	
    	PollListResponse pollResponse = pollService.getPollBySearchCriteria(query);
        channelListDto.setCurrentPage(pollResponse.getCurrentPage());
        channelListDto.setTotalNumberOfPages(pollResponse.getTotalNumberOfPages());
    	
        ArrayList<PollChannelDto> pollChannelList = new ArrayList<>();
        for(PollResponseType responseType : pollResponse.getPollList()) {
        	PollChannelDto terminal = PollMapper.mapPollChannel(responseType.getMobileTerminal());
        	terminal.setPoll(PollMapper.mapPoll(responseType));
        	pollChannelList.add(terminal);
        }
        channelListDto.setPollableChannels(pollChannelList);
        return channelListDto;
    }

    public PollChannelListDto getPollableChannels(PollableQuery query) throws MobileTerminalServiceMapperException, MobileTerminalModelException {
        PollChannelListDto channelListDto = new PollChannelListDto();

        MobileTerminalListResponse response = mobileTerminalService.getPollableMobileTerminal(query);
        channelListDto.setCurrentPage(response.getCurrentPage());
        channelListDto.setTotalNumberOfPages(response.getTotalNumberOfPages());

        ArrayList<PollChannelDto> pollChannelList = new ArrayList<>();
        for(MobileTerminalType terminalType : response.getMobileTerminal()) {
        	PollChannelDto terminal = PollMapper.mapPollChannel(terminalType);
        	pollChannelList.add(terminal);
        }
        channelListDto.setPollableChannels(pollChannelList);
        return channelListDto;
    }
}
