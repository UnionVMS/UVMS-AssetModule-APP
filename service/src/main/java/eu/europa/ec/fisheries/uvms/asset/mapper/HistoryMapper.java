package eu.europa.ec.fisheries.uvms.asset.mapper;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChangeHistoryRow;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChannelChangeHistory;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.MobileTerminalDto;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryMapper {


    public static final String ASSET_UPDATER_FIELD = "updatedBy";
    public static final String ASSET_UPDATE_TIME_FIELD = "updateTime";
    public static final String ASSET_MOBILE_TERMINALS_FIELD = "mobileTerminals";

    public static final String MOBILE_TERMINAL_UPDATER_FIELD = "updateuser";
    public static final String MOBILE_TERMINAL_UPDATE_TIME_FIELD = "updatetime";
    public static final String MOBILE_TERMINAL_PLUGIN_FIELD = "plugin";
    public static final String MOBILE_TERMINAL_CHANNEL_FIELD = "channels";
    public static final String MOBILE_TERMINAL_HISTORY_ID = "historyId";

    public static final String CHANNEL_UPDATER_FIELD = "updateUser";
    public static final String CHANNEL_UPDATE_TIME_FIELD = "updateTime";
    public static final String CHANNEL_MOBILE_TERMINAL_FIELD = "mobileTerminal";
    public static final String CHANNEL_HISTORY_ID = "historyId";

    public static List<ChangeHistoryRow> assetChangeHistory(List<Asset> histories) {
        try {
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
                ChangeHistoryRow row = new ChangeHistoryRow(updater, updateTime);
                for (Field field : fields) {
                    Object oldValue;
                    Object newValue;
                    if (field.getName().equals(ASSET_UPDATER_FIELD) || field.getName().equals(ASSET_UPDATE_TIME_FIELD)
                            || field.getName().equals(ASSET_MOBILE_TERMINALS_FIELD)) {
                        if(!field.getName().equals(ASSET_MOBILE_TERMINALS_FIELD)) {
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
                ChangeHistoryRow row = new ChangeHistoryRow(updater, updateTime);
                for (Field field : fields) {
                    Object oldValue;
                    Object newValue;
                    if (field.getName().equals(MOBILE_TERMINAL_UPDATER_FIELD)
                            || field.getName().equals(MOBILE_TERMINAL_UPDATE_TIME_FIELD)
                            || field.getName().equals(MOBILE_TERMINAL_PLUGIN_FIELD)
                            || field.getName().equals(MOBILE_TERMINAL_HISTORY_ID)) {
                        continue;
                    }else {
                        oldValue = FieldUtils.readDeclaredField(previousMt, field.getName(), true);
                        newValue = FieldUtils.readDeclaredField(mt, field.getName(), true);
                    }
                    if (!Objects.equals(oldValue, newValue)) {
                        if(field.getName().equals(MOBILE_TERMINAL_CHANNEL_FIELD)){
                            Map<UUID, ChannelChangeHistory> channelChangeHistoryRows = checkDifferencesBetweenChannels(previousMt.getChannels(), mt.getChannels());
                            row.setChannelChanges(channelChangeHistoryRows);
                        }else {
                            row.addNewItem(field.getName(), oldValue, newValue);
                        }
                    }
                }
                row.setSnapshot(mt);
                returnList.add(row);
                previousMt = mt;
            }

            return returnList;
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    private static Map<UUID, ChannelChangeHistory> checkDifferencesBetweenChannels(Set<ChannelDto> oldInputSet, Set<ChannelDto> newInputSet){
        Map<UUID, ChannelChangeHistory> returnMap = new HashMap<>();
        Set<ChannelDto> workingNewSet = new HashSet<>(newInputSet);

        for (ChannelDto channelDto : oldInputSet) { //for every channel in the old group, check if it exists in the new group
            Optional<ChannelDto> sameChannelInNewSet = workingNewSet.stream().filter(c -> c.getId().equals(channelDto.getId())).findAny();
            ChannelChangeHistory channelChangeHistory = new ChannelChangeHistory();
            if(sameChannelInNewSet.isPresent()) {
                if(!channelDto.getHistoryId().equals(sameChannelInNewSet.get().getHistoryId())) {
                    channelChangeHistory.setChanges(channelChangeHistory(Arrays.asList(channelDto, sameChannelInNewSet.get())).get(0).getChanges());
                    channelChangeHistory.setChangeType("UPDATED");
                }

                workingNewSet.remove(sameChannelInNewSet.get());

            }else{  //if the old channel is not among the new channels
                ChannelDto creatorAndTimeChannel = new ChannelDto();
                creatorAndTimeChannel.setUpdateUser(channelDto.getUpdateUser());
                creatorAndTimeChannel.setUpdateTime(channelDto.getUpdateTime());
                channelChangeHistory.setChanges(channelChangeHistory(Arrays.asList(channelDto, creatorAndTimeChannel)).get(0).getChanges());
                channelChangeHistory.setChangeType("REMOVED");

            }
            if (channelChangeHistory.getChanges() != null && !channelChangeHistory.getChanges().isEmpty()) {
                returnMap.put(channelDto.getId(), channelChangeHistory);
            }

        }
        for (ChannelDto channelDto : workingNewSet) {   // new channels that where not in the old set
            ChannelChangeHistory newChannelAddition = new ChannelChangeHistory();
            newChannelAddition.setChanges(channelChangeHistory(Arrays.asList(new ChannelDto(), channelDto)).get(0).getChanges());
            newChannelAddition.setChangeType("CREATED");

            returnMap.put(channelDto.getId(), newChannelAddition);
        }
        return returnMap;
    }

    public static List<ChangeHistoryRow> channelChangeHistory(List<ChannelDto> histories) {
        try {
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
                ChangeHistoryRow row = new ChangeHistoryRow(updater, updateTime);
                for (Field field : fields) {
                    Object oldValue;
                    Object newValue;
                    if (field.getName().equals(CHANNEL_UPDATER_FIELD)
                            || field.getName().equals(CHANNEL_UPDATE_TIME_FIELD)
                            || field.getName().equals(CHANNEL_MOBILE_TERMINAL_FIELD)
                            || field.getName().equals(CHANNEL_HISTORY_ID)) {
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
