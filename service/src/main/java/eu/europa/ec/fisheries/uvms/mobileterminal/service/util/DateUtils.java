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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static Date parseToUTC(String format, String dateString) {
    	DateTimeFormatter formatter = DateTimeFormat.forPattern(format).withOffsetParsed();
    	DateTime dateTime = formatter.withZoneUTC().parseDateTime(dateString);
    	GregorianCalendar cal = dateTime.toGregorianCalendar();
    	return cal.getTime();
    }
    
    public static Date getUTCNow() {
        return new DateTime(DateTimeZone.UTC).toDate();
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
    
    public static boolean equalsDate(Date one, Date two) {
    	if(one == null && two == null) return true;
		if(one == null || two == null) return false;
		DateTime dateTimeOne = new DateTime(one);
		DateTime dateTimeTwo = new DateTime(two);
		return dateTimeOne.withTimeAtStartOfDay().isEqual(dateTimeTwo.withTimeAtStartOfDay());
    }

    public static XMLGregorianCalendar getXMLGregorianCalendarInUTC(Date dateTimeInUTC){
        if (dateTimeInUTC != null) {
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date theDate = sdf.parse(dateTimeInUTC.toString());
                calendar.setTime(theDate);
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            } catch (DatatypeConfigurationException e) {
                LOG.error("[ Error when getting XML Gregorian calendar. ] ", e);
            } catch (ParseException e) {
                LOG.error("Could not parse dateTimeInUTC: "+dateTimeInUTC.toString()+ " with pattern: " + DATE_TIME_PATTERN);
            }
        }
        return null;
    }
}
