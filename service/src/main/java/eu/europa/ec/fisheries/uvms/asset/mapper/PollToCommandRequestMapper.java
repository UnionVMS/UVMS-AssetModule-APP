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
package eu.europa.ec.fisheries.uvms.asset.mapper;

import eu.europa.ec.fisheries.schema.exchange.common.v1.KeyValueType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PollTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Stateless
public class PollToCommandRequestMapper {

    @Inject
    private MobileTerminalServiceBean terminalServiceBean;

    public enum PollReceiverInmarsatC {
        MOBILE_TERMINAL_ID, CONNECT_ID, SERIAL_NUMBER, DNID, MEMBER_NUMBER,
        LES_NAME, LES_SERVICE_NAME, SATELLITE_NUMBER, AOR_E, AOR_W, POR, IOR
    }

    public enum PollReceiverIridium {
        MOBILE_TERMINAL_ID, CONNECT_ID,
        SERIAL_NUMBER, AOR_E, AOR_W, POR, IOR
    }

    private PollTypeType mapToPollType(eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType pollType) {
        switch (pollType) {
            case CONFIGURATION_POLL:
                return PollTypeType.CONFIG;
            case SAMPLING_POLL:
                return PollTypeType.SAMPLING;
            case MANUAL_POLL:
            case PROGRAM_POLL:
            case AUTOMATIC_POLL:
                return PollTypeType.POLL;
            default:
                throw new IllegalArgumentException("Error when mapping PollType to PollTypeType ");
        }
    }

