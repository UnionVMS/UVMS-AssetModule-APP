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

public enum FishingGearEnum {
	
	PS(1L), LA(2L), SB(3L), SDN(4L), SSC(5L), SPR(6L), TBB(7L), OTB(8L), PTB(9L),
	OTM(10L), PTM(11L), OTT(12L), DRB(13L), DRH(14L), HMD(15L), LNB(16L), LNS(17L),
	GNS(18L), GND(19L), GNC(20L), GTR(21L), GTN(22L), FPO(23L), LHP(24L), LHM(25L),
	LLS(26L), LLD(27L), LTL(28L), NK(29L), NO(30L);
	
	private Long id;

	private FishingGearEnum(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public FishingGearEnum getType(Long id) {
		if(id != null) {
			for(FishingGearEnum gear : FishingGearEnum.values()) {
				if(id.equals(gear.getId())) {
					return gear;
				}
			}
		}
		return null;
	}

}