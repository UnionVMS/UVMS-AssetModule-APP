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
package eu.europa.ec.fisheries.uvms.mobileterminal.service;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollDto;

import javax.ejb.Local;
import java.util.List;

@Local
public interface MappedPollService {

    /**
     * Create poll
     * 
     * @param pollRequest
     * @return
     * @throws
     */
    CreatePollResultDto createPoll(PollRequestType pollRequest, String username);

    /**
     * Get running program polls
     * 
     * @return
     * @throws
     */
    List<PollDto> getRunningProgramPolls();

    /**
     * Start program poll
     * 
     * @param pollId
     * @return
     * @throws
     */
    PollDto startProgramPoll(String pollId, String username);

    /**
     * Stop program poll
     * 
     * @param pollId
     * @return
     * @throws
     */
    PollDto stopProgramPoll(String pollId, String username);

    /**
     * Inactivate program poll
     * 
     * @param pollId
     * @return
     * @throws
     */
    PollDto inactivateProgramPoll(String pollId, String username);

    /**
     * Get poll by search criteria
     * 
     * @param query
     * @return
     * @throws
     */
    PollChannelListDto getPollBySearchQuery(PollListQuery query);

    /**
     * Get pollable channels
     * 
     * @param query
     * 
     * @return
     * @throws
     */
    PollChannelListDto getPollableChannels(PollableQuery query) throws MobileTerminalModelException;
}
