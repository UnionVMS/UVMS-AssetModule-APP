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
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

import javax.ejb.Local;
import java.util.List;

@Local
public interface PollService {

    /**
     *
     * Creates a Poll
     *
     * @param poll
     * @return
     * @throws MobileTerminalServiceException
     */
    CreatePollResultDto createPoll(PollRequestType poll, String username) throws MobileTerminalServiceException;

    /**
     *
     * @return @throws MobileTerminalServiceException
     */
    List<PollResponseType> getRunningProgramPolls() throws MobileTerminalServiceException;

    /**
     *
     * @param pollId
     * @return
     * @throws MobileTerminalServiceException
     */
    PollResponseType startProgramPoll(String pollId, String username) throws MobileTerminalServiceException;

    /**
     *
     * @param pollId
     * @return
     * @throws MobileTerminalServiceException
     */
    PollResponseType stopProgramPoll(String pollId, String username) throws MobileTerminalServiceException;

    /**
     *
     * @param pollId
     * @return
     * @throws MobileTerminalServiceException
     */
    PollResponseType inactivateProgramPoll(String pollId, String username) throws MobileTerminalServiceException;

    /**
     *
     * @param query
     * @return
     * @throws MobileTerminalServiceException
     */
    PollListResponse getPollBySearchCriteria(PollListQuery query) throws MobileTerminalServiceException;

    /**
     *
     * @return List of poll programs
     * @throws MobileTerminalServiceException
     */
    List<PollResponseType> timer() throws MobileTerminalException;
}
