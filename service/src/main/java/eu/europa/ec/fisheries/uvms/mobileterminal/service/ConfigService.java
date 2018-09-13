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

import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ConfigService {

    /**
     * Get all defined terminal system transponders
     *
     * @return
     */
    List<TerminalSystemType> getTerminalSystems();
	
	/**
	 * Get configuration
	 * 
	 * @return
	 */
	List<ConfigList> getConfig();
	
	/**
	 * 
	 * @param pluginList
	 * @return
	 */
	List<Plugin> upsertPlugins(List<PluginService> pluginList, String username);
	
	/**
	 * Get plugins (from exchange) matching MobileTerminal plugins
	 * @return
	 */
	List<ServiceResponseType> getRegisteredMobileTerminalPlugins();
}
