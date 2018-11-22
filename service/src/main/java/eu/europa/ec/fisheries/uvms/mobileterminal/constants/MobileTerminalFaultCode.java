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
package eu.europa.ec.fisheries.uvms.mobileterminal.constants;

public enum MobileTerminalFaultCode {

	DOMAIN_ERROR(2800),
	DOMAIN_MESSAGE_ERROR(2802),
	DOMAIN_INPUT_ERROR(2803),
	DOMAIN_MAPPING_ERROR(2804),
	DOMAIN_DAO_ERROR(2805),
	DOMAIN_TERMINAL_EXISTS_ERROR(2806);
	
	private int code;
	
	MobileTerminalFaultCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
