package eu.europa.ec.fisheries.uvms.asset.arquillian;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.CustomCodeDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.transaction.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RunWith(Arquillian.class)
public class CustomCodesDaoTestIT extends TransactionalTests {

    private static final String CONSTANT = "TESTcarrieractiveTEST";


    private ObjectMapper MAPPER = new ObjectMapper();


    @Inject
    CustomCodeDao mdrlitedao;


    @Test
    @OperateOnDeployment("normal")
    public void create() throws JsonProcessingException {


        CustomCode record_active = createHelper(CONSTANT, true);
        CustomCode record_inactive = createHelper(CONSTANT, false);
        CustomCode createdrecord_active = mdrlitedao.create(record_active);
        String constant_active = createdrecord_active.getPrimaryKey().getConstant();
        String value_active = createdrecord_active.getPrimaryKey().getCode();
        CustomCode fetched_record = mdrlitedao.get(createdrecord_active.getPrimaryKey());
        Assert.assertEquals(constant_active, fetched_record.getPrimaryKey().getConstant());
        Assert.assertEquals(value_active, fetched_record.getPrimaryKey().getCode());
        CustomCode createdrecord_inactive = mdrlitedao.create(record_inactive);
        String constant_inactive = createdrecord_inactive.getPrimaryKey().getConstant();
        String value_inactive = createdrecord_inactive.getPrimaryKey().getCode();
        CustomCode fetched_inactiverecord = mdrlitedao.get(createdrecord_inactive.getPrimaryKey());
        Assert.assertEquals(constant_inactive, fetched_inactiverecord.getPrimaryKey().getConstant());
        Assert.assertEquals(value_inactive, fetched_inactiverecord.getPrimaryKey().getCode());
        List<CustomCode> rs = mdrlitedao.getAllFor(record_active.getPrimaryKey().getConstant());
        Assert.assertEquals(rs.size(), 2);
        mdrlitedao.delete(record_active.getPrimaryKey());
        mdrlitedao.delete(record_inactive.getPrimaryKey());

    }


    @Test
    @OperateOnDeployment("normal")
    public void tryToCreateDups() throws JsonProcessingException {

        CustomCodesPK primaryKey = createPrimaryKey(CONSTANT,"aKOD");
        CustomCode record1 = createHelper(CONSTANT, true, primaryKey);
        CustomCode record2 = createHelper(CONSTANT, true, primaryKey);
        CustomCode createdrecord1 = mdrlitedao.create(record1);
        CustomCode createdrecord2 = mdrlitedao.create(record2);
        List<CustomCode> rs = mdrlitedao.getAllFor(record1.getPrimaryKey().getConstant());
        Assert.assertEquals(rs.size(), 1);
        mdrlitedao.delete(record1.getPrimaryKey());
    }


    @Test
    @OperateOnDeployment("normal")
    public void get() throws JsonProcessingException {

        CustomCode record = createHelper(CONSTANT, true);
        CustomCode createdrecord = mdrlitedao.create(record);
        CustomCode rec = mdrlitedao.get(createdrecord.getPrimaryKey());
        Assert.assertNotNull(rec);
        mdrlitedao.delete(record.getPrimaryKey());
    }


    @Test
    @OperateOnDeployment("normal")
    public void exists() throws JsonProcessingException {

        CustomCode record = createHelper(CONSTANT, true);
        CustomCode createdrecord = mdrlitedao.create(record);
        Boolean exists = mdrlitedao.exists(createdrecord.getPrimaryKey());
        Assert.assertTrue(exists);
        mdrlitedao.delete(record.getPrimaryKey());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() throws JsonProcessingException {

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            CustomCode record = createHelper(CONSTANT, "kod" + iStr, "description" + iStr);
            mdrlitedao.create(record);
        }

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            CustomCode record = createHelper(CONSTANT + "2", "kod" + iStr, "description" + iStr);
            mdrlitedao.create(record);
        }

        List<CustomCode> rs1 = mdrlitedao.getAllFor(CONSTANT);
        List<CustomCode> rs2 = mdrlitedao.getAllFor(CONSTANT + "2");
        Assert.assertEquals(10,rs1.size());
        Assert.assertEquals(10, rs2.size());

