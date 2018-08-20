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
package eu.europa.ec.fisheries.uvms.asset;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;

import java.time.OffsetDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;


public interface CustomCodesService {


    /**
     *
     * @param constant @description constants
     * @param code @description  the code to be valid for this constants
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate @description code is valid to this date inclusive
     * @param description @description human readable description of the code for dropdowns/prints etc
     * @return a CustomCode Object
     */
    CustomCode create(String constant, String code, OffsetDateTime validFromDate, OffsetDateTime validToDate, String description);

    CustomCode create(CustomCode customCode);


        /**
         *
         * @param constant @description constants
         * @param code @description  the code to be valid for this constants
         * @param validFromDate @description code is valid from this date inclusive
         * @param validToDate @description code is valid to this date inclusive
         * @return a CustomCodes object
         */
    CustomCode get(String constant, String code , OffsetDateTime validFromDate, OffsetDateTime validToDate);

    CustomCode get(CustomCodesPK customCodesPrimaryKey);



        /**
         *
         * @param constant @description constants
         * @param code @description  the code to be valid for this constants
         * @param validFromDate @description code is valid from this date inclusive
         * @param validToDate @description code is valid to this date inclusive
         * @return a boolean indicating exists or not  used for validation on incoming data
         */
    Boolean exists(String constant, String code, OffsetDateTime validFromDate, OffsetDateTime validToDate );


        /**
         *
         * @param constant @description constants
         * @param code @description  the code to be valid for this constants
         * @param validFromDate @description code is valid from this date inclusive
         * @param validToDate @description code is valid to this date inclusive
         * @param newValue @description  new description
         * @return a the updated CustomCodes Object
         */
    CustomCode update(String constant, String code,  OffsetDateTime validFromDate, OffsetDateTime validToDate, String newValue);

    /**
     *
     * @param constant @description constants
     * @param code @description  the code to be valid for this constants
     * @param validFromDate @description code is valid from this date inclusive
     * @param validToDate @description code is valid to this date inclusive
     */
    void delete(String constant, String code,  OffsetDateTime validFromDate, OffsetDateTime validToDate);


        /**
         *
         * @param constant @description constants
         * @return a list of CustomCode for a given constants
         */
    List<CustomCode> getAllFor(String constant);

    /**
     *
     * @param constant @description constants delete all codes for this constants
     */
    void deleteAllFor(String constant);

    /** Get all constants distinct
     *
     * @return
     */
    List<String> getAllConstants();


    List<CustomCode> getForDate(String constant, String code, OffsetDateTime aDate);

    Boolean verify(String constant, String code, OffsetDateTime aDate);


    /**
     *
     * @param customCode
     * @return
     */
    CustomCode replace(CustomCode customCode);



}



