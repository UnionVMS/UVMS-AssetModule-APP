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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.constants;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.InmarsatCHistoryOceanRegion;
import org.apache.commons.collections.CollectionUtils;

import java.util.Objects;
import java.util.Set;

public class EqualsUtil {

	public static boolean compare(String one, String two) {
		return Objects.equals(one, two);
	}
	
//	public static boolean compare(Date one, Date two) {
//		return DateUtils.equalsDate(one, two);
//	}

	public static boolean compare(Boolean one, Boolean two) {
		return Objects.equals(one, two);
	}
	
	public static boolean compare(Integer one, Integer two) {
		return Objects.equals(one, two);
	}
	
	public static int getHashCode(Object obj) {
		return Objects.hashCode(obj);
	}

	public static boolean equalsOceanRegions(Set<InmarsatCHistoryOceanRegion> oceanRegionsOne, Set<InmarsatCHistoryOceanRegion> oceanRegionsTwo) {
	    boolean hasOceanRegionsOne = !CollectionUtils.isEmpty(oceanRegionsOne);
	    boolean hasOceanRegionsTwo = !CollectionUtils.isEmpty(oceanRegionsTwo);

	    if (!hasOceanRegionsOne && !hasOceanRegionsTwo) {
	        // Neither has ocean regions
	        return true;
	    }
	    else if (hasOceanRegionsOne ^ hasOceanRegionsTwo) {
	        // One has and the other hasn't
	        return false;
	    }
	    else {
	        // True iff both sets of regions are equal
	        return CollectionUtils.isEqualCollection(oceanRegionsOne, oceanRegionsTwo);
	    }
	}
}
