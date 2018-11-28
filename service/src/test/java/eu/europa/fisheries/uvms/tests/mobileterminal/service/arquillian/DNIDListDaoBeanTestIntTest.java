package eu.europa.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.uvms.mobileterminal.dao.DNIDListDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.DNIDList;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class DNIDListDaoBeanTestIntTest extends TransactionalTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final static String PLUGIN_NAME = "TEST_PLUGIN_NAME";
    private final static String DN_ID = "TEST_DN_ID";
    private final static String USERNAME = "TEST";

    @EJB
    private DNIDListDaoBean dnidListDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetDNIDListEmptyList() {
        List<DNIDList> plugins = dnidListDao.getDNIDList(PLUGIN_NAME + Calendar.getInstance().getTimeInMillis());
        assertNotNull(plugins);
        assertEquals(0,plugins.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetDNIDListByPlugin() {
        DNIDList dnidList = createDnidList();
        dnidListDao.create(dnidList);
        em.flush();

        List<DNIDList> plugins = dnidListDao.getDNIDList(PLUGIN_NAME);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreate() {
        DNIDList dnidList = createDnidList();
        dnidList = dnidListDao.create(dnidList);
        em.flush();
        assertNotNull(dnidList.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateWithBadDNID() {

        thrown.expect(ConstraintViolationException.class);

        DNIDList dnidList = createDnidList();
        char[] dnId = new char[101];
        Arrays.fill(dnId, 'x');
        dnidList.setDnid(new String(dnId));
        dnidListDao.create(dnidList);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateWithBadPluginName(){

        thrown.expect(ConstraintViolationException.class);

        DNIDList dnidList = createDnidList();
        char[] pluginName = new char[501];
        Arrays.fill(pluginName, 'x');
        dnidList.setPluginName(new String(pluginName));
        dnidListDao.create(dnidList);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testCreateWithBadUpdateUser() {

        thrown.expect(ConstraintViolationException.class);

        DNIDList dnidList = createDnidList();
        char[] updateUser = new char[61];
        Arrays.fill(updateUser, 'x');
        dnidList.setUpdatedBy(new String(updateUser));
        dnidListDao.create(dnidList);
        em.flush();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testRemoveByPluginName()  {
        DNIDList dnidList = createDnidList();
        dnidList = dnidListDao.create(dnidList);
        em.flush();
        assertNotNull(dnidList.getId());
        List<DNIDList> dnidLists = dnidListDao.getDNIDList(PLUGIN_NAME);
        assertEquals(1, dnidLists.size());

        dnidListDao.removeByPluginName(PLUGIN_NAME);
        List<DNIDList> plugins = dnidListDao.getDNIDList(PLUGIN_NAME);
        assertNotNull(plugins);
        assertEquals(0, plugins.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetAllDNIDList() {
        List<DNIDList> before = dnidListDao.getAllDNIDList();
        assertNotNull(before);

        DNIDList dnidList = createDnidList();
        dnidListDao.create(dnidList);
        em.flush();

        List<DNIDList> after = dnidListDao.getAllDNIDList();
        assertNotNull(after);

        assertEquals(before.size(), (after.size() - 1));
    }

    private DNIDList createDnidList() {
        DNIDList dnidList = new DNIDList();
        dnidList.setDnid(DN_ID);
        dnidList.setPluginName(PLUGIN_NAME);
        dnidList.setUpdateTime(OffsetDateTime.now(ZoneOffset.UTC));
        dnidList.setUpdatedBy(USERNAME);
        return dnidList;
    }
}
