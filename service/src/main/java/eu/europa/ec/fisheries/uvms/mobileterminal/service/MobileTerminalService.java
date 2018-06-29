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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.DataSourceQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

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
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType createMobileTerminal(MobileTerminalType mobileTerminal, MobileTerminalSource source, String username) throws MobileTerminalModelException;

    /**
     * Get a list of mobile terminals defined by query
     *
     * @param query
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalListResponse getMobileTerminalList(MobileTerminalListQuery query) throws MobileTerminalModelException;

    /**
     * Get a mobile terminal by guid
     *
     * @param guid
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType getMobileTerminalById(String guid) throws MobileTerminalModelException;

    /**
     * Get a mobile terminal by mobile terminal id type
     *
     * @param id
     * @param queue
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType getMobileTerminalById(MobileTerminalId id, DataSourceQueue queue) throws MobileTerminalModelException;

    /**
     *
     * Updates mobile terminal if it exists and creates a new mobile terminal if
     * it does not exist
     *
     * @param data
     * @param source
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType upsertMobileTerminal(MobileTerminalType data, MobileTerminalSource source, String username) throws MobileTerminalModelException;

    /**
     * Update mobile terminal
     *
     * @param data
     * @param comment
     * @param source
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType updateMobileTerminal(MobileTerminalType data, String comment, MobileTerminalSource source, String username)
            throws MobileTerminalModelException;

    /**
     * Assigns the selected mobile terminal from the selected carrier
     *
     * @param query
     * @param comment
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType assignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalModelException;

    /**
     * Unassigns the selected mobile terminal from the selected carrier
     *
     * @param query
     * @param comment
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType unAssignMobileTerminal(MobileTerminalAssignQuery query, String comment, String username) throws MobileTerminalModelException;

    /**
     * Set status of a mobile terminal
     *
     * @param terminalId
     * @param comment
     * @param status
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, String comment, MobileTerminalStatus status, String username)
            throws MobileTerminalModelException;

    /**
     * Get mobile terminal history list for one mobile terminal
     *
     * @param guid
     * @return
     */
    MobileTerminalHistory getMobileTerminalHistoryList(String guid) throws MobileTerminalModelException;

    /**
     * Get pollable mobile terminals matching query
     *
     * @param query
     * @return
     * @throws MobileTerminalServiceException
     */
    MobileTerminalListResponse getPollableMobileTerminal(PollableQuery query) throws MobileTerminalModelException;
}
