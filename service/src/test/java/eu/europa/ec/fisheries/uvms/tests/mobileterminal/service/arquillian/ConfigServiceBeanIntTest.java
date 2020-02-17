package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;
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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ConfigServiceBeanIntTest extends TransactionalTests {

    private static final String MESSAGE_PRODUCER_METHODS_FAIL = "MESSAGE_PRODUCER_METHODS_FAIL";
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
    @Ignore
    public void testGetRegisteredMobileTerminalPlugins_fail() {

        // TODO: getRegisteredMobileTerminalPlugins() method has TransactionAttributeType.NEVER defined.
        // Which makes this tests to fail because that method throws exception when client code has a transaction.
        thrown.expect(/*MobileTerminalModel*/Exception.class);

        System.setProperty(MESSAGE_PRODUCER_METHODS_FAIL, "true");
        configService.getRegisteredMobileTerminalPlugins();
    }

    @Test
    @OperateOnDeployment("normal")
    public void testGetRegisteredMobileTerminalPlugins() {

        List<ServiceResponseType> output = configService.getRegisteredMobileTerminalPlugins();

        assertEquals(1, output.size());
        ServiceResponseType serviceResponseType = output.get(0);

        //values from exchange module mock, the one in service
        assertEquals("eu.europa.ec.fisheries.uvms.plugins.test", serviceResponseType.getServiceClassName());
        assertEquals("Test&Test", serviceResponseType.getName());
        assertEquals("INMARSAT_D", serviceResponseType.getSatelliteType());
        assertEquals(1, serviceResponseType.getCapabilityList().getCapability().size());
        assertEquals(CapabilityTypeType.POLLABLE, serviceResponseType.getCapabilityList().getCapability().get(0).getType());

    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPlugins() {
        List<PluginService> pluginList = Collections.singletonList(createPluginService());
        List<MobileTerminalPlugin> plugins = configService.upsertPlugins(pluginList, "TEST");
        assertNotNull(plugins);
        assertTrue(pluginsContains(pluginList, "TEST_SERVICE"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsUpdate()  {
        List<PluginService> pluginList = Collections.singletonList(createPluginService());
        List<MobileTerminalPlugin> plugins = configService.upsertPlugins(pluginList, "TEST");
        assertNotNull(plugins);
        assertTrue(pluginsContains(pluginList, "TEST_SERVICE"));
        assertEquals(1, pluginList.size());
        //assertEquals(4, plugins.size());

        for (PluginService ps : pluginList) {
            ps.setLabelName("NEW_IRIDIUM_TEST_SERVICE");
        }

        assertEquals(1, pluginList.size());

        List<MobileTerminalPlugin> updatedPlugins = configService.upsertPlugins(pluginList, "TEST");
        assertNotNull(updatedPlugins);
        assertEquals(1, updatedPlugins.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadServiceName() {

        try {
            List<PluginService> pluginList = new ArrayList<>();
            PluginService pluginService = createPluginService();
            pluginService.setServiceName("");
            pluginList.add(pluginService);
            configService.upsertPlugins(pluginList, "TEST");
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
        List<PluginService> pluginList = new ArrayList<>();
        PluginService pluginService = createPluginService();
        pluginService.setLabelName("");
        pluginList.add(pluginService);

        configService.upsertPlugins(pluginList, "TEST");
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertTrue(true);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void testUpsertPluginsBadSatelliteType() {
        try {
            List<PluginService> pluginList = new ArrayList<>();
            PluginService pluginService = createPluginService();
            pluginService.setSatelliteType("");
            pluginList.add(pluginService);
            configService.upsertPlugins(pluginList, "TEST");
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

    private boolean pluginsContains(List<PluginService> pluginList, String name) {
        for (PluginService item : pluginList) {
            if (item.getServiceName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
