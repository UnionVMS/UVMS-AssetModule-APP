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
package eu.europa.ec.fisheries.uvms.mobileterminal;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;

import javax.ejb.Local;

@Local
public interface MobileTerminalService {

    /**
     * Create mobile terminal
     *
     * @param mobileTerminal
     * @param source
     * @param username
     * @return
     */
    MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, MobileTerminalSource source, String username);

    /**
     * Get a list of mobile terminals defined by query
     *
     * @param query
     * @return
     */
    MobileTerminalListResponse getMobileTerminalList(MobileTerminalListQuery query);

    /**
     * Get a mobile terminal by guid
     *
     * @param guid
     * @return
     */
    MobileTerminalType getMobileTerminalById(String guid);

    /**
     * Get a mobile terminal by mobile terminal id type
     *
     * @param id
     * @param queue
     * @return
     */
    MobileTerminalType getMobileTerminalById(MobileTerminalId id, AssetDataSourceQueue queue);

    /**
     *
     * Updates mobile terminal if it exists and creates a new mobile terminal if
     * it does not exist
     *
     * @param data
     * @param source
     * @return
     */
    MobileTerminalType upsertMobileTerminal(MobileTerminalType data, MobileTerminalSource source, String username);

    /**
     * Update mobile terminal
     *
     * @param data
     * @param comment
     * @param source
     * @return
     */
    MobileTerminalType updateMobileTerminal(MobileTerminalType data, String comment, MobileTerminalSource source, String username);

    /**
     * Assigns the selected mobile terminal from the selected carrier
     *
     * @param query
     * @param comment
     * @return
     */
    MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username);

    /**
     * Unassigns the selected mobile terminal from the selected carrier
     *
     * @param query
     * @param comment
     * @return
     */
    MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username);

    /**
     * Set status of a mobile terminal
     *
     * @param terminalId
     * @param comment
     * @param status
     * @return
     */
    MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username);

    /**
     * Get mobile terminal history list for one mobile terminal
     *
     * @param guid
     * @return
     */
    MobileTerminalHistory getMobileTerminalHistoryList(String guid);

    /**
     * Get pollable mobile terminals matching query
     *
     * @param query
     * @return
     */
    MobileTerminalListResponse getPollableMobileTerminal(PollableQuery query);
}
