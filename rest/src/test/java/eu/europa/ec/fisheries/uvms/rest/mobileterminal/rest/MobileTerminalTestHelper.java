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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.constants.MobileTerminalConstants;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MobileTerminalTestHelper {

    private static String serialNumber;

    public static MobileTerminalType createBasicMobileTerminal() {
        MobileTerminalType mobileTerminal = new MobileTerminalType();
        mobileTerminal.setSource(MobileTerminalSource.INTERNAL);
        mobileTerminal.setType("INMARSAT_C");
        List<MobileTerminalAttribute> attributes = mobileTerminal.getAttributes();
        serialNumber = generateARandomStringWithMaxLength(10);
        addAttribute(attributes, MobileTerminalConstants.SERIAL_NUMBER, serialNumber);
        addAttribute(attributes, "SATELLITE_NUMBER", "S" + generateARandomStringWithMaxLength(4));
        addAttribute(attributes, "ANTENNA", "A");
        addAttribute(attributes, "TRANSCEIVER_TYPE", "A");
        addAttribute(attributes, "SOFTWARE_VERSION", "A");

        List<ComChannelType> channels = mobileTerminal.getChannels();
        ComChannelType comChannelType = new ComChannelType();
        channels.add(comChannelType);
        //comChannelType.setGuid(UUID.randomUUID().toString());
        comChannelType.setName("VMS");

        addChannelAttribute(comChannelType, "FREQUENCY_GRACE_PERIOD", "54000");
        addChannelAttribute(comChannelType, "MEMBER_NUMBER", "100");
        addChannelAttribute(comChannelType, "FREQUENCY_EXPECTED", "7200");
        addChannelAttribute(comChannelType, "FREQUENCY_IN_PORT", "10800");
        addChannelAttribute(comChannelType, "LES_DESCRIPTION", "Thrane&Thrane");
        addChannelAttribute(comChannelType, "DNID", "1" + generateARandomStringWithMaxLength(3));
        addChannelAttribute(comChannelType, "INSTALLED_BY", "Mike Great");

        addChannelCapability(comChannelType, "POLLABLE", true);
        addChannelCapability(comChannelType, "CONFIGURABLE", true);
        addChannelCapability(comChannelType, "DEFAULT_REPORTING", true);

        Plugin plugin = new Plugin();
        plugin.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        plugin.setLabelName("Thrane&Thrane");
        plugin.setSatelliteType("INMARSAT_C");
        plugin.setInactive(false);

        mobileTerminal.setPlugin(plugin);

        return mobileTerminal;
    }

    private static String generateARandomStringWithMaxLength(int len) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = new Random().nextInt(10);
            ret.append(String.valueOf(val));
        }
        return ret.toString();
    }

    private static void addChannelCapability(ComChannelType comChannelType, String type, boolean value) {
        ComChannelCapability channelCapability = new ComChannelCapability();

        channelCapability.setType(type);
        channelCapability.setValue(value);
        comChannelType.getCapabilities().add(channelCapability);
    }

    private static void addChannelAttribute(ComChannelType comChannelType, String type, String value) {
        ComChannelAttribute channelAttribute = new ComChannelAttribute();
        channelAttribute.setType(type);
        channelAttribute.setValue(value);
        comChannelType.getAttributes().add(channelAttribute);
    }

    private static void addAttribute(List<MobileTerminalAttribute> attributes, String type, String value) {
        MobileTerminalAttribute serialNumberMobileTerminalAttribute = new MobileTerminalAttribute();
        serialNumberMobileTerminalAttribute.setType(type);
        serialNumberMobileTerminalAttribute.setValue(value);
        attributes.add(serialNumberMobileTerminalAttribute);
    }

    public static MobileTerminalListQuery createMobileTerminalListQuery() {

        MobileTerminalListQuery query = new MobileTerminalListQuery();

        // ListPagination
        ListPagination pagination = new ListPagination();
        pagination.setListSize(1);
        pagination.setPage(1);

        // MobileTerminalSearchCriteria
        MobileTerminalSearchCriteria criteria = new MobileTerminalSearchCriteria();

        ListCriteria crt = new ListCriteria();
        SearchKey searchKey = SearchKey.SERIAL_NUMBER;
        crt.setKey(searchKey);
        crt.setValue(serialNumber);

        criteria.getCriterias().add(crt);

        query.setPagination(pagination);
        query.setMobileTerminalSearchCriteria(criteria);

        return query;
    }

    public static String getSerialNumber() {
        return serialNumber;
    }
}
