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
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.Random;

public class MobileTerminalTestHelper {

    private static String serialNumber;

    public static MobileTerminal createBasicMobileTerminal() {
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        serialNumber = generateARandomStringWithMaxLength(10);
        mobileTerminal.setSerialNo(serialNumber);
        mobileTerminal.setInstalledBy("Mike Great");

        mobileTerminal.setSatelliteNumber("S" + generateARandomStringWithMaxLength(4));
        mobileTerminal.setAntenna("A");
        mobileTerminal.setTransceiverType("A");
        mobileTerminal.setSoftwareVersion("A");

        Channel channel = createBasicChannel();
        channel.setMobileTerminal(mobileTerminal);

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

    public static Channel createBasicChannel(){
        Channel channel = new Channel();
        channel.setName("VMS");
        channel.setFrequencyGracePeriod(Duration.ofSeconds(54000));
        channel.setMemberNumber(generateARandomStringWithMaxLength(3));
        channel.setExpectedFrequency(Duration.ofSeconds(7200));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(10800));
        channel.setLesDescription("Thrane&Thrane");
        channel.setDNID("1" + generateARandomStringWithMaxLength(3));
        channel.setArchived(false);
        channel.setConfigChannel(true);
        channel.setDefaultChannel(true);
        channel.setPollChannel(true);
        return channel;
    }

    public static String generateARandomStringWithMaxLength(int len) {
        Random random = new Random();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = random.nextInt(10);
            ret.append(String.valueOf(val));
        }
        return ret.toString();
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

    public static MobileTerminal createRestMobileTerminal(WebTarget webTarget, Asset asset, String token) {
        MobileTerminal mt = createBasicMobileTerminal();
        if(asset != null)
            mt.setAsset(asset);

        return webTarget
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .post(Entity.json(mt), MobileTerminal.class);
    }

    public static MobileTerminal restMobileTerminalUpdate(WebTarget webTarget, MobileTerminal mt, String token){
        return webTarget
                .path("mobileterminal")
                .queryParam("comment", mt.getComment())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .put(Entity.json(mt), MobileTerminal.class);
    }
}
