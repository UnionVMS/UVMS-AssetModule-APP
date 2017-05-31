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

public enum FishingGearTypeEnum {
	TRAWL(1), SCRAPING_TOOL(2), LIFT_NETS(3), GILLNETS(4), TRAP(5), HOOKS_AND_LINES(6), UNKNOWN(7), NONE(8), SURROUNDING(9), SEINES(10);
	
	private long id;
	
	private FishingGearTypeEnum(long id) {
		this.id = id;
	}
	
	public static FishingGearTypeEnum getType(long id) {
		for(FishingGearTypeEnum gearType : FishingGearTypeEnum.values()) {
			if(id == gearType.getId()) {
				return gearType;
			}
		}
		return null;
	}
	
	public long getId() {
		return id;
	}
}