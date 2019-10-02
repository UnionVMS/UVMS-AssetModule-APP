package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by roblar on 2017-04-28.
 */
@RunWith(Arquillian.class)
public class MobileTerminalPluginDaoBeanIntTest extends TransactionalTests {

    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginListWithNewPlugin() {
        List<MobileTerminalPlugin> mobileTerminalPluginListBefore = mobileTerminalPluginDao.getPluginList();
        assertNotNull(mobileTerminalPluginListBefore);

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        List<MobileTerminalPlugin> mobileTerminalPluginList = mobileTerminalPluginDao.getPluginList();

        assertNotNull(mobileTerminalPlugin.getId());
        assertNotNull(mobileTerminalPluginList);
        assertEquals(mobileTerminalPluginListBefore.size() + 1, mobileTerminalPluginList.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin()  {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        MobileTerminalPlugin mobileTerminalPluginAfterCreation = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        MobileTerminalPlugin mobileTerminalPluginReadFromDatabase = mobileTerminalPluginDao.getPluginByServiceName(mobileTerminalPlugin.getPluginServiceName());

        assertNotNull(mobileTerminalPluginAfterCreation);
        assertNotNull(mobileTerminalPluginReadFromDatabase);
        assertSame(mobileTerminalPlugin, mobileTerminalPluginAfterCreation);
        assertEquals(mobileTerminalPlugin.getPluginServiceName(), mobileTerminalPluginReadFromDatabase.getPluginServiceName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_persistNullEntityFailsWithTerminalDaoException() {
        try {
            mobileTerminalPluginDao.createMobileTerminalPlugin(null);
            Assert.fail(); // it MUST fail so coming here is ERROR
        } catch (EJBTransactionRolledbackException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_nameConstraintViolation()  {
        thrown.expect(ConstraintViolationException.class);

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] name = new char[41];
        Arrays.fill(name, 'x');
        mobileTerminalPlugin.setName(new String(name));

        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_descriptionConstraintViolation() {
        thrown.expect(ConstraintViolationException.class);

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] description = new char[81];
        Arrays.fill(description, 'x');
        mobileTerminalPlugin.setDescription(new String(description));

        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_serviceNameConstraintViolation() {
        thrown.expect(ConstraintViolationException.class);

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] serviceName = new char[501];
        Arrays.fill(serviceName, 'x');
        mobileTerminalPlugin.setPluginServiceName(new String(serviceName));

        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_satelliteTypeConstraintViolation() {
        thrown.expect(ConstraintViolationException.class);

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] satelliteType = new char[51];
        Arrays.fill(satelliteType, 'x');
        mobileTerminalPlugin.setPluginSatelliteType(new String(satelliteType));

        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateMobileTerminalPlugin_updateUserConstraintViolation() {
        thrown.expect(ConstraintViolationException.class);

        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        char[] updatedBy = new char[61];
        Arrays.fill(updatedBy, 'x');
        mobileTerminalPlugin.setUpdatedBy(new String(updatedBy));

        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName() {
        final String serviceName = "test_serviceName";
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();
        mobileTerminalPlugin = mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        MobileTerminalPlugin mobileTerminalPluginAfterGetter = mobileTerminalPluginDao.getPluginByServiceName(serviceName);

        assertNotNull(mobileTerminalPlugin.getId());
        assertNotNull(mobileTerminalPluginAfterGetter);
        assertEquals(serviceName, mobileTerminalPluginAfterGetter.getPluginServiceName());
    }

    @Test()
    @OperateOnDeployment("normal")
    public void testGetPluginByServiceName_wrongServiceNameThrowsNoEntityFoundException() {
        try {
            mobileTerminalPluginDao.getPluginByServiceName("thisServiceNameDoesNotExist");
            Assert.fail();
        }
        catch(Throwable t){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin() {
        final String newServiceName = "change_me";
        MobileTerminalPlugin created = createMobileTerminalPluginHelper();
        created = mobileTerminalPluginDao.createMobileTerminalPlugin(created);

        created.setPluginServiceName(newServiceName);
        MobileTerminalPlugin updated = mobileTerminalPluginDao.updateMobileTerminalPlugin(created);

        assertNotNull(created);
        assertEquals(updated.getId(), created.getId());
        assertEquals(updated.getPluginServiceName(), created.getPluginServiceName());
        assertEquals(newServiceName, created.getPluginServiceName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin_updateInsteadOfPersistFailsWithTerminalDaoException() {
        MobileTerminalPlugin mobileTerminalPlugin = createMobileTerminalPluginHelper();

        MobileTerminalPlugin obj = mobileTerminalPluginDao.updateMobileTerminalPlugin(mobileTerminalPlugin);
        Assert.assertNotNull(obj);
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpdatePlugin_persistNullEntityFailsWithTerminalDaoException() {
        try {
            mobileTerminalPluginDao.updateMobileTerminalPlugin(null);
            Assert.fail();
        } catch (EJBTransactionRolledbackException e) {
            Assert.assertTrue(true);
        }
    }

    private MobileTerminalPlugin createMobileTerminalPluginHelper() {

        MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
        String testName = UUID.randomUUID().toString();

        mobileTerminalPlugin.setName(testName);
        mobileTerminalPlugin.setDescription("test_description");
        mobileTerminalPlugin.setPluginServiceName("test_serviceName");
        mobileTerminalPlugin.setPluginSatelliteType("test_satelliteType");
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPlugin.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        mobileTerminalPlugin.setUpdatedBy("test_user");

        return mobileTerminalPlugin;
    }
}
