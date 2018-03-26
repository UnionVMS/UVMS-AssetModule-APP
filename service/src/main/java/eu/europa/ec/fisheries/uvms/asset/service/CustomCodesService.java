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
package eu.europa.ec.fisheries.uvms.asset.service;

import eu.europa.ec.fisheries.uvms.entity.CustomCodes;

import javax.ejb.Local;
import java.util.List;


@Local
public interface CustomCodesService {

    /**
     *
     * @param constant @description constant
     * @param code @description  the code to be valid for this constant
     * @param description @description human readable description of the code for dropdowns/prints etc
     * @param extradata @description free data complementing info for the code  could be a json . . .
     * @return a CustomCode Object
     */
    CustomCodes create(String constant, String code, String description, String extradata);

    /**
     *
     * @param constant @description constant
     * @param code @description  the code to be valid for this constant
     * @return a CustomCodes object
     */
    CustomCodes get(String constant, String code );

    /**
     *
     * @param constant @description constant
     * @param code @description  the code to be valid for this constant
     * @return a boolean indicating exists or not  used for validation on incoming data
     */
    Boolean exists(String constant, String code);

    /**
     *
     * @param constant @description constant
     * @param code @description  the code to be valid for this constant
     * @param newValue @description  new description
     * @param newExtraData @description  new extradata
     * @return a the updated CustomCodes Object
     */
    CustomCodes update(String constant, String code, String newValue, String newExtraData);

    /**
     *
     * @param constant @description constant
     * @param code @description  the code to be valid for this constant
     */
    void delete(String constant, String code);

    /**
     *
     * @param constant @description constant
     * @return a list of CustomCode for a given constant
     */
    List<CustomCodes> getAllFor(String constant);

    /**
     *
     * @param constant @description constant delete all codes for this constant
     */
    void deleteAllFor(String constant);

}



