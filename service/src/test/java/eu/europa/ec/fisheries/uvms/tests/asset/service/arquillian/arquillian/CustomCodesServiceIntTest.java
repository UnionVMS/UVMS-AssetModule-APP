package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.CustomCodesService;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.transaction.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

@RunWith(Arquillian.class)
public class CustomCodesServiceIntTest extends TransactionalTests {

    private static final String CONSTANT = "testconstant";
    private static final String CODE = "testcode";
    private Random rnd = new Random();
    @EJB
    private CustomCodesService service;

    @Test
    @OperateOnDeployment("normal")
    public void create() {

        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode createdCustomCode = service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        CustomCode fetchedCustomCode = service.get(CONSTANT, CODE, fromDate, toDate);
        Assert.assertNotNull(fetchedCustomCode);
        service.delete(CONSTANT, CODE, fromDate, toDate);
    }

    @Test
    @OperateOnDeployment("normal")
    public void tryToCreateDups() {

        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode createdCustomCode1 = service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        try {
            CustomCode createdCustomCode2 = service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        }catch(Throwable e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void exists() {

        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode createdCustomCode1 = service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        Boolean exist = service.exists(CONSTANT, CODE, fromDate, toDate);
        Assert.assertNotNull(exist);
        service.delete(CONSTANT, CODE, fromDate, toDate);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT, CODE + iStr, fromDate, toDate, CODE + "Description");
        }

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT + "2", CODE + iStr, fromDate, toDate, CODE + "Description");
        }

        List<CustomCode> rs1 = service.getAllFor(CONSTANT);
        List<CustomCode> rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(10, rs1.size());
        Assert.assertEquals(10, rs2.size());

        userTransaction.commit();
        userTransaction.begin();

        service.deleteAllFor(CONSTANT + "2");

        userTransaction.commit();
        userTransaction.begin();

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(10, rs1.size());
        Assert.assertEquals(0, rs2.size());

        service.deleteAllFor(CONSTANT);
        userTransaction.commit();
        userTransaction.begin();

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(0, rs1.size());
        Assert.assertEquals(0, rs2.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode created_record = service.create(CONSTANT, CODE, fromDate, toDate, CODE + "Description");
        String createdDescription = created_record.getDescription();

        created_record.setDescription("CHANGED");
        service.update(created_record.getPrimaryKey().getConstant(), created_record.getPrimaryKey().getCode(), fromDate, toDate, "CHANGED");
        userTransaction.commit();
        userTransaction.begin();

        CustomCode fetched_record = service.get(created_record.getPrimaryKey().getConstant(), created_record.getPrimaryKey().getCode(), fromDate, toDate);

        Assert.assertNotEquals(createdDescription, fetched_record.getDescription());
        Assert.assertEquals("CHANGED", fetched_record.getDescription());

        service.deleteAllFor(CONSTANT);
        userTransaction.commit();
        userTransaction.begin();
    }

    private CustomCodesPK createPrimaryKey() {

        int n = rnd.nextInt(10);
        int duration = rnd.nextInt(90);
        OffsetDateTime fromDate = OffsetDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        OffsetDateTime toDate = OffsetDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);
        CustomCodesPK primaryKey = new CustomCodesPK();
        primaryKey.setConstant("TEST_constant_TEST");
        primaryKey.setCode("TEST_code_TEST");
        primaryKey.setValidFromDate(fromDate);
        primaryKey.setValidToDate(toDate);
        return primaryKey;
    }

    @Test
    @OperateOnDeployment("normal")
    public void storeLatest() {
        CustomCode customCode = new CustomCode();
        CustomCodesPK primaryKey = createPrimaryKey();

        customCode.setPrimaryKey(primaryKey);
        customCode.setDescription("TEST_DESCRIPTION_TEST");
        CustomCode created_record = service.replace(customCode);

        CustomCode aSecondCustomCode = new CustomCode();
        aSecondCustomCode.setPrimaryKey(primaryKey);
        aSecondCustomCode.setDescription("TEST_DESCRIPTION_TEST_SECONF");
        CustomCode creasted_ASecond = service.replace(aSecondCustomCode);

        CustomCode fetched_record = service.get(primaryKey);

        service.deleteAllFor("TEST_constant_TEST");
    }
}
