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
import java.util.List;
import java.util.UUID;

@RunWith(Arquillian.class)
public class CustomCodesDaoTestIT extends TransactionalTests {

    private static final String CONSTANT = "TESTcarrieractiveTEST";

    private class ExtraData {

        private String data1;
        private String data2;

        public ExtraData() {
        }

        public ExtraData(String data1, String data2) {
            this.data1 = data1;
            this.data2 = data2;
        }

        public String getData1() {
            return data1;
        }

        public void setData1(String data1) {
            this.data1 = data1;
        }

        public String getData2() {
            return data2;
        }

        public void setData2(String data2) {
            this.data2 = data2;
        }
    }


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

        CustomCode record1 = createHelper(CONSTANT, true);
        CustomCode record2 = createHelper(CONSTANT, true);

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
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 10);

        mdrlitedao.deleteAllFor(CONSTANT + "2");

        rs1 = mdrlitedao.getAllFor(CONSTANT);
        rs2 = mdrlitedao.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 0);

        mdrlitedao.deleteAllFor(CONSTANT);

        rs1 = mdrlitedao.getAllFor(CONSTANT);
        rs2 = mdrlitedao.getAllFor(CONSTANT+"2");
        Assert.assertEquals(rs1.size(), 0);
        Assert.assertEquals(rs2.size(), 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() throws JsonProcessingException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SystemException, NotSupportedException {

        CustomCode record = createHelper(CONSTANT, "kod" , "description" );
        CustomCode created_record = mdrlitedao.create(record);
        String createdDescription = created_record.getDescription();

        created_record.setDescription("CHANGED");
        mdrlitedao.update(created_record.getPrimaryKey(),"CHANGED",null);
        userTransaction.commit();
        userTransaction.begin();

        CustomCode fetched_record = mdrlitedao.get(created_record.getPrimaryKey());

        Assert.assertNotEquals(createdDescription,fetched_record.getDescription());
        Assert.assertEquals("CHANGED",fetched_record.getDescription());

        mdrlitedao.deleteAllFor(CONSTANT);
    }


    private CustomCode createHelper(String constant, Boolean active) throws JsonProcessingException {


        CustomCode record = new CustomCode();
        if (active) {
            CustomCodesPK primaryKey = new CustomCodesPK(constant, "1");
            record.setPrimaryKey(primaryKey);
            record.setDescription("Active");
            ExtraData extradata = new ExtraData(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            String extradataJson = MAPPER.writeValueAsString(extradata);
            record.setExtraData(extradataJson);
        } else {
            CustomCodesPK primaryKey = new CustomCodesPK(constant, "0");
            record.setPrimaryKey(primaryKey);
            record.setDescription("InActive");
            ExtraData extradata = new ExtraData(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            String extradataJson = MAPPER.writeValueAsString(extradata);
            record.setExtraData(extradataJson);
        }
        return record;
    }


    private CustomCode createHelper(String constant, String code, String descr) throws JsonProcessingException {

        CustomCode record = new CustomCode();
        CustomCodesPK primaryKey = new CustomCodesPK(constant, code);
        record.setPrimaryKey(primaryKey);
        record.setDescription(descr);
        return record;
    }


}


