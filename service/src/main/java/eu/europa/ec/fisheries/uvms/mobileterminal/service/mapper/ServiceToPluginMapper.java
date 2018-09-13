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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;

import java.util.ArrayList;
import java.util.List;

public class ServiceToPluginMapper {

	public static List<PluginService> mapToPluginList(List<ServiceResponseType> serviceList) {
		List<PluginService> pluginList = new ArrayList<>();
		for(ServiceResponseType service : serviceList) {
			PluginService plugin = new PluginService();
			plugin.setInactive(!service.isActive()); 
			plugin.setLabelName(service.getName());
			plugin.setServiceName(service.getServiceClassName());
			plugin.setSatelliteType(service.getSatelliteType());
			if(service.getCapabilityList() != null) {
				plugin.getCapability().addAll(mapToPluginCapabilityList(service.getCapabilityList().getCapability()));
			}
			pluginList.add(plugin);
		}
		return pluginList;
	}
	
	private static List<PluginCapability> mapToPluginCapabilityList(List<CapabilityType> capabilities) {
		List<PluginCapability> capabilityList = new ArrayList<>();
		if(capabilities != null) {
			for(CapabilityType capability : capabilities) {
				PluginCapability pluginCapability = new PluginCapability();
				pluginCapability.setName(PluginCapabilityType.fromValue(capability.getType().name()));
				pluginCapability.setValue(capability.getValue());
				capabilityList.add(pluginCapability);
			}
		}
		return capabilityList;
	}
}
