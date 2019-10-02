package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.CustomCodeDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class CustomCodesDaoTestIT extends TransactionalTests {

    private static final String CONSTANT = "TESTcarrieractiveTEST";

    private Random rnd = new Random();

    @Inject
    private CustomCodeDao customCodesDao;

    @Test
    @OperateOnDeployment("normal")
    public void create() {
        CustomCode record_active = createHelper(CONSTANT, true);
        CustomCode record_inactive = createHelper(CONSTANT, false);
        CustomCode createdrecord_active = customCodesDao.create(record_active);
        String constant_active = createdrecord_active.getPrimaryKey().getConstant();
        String value_active = createdrecord_active.getPrimaryKey().getCode();
        CustomCode fetched_record = customCodesDao.get(createdrecord_active.getPrimaryKey());
        assertEquals(constant_active, fetched_record.getPrimaryKey().getConstant());
        assertEquals(value_active, fetched_record.getPrimaryKey().getCode());
        CustomCode createdrecord_inactive = customCodesDao.create(record_inactive);
        String constant_inactive = createdrecord_inactive.getPrimaryKey().getConstant();
        String value_inactive = createdrecord_inactive.getPrimaryKey().getCode();
        CustomCode fetched_inactiverecord = customCodesDao.get(createdrecord_inactive.getPrimaryKey());
        assertEquals(constant_inactive, fetched_inactiverecord.getPrimaryKey().getConstant());
        assertEquals(value_inactive, fetched_inactiverecord.getPrimaryKey().getCode());
        List<CustomCode> rs = customCodesDao.getAllFor(record_active.getPrimaryKey().getConstant());
        assertEquals(2, rs.size());
        customCodesDao.delete(record_active.getPrimaryKey());
        customCodesDao.delete(record_inactive.getPrimaryKey());
    }

    @Test
    @OperateOnDeployment("normal")
    public void get() {
        CustomCode record = createHelper(CONSTANT, true);
        CustomCode createdrecord = customCodesDao.create(record);
        CustomCode rec = customCodesDao.get(createdrecord.getPrimaryKey());
        assertNotNull(rec);
        customCodesDao.delete(record.getPrimaryKey());
    }

    @Test
    @OperateOnDeployment("normal")
    public void exists() {
        CustomCode record = createHelper(CONSTANT, true);
        CustomCode created = customCodesDao.create(record);
        Boolean exists = customCodesDao.exists(created.getPrimaryKey());
        Assert.assertTrue(exists);
        customCodesDao.delete(record.getPrimaryKey());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() {
        for (int i = 0; i < 10; i++) {
            CustomCode record = createHelper(CONSTANT, "kod" + i, "description" + i);
            customCodesDao.create(record);
        }
        em.flush();
        for (int i = 0; i < 10; i++) {
            CustomCode record = createHelper(CONSTANT + "2", "kod" + i, "description" + i);
            customCodesDao.create(record);
        }
        em.flush();
        List<CustomCode> rs1 = customCodesDao.getAllFor(CONSTANT);
        List<CustomCode> rs2 = customCodesDao.getAllFor(CONSTANT + "2");
        assertEquals(10, rs1.size());
        assertEquals(10, rs2.size());

        customCodesDao.deleteAllFor(CONSTANT);
        rs1 = customCodesDao.getAllFor(CONSTANT);
        assertEquals(0, rs1.size());

        customCodesDao.deleteAllFor(CONSTANT + "2");
        rs2 = customCodesDao.getAllFor(CONSTANT + "2");
        assertEquals(0, rs2.size());
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() {

        CustomCode record = createHelper(CONSTANT, "kod", "description");
        CustomCode created = customCodesDao.create(record);
        String createdDescription = created.getDescription();

        created.setDescription("CHANGED");
        customCodesDao.update(created.getPrimaryKey(), "CHANGED");
        em.flush();

        CustomCode fetched = customCodesDao.get(created.getPrimaryKey());

        assertNotEquals(createdDescription, fetched.getDescription());
        assertEquals("CHANGED", fetched.getDescription());

        customCodesDao.deleteAllFor(CONSTANT);
    }

    @Test
    @OperateOnDeployment("normal")
    public void storeLatest() {

        CustomCode record = createHelper(CONSTANT, "kod", "description");
        CustomCode created = customCodesDao.replace(record);

        CustomCode aNewCustomCode = new CustomCode();

        aNewCustomCode.setPrimaryKey(created.getPrimaryKey());
        aNewCustomCode.setDescription("STORE_LATEST");

        customCodesDao.replace(aNewCustomCode);
        em.flush();

        CustomCode fetched = customCodesDao.get(created.getPrimaryKey());
        assertNotNull(fetched);
        customCodesDao.deleteAllFor(CONSTANT);
    }

    private CustomCode createHelper(String constant, Boolean active) {
        CustomCode record = new CustomCode();
        if (active) {
            CustomCodesPK primaryKey = createPrimaryKey(constant, "1");
            record.setPrimaryKey(primaryKey);
            record.setDescription("Active");
        } else {
            CustomCodesPK primaryKey = createPrimaryKey(constant, "0");
            record.setPrimaryKey(primaryKey);
            record.setDescription("InActive");
        }
        return record;
    }

    private CustomCode createHelper(String constant, Boolean active, CustomCodesPK primaryKey) {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode record = new CustomCode();
        if (active) {
            record.setPrimaryKey(primaryKey);
            record.setDescription("Active");
        } else {
            record.setPrimaryKey(primaryKey);
            record.setDescription("InActive");
        }
        return record;
    }

    private CustomCode createHelper(String constant, String code, String descr) {
        CustomCode record = new CustomCode();
        CustomCodesPK primaryKey = createPrimaryKey(constant, code);
        record.setPrimaryKey(primaryKey);
        record.setDescription(descr);
        return record;
    }

    private CustomCodesPK createPrimaryKey(String constant, String code) {
        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);
        return new CustomCodesPK(constant, code, fromDate, toDate);
    }
}
