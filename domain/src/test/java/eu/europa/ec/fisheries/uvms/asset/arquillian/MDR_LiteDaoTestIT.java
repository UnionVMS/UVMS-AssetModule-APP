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
import java.util.List;
import java.util.UUID;

@RunWith(Arquillian.class)
public class MDR_LiteDaoTestIT  extends TransactionalTests {

    private class ExtraData{

        private String data1;
        private String data2;
        public ExtraData(){}

        public ExtraData(String data1, String data2){
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

        MDR_Lite record_active = createHelper(true);
        MDR_Lite record_inactive = createHelper(false);

        MDR_Lite createdrecord_active =  mdrlitedao.create(record_active);
        String constant_active = createdrecord_active.getConstant();
        String value_active = createdrecord_active.getCode();

        MDR_Lite fetched_record =  mdrlitedao.get(constant_active,value_active);

        Assert.assertEquals(constant_active,fetched_record.getConstant());
        Assert.assertEquals(value_active,fetched_record.getCode());

        MDR_Lite createdrecord_inactive =  mdrlitedao.create(record_inactive);
        String constant_inactive = createdrecord_inactive.getConstant();
        String value_inactive = createdrecord_inactive.getCode();

        MDR_Lite fetched_inactiverecord =  mdrlitedao.get(constant_inactive,value_inactive);

        Assert.assertEquals(constant_inactive,fetched_inactiverecord.getConstant());
        Assert.assertEquals(value_inactive,fetched_inactiverecord.getCode());

        List<MDR_Lite> rs =  mdrlitedao.getAllFor(record_active.getConstant());
        Assert.assertEquals(rs.size(),2);


        mdrlitedao.delete(record_active.getConstant(),record_active.getCode());
        mdrlitedao.delete(record_active.getConstant(),record_inactive.getCode());


    }


    @Test
    @OperateOnDeployment("normal")
    public void tryToCreateDups() throws JsonProcessingException {

        MDR_Lite record1 = createHelper(true);
        MDR_Lite record2 = createHelper(true);

        MDR_Lite createdrecord1 =  mdrlitedao.create(record1);
        MDR_Lite createdrecord2 =  mdrlitedao.create(record2);


        List<MDR_Lite> rs =  mdrlitedao.getAllFor(record1.getConstant());
        Assert.assertEquals(rs.size(),1);

        mdrlitedao.delete(record1.getConstant(),record1.getCode());


    }






    private MDR_Lite createHelper(Boolean active ) throws JsonProcessingException {

        MDR_Lite record = new MDR_Lite();

        record.setConstant("TESTcarrieractiveTEST");
        if(active) {
            record.setCode("1");
            record.setDescription("Active");

            ExtraData extradata   = new ExtraData(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            String extradataJson = MAPPER.writeValueAsString(extradata);
            record.setJsonstr(extradataJson);
        }else{
            record.setCode("0");
            record.setDescription("InActive");

            ExtraData extradata   = new ExtraData(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            String extradataJson = MAPPER.writeValueAsString(extradata);
            record.setJsonstr(extradataJson);

        }

        return record;

    }

}


