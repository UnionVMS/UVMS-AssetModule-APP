/*
 Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 © European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
 redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
 the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
 copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.mobileterminal.constants;

import eu.europa.ec.fisheries.uvms.mobileterminal.exception.ErrorCode;

public enum AttributeIridium {
    //ATTRIBUTE_VALUE,
    SERIAL_NUMBER;

    public static AttributeIridium getAttribute(String type) {
        for(AttributeIridium attr : AttributeIridium.values()) {
            if(attr.name().equalsIgnoreCase(type)) {
                return attr;
            }
        }
        throw new IllegalArgumentException(ErrorCode.MAPPING_ATTR_TYPE_ERROR.getMessage() + type);
    }
}
