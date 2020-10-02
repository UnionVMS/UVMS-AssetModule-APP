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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;

public class PollDataSourceRequestMapper {

    public static PollRequestType mapCreatePollRequest(PollResponseType pollProgram) {
        PollRequestType poll = new PollRequestType();
        poll.setPollType(PollType.AUTOMATIC_POLL);
        poll.setComment(pollProgram.getComment());
        poll.setUserName("Program Poll, user: " + pollProgram.getUserName());

        poll.getAttributes().addAll(pollProgram.getAttributes());

        String connectId = pollProgram.getMobileTerminal().getConnectId();
        String mobileTerminalId = pollProgram.getMobileTerminal().getMobileTerminalId().getGuid();
        String channelId = pollProgram.getMobileTerminal().getChannels().get(0).getGuid();

        PollMobileTerminal pollMobileTerminal = new PollMobileTerminal();
        pollMobileTerminal.setComChannelId(channelId);
        pollMobileTerminal.setMobileTerminalId(mobileTerminalId);

        poll.getMobileTerminals().add(pollMobileTerminal);

        return poll;
    }
}
