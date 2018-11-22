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


import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;

public enum SearchField {
    CONNECT_ID("connectValue", "connectionValue"),
    TERMINAL_TYPE("mobileTerminalType", "mobileTerminalType", MobileTerminalTypeEnum.class),
    SERIAL_NUMBER("serialNumber", "serialNumber"),
	MEMBER_NUMBER("memberNumber", "memberNumber"),
	DNID("dnid", "dnid"),
	SATELLITE_NUMBER("satelliteNumber", "satelliteNumber");

    private final String sqlColumnName;
    private final String sqlReplaceToken;
    private final Class clazz;
    
    SearchField(String sqlColumnName, String sqlReplaceToken) {
        this.sqlColumnName = sqlColumnName;
        this.sqlReplaceToken = sqlReplaceToken;
        this.clazz = String.class;
    }
    
    SearchField(String sqlColumnName, String sqlReplaceToken, Class clazz) {
        this.sqlColumnName = sqlColumnName;
        this.sqlReplaceToken = sqlReplaceToken;
        this.clazz = clazz;
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