    public PollType mapToPollType(PollResponseType pollResponse) {
        PollType pollType = new PollType();

        String pollId = pollResponse.getPollId() == null ? null : pollResponse.getPollId().getGuid();
        List<PollAttribute> pollAttributes = pollResponse.getAttributes() == null ? new ArrayList<>() : pollResponse.getAttributes();

        pollType.setPollTypeType(mapToPollType(pollResponse.getPollType()));
        pollType.setPollId(pollId);
        pollType.setMessage(pollResponse.getUserName() + " : " + pollResponse.getComment());

        for (PollAttribute attr : pollAttributes) {
            pollType.getPollPayload().add(mapPollAttributeToKeyValue(attr.getKey(), attr.getValue()));
        }

        if (pollResponse.getMobileTerminal() != null) {

            MobileTerminalType mobTerm = pollResponse.getMobileTerminal();
            String mobileTerminalType = mobTerm.getType();

            if ("INMARSAT_C".equalsIgnoreCase(mobileTerminalType)) {
                String connectId = mobTerm.getConnectId();
                String mobileTerminalId = mobTerm.getMobileTerminalId() == null ? null : mobTerm.getMobileTerminalId().getGuid();
                Plugin plugin = mobTerm.getPlugin();
                if (plugin != null) {
                    pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.LES_NAME, plugin.getLabelName()));
                    pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.LES_SERVICE_NAME, plugin.getServiceName()));
                }
                pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.CONNECT_ID, connectId));
                pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.MOBILE_TERMINAL_ID, mobileTerminalId));

                addOceanRegions(pollType, mobileTerminalId);

                List<MobileTerminalAttribute> attributes = mobTerm.getAttributes();
                for (MobileTerminalAttribute attr : attributes) {
                    if (PollReceiverInmarsatC.SERIAL_NUMBER.name().equalsIgnoreCase(attr.getType())) {
                        pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.SERIAL_NUMBER, attr.getValue()));
                    }
                    if (PollReceiverInmarsatC.SATELLITE_NUMBER.name().equalsIgnoreCase(attr.getType())) {
                        pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.SATELLITE_NUMBER, attr.getValue()));
                    }
                }

                for (ComChannelType channel : mobTerm.getChannels()) {
                    for (ComChannelAttribute attr : channel.getAttributes()) {
                        if (PollReceiverInmarsatC.DNID.name().equalsIgnoreCase(attr.getType())) {
                            pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.DNID, attr.getValue()));
                        }
                        if (PollReceiverInmarsatC.MEMBER_NUMBER.name().equalsIgnoreCase(attr.getType())) {
                            pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverInmarsatC.MEMBER_NUMBER, attr.getValue()));
                        }
                    }
                }
            } else if ("IRIDIUM".equalsIgnoreCase(mobileTerminalType)) {
                String connectId = mobTerm.getConnectId();
                String mobileTerminalId = mobTerm.getMobileTerminalId() == null ? null : mobTerm.getMobileTerminalId().getGuid();

                pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverIridium.CONNECT_ID, connectId));
                pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverIridium.MOBILE_TERMINAL_ID, mobileTerminalId));

                addOceanRegions(pollType, mobileTerminalId);

                List<MobileTerminalAttribute> attributes = mobTerm.getAttributes();
                for (MobileTerminalAttribute attr : attributes) {
                    if (PollReceiverIridium.SERIAL_NUMBER.name().equalsIgnoreCase(attr.getType())) {
                        pollType.getPollReceiver().add(mapReceiverToKeyValue(PollReceiverIridium.SERIAL_NUMBER, attr.getValue()));
                    }
                }
            }
        }
        return pollType;
    }

    private void addOceanRegions(PollType pollType, String mobileTerminalId) {
        if(mobileTerminalId != null) {
            List<String> oceanRegionList = getOceanRegions(mobileTerminalId);
            for (String or : oceanRegionList) {
                switch (or) {
                    case "AOR_E":
                        pollType.getPollReceiver().add(mapReceiverToKeyValue(
                                PollReceiverInmarsatC.AOR_E, PollReceiverInmarsatC.AOR_E.name()));
                        break;
                    case "AOR_W":
                        pollType.getPollReceiver().add(mapReceiverToKeyValue(
                                PollReceiverInmarsatC.AOR_W, PollReceiverInmarsatC.AOR_W.name()));
                        break;
                    case "POR":
                        pollType.getPollReceiver().add(mapReceiverToKeyValue(
                                PollReceiverInmarsatC.POR, PollReceiverInmarsatC.POR.name()));
                        break;
                    case "IOR":
                        pollType.getPollReceiver().add(mapReceiverToKeyValue(
                                PollReceiverInmarsatC.IOR, PollReceiverInmarsatC.IOR.name()));
                        break;
                }
            }
        }
    }

    private List<String> getOceanRegions(String mobileTerminalId) {
        MobileTerminal entity = terminalServiceBean.getMobileTerminalEntityById(UUID.fromString(mobileTerminalId));
        List<String> oceanRegions = new ArrayList<>();
        if(entity.getEastAtlanticOceanRegion()) oceanRegions.add(PollReceiverInmarsatC.AOR_E.name());
        if(entity.getWestAtlanticOceanRegion()) oceanRegions.add(PollReceiverInmarsatC.AOR_W.name());
        if(entity.getPacificOceanRegion()) oceanRegions.add(PollReceiverInmarsatC.POR.name());
        if(entity.getIndianOceanRegion()) oceanRegions.add(PollReceiverInmarsatC.IOR.name());
        return oceanRegions;
    }

    private KeyValueType mapPollAttributeToKeyValue(PollAttributeType key, String value) {
        KeyValueType keyValue = new KeyValueType();
        keyValue.setKey(key.name());
        keyValue.setValue(value);
        return keyValue;
    }

    private KeyValueType mapReceiverToKeyValue(PollReceiverInmarsatC key, String value) {
        KeyValueType keyValue = new KeyValueType();
        keyValue.setKey(key.name());
        keyValue.setValue(value);
        return keyValue;
    }

    private KeyValueType mapReceiverToKeyValue(PollReceiverIridium key, String value) {
        KeyValueType keyValue = new KeyValueType();
        keyValue.setKey(key.name());
        keyValue.setValue(value);
        return keyValue;
    }
}
