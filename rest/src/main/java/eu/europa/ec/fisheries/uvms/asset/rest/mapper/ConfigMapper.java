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
package eu.europa.ec.fisheries.uvms.asset.rest.mapper;


import eu.europa.ec.fisheries.asset.types.Config;
import eu.europa.ec.fisheries.asset.types.ConfigValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConfigMapper {

	public static Map<String, List> mapConfiguration(List<Config> configuration) {
		Map<String, List> config = new HashMap<>();
		if(configuration != null) {
			for(Config conf : configuration) {
				List retValues = new ArrayList();
				List<ConfigValue> values = conf.getValues();
				for(ConfigValue value : values) {
					if(value.getValues() != null && !value.getValues().isEmpty()) {
						Map<String, List<String>> tmp = new HashMap<>();
						tmp.put(value.getKeyValue(), value.getValues());
						retValues.add(tmp);
					} else {
						retValues.add(value.getKeyValue());
					}
				}
				config.put(conf.getField().name(), retValues);
			}
		}
		return config;
	}

}