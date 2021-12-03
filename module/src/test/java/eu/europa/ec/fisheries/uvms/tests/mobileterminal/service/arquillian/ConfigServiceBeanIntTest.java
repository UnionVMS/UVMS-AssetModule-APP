package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConfigType;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @EJB
    private ConfigServiceBeanMT configService;
    @EJB
    private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

    @Test
    @OperateOnDeployment("normal")
    public void testGetConfig() {
        List<ConfigList> rs = configService.getConfigValues();
        assertNotNull(rs);
        assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TIME_SPAN.toString()));
        assertTrue(configListContains(rs, MobileTerminalConfigType.POLL_TYPE.toString()));
        assertTrue(configListContains(rs, MobileTerminalConfigType.TRANSPONDERS.toString()));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAddPlugin() {
        MobileTerminalPlugin plugin = configService.upsertPlugin(createPluginService());
        assertNotNull(plugin);
        assertEquals("TEST_SERVICE", plugin.getPluginServiceName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testInactivatePlugin() {
        PluginService pluginService = createPluginService();
        MobileTerminalPlugin plugin = configService.upsertPlugin(pluginService);
        assertNotNull(plugin);
        assertEquals("TEST_SERVICE", plugin.getPluginServiceName());

        MobileTerminalPlugin inactivatedPlugin = configService.inactivatePlugin(pluginService);
        assertEquals(true, inactivatedPlugin.getPluginInactive());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testAddPluginUpdate()  {
        PluginService pluginService = createPluginService();
        MobileTerminalPlugin plugin = configService.upsertPlugin(pluginService);
        assertNotNull(plugin);
        assertEquals("TEST_SERVICE", plugin.getPluginServiceName());

        String newLabelName = "NEW_IRIDIUM_TEST_SERVICE";
        pluginService.setLabelName(newLabelName);

        MobileTerminalPlugin updatedPlugins = configService.upsertPlugin(pluginService);
        assertNotNull(updatedPlugins);
        assertEquals(newLabelName, plugin.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadServiceName() {

        try {
            PluginService pluginService = createPluginService();
            pluginService.setServiceName("");
            configService.upsertPlugin(pluginService);
            Assert.fail();  // error if we come here
        }
        catch(Throwable t){
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadLabelName() {
        try {
        PluginService pluginService = createPluginService();
        pluginService.setLabelName("");

        configService.upsertPlugin(pluginService);
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadSatelliteType() {
        try {
            PluginService pluginService = createPluginService();
            pluginService.setSatelliteType("");
            configService.upsertPlugin(pluginService);
            // if we end up here we are wrong
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetTerminalSystems() {
        MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
        mobileTerminalPlugin.setName("TEST");
        mobileTerminalPlugin.setPluginSatelliteType("TEST");
        mobileTerminalPlugin.setDescription("TEST");
        mobileTerminalPlugin.setPluginSatelliteType(MobileTerminalTypeEnum.INMARSAT_C.toString());
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPluginDao.createMobileTerminalPlugin(mobileTerminalPlugin);

        List<TerminalSystemType> rs = configService.getTerminalSystems();
        assertNotNull(rs);
        assertTrue(rs.size() > 0);
        assertTrue(terminalSystemsContains(rs, MobileTerminalTypeEnum.INMARSAT_C.toString()));
    }

    private PluginService createPluginService() {
        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("IRIDIUM_TEST_SERVICE");
        pluginService.setSatelliteType("IRIDIUM");
        pluginService.setServiceName("TEST_SERVICE");
        return pluginService;
    }

    private boolean terminalSystemsContains(List<TerminalSystemType> list, String type) {
        for (TerminalSystemType item : list) {
            if (item.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean configListContains(List<ConfigList> configLists, String value) {
        for (ConfigList item : configLists) {
            if (value.equals(item.getName())) {
                return true;
            }
        }
        return false;
    }
}
