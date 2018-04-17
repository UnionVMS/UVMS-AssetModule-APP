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

import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface CustomCodesService {


    /**
     *
     * @param constant @description constant
     * @param code @description  the code to be valid for this constant
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate @description code is valid to this date inclusive
     * @param description @description human readable description of the code for dropdowns/prints etc
     * @param nameValue @description free data complementing info for the code  could be a json . . .
     * @return a CustomCode Object
     */
    CustomCode create(String constant, String code, LocalDateTime validFromDate, LocalDateTime validToDate, String description, Map<String,String> nameValue);

    CustomCode create(CustomCode customCode);


        /**
         *
         * @param constant @description constant
         * @param code @description  the code to be valid for this constant
         * @param validFromDate @description code is valid from this date inclusive
         * @param validToDate @description code is valid to this date inclusive
         * @return a CustomCodes object
         */
    CustomCode get(String constant, String code , LocalDateTime validFromDate, LocalDateTime validToDate);

    CustomCode get(CustomCodesPK customCodesPrimaryKey);



        /**
         *
         * @param constant @description constant
         * @param code @description  the code to be valid for this constant
         * @param validFromDate @description code is valid from this date inclusive
         * @param validToDate @description code is valid to this date inclusive
         * @return a boolean indicating exists or not  used for validation on incoming data
         */
    Boolean exists(String constant, String code, LocalDateTime validFromDate, LocalDateTime validToDate );


        /**
         *
         * @param constant @description constant
         * @param code @description  the code to be valid for this constant
         * @param validFromDate @description code is valid from this date inclusive
         * @param validToDate @description code is valid to this date inclusive
         * @param newValue @description  new description
         * @param nameValue @description  new extradata
         * @return a the updated CustomCodes Object
         */
    CustomCode update(String constant, String code,  LocalDateTime validFromDate, LocalDateTime validToDate, String newValue, Map<String,String> nameValue);

    /**
     *
     * @param constant @description constant
     * @param code @description  the code to be valid for this constant
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate @description code is valid to this date inclusive
     */
    void delete(String constant, String code,  LocalDateTime validFromDate, LocalDateTime validToDate);


        /**
         *
         * @param constant @description constant
         * @return a list of CustomCode for a given constant
         */
    List<CustomCode> getAllFor(String constant);

    /**
     *
     * @param constant @description constant delete all codes for this constant
     */
    void deleteAllFor(String constant);

    /** Get all constants distinct
     *
     * @return
     */
    List<String> getAllConstants();


    List<CustomCode> getForDate(String constant, String code, LocalDateTime aDate);

    Boolean verify(String constant, String code, LocalDateTime aDate);
}



