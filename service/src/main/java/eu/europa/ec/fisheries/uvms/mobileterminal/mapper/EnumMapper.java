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
package eu.europa.ec.fisheries.uvms.mobileterminal.mapper;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollStatus;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.EventCode;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.TerminalSourceEnum;

public class EnumMapper {

    // Terminalsource
    // TerminalSourceEnum, MobileTerminalSource, Terminalsource
    public static TerminalSourceEnum getSourceTypeFromId(Integer id) {
        if (id != null) {
            for (TerminalSourceEnum source : TerminalSourceEnum.values()) {
                if (id.equals(source.getId())) {
                    return source;
                }
            }
        }
        throw new NullPointerException("Couldn't map enum (from id) in " + TerminalSourceEnum.class.getName());
    }

    public static MobileTerminalSource getSourceModelFromType(TerminalSourceEnum type) {
        if (type != null) {
            switch (type) {
            case INTERNAL:
                return MobileTerminalSource.INTERNAL;
            case NATIONAL:
                return MobileTerminalSource.NATIONAL;
            default:
                throw new IllegalArgumentException("Couldn't map enum (from type) in " + TerminalSourceEnum.class.getName());
            }
        }
        throw new NullPointerException("TerminalSourceEnum is null");
    }

    public static TerminalSourceEnum getSourceTypeFromModel(MobileTerminalSource model) {
        if (model != null) {
            switch (model) {
            case INTERNAL:
                return TerminalSourceEnum.INTERNAL;
            case NATIONAL:
                return TerminalSourceEnum.NATIONAL;
            default:
                throw new IllegalArgumentException("Couldn't map enum (from model) in " + TerminalSourceEnum.class.getName());
            }
        }
        throw new NullPointerException("MobileTerminalSource parameter is null");
    }

    // Terminaleventtype
    public static EventCodeEnum getEventTypeFromId(Integer id) {
        if (id != null) {
            for (EventCodeEnum channel : EventCodeEnum.values()) {
                if (id.equals(channel.getId())) {
                    return channel;
                }
            }
        }
        throw new NullPointerException("Couldn't map enum (from id) in " + EventCodeEnum.class.getName());
    }

    public static EventCode getEventModelFromType(EventCodeEnum type) {
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
            default:
                throw new IllegalArgumentException("Couldn't map enum (from type) in " + EventCodeEnum.class.getName());
            }
        }
        throw new NullPointerException("EventCodeEnum parameter is null");
    }

    public static EventCodeEnum getEventTypeFromModel(EventCode model) {
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
            default:
                throw new IllegalArgumentException("Couldn't map enum (from model) in " + EventCodeEnum.class.getName());
            }
        }
        throw new NullPointerException("EventCode parameter is null");
    }

    public static PollTypeEnum getPollTypeFromId(Integer id) {
        if (id != null) {
            for (PollTypeEnum polltype : PollTypeEnum.values()) {
                if (id.equals(polltype.getId())) {
                    return polltype;
                }
            }
        }
        throw new NullPointerException("Couldn't map enum (from id) in " + PollTypeEnum.class.getName());
    }

    public static PollType getPollModelFromType(PollTypeEnum type) {
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
                default:
                    throw new IllegalArgumentException("Couldn't map enum (from type) in " + PollTypeEnum.class.getName());
            }
        }
        throw new NullPointerException("PollTypeEnum parameter is null");
    }

    public static PollTypeEnum getPollTypeFromModel(PollType model) {
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
            default:
                throw new IllegalArgumentException("Couldn't map enum (from model) in " + PollTypeEnum.class.getName());
            }
        }
        throw new NullPointerException("PollType parameter is null");
    }

    public static PollStateEnum getPollStateTypeFromId(Integer id) {
        if (id != null) {
            for (PollStateEnum polltype : PollStateEnum.values()) {
                if (id.equals(polltype.getId())) {
                    return polltype;
                }
            }
        }
        throw new NullPointerException("Couldn't map enum (from id) in " + PollStateEnum.class.getName());
    }

    public static PollStatus getPollStateModelFromType(PollStateEnum type) {
        if (type != null) {
            switch (type) {
            case STARTED:
                return PollStatus.STARTED;
            case STOPPED:
                return PollStatus.STOPPED;
            case ARCHIVED:
                return PollStatus.ARCHIVED;
            default:
                throw new IllegalArgumentException("Couldn't map enum (from type) in " + PollTypeEnum.class.getName());
            }
        }
        throw new NullPointerException("PollStateEnum parameter is null");
    }

    public static PollStateEnum getPollStateTypeFromModel(PollStatus model) {
        if (model != null) {
            switch (model) {
            case STARTED:
                return PollStateEnum.STARTED;
            case STOPPED:
                return PollStateEnum.STOPPED;
            case ARCHIVED:
                return PollStateEnum.ARCHIVED;
            default:
                throw new IllegalArgumentException("Couldn't map enum (from model) in " + PollStateEnum.class.getName());
            }
        }
        throw new NullPointerException("PollStatus parameter is null");
    }
}
