package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCodesPK;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

public class CustomCodesPkTest {

    @Test
    public void testEquals() {

        String constant = "TESTCONSTANT____";
        String code = "TESTCODE___";

        Instant from = Instant.now(Clock.systemUTC());
        from = from.minus(5, ChronoUnit.DAYS);

        Instant to = Instant.now(Clock.systemUTC());
        to = from.plus(5, ChronoUnit.DAYS);

        // All equal parameters
        CustomCodesPK primaryKey1 = new CustomCodesPK(constant, code, from, to);
        CustomCodesPK primaryKey2 = new CustomCodesPK(constant, code, from, to);
        Assert.assertEquals(primaryKey1, primaryKey2);
        //Assert.assertTrue(primaryKey1.equals(primaryKey2));

        // Unequal constant parameters
        primaryKey1 = new CustomCodesPK(constant, code, from, to);
        primaryKey2 = new CustomCodesPK(constant+"xx", code, from, to);
        Assert.assertNotEquals(primaryKey1, primaryKey2);

        // Unequal code parameters
        primaryKey1 = new CustomCodesPK(constant, code, from, to);
        primaryKey2 = new CustomCodesPK(constant, code+"xx", from, to);
        Assert.assertNotEquals(primaryKey1, primaryKey2);

        // Unequal from parameters
        primaryKey1 = new CustomCodesPK(constant, code, from, to);
        primaryKey2 = new CustomCodesPK(constant, code, from.plus(1, ChronoUnit.DAYS), to);
        Assert.assertNotEquals(primaryKey1, primaryKey2);

        // Unequal to parameters
        primaryKey1 = new CustomCodesPK(constant, code, from, to);
        primaryKey2 = new CustomCodesPK(constant, code, from, to.plus(1, ChronoUnit.DAYS));
        Assert.assertNotEquals(primaryKey1, primaryKey2);
    }

    @Test
    public void CustomCodesPKTest () {
        CustomCodesPK customcodespk = new CustomCodesPK("TEST","testing");
        assertEquals("TEST", customcodespk.getConstant());
        assertEquals("testing", customcodespk.getCode());
    }
}
