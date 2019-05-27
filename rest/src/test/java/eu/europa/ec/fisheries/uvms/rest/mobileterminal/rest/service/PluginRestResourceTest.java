package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public class PluginRestResourceTest extends AbstractAssetRestTest {

    @Test
    @OperateOnDeployment("normal")
    public void upsertPluginTest() {
        List<PluginService> pluginList = createPluginList();

        Response response = getWebTarget()
                .path("/plugin")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .post(Entity.json(pluginList));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        List<MobileTerminalPlugin> pluginEntityList = response.readEntity(new GenericType<List<MobileTerminalPlugin>>() {});

        assertEquals(2, pluginEntityList.size());
        assertEquals("eu.europa.ec.fisheries.uvms.plugins.inmarTEST", pluginEntityList.get(0).getPluginServiceName());
        assertEquals("eu.europa.ec.fisheries.uvms.plugins.inmarsat", pluginEntityList.get(1).getPluginServiceName());
    }

    private List<PluginService> createPluginList() {
        List<PluginService> returnList = new ArrayList<>();

        PluginService pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("Thrane&Thrane&Test");
        pluginService.setSatelliteType("INMARSAT_C");
        pluginService.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarTEST");

        PluginCapability capability = new PluginCapability();
        capability.setName(PluginCapabilityType.POLLABLE);
        capability.setValue("TRUE");

        pluginService.getCapability().add(capability);

        returnList.add(pluginService);

        pluginService = new PluginService();
        pluginService.setInactive(false);
        pluginService.setLabelName("Thrane&Thrane");
        pluginService.setSatelliteType("INMARSAT_C");
        pluginService.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        pluginService.getCapability().add(capability);

        returnList.add(pluginService);
        return returnList;
    }
}
