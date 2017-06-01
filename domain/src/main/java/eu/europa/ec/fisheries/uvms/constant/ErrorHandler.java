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
package eu.europa.ec.fisheries.uvms.constant;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.ConfigModelException;
import eu.europa.ec.fisheries.uvms.dao.exception.*;
import eu.europa.ec.fisheries.uvms.asset.model.constants.FaultCode;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;

public class ErrorHandler {

	public static FaultCode getFaultCode(AssetModelException e) {
		if(e instanceof InputArgumentException) {
			return FaultCode.ASSET_DOMAIN_INPUT_ERROR;
		}
		if (e instanceof ConfigModelException) {
			return FaultCode.ASSET_DOMAIN_CONFIG_ERROR;
		}
		if(e instanceof AssetDaoMappingException) {
			return FaultCode.ASSET_DOMAIN_MAPPING_ERROR;
		}
		if(e instanceof AssetDaoException) {
			return FaultCode.ASSET_DOMAIN_DAO;
		}
		if(e instanceof AssetGroupDaoException) {
			return FaultCode.ASSET_DOMAIN_DAO_GROUP;
		}
		if(e instanceof AssetSearchMapperException) {
			return FaultCode.ASSET_DOMAIN_MAPPING_SEARCH;
		}
		return FaultCode.ASSET_DOMAIN;
	}

}