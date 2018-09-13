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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.Capability;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.CapabilityConfiguration;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MTMobileTerminalConfig {

    private MTMobileTerminalConfig() {}

    public static List<MTMobileTerminalDeviceConfig> mapConfigTransponders(List<TerminalSystemType> list) {
        List<MTMobileTerminalDeviceConfig> transponders = new ArrayList<>();
      
        for (TerminalSystemType system : list) {

            MTMobileTerminalDeviceConfig transponder = new MTMobileTerminalDeviceConfig();
            transponder.setTerminalSystemType(system.getType());

            if (system.getTerminalConfiguration() != null) {
                transponder.setTerminalFieldList(system.getTerminalConfiguration().getAttribute());
            }
            if (system.getComchannelConfiguration() != null) {
                transponder.setChannelFieldList(system.getComchannelConfiguration().getAttribute());
            }

            if (system.getCapabilityConfiguration() != null) {
                CapabilityConfiguration configuration = system.getCapabilityConfiguration();
                if (configuration != null && configuration.getCapability() != null) {
                    List<MTCapabilityDto> capabilityList = new ArrayList<>();
                    for (Capability capability : configuration.getCapability()) {
                        MTCapabilityDto dto = new MTCapabilityDto();
                        dto.setName(capability.getName());
                        if (capability.getOptions() != null) {
                            dto.setOptionList(capability.getOptions());
                        }
                        capabilityList.add(dto);
                    }
                    transponder.setCapabilityList(capabilityList);
                }
            }
            transponders.add(transponder);
        }
        return transponders;
    }

    public static Map<String, List<String>> mapConfigList(List<ConfigList> config) {
        Map<String, List<String>> configValues = new HashMap<>();
        for (ConfigList list : config) {
            configValues.put(list.getName(), list.getValue());
        }
        return configValues;
    }
}
