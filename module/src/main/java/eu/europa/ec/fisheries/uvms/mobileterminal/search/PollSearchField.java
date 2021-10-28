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
package eu.europa.ec.fisheries.uvms.mobileterminal.search;

import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;

import java.util.UUID;

public enum PollSearchField {
	CONNECT_ID(SearchTable.ASSET, "id", "connectionValue", UUID.class),
	TERMINAL_TYPE(SearchTable.TERMINAL, "mobileTerminalType", "mobileTerminalType", MobileTerminalTypeEnum.class),
	POLL_ID(SearchTable.POLL_BASE, "id", "id", UUID.class),
	CONFIGURATION_POLL_ID(SearchTable.CONFIGURATION_POLL, "id", "id", UUID.class),
	SAMPLING_POLL_ID(SearchTable.SAMPLING_POLL, "id", "id", UUID.class),
	PROGRAM_POLL_ID(SearchTable.PROGRAM_POLL, "id", "id", UUID.class),
	POLL_TYPE(SearchTable.POLL_BASE, "pollTypeEnum", "pollTypeEnum", PollTypeEnum.class),
	USER(SearchTable.POLL_BASE, "creator", "creator");

	
	private final SearchTable table;
    private final String sqlColumnName;
    private final String sqlReplaceToken;
    private final Class clazz;
    
    PollSearchField(SearchTable table, String sqlColumnName, String sqlReplaceToken) {
    	this.table = table;
        this.sqlColumnName = sqlColumnName;
        this.sqlReplaceToken = sqlReplaceToken;
        this.clazz = String.class;
    }
    
    PollSearchField(SearchTable table, String sqlColumnName, String sqlReplaceToken, Class clazz) {
    	this.table = table;
        this.sqlColumnName = sqlColumnName;
        this.sqlReplaceToken = sqlReplaceToken;
        this.clazz = clazz;
    }

    public SearchTable getTable() {
    	return table;
    }
    
    public String getSqlColumnName() {
        return sqlColumnName;
    }

    public String getSqlReplaceToken() {
        return sqlReplaceToken;
    }
    
    public Class getClazz() {
    	return clazz;
    }
}
