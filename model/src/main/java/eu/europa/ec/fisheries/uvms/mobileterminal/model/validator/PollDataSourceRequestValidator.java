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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollAttributeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PollDataSourceRequestValidator {

    private PollDataSourceRequestValidator () {}

    static final Logger LOG = LoggerFactory.getLogger(PollDataSourceRequestValidator.class);

    private static final int CONFIGURATION_POLL_MAX_SIZE = 1;
    private static final int SAMPLING_POLL_MAX_SIZE = 1;

    public static void validateMobilePollRequestType(PollRequestType pollRequest) {
        if (pollRequest == null)
            throw new NullPointerException("No poll request to validate");
        validateHasUser(pollRequest.getUserName());
        validateCorrectRequestType(pollRequest);
        validateMobileTerminals(pollRequest);
    }

    static void validateHasUser(String userName) {
        if (userName == null || userName.isEmpty())
            throw new NullPointerException("No user of poll request");
    }

    static void validateMobileTerminals(PollRequestType pollRequest) {
        if (pollRequest.getMobileTerminals().isEmpty()) {
            throw new NullPointerException("No mobile terminals to poll");
        }
        switch (pollRequest.getPollType()) {
        case CONFIGURATION_POLL:
            boolean canPollMultiple = true;
            for (PollAttribute attr : pollRequest.getAttributes()) {
                if (PollAttributeType.DNID.equals(attr.getKey())) {
                    canPollMultiple = false;
                }
                if (PollAttributeType.MEMBER_NUMBER.equals(attr.getKey())) {
                    canPollMultiple = false;
                }
            }
            if (!canPollMultiple && pollRequest.getMobileTerminals().size() > CONFIGURATION_POLL_MAX_SIZE) {
                throw new IllegalArgumentException("Too many mobile terminals to send a configuration of dnid/memberid poll");
            }
            break;
        case SAMPLING_POLL:
            if (pollRequest.getMobileTerminals().size() > SAMPLING_POLL_MAX_SIZE) {
                throw new IllegalArgumentException("Too many mobile terminals to send a configuration poll");
            }
            break;
        default:
            break;
        }
    }

    private static void validateCorrectRequestType(PollRequestType pollRequest) {

        switch (pollRequest.getPollType()) {
        case CONFIGURATION_POLL:
            checkConfigurationPollParams(pollRequest);
            break;
        case MANUAL_POLL:
            break;
        case PROGRAM_POLL:
            checkProgramPollParams(pollRequest);
            break;
        case SAMPLING_POLL:
            checkSamplingPollParams(pollRequest);
            break;
        default:
            throw new IllegalArgumentException("pollRequest with PollType " + pollRequest.getPollType() + " validation not impemented");
        }
    }

    static void checkConfigurationPollParams(PollRequestType pollRequest) {
        checkOneOfFields(pollRequest, PollAttributeType.REPORT_FREQUENCY, PollAttributeType.GRACE_PERIOD, PollAttributeType.IN_PORT_GRACE,
                PollAttributeType.DNID, PollAttributeType.MEMBER_NUMBER);
    }

    static void checkProgramPollParams(PollRequestType pollRequest) {
        checkFields(pollRequest, PollAttributeType.FREQUENCY, PollAttributeType.START_DATE, PollAttributeType.END_DATE);
    }

    static void checkSamplingPollParams(PollRequestType pollRequest) {
        checkFields(pollRequest, PollAttributeType.START_DATE, PollAttributeType.END_DATE);
    }

    private static void checkOneOfFields(PollRequestType pollRequest, PollAttributeType... attributes) {
        Set<PollAttributeType> attributesToCheck = new HashSet<>(Arrays.asList(attributes));
        Set<PollAttributeType> attributesProvided = new HashSet<>();

        if (pollRequest != null) {
            for (PollAttribute attribute : pollRequest.getAttributes()) {
                attributesProvided.add(attribute.getKey());
            }
        }

        StringBuilder builder = new StringBuilder("Request must contain at least one of the following attributes: ");
        int nbrOfAttributes = 0;
        for (PollAttributeType attrToCheck : attributesToCheck) {
            if (attributesProvided.contains(attrToCheck)) {
                nbrOfAttributes++;
            } else {
                builder
                        .append("[")
                        .append(attrToCheck.name())
                        .append("] ");
            }
        }

        if (nbrOfAttributes == 0) {
            throw new RuntimeException(builder.toString());
        }
    }

    private static void checkFields(PollRequestType pollRequest, PollAttributeType... attributes) {

        Set<PollAttributeType> attributesToCheck = new HashSet<>(Arrays.asList(attributes));
        Set<PollAttributeType> attributesProvided = new HashSet<>();

        if (pollRequest != null) {
            for (PollAttribute attribute : pollRequest.getAttributes()) {
                attributesProvided.add(attribute.getKey());
            }
        }

        for (PollAttributeType attrToCheck : attributesToCheck) {
            int count = 0;
            StringBuilder builder = new StringBuilder("Request must contain following attributes: ");
            if (!attributesProvided.contains(attrToCheck)) {
                builder
                        .append("[")
                        .append(attrToCheck.name())
                        .append("] ");
                count++;
            }
            if(count > 0)
                throw new RuntimeException(builder.toString());
        }
    }
}
