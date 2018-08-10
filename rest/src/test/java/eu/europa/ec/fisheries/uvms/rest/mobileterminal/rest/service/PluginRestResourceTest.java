package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPluginCapability;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
@RunAsClient
public class PluginRestResourceTest extends AbstractAssetRestTest {

    @Test
    public void testTest(){
        assertTrue(true);
    }

    private MobileTerminalPlugin createPlugin() {
        MobileTerminalPlugin mobileTerminalPlugin = new MobileTerminalPlugin();
        mobileTerminalPlugin.setPluginInactive(false);
        mobileTerminalPlugin.setName("Thrane&Thrane");
        mobileTerminalPlugin.setPluginSatelliteType("INMARSAT_C");
        mobileTerminalPlugin.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");

        MobileTerminalPluginCapability capability = new MobileTerminalPluginCapability();
        capability.setName(CapabilityTypeType.POLLABLE.name());
        capability.setValue("TRUE");
        capability.setPlugin(mobileTerminalPlugin);

        /*capabilityType = new CapabilityType();
        capabilityType.setType(CapabilityTypeType.CONFIGURABLE);
        capabilityType.setValue("TRUE");
        capabilityList.getCapability().add(capabilityType);*/

        Set<MobileTerminalPluginCapability> capSet = new HashSet<>();
        capSet.add(capability);
        mobileTerminalPlugin.setCapabilities(capSet);
        return mobileTerminalPlugin;
    }
}
