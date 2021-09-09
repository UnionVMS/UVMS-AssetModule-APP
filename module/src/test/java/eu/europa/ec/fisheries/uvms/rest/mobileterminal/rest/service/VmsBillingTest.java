package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetBO;
import eu.europa.ec.fisheries.uvms.asset.util.JsonBConfiguratorAsset;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.VmsBillingDto;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetMatcher;
import eu.europa.ec.fisheries.uvms.rest.asset.mapper.CustomAssetAdapter;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.PostConstruct;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class VmsBillingTest extends AbstractAssetRestTest{
        
        @Test
        @OperateOnDeployment("normal")
        public void getVmsResponseListTest() {
            
            Asset createdAsset = createAsset("vmsBillingTestAsset");
            MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, false, false);
            
            assertEquals(1, created.getChannels().size());
            
            List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
            assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getVesselId().equals(createdAsset.getNationalId())));
            
        }
        
        @Test
        @OperateOnDeployment("normal")
        public void getVmsResponseListRemovedChannelTest() {
            Asset createdAsset = createAsset("RemovedChannelTest");
            MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, false, false);
            assertEquals(1, created.getChannels().size());
            created.getChannels().clear();
            MobileTerminal updated = updateMobileTerminal(created);
            assertEquals(0, updated.getChannels().size());
            
            List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
            assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getVesselId().equals(createdAsset.getNationalId())));
        }
        
        @Test
        @OperateOnDeployment("normal")
        public void getVmsResponseListIncludeNonDefultChannelIfNotPollOrConfigTest() {
            Asset createdAsset = createAsset("NotPollOrConfigTest");
            MobileTerminal created = createMobileTerminalWithChannel(createdAsset, false, false, false);
            assertEquals(1, created.getChannels().size());
            List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
            assertTrue(vmsResponse.stream().anyMatch(vms -> vms.getName().equals(createdAsset.getName())));
        }
        
        @Test
        @OperateOnDeployment("normal")
        public void getVmsResponseListDontIncludePollChannelTest() {
            Asset createdAsset = createAsset("DontIncludePollChannelTest");
            MobileTerminal created = createMobileTerminalWithChannel(createdAsset, true, true, true);
            assertEquals(1, created.getChannels().size());
            
            List<VmsBillingDto> vmsResponse = getVmsBillingResultList();
            assertFalse(vmsResponse.stream().anyMatch(vms -> vms.getName().equals(createdAsset.getName())));
        }
        
        
        /*
         * 
         * Helper methods below
         * 
         */
        
        private List<VmsBillingDto> getVmsBillingResultList() {
            Jsonb jsonb = JsonbBuilder.create();
            Response response = getWebTargetInternal()
                    .path("internal")
                    .path("/vmsBilling")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getTokenInternalRest())
                    .get();
            assertEquals(200, response.getStatus());
            String result = response.readEntity(String.class);
            List<VmsBillingDto> vmsResponse = jsonb.fromJson(result, 
                    new ArrayList<VmsBillingDto>(){private static final long serialVersionUID = 1L;}.getClass().getGenericSuperclass());
            
            return vmsResponse;
        }
        
        private Asset createAsset(String name){
            Asset asset = AssetHelper.createBasicAsset();
            Long natId = Long.valueOf((int) (100000 + (Math.random() * (1000000 - 100000))));
            asset.setNationalId(natId);
            asset.setName(name);
            Asset assetRet = getWebTargetInternal()
                    .path("/asset")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                    .post(Entity.json(asset), Asset.class);
            
            return assetRet;
        }
        
        private MobileTerminal createMobileTerminalWithChannel(Asset asset, 
                boolean defaultChannel, boolean pollChannel, boolean configChannel){
            Integer memberNr = (int) (10000 + (Math.random() * (100000 - 10000))) ;
            Integer dnid = (int) (100 + (Math.random() * (1000 - 100))) ; 

            MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
            mobileTerminal.setAsset(asset);
            mobileTerminal.setAssetUUID(asset.getId().toString());
            
            Channel channel = MobileTerminalTestHelper.createBasicChannel();
            channel.setDnid(dnid);
            channel.setMemberNumber(memberNr);
            channel.setMobileTerminal(mobileTerminal);
            channel.setConfigChannel(configChannel);
            channel.setPollChannel(pollChannel);
            channel.setDefaultChannel(defaultChannel);
            channel.setName(asset.getName());
            Set<Channel> channels = new HashSet<>();
            channel.setMobileTerminal(mobileTerminal);
            channels.add(channel);
            mobileTerminal.getChannels().clear();
            mobileTerminal.setChannels(channels);
            
            MobileTerminal createdMt = getWebTargetInternal()
                    .path("mobileterminal")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                    .post(Entity.json(mobileTerminal), MobileTerminal.class);
            return createdMt;
        }
        
        private MobileTerminal updateMobileTerminal(MobileTerminal mobileTerminal){
            return getWebTargetInternal()
                    .path("mobileterminal")
                    .queryParam("comment", "UPDATE_MT_COMMENT")
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, getTokenInternal())
                    .put(Entity.json(mobileTerminal), MobileTerminal.class);
        }
        
        

}
