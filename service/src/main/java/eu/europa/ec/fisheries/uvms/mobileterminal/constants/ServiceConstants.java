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

public class ServiceConstants {

	public static final String FIND_BY_NAME = "Parameter.findByName";

    /* The name used to represent the Mobile terminal module in the Config module. */
    public static final String MOBILE_TERMINAL_CONFIG_NAME = "mobileTerminal";
    public static final String DB_ACCESS_POLL_DOMAIN_MODEL = "java:global/mobileterminal-dbaccess-module/mobileterminal-dbaccess-domain/PollDomainModelBean!eu.europa.ec.fisheries.uvms.mobileterminal.PollDomainModel";
    public static final String DB_ACCESS_CONFIG_MODEL = "java:global/mobileterminal-dbaccess-module/mobileterminal-dbaccess-domain/ConfigModelBean!eu.europa.ec.fisheries.uvms.mobileterminal.ConfigModel";
    public static final String DB_ACCESS_MOBILE_TERMINAL_DOMAIN_MODEL = "java:global/mobileterminal-dbaccess-module/mobileterminal-dbaccess-domain/MobileTerminalDomainModelBean!eu.europa.ec.fisheries.uvms.mobileterminal.MobileTerminalDomainModel";
}
