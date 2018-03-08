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


public enum EventCodeEnum {

	CEN(1L), CST(2L), CHA(3L), IMP(4L), MOD(5L), DES(6L), RET(7L), EXP(8L), UNK(9L);
	
	private Long id;
	
	private EventCodeEnum(Long id) {
		this.id = id;
	}
	
	public static EventCodeEnum getType(Long id) {
		if(id != null) {
			for(EventCodeEnum code : eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.values()) {
				if(id.equals(code.id)) {
					return code;
				}
			}
		}
		return null;
	}

	public Long getId() {
		return id;
	}

	public static EventCodeEnum getType(eu.europa.ec.fisheries.asset.enums.EventCodeEnum eventCode) {
		if(eventCode != null) {
			switch(eventCode) {
			case CEN:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.CEN;
			case CHA:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.CHA;
			case CST:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.CST;
			case DES:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.DES;
			case EXP:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.EXP;
			case IMP:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.IMP;
			case RET:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.RET;
			case UNK:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.UNK;
			case MOD:
			default:
				return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.MOD;
			}
		}
		return eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum.MOD;
	}
	
	public static eu.europa.ec.fisheries.asset.enums.EventCodeEnum getModel(EventCodeEnum eventCode) {
		if(eventCode != null) {
			switch(eventCode) {
			case CEN:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.CEN;
			case CHA:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.CHA;
			case CST:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.CST;
			case DES:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.DES;
			case EXP:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.EXP;
			case IMP:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.IMP;
			case RET:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.RET;
			case UNK:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.UNK;
			case MOD:
			default:
				return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.MOD;
			}
		}
		return eu.europa.ec.fisheries.asset.enums.EventCodeEnum.MOD;
	}
}