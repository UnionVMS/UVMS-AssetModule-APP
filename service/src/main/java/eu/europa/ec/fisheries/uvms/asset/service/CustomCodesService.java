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
     * @param constant
     * @param code
     * @param description
     * @param extradata
     * @return
     */
    CustomCodes create(String constant, String code, String description, String extradata);

    /**
     *
     * @param constant
     * @param code
     * @return
     */
    CustomCodes get(String constant, String code );

    /**
     *
     * @param constant
     * @param code
     * @return
     */
    Boolean exists(String constant, String code);

    /**
     *
     * @param constant
     * @param code
     * @param newValue
     * @param newExtraData
     * @return
     */
    CustomCodes update(String constant, String code, String newValue, String newExtraData);

    /**
     *
     * @param constant
     * @param code
     */
    void delete(String constant, String code);

    /**
     *
     * @param constant
     * @return
     */
    List<CustomCodes> getAllFor(String constant);

    /**
     *
     * @param constant
     */
    void deleteAllFor(String constant);

}