        mdrlitedao.deleteAllFor(CONSTANT + "2");

        rs1 = mdrlitedao.getAllFor(CONSTANT);
        rs2 = mdrlitedao.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 0);

        mdrlitedao.deleteAllFor(CONSTANT);

        rs1 = mdrlitedao.getAllFor(CONSTANT);
        rs2 = mdrlitedao.getAllFor(CONSTANT + "2");
        Assert.assertEquals(0,rs1.size());
        Assert.assertEquals(0, rs2.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() throws JsonProcessingException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SystemException, NotSupportedException {

        CustomCode record = createHelper(CONSTANT, "kod", "description");
        CustomCode created_record = mdrlitedao.create(record);
        String createdDescription = created_record.getDescription();

        created_record.setDescription("CHANGED");
        mdrlitedao.update(created_record.getPrimaryKey(), "CHANGED", null);
        userTransaction.commit();
        userTransaction.begin();

        CustomCode fetched_record = mdrlitedao.get(created_record.getPrimaryKey());

        Assert.assertNotEquals(createdDescription, fetched_record.getDescription());
        Assert.assertEquals("CHANGED", fetched_record.getDescription());

        mdrlitedao.deleteAllFor(CONSTANT);
    }

    @Test
    @OperateOnDeployment("normal")
    public void storeLatest() throws JsonProcessingException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SystemException, NotSupportedException {

        CustomCode record = createHelper(CONSTANT, "kod", "description");
        CustomCode created_record = mdrlitedao.storeLatest(record);

        CustomCode aNewCustomCode = new CustomCode();

        aNewCustomCode.setPrimaryKey(created_record.getPrimaryKey());
        aNewCustomCode.setDescription("STORE_LATEST");

        Map<String,String> props = new HashMap<>();

        props.put("A_STORED_ONE", "DATA_DATA_DATA");

        aNewCustomCode.setNameValue(props);
        mdrlitedao.storeLatest(aNewCustomCode);
        userTransaction.commit();
        userTransaction.begin();

        CustomCode fetched_record = mdrlitedao.get(created_record.getPrimaryKey());
        Map<String,String> fetchedProps = fetched_record.getNameValue();
        Assert.assertNotNull(fetchedProps);
        Assert.assertTrue(fetchedProps.containsKey("A_STORED_ONE"));
        Assert.assertTrue(!fetchedProps.containsKey("status"));

        mdrlitedao.deleteAllFor(CONSTANT);
    }





    Random rnd = new Random();

    private CustomCode createHelper(String constant, Boolean active) throws JsonProcessingException {
        CustomCode record = new CustomCode();
        if (active) {
            CustomCodesPK primaryKey = createPrimaryKey(constant, "1");
            record.setPrimaryKey(primaryKey);
            record.setDescription("Active");
            record.getNameValue().put("status", "active");
        } else {
            CustomCodesPK primaryKey = createPrimaryKey(constant, "0");
            record.setPrimaryKey(primaryKey);
            record.setDescription("InActive");
            record.getNameValue().put("status", "inactive");

        }
        return record;
    }

    private CustomCode createHelper(String constant, Boolean active, CustomCodesPK primaryKey) throws JsonProcessingException {
        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode record = new CustomCode();
        if (active) {
            record.setPrimaryKey(primaryKey);
            record.setDescription("Active");
            record.getNameValue().put("status", "active");
        } else {
            record.setPrimaryKey(primaryKey);
            record.setDescription("InActive");
            record.getNameValue().put("status", "inactive");
        }
        return record;
    }

    private CustomCode createHelper(String constant, String code, String descr) throws JsonProcessingException {

        CustomCode record = new CustomCode();
        CustomCodesPK primaryKey = createPrimaryKey(constant, code);
        record.setPrimaryKey(primaryKey);
        record.setDescription(descr);
        return record;
    }



    private CustomCodesPK createPrimaryKey(String constant, String code) {

        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);
        CustomCodesPK primaryKey = new CustomCodesPK(constant, code, fromDate, toDate);
        return primaryKey;
    }



}


