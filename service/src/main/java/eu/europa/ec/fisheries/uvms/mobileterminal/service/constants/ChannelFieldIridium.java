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

package eu.europa.ec.fisheries.uvms.mobileterminal.service.constants;

import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.MobileTerminalServiceException;

import static eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.ErrorCode.MAPPING_ATTR_TYPE_ERROR;

public enum ChannelFieldIridium {
    END_DATE,
    START_DATE,
    FREQUENCY_IN_PORT,
    FREQUENCY_GRACE_PERIOD,
    FREQUENCY_EXPECTED,
    UNINSTALLED_ON,
    INSTALLED_ON,
    INSTALLED_BY;

    public static ChannelFieldIridium getAttribute(String type) throws MobileTerminalServiceException {
        for(ChannelFieldIridium attr : ChannelFieldIridium.values()) {
            if(attr.name().equalsIgnoreCase(type)) {
                return attr;
            }
        }
        throw new MobileTerminalServiceException(MAPPING_ATTR_TYPE_ERROR.getMessage() + type, MAPPING_ATTR_TYPE_ERROR.getCode());
    }
}
