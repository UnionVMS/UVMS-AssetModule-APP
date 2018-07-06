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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {
    private static Logger LOG = LoggerFactory.getLogger(DateUtils.class);
    private final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss Z";
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_PATTERN= "yyyy-MM-dd HH:mm:ss.SSS";
    
    public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
        if (date != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            try {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            } catch (DatatypeConfigurationException e) {
                LOG.error("[ Error when creating calendar instance. ] {} {}", e.getMessage(), e.getStackTrace());
            }
        }
        return null;
    }

    public static Date toDate(XMLGregorianCalendar cal) {
        if (cal != null) {
            return cal.toGregorianCalendar().getTime();
        }
        return null;
    }

    public static boolean isBetween(Date startDate, Date endDate, Date compareDate) {
        if (startDate == null || compareDate == null) {
            LOG.debug("Start date or compare date was null, returning false.");
            return false;
        }
        // Must compare time, because java.util.Date does not compare on
        // milliseconds
        if (compareDate.getTime() >= startDate.getTime()) {
            if (endDate == null || endDate.getTime() >= compareDate.getTime()) {
                return true;
            }
        }
        return false;
    }

    public static Date parseToUTCDateTime(String dateString) {
        return parseToUTC(DATE_TIME_FORMAT, dateString);
    }
    
    public static Date parseToUTCDate(String dateString) {
    	return parseToUTC(DATE_FORMAT, dateString);
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
    /* //Old code for the function above
    private static Date parseToUTC(String format, String dateString) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format).withOffsetParsed();
        DateTime dateTime = formatter.withZoneUTC().parseDateTime(dateString);
        GregorianCalendar cal = dateTime.toGregorianCalendar();
        return cal.getTime();
    }*/

    public static Date getUTCNow()
    {
        return new Date(System.currentTimeMillis());
    }

    public static String parseUTCDateTimeToString(Date date) {
    	return parseUTCToString(DATE_TIME_FORMAT, date);
    }

    public static String parseUTCDateToString(Date date) {
        return parseUTCToString(DATE_FORMAT, date);
    }
    
    private static String parseUTCToString(String format, Date date) {
        String dateString = null;
        if (date != null) {
            DateFormat df = new SimpleDateFormat(format);
            dateString = df.format(date);
        }
        return dateString;
    }

    // TODO FIX this
    public static boolean equalsDate(Date one, Date two) {
        throw new NotImplementedException();
    }

    public static XMLGregorianCalendar getXMLGregorianCalendarInUTC(Date dateTimeInUTC){
        if (dateTimeInUTC != null) {
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
            //SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
            //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                //Date theDate = sdf.parse(dateTimeInUTC.toString());
                calendar.setTime(dateTimeInUTC);
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            } catch (DatatypeConfigurationException e) {
                LOG.error("[ Error when getting XML Gregorian calendar. ] ", e);
            }
        }
        return null;
    }
}
