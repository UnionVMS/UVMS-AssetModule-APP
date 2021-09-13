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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.validator;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobileTerminalDataSourceRequestValidator {

    private MobileTerminalDataSourceRequestValidator() {}

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalDataSourceRequestValidator.class);
    private static final String IRIDIUM = "IRIDIUM";

    public static void validateCreateMobileTerminalType(MobileTerminalType mobTermType) {
        if(mobTermType.isInactive()){
            throw new RuntimeException("Cannot create a Mobile Terminal with status set to inactive");
        }
        validateMobileTerminalAttributes(mobTermType.getAttributes());
        if(!IRIDIUM.equalsIgnoreCase(mobTermType.getType())) {
            validateComChannels(mobTermType);
        }
    }
	
    public static void validateMobileTerminalType(MobileTerminalType mobTermType) {
        validateMobileTerminalId(mobTermType.getMobileTerminalId());
        validateMobileTerminalAttributes(mobTermType.getAttributes());
        if(!IRIDIUM.equalsIgnoreCase(mobTermType.getType())) {
            validateComChannels(mobTermType);
        }
    }

    public static void validateMobileTerminalId(MobileTerminalId id) {
    	if(id == null || id.getGuid() == null || id.getGuid().isEmpty()) {
    		throw new NullPointerException("Non valid mobile terminal id");
    	}
    }

    public static void validateMobileTerminalAttributes(List<MobileTerminalAttribute> attributes) {
        Set<String> uniqueFields = new HashSet<>();
        for (MobileTerminalAttribute attr : attributes) {
        	if(!"MULTIPLE_OCEAN".equalsIgnoreCase(attr.getType())) {
        		if (!uniqueFields.add(attr.getType())) {
                    throw new IllegalArgumentException("Non unique attribute field " + attr.getType());
                }
        	}
        }
    }

    public static void validateComChannels(MobileTerminalType type) {
    	//TODO terminaltype -> comchannelvaluetype instead of channeltype when validate
        for (ComChannelType channel : type.getChannels()) {
        	if("VMS".equalsIgnoreCase(channel.getName())) {
        		validateVMS(channel);
        	}
        	else {
        	    LOG.debug("Channel name is not VMS. Will not validate further, and will probably fail validation in the future.");
        	}
        	//	throw new MobileTerminalModelValidationException("ComChannel with SystemType " + type.getType() + " validation not implemented");
        }
    }

    private static void validateVMS(ComChannelType channel) {
        Set<String> fields = new HashSet<>();

        for (ComChannelAttribute attribute : channel.getAttributes()) {
            fields.add(attribute.getType());
        }

        boolean dnid = fields.contains("DNID");
        boolean memberId = fields.contains("MEMBER_NUMBER");

        if (!dnid || !memberId) {
            throw new IllegalArgumentException("A Com channel with channelType " + channel.getName() + " must contain DNID and Member Number");
        }

        if (fields.size() != channel.getAttributes().size()) {
            throw new IllegalArgumentException("ChannelType ids can only occur once!");
        }
    }
}
