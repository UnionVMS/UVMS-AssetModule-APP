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


import eu.europa.ec.fisheries.asset.types.EventCode;

public enum EventCodeEnum {

	CEN(1L), CST(2L), CHA(3L), IMP(4L), MOD(5L), DES(6L), RET(7L), EXP(8L), UNK(9L);
	
	private Long id;
	
	private EventCodeEnum(Long id) {
		this.id = id;
	}
	
	public static EventCodeEnum getType(Long id) {
		if(id != null) {
			for(EventCodeEnum code : EventCodeEnum.values()) {
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

	public static EventCodeEnum getType(EventCode eventCode) {
		if(eventCode != null) {
			switch(eventCode) {
			case CEN:
				return EventCodeEnum.CEN;
			case CHA:
				return EventCodeEnum.CHA;
			case CST:
				return EventCodeEnum.CST;
			case DES:
				return EventCodeEnum.DES;
			case EXP:
				return EventCodeEnum.EXP;
			case IMP:
				return EventCodeEnum.IMP;
			case RET:
				return EventCodeEnum.RET;
			case UNK:
				return EventCodeEnum.UNK;
			case MOD:
			default:
				return EventCodeEnum.MOD;
			}
		}
		return EventCodeEnum.MOD;
	}
	
	public static EventCode getModel(EventCodeEnum eventCode) {
		if(eventCode != null) {
			switch(eventCode) {
			case CEN:
				return EventCode.CEN;
			case CHA:
				return EventCode.CHA;
			case CST:
				return EventCode.CST;
			case DES:
				return EventCode.DES;
			case EXP:
				return EventCode.EXP;
			case IMP:
				return EventCode.IMP;
			case RET:
				return EventCode.RET;
			case UNK:
				return EventCode.UNK;
			case MOD:
			default:
				return EventCode.MOD;
			}
		}
		return EventCode.MOD;
	}
}