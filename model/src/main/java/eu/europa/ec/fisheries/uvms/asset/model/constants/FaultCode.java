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
package eu.europa.ec.fisheries.uvms.asset.model.constants;

public enum FaultCode {
	ASSET_DOMAIN(1800),
    ASSET_DOMAIN_MODEL(1810),
    ASSET_DOMAIN_MODEL_GROUP(1811),
    ASSET_DOMAIN_MESSAGE(1820),
    ASSET_DOMAIN_MAPPING_ERROR(1830),
    ASSET_DOMAIN_MAPPING_SEARCH(1831),
    ASSET_DOMAIN_CONFIG_ERROR(1840),
    ASSET_DOMAIN_DAO(1840),
    ASSET_DOMAIN_DAO_NO_ENTITY_FOUND(1841),
    ASSET_DOMAIN_DAO_GROUP(1842),
    ASSET_MESSAGE(1700),
    ASSET_DOMAIN_INPUT_ERROR(1801);
	
	//VesselModelException (parameterDao)
	
	private final int code;
	
	private FaultCode(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}