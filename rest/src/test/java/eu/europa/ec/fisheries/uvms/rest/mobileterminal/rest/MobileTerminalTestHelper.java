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

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalAttributes;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalSourceEnum;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.Random;
import java.util.Set;

public class MobileTerminalTestHelper {

    private static String serialNumber;

    public static MobileTerminal createBasicMobileTerminal() {
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        Set<MobileTerminalAttributes> attributes = mobileTerminal.getMobileTerminalAttributes();
        serialNumber = generateARandomStringWithMaxLength(10);
        mobileTerminal.setSerialNo(serialNumber);

        addAttribute(attributes, MobileTerminalConstants.SERIAL_NUMBER, serialNumber, mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.SATELLITE_NUMBER, "S" + generateARandomStringWithMaxLength(4), mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.ANTENNA, "A", mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.TRANSCEIVER_TYPE, "A", mobileTerminal);
        addAttribute(attributes, MobileTerminalConstants.SOFTWARE_VERSION, "A", mobileTerminal);

        Channel channel = new Channel();
        channel.setName("VMS");
        channel.setFrequencyGracePeriod(Duration.ofSeconds(54000));
        channel.setMemberNumber(generateARandomStringWithMaxLength(3));
        channel.setExpectedFrequency(Duration.ofSeconds(7200));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(10800));
        channel.setLesDescription("Thrane&Thrane");
        channel.setDNID("1" + generateARandomStringWithMaxLength(3));
        channel.setInstalledBy("Mike Great");
        channel.setArchived(false);
        channel.setConfigChannel(true);
        channel.setDefaultChannel(true);
        channel.setPollChannel(true);
        channel.setMobileTerminal(mobileTerminal);

        mobileTerminal.setConfigChannel(channel);
        mobileTerminal.setDefaultChannel(channel);
        mobileTerminal.setPollChannel(channel);

        mobileTerminal.getChannels().clear();
        mobileTerminal.getChannels().add(channel);

        MobileTerminalPlugin plugin = new MobileTerminalPlugin();
        plugin.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        plugin.setName("Thrane&Thrane");
        plugin.setPluginSatelliteType("INMARSAT_C");
        plugin.setPluginInactive(false);
        mobileTerminal.setPlugin(plugin);

        return mobileTerminal;
    }

    private static String generateARandomStringWithMaxLength(int len) {
        Random random = new Random();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = random.nextInt(10);
            ret.append(String.valueOf(val));
        }
        return ret.toString();
    }

    private static void addAttribute(Set<MobileTerminalAttributes> attributes, String type, String value, MobileTerminal mobileTerminal) {
        MobileTerminalAttributes attribute = new MobileTerminalAttributes();
        attribute.setAttribute(type);
        attribute.setValue(value);
        attribute.setMobileTerminal(mobileTerminal);
        attributes.add(attribute);
    }

    public static MobileTerminalListQuery createMobileTerminalListQuery() {

        MobileTerminalListQuery query = new MobileTerminalListQuery();

        // ListPagination
        ListPagination pagination = new ListPagination();
        pagination.setListSize(100);
        pagination.setPage(1);

        // MobileTerminalSearchCriteria
        MobileTerminalSearchCriteria criteria = new MobileTerminalSearchCriteria();

        ListCriteria crt = new ListCriteria();
        crt.setKey(SearchKey.SERIAL_NUMBER);
        crt.setValue(serialNumber);

        criteria.getCriterias().add(crt);

        query.setPagination(pagination);
        query.setMobileTerminalSearchCriteria(criteria);

        return query;
    }

    public static String getSerialNumber() {
        return serialNumber;
    }

    public static MobileTerminal createRestMobileTerminal(WebTarget webTarget, Asset asset) {
        MobileTerminal mt = createBasicMobileTerminal();
        if(asset != null)
            mt.setAsset(asset);

        return webTarget
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mt), MobileTerminal.class);
    }
}
