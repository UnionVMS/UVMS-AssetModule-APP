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

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalCapability;

public class CapabilitiesInmarsatC {

    private CapabilitiesInmarsatC () {}

    public static boolean getCapability(TerminalCapability capability) {
        if(capability != null) {
            switch(capability) {
                case SUPPORT_MULTIPLE_CHANNEL:
                    return true;
                case PLUGIN:
                    return true;
                case IS_CONFIGURABLE:
                    return true;
                case IS_POLLABLE:
                    return true;
                case SUPPORT_MULTIPLE_OCEAN:
                    return true;
                case SUPPORT_SAMPLING:
                    return true;
                case SUPPORT_SINGLE_OCEAN:
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }
}
