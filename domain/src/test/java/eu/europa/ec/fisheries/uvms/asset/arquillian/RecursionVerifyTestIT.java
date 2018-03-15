package eu.europa.ec.fisheries.uvms.asset.arquillian;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.types.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecursionVerifyTestIT {



    private ObjectMapper MAPPER = new ObjectMapper();


    @Test
    public void testJsonWithJackson() throws AssetGroupDaoException, IOException {

        /*
            <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>2.6.7</version>
        </dependency>

         */

        MAPPER.findAndRegisterModules();
        AssetGroupEntity groupEntity =  createAssetGroupEntity("test",5);
        String json = MAPPER.writeValueAsString(groupEntity);
        AssetGroupEntity deserialized = MAPPER.readValue(json, AssetGroupEntity.class);
        String json2 = MAPPER.writeValueAsString(deserialized);

        Assert.assertEquals(json,json2);
    }

    private AssetGroupEntity createAssetGroupEntity(String user, int numberOfGroupFields) {
        AssetGroupEntity assetGroupEntity = new AssetGroupEntity();

        LocalDateTime dt = LocalDateTime.now(Clock.systemUTC());
        assetGroupEntity.setId(UUID.randomUUID());
        assetGroupEntity.setUpdatedBy("test");
        assetGroupEntity.setUpdateTime(dt);
        assetGroupEntity.setArchived(false);
        assetGroupEntity.setName("The Name");
        assetGroupEntity.setOwner(user);
        assetGroupEntity.setDynamic(false);
        assetGroupEntity.setGlobal(true);


        List<AssetGroupField> groupFields = new ArrayList<>();
        for (long i = 0; i < numberOfGroupFields; i++) {
            AssetGroupField field = new AssetGroupField();
            field.setAssetGroup(assetGroupEntity);
            field.setUpdatedBy(user);
            field.setUpdateTime(dt);
            field.setField(ConfigSearchFieldEnum.GUID.value());
            field.setValue(UUID.randomUUID().toString());
            field.setId(i);
            groupFields.add(field);
        }
        assetGroupEntity.setFields(groupFields);
        return assetGroupEntity;
    }



}
