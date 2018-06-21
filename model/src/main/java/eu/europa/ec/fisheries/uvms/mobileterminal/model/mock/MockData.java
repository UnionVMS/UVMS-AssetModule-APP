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
package eu.europa.ec.fisheries.uvms.mobileterminal.model.mock;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 **/
public class MockData {

    private static Logger LOG = LoggerFactory.getLogger(MockData.class);

    public static MobileTerminalType createMobileTerminalDto(Integer id) {
        MobileTerminalType dto = new MobileTerminalType();
        MobileTerminalId mobId = new MobileTerminalId();
        mobId.setGuid(String.valueOf(id));
        dto.getChannels().addAll(createMobileTerminalChannels(2));
        dto.setMobileTerminalId(mobId);
        return dto;
    }

    public static MobileTerminalType createMobileTerminalDtoWithoutId(Integer id, String terminalType) {
        MobileTerminalType dto = new MobileTerminalType();
        MobileTerminalId mobId = new MobileTerminalId();
        dto.setType(terminalType);
        dto.getChannels().addAll(createMobileTerminalChannels(2));
        dto.setMobileTerminalId(mobId);
        return dto;
    }

    public static MobileTerminalType createMobileTerminalDto(Integer id, String terminalType) {
        MobileTerminalType dto = new MobileTerminalType();
        MobileTerminalId mobId = new MobileTerminalId();
        mobId.setGuid(String.valueOf(id));
        dto.setType(terminalType);
        dto.getChannels().addAll(createMobileTerminalChannels(2));
        dto.setMobileTerminalId(mobId);
        return dto;
    }

    public static ComChannelAttribute createComChannelAttribute(String type, String value) {
        ComChannelAttribute channelID = new ComChannelAttribute();
        channelID.setType(type);
        channelID.setValue(value);
        return channelID;
    }

    public static ComChannelType createComChannel(Integer id) {
        ComChannelType channeltype = new ComChannelType();
        channeltype.setName("VMS");

        ComChannelAttribute memberId = createComChannelAttribute("MEMBER_NUMBER", id.toString());
        ComChannelAttribute dnId = createComChannelAttribute("DNID", id.toString());

        channeltype.getAttributes().add(memberId);
        channeltype.getAttributes().add(dnId);
        return channeltype;
    }

    public static List<ComChannelType> createMobileTerminalChannels(Integer amount) {
        List<ComChannelType> dtoList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            dtoList.add(createComChannel(i));
        }
        return dtoList;
    }

    public static List<MobileTerminalType> createMobileTerminalDtoList(Integer amount) {
        List<MobileTerminalType> dtoList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            dtoList.add(createMobileTerminalDto(i));
        }
        return dtoList;
    }

    public static MobileTerminalListResponse createMobileTerminalListResponse() {
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(1);
        response.setTotalNumberOfPages(1);
        response.getMobileTerminal().addAll(createMobileTerminalDtoList(1));
        return response;
    }

    public static MobileTerminalAttribute createMobileTerminalAttribute(String type, String value) {
        MobileTerminalAttribute response = new MobileTerminalAttribute();
        response.setType(type);
        response.setValue(value);
        return response;
    }

    private static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
        if (date != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            try {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            } catch (DatatypeConfigurationException e) {
                LOG.error("[ Error when getting XML calendar. ] {} {}", e.getMessage(), e.getStackTrace());
            }
        }
        return null;
    }

    public static MobileTerminalType setStatusMobileTerminal(MobileTerminalId terminalId, MobileTerminalStatus status) {
        MobileTerminalType type = new MobileTerminalType();

        MobileTerminalAttribute attribute = new MobileTerminalAttribute();
        attribute.setType("SOFTWARE_VERSION");
        attribute.setValue("MOCK!");
        type.getAttributes().add(attribute);

        type.setMobileTerminalId(terminalId);
        type.setSource(MobileTerminalSource.INTERNAL);

        switch (status) {
        case INACTIVE:
            type.setInactive(true);
            break;
        case ACTIVE:
            type.setInactive(false);
            break;
        case ARCHIVE:
            break;
        }
        return type;
    }

    public static List<MobileTerminalHistory> getMobileTerminalHistory(MobileTerminalId terminalId) {
        List<MobileTerminalHistory> list = new ArrayList<>();
        MobileTerminalHistory history = new MobileTerminalHistory();
        list.add(history);
        return list;
    }

    public static List<PollResponseType> createProgramPollTypeList(Integer amount) {
        List<PollResponseType> responseList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            responseList.add(createProgramPollType(i));
        }
        return responseList;
    }

    public static PollResponseType createProgramPollType(Integer id) {
        PollResponseType response = new PollResponseType();
        response.setMobileTerminal(createMobileTerminalDto(id));
        response.setPollType(PollType.PROGRAM_POLL);
        response.setUserName("backend");
        response.getAttributes().add(createPollAttribute(PollAttributeType.DNID, "DNID"));
        response.getAttributes().add(createPollAttribute(PollAttributeType.END_DATE, "2015-12-02"));
        response.getAttributes().add(createPollAttribute(PollAttributeType.START_DATE, "2015-03-23"));
        response.getAttributes().add(createPollAttribute(PollAttributeType.FREQUENCY, "2"));
        response.getAttributes().add(createPollAttribute(PollAttributeType.IN_PORT_GRACE, "AMAZING GRACE"));
        response.getAttributes().add(createPollAttribute(PollAttributeType.MEMBER_NUMBER, "132456"));
        response.getAttributes().add(createPollAttribute(PollAttributeType.REPORT_FREQUENCY, "123"));
        response.getAttributes().add(createPollAttribute(PollAttributeType.PROGRAM_RUNNING, "TRUE"));
        return response;
    }

    public static PollAttribute createPollAttribute(PollAttributeType type, String value) {
        PollAttribute attribute = new PollAttribute();
        attribute.setKey(type);
        attribute.setValue(value);
        return attribute;
    }

    /*
     * public static List<MobileTerminalListType> getMobileTerminalList() {
     * List<MobileTerminalListType> list = new
     * ArrayList<MobileTerminalListType>(); MobileTerminalListType one = new
     * MobileTerminalListType(); ChannelListType channel = new
     * ChannelListType(); channel.setDnid("122");
     * channel.setMemberNumber("123"); channel.setSerialNumber("1234567");
     * channel.setTransponderType(TerminalSystemType.INMARSAT_C);
     * List<ChannelListType> channels = new ArrayList<ChannelListType>();
     * channels.add(channel); one.setChannels(channels);
     * one.setFlagState("SWE"); one.setVesselName("vesselName"); list.add(one);
     * return list; }
     */
}