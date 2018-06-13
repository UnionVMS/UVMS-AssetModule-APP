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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.uvms.config.constants.ConfigHelper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.ParameterKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.ServiceConstants;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MobileTerminalConfigHelper implements ConfigHelper {

    @PersistenceContext(unitName = ServiceConstants.MOBILE_TERMINAL_CONFIG_NAME)
    protected EntityManager em;

    @Override
    public List<String> getAllParameterKeys() {
        List<String> allParameterKeys = new ArrayList<String>();
        for (ParameterKey parameterKey : ParameterKey.values()) {
            allParameterKeys.add(parameterKey.getKey());
        }
        return allParameterKeys;
    }

    @Override
    public String getModuleName() {
        return ServiceConstants.MOBILE_TERMINAL_CONFIG_NAME;
    }
    
	@Override
	public EntityManager getEntityManager() {
		return em;
	}
}
