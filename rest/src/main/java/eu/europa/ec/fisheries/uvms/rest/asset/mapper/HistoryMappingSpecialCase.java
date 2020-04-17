package eu.europa.ec.fisheries.uvms.rest.asset.mapper;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.rest.asset.dto.ChangeHistoryRow;

import java.util.*;
import java.util.stream.Collectors;

public enum HistoryMappingSpecialCase {
    ASSET_UPDATED_BY("updatedBy"),
    ASSET_UPDATED_TIME("updateTime"),
    MOBILE_TERMINAL_UPDATED_BY("updateuser"),
    MOBILE_TERMINAL_UPDATED_TIME("updatetime"),
    MOBILE_TERMINAL_PLUGIN("plugin"),
    CHANNEL_UPDATED_BY("updateUser"),
    CHANNEL_UPDATED_TIME("updateTime"),
    CHANNEL_MOBILE_TERMINAL("mobileTerminal"),
    ASSET_MOBILE_TERMINAL("mobileTerminals", false){
        @Override
        public Object specialCase(Object input) {
            Set<MobileTerminal> inputSet = (Set<MobileTerminal>)input;
            Set<UUID> returnSet = inputSet.stream().map(MobileTerminal::getId).collect(Collectors.toSet());
            return returnSet;
        }
    },
    MOBILE_TERMINAL_DTO_CHANNEL("channels", false, true){
        @Override
        public List<ChangeHistoryRow> recursive(Object oldInput, Object newInput) {
            Set<ChannelDto> oldChannelSet = oldInput != null ? (Set<ChannelDto>) oldInput : new HashSet<>();
            Set<ChannelDto> newChannelSet = newInput != null ? (Set<ChannelDto>) newInput : new HashSet<>();
            return HistoryMapper.checkDifferencesBetweenChannels(oldChannelSet, newChannelSet);
        }
    };


    private String fieldName;

    private boolean shouldContinue = true;

    private boolean checkSubclass = false;

    HistoryMappingSpecialCase(String fieldName) {
        this.fieldName = fieldName;
    }

    HistoryMappingSpecialCase(String fieldName, boolean shouldContinue) {
        this.fieldName = fieldName;
        this.shouldContinue = shouldContinue;
    }

    HistoryMappingSpecialCase(String fieldName, boolean shouldContinue, boolean checkSubclass) {
        this.fieldName = fieldName;
        this.shouldContinue = shouldContinue;
        this.checkSubclass = checkSubclass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean shouldContinue() {
        return shouldContinue;
    }

    public boolean checkSubclass() {
        return checkSubclass;
    }

    public Object specialCase(Object input){
        return null;
    }

    public List<ChangeHistoryRow> recursive(Object oldInput, Object newInput){
        return null;
    }
}
