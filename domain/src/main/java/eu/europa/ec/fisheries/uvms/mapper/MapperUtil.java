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
package eu.europa.ec.fisheries.uvms.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.europa.ec.fisheries.wsdl.asset.types.AssetContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import java.math.BigDecimal;
import java.util.TimeZone;

public class MapperUtil {
	private static final Logger LOG = LoggerFactory.getLogger(MapperUtil.class);
    private static final String DATE_TIME_PATTERN= "yyyy-MM-dd HH:mm:ss.SSS";
	
    private static boolean equals(Object one, Object two) {
        if (one == null && two == null) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }
    
    private static boolean equals(BigDecimal one, BigDecimal two) {
        if (one == null && two == null) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return one.compareTo(two) == 0;
    }

    public static boolean vesselEquals(Asset newAsset, Asset originalAsset) {
        if (newAsset != null && originalAsset != null) {
            if (newAsset.isActive() != originalAsset.isActive()) {
                return false;
            }
            if (!equals(newAsset.getHasIrcs(), originalAsset.getHasIrcs())) {
                return false;
            }
            if (newAsset.isHasLicense() != originalAsset.isHasLicense()) {
                return false;
            }
            if (!equals(newAsset.getCfr(), originalAsset.getCfr())) {
                return false;
            }
            if (!equals(newAsset.getExternalMarking(), originalAsset.getExternalMarking())) {
                return false;
            }
            if (!equals(newAsset.getGrossTonnage(), originalAsset.getGrossTonnage())) {
                return false;
            }
            if (!equals(newAsset.getHomePort(), originalAsset.getHomePort())) {
                return false;
            }
            if (!equals(newAsset.getImo(), originalAsset.getImo())) {
                return false;
            }
            if (!equals(newAsset.getIrcs(), originalAsset.getIrcs())) {
                return false;
            }
            if (!equals(newAsset.getLengthBetweenPerpendiculars(), originalAsset.getLengthBetweenPerpendiculars())) {
                return false;
            }
            if (!equals(newAsset.getLengthOverAll(), originalAsset.getLengthOverAll())) {
                return false;
            }
            if (!equals(newAsset.getMmsiNo(), originalAsset.getMmsiNo())) {
                return false;
            }
            if (!equals(newAsset.getName(), originalAsset.getName())) {
                return false;
            }
            if (!equals(newAsset.getOtherGrossTonnage(), originalAsset.getOtherGrossTonnage())) {
                return false;
            }
            if (!equals(newAsset.getPowerAux(), originalAsset.getPowerAux())) {
                return false;
            }
            if (!equals(newAsset.getPowerMain(), originalAsset.getPowerMain())) {
                return false;
            }
            if (!equals(newAsset.getSafetyGrossTonnage(), originalAsset.getSafetyGrossTonnage())) {
                return false;
            }
            if (!equals(newAsset.getSource(), originalAsset.getSource())) {
                return false;
            }
            if (!(newAsset.getAssetId() == null && originalAsset.getAssetId() == null)) {
                if (newAsset.getAssetId() == null || originalAsset.getAssetId() == null) {
                    return false;
                }
            }
            if(newAsset.getGearType() != null && originalAsset.getGearType() != null) {
            	if(!equals(newAsset.getGearType(), originalAsset.getGearType())) {
            		return false;
            	}
            }
            if(newAsset.getLicenseType() != null && originalAsset.getLicenseType() != null) {
            	if(!equals(newAsset.getLicenseType(), originalAsset.getLicenseType())) {
            		return false;
            	}
            }
            if(newAsset.getGrossTonnageUnit() != null && originalAsset.getGrossTonnageUnit() != null) {
            	if(!equals(newAsset.getGrossTonnageUnit(), originalAsset.getGrossTonnageUnit())) {
                	return false;
                }
            }

            if(newAsset.getProducer()!=null && originalAsset.getProducer()!=null){
                if((!equals(newAsset.getProducer().getId(), originalAsset.getProducer().getId()))) {
                    return false;
                }
            }

            if(newAsset.getContact()!=null && originalAsset.getContact()!=null){
                if (newAsset.getContact().size() != originalAsset.getContact().size()) {
                    return false;
                }
                for (AssetContact originalContact : originalAsset.getContact()) {
                    boolean inNew = false;
                    for (AssetContact newContact : newAsset.getContact()) {
                        if (equals(newContact.getName(), originalContact.getName()) &&
                                equals(newContact.getNumber(), originalContact.getNumber()) &&
                                equals(newContact.getEmail(), originalContact.getEmail()) &&
                                equals(newContact.getNationality(), originalContact.getNationality()) &&
                                equals(newContact.getType(), originalContact.getType())) {
                            inNew = true;
                        }
                    }
                    if (!inNew) {
                        return false;
                    }
                }
            }

            if(!equals(newAsset.getNotes().size(), originalAsset.getNotes().size())) {
            	return false;
            }
            if(!equals(newAsset.getCountryCode(), originalAsset.getCountryCode())){
                return false;
            }

            if (!equals(newAsset.isAisIndicator(), originalAsset.isAisIndicator())) {
                return false;
            }

            if (!equals(newAsset.isErsIndicator(), originalAsset.isErsIndicator())) {
                return false;
            }

            if (!equals(newAsset.getVesselType(), originalAsset.getVesselType())) {
                return false;
            }

            if (!equals(newAsset.getVesselDateOfEntry(), originalAsset.getVesselDateOfEntry())) {
                return false;
            }


            if (!equals(newAsset.getHullMaterial(), originalAsset.getHullMaterial())) {
                return false;
            }

            if (!equals(newAsset.getYearOfConstruction(), originalAsset.getYearOfConstruction())) {
                return false;
            }

            return true;
        }
        return false;
    }

    public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
        if (date != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(date.getTime());
            try {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            } catch (DatatypeConfigurationException e) {
                LOG.error("[ Error when getting XML Gregorian calendar. ] ", e);
            }
        }
        return null;
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