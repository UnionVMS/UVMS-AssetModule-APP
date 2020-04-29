package eu.europa.ec.fisheries.uvms.rest.asset.mapper;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchFields;
import eu.europa.ec.fisheries.uvms.rest.asset.dto.ChangeHistoryRow;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryMapper {


    public static List<ChangeHistoryRow> assetChangeHistory(List<Asset> histories) {
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
                    if (field.getName().equals(SearchFields.UPDATED_BY.getFieldName()) || field.getName().equals(SearchFields.UPDATED_TIME.getFieldName())
                            || field.getName().equals(SearchFields.MOBILE_TERMINAL.getFieldName())) {
                        if(!field.getName().equals(SearchFields.MOBILE_TERMINAL.getFieldName())) {
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


    public static List<ChangeHistoryRow> mobileTerminalChangeHistory(List<MobileTerminalDto> histories) {
        try {
            String className = histories.get(0).getClass().getSimpleName();
            List<Field> fields = listMembers(histories.get(0));
            List<ChangeHistoryRow> returnList = new ArrayList<>(histories.size());

            MobileTerminalDto previousMt = null;
            for (MobileTerminalDto mt : histories) {
                if (previousMt == null) {
                    previousMt = mt;
                    continue;
                }
                String updater = mt.getUpdateuser();
                Instant updateTime = mt.getUpdatetime();
                ChangeHistoryRow row = new ChangeHistoryRow(className, updater, updateTime);
                for (Field field : fields) {
                    Object oldValue;
                    Object newValue;
                    if (field.getName().equals(MTSearchFields.UPDATE_USER.getFieldName()) || field.getName().equals(MTSearchFields.UPDATE_TIME.getFieldName())
                        || field.getName().equals(MTSearchFields.PLUGIN.getFieldName())) {
                        continue;
                    }else {
                        oldValue = FieldUtils.readDeclaredField(previousMt, field.getName(), true);
                        newValue = FieldUtils.readDeclaredField(mt, field.getName(), true);
                    }
                    if (!Objects.equals(oldValue, newValue)) {
                        if(field.getName().equals("channels")){
                            List<ChangeHistoryRow> channelChangeHistoryRows = checkDifferencesBetweenChannels(previousMt.getChannels(), mt.getChannels());
                            row.setSubclasses(channelChangeHistoryRows);
                        }else {
                            row.addNewItem(field.getName(), oldValue, newValue);
                        }
                    }
                }
                returnList.add(row);
                previousMt = mt;
            }

            return returnList;
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    private static List<ChangeHistoryRow> checkDifferencesBetweenChannels(Set<ChannelDto> oldInputSet, Set<ChannelDto> newInputSet){
        List<ChangeHistoryRow> returnList = new ArrayList<>();
        Set<ChannelDto> workingNewSet = new HashSet<>(newInputSet);

        for (ChannelDto channelDto : oldInputSet) { //for every channel in the old group, check if it exists in the new group
            Optional<ChannelDto> sameChannelInNewSet = workingNewSet.stream().filter(c -> c.getId().equals(channelDto.getId())).findAny();
            List<ChangeHistoryRow> changeHistoryRows = new ArrayList<>();
            if(sameChannelInNewSet.isPresent()) {
                if(!channelDto.getHistoryId().equals(sameChannelInNewSet.get().getHistoryId())) {
                    changeHistoryRows = channelChangeHistory(Arrays.asList(channelDto, sameChannelInNewSet.get()));
                }

                workingNewSet.remove(sameChannelInNewSet.get());

            }else{  //if the old channel is not among the new channels
                ChannelDto creatorAndTimeChannel = new ChannelDto();
                creatorAndTimeChannel.setUpdateUser(channelDto.getUpdateUser());
                creatorAndTimeChannel.setUpdateTime(channelDto.getUpdateTime());
                changeHistoryRows = channelChangeHistory(Arrays.asList(channelDto, creatorAndTimeChannel));

            }
            if (!changeHistoryRows.isEmpty() && !changeHistoryRows.get(0).getChanges().isEmpty()) {
                returnList.add(changeHistoryRows.get(0));
            }

        }
        for (ChannelDto channelDto : workingNewSet) {   // new channels that where not in the old set
            returnList.addAll(channelChangeHistory(Arrays.asList(new ChannelDto(), channelDto)));
        }
        return returnList;
    }

    public static List<ChangeHistoryRow> channelChangeHistory(List<ChannelDto> histories) {
        try {
            String className = histories.get(0).getClass().getSimpleName();
            List<Field> fields = listMembers(histories.get(0));
            List<ChangeHistoryRow> returnList = new ArrayList<>(histories.size());

            ChannelDto previousChannel = null;
            for (ChannelDto channel : histories) {
                if (previousChannel == null) {
                    previousChannel = channel;
                    continue;
                }
                String updater = channel.getUpdateUser();
                Instant updateTime = channel.getUpdateTime();
                ChangeHistoryRow row = new ChangeHistoryRow(className, updater, updateTime);
                for (Field field : fields) {
                    Object oldValue;
                    Object newValue;
                    if (field.getName().equals(MTSearchFields.CHANNEL_UPDATE_USER.getFieldName()) || field.getName().equals(MTSearchFields.CHANNEL_UPDATE_TIME.getFieldName())
                            || field.getName().equals(MTSearchFields.CHANNEL_MOBILE_TERMINAL.getFieldName())) {
                            continue;
                    } else {
                        oldValue = FieldUtils.readDeclaredField(previousChannel, field.getName(), true);
                        newValue = FieldUtils.readDeclaredField(channel, field.getName(), true);
                    }
                    if (!Objects.equals(oldValue, newValue)) {
                        row.addNewItem(field.getName(), oldValue, newValue);
                    }
                }
                returnList.add(row);
                previousChannel = channel;
            }

            return returnList;
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
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

}
