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
package eu.europa.ec.fisheries.uvms.mobileterminal.exception;

public class MobileTerminalModelException extends Exception {
	private static final long serialVersionUID = 1L;

	private final int errorCode;

	public MobileTerminalModelException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public MobileTerminalModelException(String message, Throwable cause, int errorCode) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public MobileTerminalModelException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public MobileTerminalModelException(Throwable cause, int errorCode) {
		super(cause);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return this.errorCode;
	}
}
