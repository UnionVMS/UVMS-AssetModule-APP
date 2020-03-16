package eu.europa.ec.fisheries.uvms.rest.asset.mapper;

import eu.europa.ec.fisheries.uvms.rest.asset.dto.ChangeHistoryRow;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HistoryMapper {

    public static List<ChangeHistoryRow> mapHistory(List<?> histories, String updaterField, String updateTimeField, String ...ignoredFields) throws IllegalAccessException {
        List<Field> fields = listMembers(histories.get(0));
        List<ChangeHistoryRow> returnList = new ArrayList<>(histories.size());
        List<String> ignoredList = new ArrayList<>();
        ignoredList.addAll(Arrays.asList(ignoredFields));
        ignoredList.add(updaterField);
        ignoredList.add(updateTimeField);


        Object previousObject = null;
        for (Object object : histories) {
            if(previousObject == null){
                previousObject = object;
                continue;
            }
            String updater = "" + FieldUtils.readDeclaredField(object, updaterField, true);
            Instant updateTime = (Instant)FieldUtils.readDeclaredField(object, updateTimeField, true);
            ChangeHistoryRow row = new ChangeHistoryRow(updater, updateTime);
            for (Field field : fields) {
                if(ignoredList.stream().anyMatch(s -> s.equals(field.getName()))){
                    continue;
                }
                Object oldValue = FieldUtils.readDeclaredField(previousObject, field.getName(), true);
                Object newValue = FieldUtils.readDeclaredField(object, field.getName(), true);
                if(!Objects.equals(oldValue, newValue)){
                    row.addNewItem(field.getName(), oldValue, newValue);
                }
            }
            returnList.add(row);
            previousObject = object;
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

}
