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

public enum SegmentFUP {
	MFL(1), KOM_FUP_IV_FRA(2), KOM_FPU_IV_POR(3), KOM_CAN1_CANN_ESP(4), AQU(5), CA2(6), CA3(7);

	private Long id;
	
	private SegmentFUP(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public SegmentFUP getType(Long id) {
		if(id != null) {
			for(SegmentFUP segment : SegmentFUP.values()) {
				if(id.equals(segment.getId())) {
					return segment;
				}
			}
		}
		return null;
	}

}