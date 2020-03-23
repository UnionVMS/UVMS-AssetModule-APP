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

public class MobileTerminalConstants {
	public static final String POLL_FIND_ALL = "Poll.findAll";
	public static final String POLL_FIND_BY_ID = "Poll.findById";
	public static final String POLL_FIND_BY_TYPE = "Poll.findByPollType";
	public static final String POLL_FIND_BY_COMMENT = "Poll.findByPollComment";
	public static final String POLL_FIND_BY_CREATE_DATE = "Poll.findByPollCreated";
	public static final String POLL_FIND_BY_USER = "Poll.findByPollUserCreator";

	public static final String POLL_PROGRAM_FIND_BY_ID = "PollProgram.findById";
    public static final String POLL_PROGRAM_FIND_ALIVE = "PollProgram.findAlive";
    public static final String POLL_PROGRAM_FIND_RUNNING_AND_STARTED = "PollProgram.findRunningAndStarted";

	public static final String MOBILE_TERMINAL_FIND_ALL = "Mobileterminal.findAll";
	public static final String MOBILE_TERMINAL_FIND_BY_ID = "Mobileterminal.findById";
	public static final String MOBILE_TERMINAL_FIND_BY_SERIAL_NO = "Mobileterminal.findBySerialNo";
	public static final String MOBILE_TERMINAL_FIND_BY_UNASSIGNED = "Mobileterminal.findByUnassigned";
	public static final String MOBILE_TERMINAL_FIND_BY_ASSET_ID = "Mobileterminal.findByAssetId";
	public static final String MOBILE_TERMINAL_FIND_BY_DNID_AND_MEMBER_NR_AND_TYPE = "Mobileterminal.findByDnidAndMemberNumberAndType";

	public static final String CHANNEL_FIND_ACTIVE_DNID = "Channel.findByActiveDNID";

	public static final String PLUGIN_FIND_ALL = "Plugin.findAll";
	public static final String PLUGIN_FIND_BY_SERVICE_NAME = "Plugin.findByServiceName";
	
	public static final String DNID_LIST = "DNIDList.findAll";
	public static final String DNID_LIST_BY_PLUGIN = "DNIDList.findByPlugin";
	
	public static final String OCEAN_REGIONS = "OceanRegion.findAll";
	
    public static final String CREATE_COMMENT = "Automatic create comment";

    public static final String UPDATE_USER = "UVMS";

    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";

	public static final String CAPABILITY_CONFIGURABLE = "CONFIGURABLE";
	public static final String CAPABILITY_DEFAULT_REPORTING = "DEFAULT_REPORTING";
	public static final String CAPABILITY_POLLABLE = "POLLABLE";
}
