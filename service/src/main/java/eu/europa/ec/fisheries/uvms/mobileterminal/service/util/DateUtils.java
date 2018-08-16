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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
    private static Logger LOG = LoggerFactory.getLogger(DateUtils.class);
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN= "yyyy-MM-dd HH:mm:ss.SSS";
    

    public static Date parseToUTCDateTime(String dateString) {
        return parseToUTC(DATE_TIME_FORMAT, dateString);
    }


    // TODO FIX this, check if old code returns the same value as the new code
    private static Date parseToUTC(String format, String dateString)
    {
        try{
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.parse(dateString);
        }catch(ParseException e){
            LOG.error("Unable to parse a date from string " + dateString + " according to format " + format + " due to " + e.getMessage());
            return null;
        }
    }
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
        return OffsetDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

}
