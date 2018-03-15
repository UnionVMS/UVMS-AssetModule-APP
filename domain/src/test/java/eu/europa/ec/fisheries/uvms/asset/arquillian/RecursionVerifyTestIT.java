package eu.europa.ec.fisheries.uvms.asset.arquillian;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import eu.europa.ec.fisheries.uvms.asset.types.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.stream.JsonParser;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(Arquillian.class)
public class RecursionVerifyTestIT  extends TransactionalTests{



    private ObjectMapper MAPPER ;


    @Before
    public void before(){
        MAPPER = new ObjectMapper();
        MAPPER.findAndRegisterModules();

    }


    @Test
    public void testJsonWithJackson() throws AssetGroupDaoException, IOException {

        /*
            <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>2.6.7</version>
        </dependency>

         */

        AssetGroupEntity groupEntity =  createAssetGroupEntity("test",5);
        String json = MAPPER.writeValueAsString(groupEntity);
        AssetGroupEntity deserialized = MAPPER.readValue(json, AssetGroupEntity.class);
        String json2 = MAPPER.writeValueAsString(deserialized);

        Assert.assertEquals(json,json2);
        System.out.println(json);
    }



    @Test
    public void testJsonWithJEEInternal() throws AssetGroupDaoException, IOException {

        AssetGroupEntity groupEntity =  createAssetGroupEntity("test",5);

        Jsonb jsonb = JsonbBuilder.create();

        String jsonStr = jsonb.toJson(groupEntity);


        System.out.println(jsonStr);



    }


    private AssetGroupEntity createAssetGroupEntity(String user, int numberOfGroupFields) {
        AssetGroupEntity assetGroupEntity = new AssetGroupEntity();

        LocalDateTime dt = LocalDateTime.now(Clock.systemUTC());
       // assetGroupEntity.setId(UUID.randomUUID());
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
         //   field.setId(i);
            groupFields.add(field);
        }
        assetGroupEntity.setFields(groupFields);
        return assetGroupEntity;
    }



}
