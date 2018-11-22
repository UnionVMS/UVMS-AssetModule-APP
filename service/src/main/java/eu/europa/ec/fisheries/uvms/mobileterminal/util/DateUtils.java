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
package eu.europa.ec.fisheries.uvms.mobileterminal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class DateUtils {
    private static Logger LOG = LoggerFactory.getLogger(DateUtils.class);
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN= "yyyy-MM-dd HH:mm:ss.SSS";

    public static String parseOffsetDateTimeToString(OffsetDateTime dateTime){
        if(dateTime == null){
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    //maybe check for and return null?
    public static OffsetDateTime parseStringToOffsetDateTime(String dateString){
        if(dateString == null || dateString.isEmpty()){
            return null;
        }
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)).toOffsetDateTime();
    }

}
