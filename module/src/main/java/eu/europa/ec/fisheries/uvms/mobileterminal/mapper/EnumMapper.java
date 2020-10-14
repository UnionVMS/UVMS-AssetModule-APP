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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;

public class EnumMapper {

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
                case AUTOMATIC_POLL:
                    return PollType.AUTOMATIC_POLL;
                case BASE_POLL:
                    return PollType.BASE_POLL;
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
            case AUTOMATIC_POLL:
                return PollTypeEnum.AUTOMATIC_POLL;
                case BASE_POLL:
                return PollTypeEnum.BASE_POLL;
            default:
                throw new IllegalArgumentException("Couldn't map enum (from model) in " + PollTypeEnum.class.getName());
            }
        }
        throw new NullPointerException("PollType parameter is null");
    }

}
