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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollStatus;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.EventCode;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.TerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.EnumException;

public class EnumMapper {

    // Terminalsource
    // TerminalSourceEnum, MobileTerminalSource, Terminalsource
    public static TerminalSourceEnum getSourceTypeFromId(Integer id) throws EnumException {
        if (id != null) {
            for (TerminalSourceEnum source : TerminalSourceEnum.values()) {
                if (id.equals(source.getId())) {
                    return source;
                }
            }
        }
        throw new EnumException("Couldn't map enum (from id) in " + TerminalSourceEnum.class.getName());
    }

    public static MobileTerminalSource getSourceModelFromType(TerminalSourceEnum type) throws EnumException {
        if (type != null) {
            switch (type) {
            case INTERNAL:
                return MobileTerminalSource.INTERNAL;
            case NATIONAL:
                return MobileTerminalSource.NATIONAL;
            }
        }
        throw new EnumException("Couldn't map enum (from type) in " + TerminalSourceEnum.class.getName());
    }

    public static TerminalSourceEnum getSourceTypeFromModel(MobileTerminalSource model) throws EnumException {
        if (model != null) {
            switch (model) {
            case INTERNAL:
                return TerminalSourceEnum.INTERNAL;
            case NATIONAL:
                return TerminalSourceEnum.NATIONAL;
            }
        }
        throw new EnumException("Couldn't map enum (from model) in " + TerminalSourceEnum.class.getName());
    }

    // Terminaleventtype
    public static EventCodeEnum getEventTypeFromId(Integer id) throws EnumException {
        if (id != null) {
            for (EventCodeEnum channel : EventCodeEnum.values()) {
                if (id.equals(channel.getId())) {
                    return channel;
                }
            }
        }
        throw new EnumException("Couldn't map enum (from id) in " + EventCodeEnum.class.getName());
    }

    public static EventCode getEventModelFromType(EventCodeEnum type) throws EnumException {
        if (type != null) {
            switch (type) {
            case CREATE:
                return EventCode.CREATE;
            case MODIFY:
                return EventCode.MODIFY;
            case ACTIVATE:
                return EventCode.ACTIVATE;
            case INACTIVATE:
                return EventCode.INACTIVATE;
            case ARCHIVE:
                return EventCode.ARCHIVE;
            case LINK:
                return EventCode.LINK;
            case UNLINK:
                return EventCode.UNLINK;
            }
        }
        throw new EnumException("Couldn't map enum (from type) in " + EventCodeEnum.class.getName());
    }

    public static EventCodeEnum getEventTypeFromModel(EventCode model) throws EnumException {
        if (model != null) {
            switch (model) {
            case CREATE:
                return EventCodeEnum.CREATE;
            case MODIFY:
                return EventCodeEnum.MODIFY;
            case ACTIVATE:
                return EventCodeEnum.ACTIVATE;
            case INACTIVATE:
                return EventCodeEnum.INACTIVATE;
            case ARCHIVE:
                return EventCodeEnum.ARCHIVE;
            case LINK:
                return EventCodeEnum.LINK;
            case UNLINK:
                return EventCodeEnum.UNLINK;
            }
        }
        throw new EnumException("Couldn't map enum (from model) in " + EventCodeEnum.class.getName());
    }

    public static PollTypeEnum getPollTypeFromId(Integer id) throws EnumException {
        if (id != null) {
            for (PollTypeEnum polltype : PollTypeEnum.values()) {
                if (id.equals(polltype.getId())) {
                    return polltype;
                }
            }
        }
        throw new EnumException("Couldn't map enum (from id) in " + PollTypeEnum.class.getName());
    }

    public static PollType getPollModelFromType(PollTypeEnum type) throws EnumException {
        if (type != null) {
            switch (type) {
            case MANUAL_POLL:
                return PollType.MANUAL_POLL;
            case PROGRAM_POLL:
                return PollType.PROGRAM_POLL;
            case SAMPLING_POLL:
                return PollType.SAMPLING_POLL;
            case CONFIGURATION_POLL:
                return PollType.CONFIGURATION_POLL;
            }
        }
        throw new EnumException("Couldn't map enum (from type) in " + PollTypeEnum.class.getName());
    }

    public static PollTypeEnum getPollTypeFromModel(PollType model) throws EnumException {
        if (model != null) {
            switch (model) {
            case MANUAL_POLL:
                return PollTypeEnum.MANUAL_POLL;
            case PROGRAM_POLL:
                return PollTypeEnum.PROGRAM_POLL;
            case SAMPLING_POLL:
                return PollTypeEnum.SAMPLING_POLL;
            case CONFIGURATION_POLL:
                return PollTypeEnum.CONFIGURATION_POLL;
            }
        }
        throw new EnumException("Couldn't map enum (from model) in " + PollTypeEnum.class.getName());
    }

    public static PollStateEnum getPollStateTypeFromId(Integer id) throws EnumException {
        if (id != null) {
            for (PollStateEnum polltype : PollStateEnum.values()) {
                if (id.equals(polltype.getId())) {
                    return polltype;
                }
            }
        }
        throw new EnumException("Couldn't map enum (from id) in " + PollStateEnum.class.getName());
    }

    public static PollStatus getPollStateModelFromType(PollStateEnum type) throws EnumException {
        if (type != null) {
            switch (type) {
            case STARTED:
                return PollStatus.STARTED;
            case STOPPED:
                return PollStatus.STOPPED;
            case ARCHIVED:
                return PollStatus.ARCHIVED;
            }
        }
        throw new EnumException("Couldn't map enum (from type) in " + PollTypeEnum.class.getName());
    }

    public static PollStateEnum getPollStateTypeFromModel(PollStatus model) throws EnumException {
        if (model != null) {
            switch (model) {
            case STARTED:
                return PollStateEnum.STARTED;
            case STOPPED:
                return PollStateEnum.STOPPED;
            case ARCHIVED:
                return PollStateEnum.ARCHIVED;
            }
        }
        throw new EnumException("Couldn't map enum (from model) in " + PollStateEnum.class.getName());
    }
}
