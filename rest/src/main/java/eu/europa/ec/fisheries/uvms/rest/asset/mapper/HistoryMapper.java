package eu.europa.ec.fisheries.uvms.rest.asset.mapper;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.rest.asset.dto.ChangeHistoryRow;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryMapper {

    public static List<ChangeHistoryRow> mapHistory(List<?> histories, HistoryMappingSpecialCase updaterField, HistoryMappingSpecialCase updateTimeField, HistoryMappingSpecialCase ...specialCases) {
        try {
            String className = histories.get(0).getClass().getSimpleName();
            List<Field> fields = listMembers(histories.get(0));
            List<ChangeHistoryRow> returnList = new ArrayList<>(histories.size());
            List<HistoryMappingSpecialCase> specialCaseList = new ArrayList<>();
            specialCaseList.addAll(Arrays.asList(specialCases));
            specialCaseList.add(updaterField);
            specialCaseList.add(updateTimeField);


            Object previousObject = null;
            for (Object object : histories) {
                if (previousObject == null) {
                    previousObject = object;
                    continue;
                }
                String updater = "" + FieldUtils.readDeclaredField(object, updaterField.getFieldName(), true);
                Instant updateTime = (Instant) FieldUtils.readDeclaredField(object, updateTimeField.getFieldName(), true);
                ChangeHistoryRow row = new ChangeHistoryRow(className, updater, updateTime);
                for (Field field : fields) {
                    Object oldValue;
                    Object newValue;
                    Optional<HistoryMappingSpecialCase> specialCase = specialCaseList.stream().filter(s -> s.getFieldName().equals(field.getName())).findAny();
                    if (specialCase.isPresent()) {
                        if (specialCase.get().shouldContinue()) {
                            continue;
                        }
                        oldValue = specialCase.get().specialCase(FieldUtils.readDeclaredField(previousObject, field.getName(), true));
                        newValue = specialCase.get().specialCase(FieldUtils.readDeclaredField(object, field.getName(), true));
                    } else {
                        oldValue = FieldUtils.readDeclaredField(previousObject, field.getName(), true);
                        newValue = FieldUtils.readDeclaredField(object, field.getName(), true);
                    }
                    if (!Objects.equals(oldValue, newValue)) {
                        if (specialCase.isPresent() && specialCase.get().equals(HistoryMappingSpecialCase.MOBILE_TERMINAL_DTO_CHANNEL)) {

                            Set<ChannelDto> oldChannelSet = oldValue != null ? (Set<ChannelDto>) oldValue : new HashSet<>();
                            Set<ChannelDto> newChannelSet = newValue != null ? (Set<ChannelDto>) newValue : new HashSet<>();
                            List<ChangeHistoryRow> channelHistoryRows = HistoryMapper.checkDifferencesBetweenChannels(oldChannelSet, newChannelSet);

                            row.getSubclasses().addAll(channelHistoryRows);

                        } else {
                            row.addNewItem(field.getName(), oldValue, newValue);
                        }

                    }
                }
                returnList.add(row);
                previousObject = object;
            }

            return returnList;
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    public static List<ChangeHistoryRow> checkDifferencesBetweenChannels(Set<ChannelDto> oldInputSet, Set<ChannelDto> newInputSet){
        List<ChangeHistoryRow> returnList = new ArrayList<>();
        Set<ChannelDto> workingNewSet = new HashSet<>(newInputSet);

        for (ChannelDto channelDto : oldInputSet) { //for every channel in the old group, check if it exists in the new group
            Optional<ChannelDto> sameChannelInNewSet = workingNewSet.stream().filter(c -> c.getId().equals(channelDto.getId())).findAny();
            List<ChangeHistoryRow> changeHistoryRows = new ArrayList<>();
            if(sameChannelInNewSet.isPresent()) {
                if(!channelDto.getHistoryId().equals(sameChannelInNewSet.get().getHistoryId())) {
                    changeHistoryRows = HistoryMapper.mapHistory(Arrays.asList(channelDto, sameChannelInNewSet.get()), HistoryMappingSpecialCase.CHANNEL_UPDATED_BY,
                            HistoryMappingSpecialCase.CHANNEL_UPDATED_TIME, HistoryMappingSpecialCase.CHANNEL_MOBILE_TERMINAL);
                }

                workingNewSet.remove(sameChannelInNewSet.get());

            }else{  //if the old channel is not among the new channels
                ChannelDto creatorAndTimeChannel = new ChannelDto();
                creatorAndTimeChannel.setUpdateUser(channelDto.getUpdateUser());
                creatorAndTimeChannel.setUpdateTime(channelDto.getUpdateTime());
                changeHistoryRows = HistoryMapper.mapHistory(Arrays.asList(channelDto, creatorAndTimeChannel), HistoryMappingSpecialCase.CHANNEL_UPDATED_BY,
                        HistoryMappingSpecialCase.CHANNEL_UPDATED_TIME, HistoryMappingSpecialCase.CHANNEL_MOBILE_TERMINAL);

            }
            if (!changeHistoryRows.isEmpty() && !changeHistoryRows.get(0).getChanges().isEmpty()) {
                returnList.add(changeHistoryRows.get(0));
            }

        }
        for (ChannelDto channelDto : workingNewSet) {   // new channels that where not in the old set
            returnList.addAll(HistoryMapper.mapHistory(Arrays.asList(new ChannelDto(), channelDto), HistoryMappingSpecialCase.CHANNEL_UPDATED_BY,
                    HistoryMappingSpecialCase.CHANNEL_UPDATED_TIME, HistoryMappingSpecialCase.CHANNEL_MOBILE_TERMINAL));
        }
        return returnList;
    }

    private static List<Field> listMembers(Object entity){
        List<Field> fields = new ArrayList<>();
        try {
            Field[] declaredFields = entity.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if(!field.getName().contains("this") && !field.isSynthetic() &&
                        field.getModifiers() != Modifier.STATIC + Modifier.PUBLIC + Modifier.FINAL
                        && field.getModifiers() != Modifier.STATIC + Modifier.PRIVATE + Modifier.FINAL) {
                    fields.add(field);
                }
            }
        } catch (Exception e){
            throw new RuntimeException("Error getting the fields of object: " + entity.getClass(), e);
        }
        return fields;
    }






    public static List<ChangeHistoryRow> test(List<Asset> histories) {
        try {
            String className = histories.get(0).getClass().getSimpleName();
            List<Field> fields = listMembers(histories.get(0));
            List<ChangeHistoryRow> returnList = new ArrayList<>(histories.size());

            Asset previousAsset = null;
            for (Asset asset : histories) {
                if (previousAsset == null) {
                    previousAsset = asset;
                    continue;
                }
                String updater = asset.getUpdatedBy();
                Instant updateTime = asset.getUpdateTime();
                ChangeHistoryRow row = new ChangeHistoryRow(className, updater, updateTime);
                for (Field field : fields) {
                    Object oldValue;
                    Object newValue;
                    if (field.getName().equals("updatedBy") || field.getName().equals("updateTime") || field.getName().equals("mobileTerminals")) {
                        if(!field.getName().equals("mobileTerminals")) {
                            continue;
                        }
                        oldValue = previousAsset.getMobileTerminals().stream().map(MobileTerminal::getId).collect(Collectors.toSet());
                        newValue = asset.getMobileTerminals().stream().map(MobileTerminal::getId).collect(Collectors.toSet());

                    } else {
                        oldValue = FieldUtils.readDeclaredField(previousAsset, field.getName(), true);
                        newValue = FieldUtils.readDeclaredField(asset, field.getName(), true);
                    }
                    if (!Objects.equals(oldValue, newValue)) {
                        row.addNewItem(field.getName(), oldValue, newValue);
                    }
                }
                returnList.add(row);
                previousAsset = asset;
            }

            return returnList;
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }



}
