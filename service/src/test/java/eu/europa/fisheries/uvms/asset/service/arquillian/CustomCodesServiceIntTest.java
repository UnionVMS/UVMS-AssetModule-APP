package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.asset.service.CustomCodesService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.transaction.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;


@RunWith(Arquillian.class)
public class CustomCodesServiceIntTest extends TransactionalTests {


    private Map<String, String> map() {

        Map<String, String> map = new HashMap<>();
        map.put("KEY" + UUID.randomUUID().toString(), "VALUE" + UUID.randomUUID().toString());
        return map;
    }


    Random rnd = new Random();


    @EJB
    CustomCodesService service;

    private static final String CONSTANT = "testconstant";
    private static final String CODE = "testcode";

    @Test
    @OperateOnDeployment("normal")
    public void create() {

        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode createdMDR_lite = service.create(CONSTANT, CODE, fromDate,toDate, CODE + "Description", map());
        CustomCode fetchedMDR_lite = service.get(CONSTANT, CODE,fromDate,toDate);
        Assert.assertNotNull(fetchedMDR_lite);
        service.delete(CONSTANT, CODE,fromDate,toDate);
    }

    @Test
    @OperateOnDeployment("normal")
    public void tryToCreateDups() {

        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode createdMDR_lite1 = service.create(CONSTANT, CODE, fromDate,toDate, CODE + "Description", map());
        CustomCode createdMDR_lite2 = service.create(CONSTANT, CODE, fromDate,toDate,CODE + "Description", map());
        List<CustomCode> rs = service.getAllFor(CONSTANT);
        Assert.assertEquals(1, rs.size());
        service.delete(CONSTANT, CODE,fromDate,toDate);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get() {

        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);


        CustomCode createdMDR_lite1 = service.create(CONSTANT, CODE, fromDate,toDate,CODE + "Description", map());
        CustomCode fetchedMDR_lite = service.get(CONSTANT, CODE,fromDate,toDate);
        Assert.assertNotNull(fetchedMDR_lite);
        service.delete(CONSTANT, CODE,fromDate,toDate);
    }


    @Test
    @OperateOnDeployment("normal")
    public void exists() {

        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);

        CustomCode createdMDR_lite1 = service.create(CONSTANT, CODE, fromDate,toDate,CODE + "Description", map());
        Boolean exist = service.exists(CONSTANT, CODE,fromDate,toDate);
        Assert.assertNotNull(exist);
        service.delete(CONSTANT, CODE,fromDate,toDate);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAllFor() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);


        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT, CODE + iStr, fromDate,toDate,CODE + "Description", map());
        }

        for (int i = 0; i < 10; i++) {
            String iStr = String.valueOf(i);
            service.create(CONSTANT + "2", CODE + iStr,fromDate,toDate, CODE + "Description", map());
        }

        List<CustomCode> rs1 = service.getAllFor(CONSTANT);
        List<CustomCode> rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 10);

        userTransaction.commit();
        userTransaction.begin();

        service.deleteAllFor(CONSTANT + "2");

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 10);
        Assert.assertEquals(rs2.size(), 0);

        service.deleteAllFor(CONSTANT);

        rs1 = service.getAllFor(CONSTANT);
        rs2 = service.getAllFor(CONSTANT + "2");
        Assert.assertEquals(rs1.size(), 0);
        Assert.assertEquals(rs2.size(), 0);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateDescription() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {


        Integer n = rnd.nextInt(10);
        Integer duration = rnd.nextInt(90);
        LocalDateTime fromDate = LocalDateTime.now(Clock.systemUTC());
        fromDate = fromDate.minusDays(n);
        LocalDateTime toDate = LocalDateTime.now(Clock.systemUTC());
        toDate = toDate.plusDays(duration);



        CustomCode created_record = service.create(CONSTANT, CODE, fromDate,toDate,CODE + "Description", map());
        String createdDescription = created_record.getDescription();

        created_record.setDescription("CHANGED");
        service.update(created_record.getPrimaryKey().getConstant(), created_record.getPrimaryKey().getCode(),fromDate,toDate, "CHANGED", null);
        userTransaction.commit();
        userTransaction.begin();

        CustomCode fetched_record = service.get(created_record.getPrimaryKey().getConstant(), created_record.getPrimaryKey().getCode(),fromDate,toDate);

        Assert.assertNotEquals(createdDescription, fetched_record.getDescription());
        Assert.assertEquals("CHANGED", fetched_record.getDescription());

        service.deleteAllFor(CONSTANT);
    }


}
