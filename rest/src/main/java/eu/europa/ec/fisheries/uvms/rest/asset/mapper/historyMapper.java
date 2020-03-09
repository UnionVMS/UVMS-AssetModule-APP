package eu.europa.ec.fisheries.uvms.rest.asset.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class historyMapper {

    /*@Test
    public void testSaneHistory() throws IllegalAccessException, JsonProcessingException {
        List<AssetDTO> assetHistories = AssetTestHelper.getAssetHistoryFromAssetGuid(UUID.fromString("edb02fad-f71d-422b-a1d9-57f2e646840d"));

        sanerHistory(assetHistories, "updateTime");


    }

    private void sanerHistory(List<AssetDTO> assetHistories, String keyField) throws IllegalAccessException, JsonProcessingException {
        List<Field> fields = listMembers(new AssetDTO());

        AssetDTO previousAsset = new AssetDTO();
        Map<String, List<String>> map = new HashMap<>(assetHistories.size());
        for (AssetDTO asset : assetHistories) {
            if(previousAsset == null){
                previousAsset = asset;
                continue;
            }
            String key = "" + FieldUtils.readDeclaredField(asset, keyField, true);
            map.put(key, new ArrayList<>());
            for (Field field : fields) {
                if(!field.getName().equals(keyField)){
                    Object oldValue = FieldUtils.readDeclaredField(previousAsset, field.getName(), true);
                    Object newValue = FieldUtils.readDeclaredField(asset, field.getName(), true);
                    if(!Objects.equals(oldValue, newValue)){
                        map.get(key).add(field.getName() + " OLD: " + oldValue + " NEW: " + newValue);
                    }
                }
            }
            previousAsset = asset;
        }

        String out = AssetTestHelper.writeValueAsString(map);
        System.out.println(out);
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
            //Handle your exception here.
        }
        return fields;
    }*/

}
