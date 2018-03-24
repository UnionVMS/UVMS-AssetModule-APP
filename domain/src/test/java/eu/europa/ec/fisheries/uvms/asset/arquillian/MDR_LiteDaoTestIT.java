package eu.europa.ec.fisheries.uvms.asset.arquillian;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.dao.MDR_LiteDao;
import eu.europa.ec.fisheries.uvms.entity.MDR_Lite;
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
public class MDR_LiteDaoTestIT extends TransactionalTests {

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
    MDR_LiteDao mdrlitedao;


    @Test
    @OperateOnDeployment("normal")
    public void create() throws JsonProcessingException {

        MDR_Lite record_active = createHelper(CONSTANT, true);
        MDR_Lite record_inactive = createHelper(CONSTANT, false);

        MDR_Lite createdrecord_active = mdrlitedao.create(record_active);
        String constant_active = createdrecord_active.getConstant();
        String value_active = createdrecord_active.getCode();

        MDR_Lite fetched_record = mdrlitedao.get(constant_active, value_active);

        Assert.assertEquals(constant_active, fetched_record.getConstant());
        Assert.assertEquals(value_active, fetched_record.getCode());

        MDR_Lite createdrecord_inactive = mdrlitedao.create(record_inactive);
        String constant_inactive = createdrecord_inactive.getConstant();
        String value_inactive = createdrecord_inactive.getCode();

        MDR_Lite fetched_inactiverecord = mdrlitedao.get(constant_inactive, value_inactive);

        Assert.assertEquals(constant_inactive, fetched_inactiverecord.getConstant());
        Assert.assertEquals(value_inactive, fetched_inactiverecord.getCode());

        List<MDR_Lite> rs = mdrlitedao.getAllFor(record_active.getConstant());
        Assert.assertEquals(rs.size(), 2);


        mdrlitedao.delete(record_active.getConstant(), record_active.getCode());
        mdrlitedao.delete(record_active.getConstant(), record_inactive.getCode());

    }


    @Test
    @OperateOnDeployment("normal")
    public void tryToCreateDups() throws JsonProcessingException {

        MDR_Lite record1 = createHelper(CONSTANT, true);
        MDR_Lite record2 = createHelper(CONSTANT, true);

        MDR_Lite createdrecord1 = mdrlitedao.create(record1);
        MDR_Lite createdrecord2 = mdrlitedao.create(record2);


        List<MDR_Lite> rs = mdrlitedao.getAllFor(record1.getConstant());
        Assert.assertEquals(rs.size(), 1);

        mdrlitedao.delete(record1.getConstant(), record1.getCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void get() throws JsonProcessingException {

        MDR_Lite record = createHelper(CONSTANT, true);

        MDR_Lite createdrecord = mdrlitedao.create(record);

        MDR_Lite rec = mdrlitedao.get(CONSTANT, createdrecord.getCode());
        Assert.assertNotNull(rec);

        mdrlitedao.delete(record.getConstant(), record.getCode());
    }


    @Test
    @OperateOnDeployment("normal")
    public void exists() throws JsonProcessingException {

        MDR_Lite record = createHelper(CONSTANT, true);

        MDR_Lite createdrecord = mdrlitedao.create(record);

        Boolean exists = mdrlitedao.exists(CONSTANT, createdrecord.getCode());
        Assert.assertTrue(exists);

        mdrlitedao.delete(record.getConstant(), record.getCode());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() throws JsonProcessingException {

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            MDR_Lite record = createHelper(CONSTANT, "kod" + iStr, "description" + iStr);
            mdrlitedao.create(record);
        }

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            MDR_Lite record = createHelper(CONSTANT + "2", "kod" + iStr, "description" + iStr);
            mdrlitedao.create(record);
        }

        List<MDR_Lite> rs1 = mdrlitedao.getAllFor(CONSTANT);
        List<MDR_Lite> rs2 = mdrlitedao.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 10);

        mdrlitedao.deleteAllFor(CONSTANT + "2");

        rs1 = mdrlitedao.getAllFor(CONSTANT);
        rs2 = mdrlitedao.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 0);

        mdrlitedao.deleteAllFor(CONSTANT);

        rs1 = mdrlitedao.getAllFor(CONSTANT);
        rs2 = mdrlitedao.getAllFor(CONSTANT);
        Assert.assertEquals(rs1.size(), 0);
        Assert.assertEquals(rs2.size(), 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() throws JsonProcessingException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SystemException, NotSupportedException {

        MDR_Lite record = createHelper(CONSTANT, "kod" , "description" );
        MDR_Lite created_record = mdrlitedao.create(record);
        String createdDescription = created_record.getDescription();

        created_record.setDescription("CHANGED");
        mdrlitedao.update(created_record.getConstant(), created_record.getCode(),"CHANGED",null);
        userTransaction.commit();
        userTransaction.begin();

        MDR_Lite fetched_record = mdrlitedao.get(created_record.getConstant(), created_record.getCode());

        Assert.assertNotEquals(createdDescription,fetched_record.getDescription());
        Assert.assertEquals("CHANGED",fetched_record.getDescription());

        mdrlitedao.deleteAllFor(CONSTANT);
    }


    private MDR_Lite createHelper(String constant, Boolean active) throws JsonProcessingException {

        MDR_Lite record = new MDR_Lite();
        record.setConstant(constant);
        if (active) {
            record.setCode("1");
            record.setDescription("Active");
            ExtraData extradata = new ExtraData(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            String extradataJson = MAPPER.writeValueAsString(extradata);
            record.setJsonstr(extradataJson);
        } else {
            record.setCode("0");
            record.setDescription("InActive");
            ExtraData extradata = new ExtraData(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            String extradataJson = MAPPER.writeValueAsString(extradata);
            record.setJsonstr(extradataJson);
        }
        return record;
    }


    private MDR_Lite createHelper(String constant, String code, String descr) throws JsonProcessingException {

        MDR_Lite record = new MDR_Lite();
        record.setConstant(constant);
        record.setCode(code);
        record.setDescription(descr);
        return record;
    }


}


