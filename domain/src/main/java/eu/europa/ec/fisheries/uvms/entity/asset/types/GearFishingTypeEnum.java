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
package eu.europa.ec.fisheries.uvms.entity.asset.types;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelValidationException;

public enum GearFishingTypeEnum {
	PELAGIC(1), DERMERSAL(2), DEMERSAL_AND_PELAGIC(3), UNKNOWN(4);
	
	private int id;
	
	private GearFishingTypeEnum(int id) {
		this.id = id;
	}
	
	public static GearFishingTypeEnum getType(long id) {
		for(GearFishingTypeEnum type : GearFishingTypeEnum.values()) {
			if(id == type.getId()) {
				return type;
			}
		}
		return null;
	}
	
	public int getId() {
		return id;
	}
	
	public static GearFishingTypeEnum getType(String fishingType) throws AssetModelValidationException {
		if(fishingType != null && !fishingType.isEmpty()) {
			try {
				return GearFishingTypeEnum.valueOf(fishingType);
			} catch (IllegalArgumentException e) {
				throw new AssetModelValidationException("Invalid fishing gear type" ,e);
			}
		}
		//return GearFishingTypeEnum.UNKNOWN;
		return null;
	}

	public static String getValue(String fishingType) throws AssetModelValidationException {
		GearFishingTypeEnum typeEnum = getType(fishingType);
		return String.valueOf(typeEnum.getId());
	}
}