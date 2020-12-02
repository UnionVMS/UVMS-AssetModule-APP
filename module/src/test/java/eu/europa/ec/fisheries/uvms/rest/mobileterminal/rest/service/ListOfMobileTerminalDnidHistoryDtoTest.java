package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.MobileTerminalDnidHistoryDto;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.MobileTerminalTypeEnum;
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

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
@RunAsClient
public class ListOfMobileTerminalDnidHistoryDtoTest extends AbstractAssetRestTest{

    @Test
    @OperateOnDeployment("normal")
    public void getListOfMobileTerminalDnidHistoryDtoTest() throws InterruptedException {
        
        MobileTerminal mobileTerminal = MobileTerminalTestHelper.createBasicMobileTerminal();
        mobileTerminal.setAsset(createAndRestBasicAsset());
        Thread.sleep(100);
        mobileTerminal = createMobileTerminal(mobileTerminal);
        Response response = getWebTargetExternal()
                .path("/internal")
                .path("/channelhistory")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);
        
        assertEquals(200, response.getStatus());

        assertNotNull(response);
        
        List<MobileTerminalDnidHistoryDto> listOfMobileTerminalDnidHistoryDto= response.readEntity(new GenericType<List<MobileTerminalDnidHistoryDto>>() {});
        assertNotNull(listOfMobileTerminalDnidHistoryDto);
        assertNotNull(listOfMobileTerminalDnidHistoryDto.get(0).getAssetId());
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getListOfMobileTerminalDnidHistoryDtoSetStartDateTest() throws InterruptedException {
        
        MobileTerminal mobileTerminal = createBasicMobileTerminal();
        Channel channelHavNotDefault = createBasicChannel(false, true, "HAV");
        Channel channelVmsDefault = createBasicChannel(true, true, "VMS");
        Set<Channel> channelSet = new HashSet<>();
        channelSet.add(channelHavNotDefault);
        channelSet.add(channelVmsDefault);
        mobileTerminal.setChannels(channelSet);
        mobileTerminal.setAsset(createAndRestBasicAsset());
        
        Thread.sleep(100);
        mobileTerminal = createMobileTerminal(mobileTerminal);
        
        Response response = getWebTargetExternal()
                .path("/internal")
                .path("/channelhistory")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);
        
        assertEquals(200, response.getStatus());
        assertNotNull(response);
        
        List<MobileTerminalDnidHistoryDto> listOfMobileTerminalDnidHistoryDto= response.readEntity(new GenericType<List<MobileTerminalDnidHistoryDto>>() {});
        assertNotNull(listOfMobileTerminalDnidHistoryDto.get(0).getStartDate());
        assertTrue(listOfMobileTerminalDnidHistoryDto.size() >= 2);
        assertNotNull(listOfMobileTerminalDnidHistoryDto);
    }
    
    
    @Test
    @OperateOnDeployment("normal")
    public void getListOfMobileTerminalDnidHistoryDtoEndDateSetChannelTest() throws InterruptedException {
        
        MobileTerminal mobileTerminal = createBasicMobileTerminal();
        Channel channelHavNotDefault = createBasicChannel(false, true, "HAV");
        Channel channelVmsDefault = createBasicChannel(true, true, "VMSENDDATETEST");
        Set<Channel> channelSet = new HashSet<>();
        channelSet.add(channelHavNotDefault);
        channelSet.add(channelVmsDefault);
        mobileTerminal.setChannels(channelSet);
        mobileTerminal.setAsset(createAndRestBasicAsset());
        mobileTerminal = createMobileTerminal(mobileTerminal);
        
        Channel channel = mobileTerminal.getChannels().stream()
                .filter(c -> c.getName().equals("VMSENDDATETEST"))
                .findFirst()
                .orElse(null);
        
        Response response = getWebTargetExternal()
                .path("/internal")
                .path("/channelhistory")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);
        
        assertEquals(200, response.getStatus());
        assertNotNull(response);
        
        List<MobileTerminalDnidHistoryDto> listOfMobileTerminalDnidHistoryDto = response.readEntity(new GenericType<List<MobileTerminalDnidHistoryDto>>() {});
        assertNotNull(listOfMobileTerminalDnidHistoryDto);
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getListOfMobileTerminalDnidHistoryDtoEndDateSetAutomagicChannelTest() throws InterruptedException {
        final String channelname = "VMS-ENDDATE-TEST-NOENDDATE";
        
        MobileTerminal mobileTerminal = createBasicMobileTerminal();
        Channel channelVmsDefault = createBasicChannel(true, true, channelname);
        Channel channel2 = createBasicChannel(false, true, "c2");
        Channel channel3 = createBasicChannel(false, true, "c3");
        
        Set<Channel> channelSet = new HashSet<>();
        channelSet.add(channelVmsDefault);
        channelSet.add(channel2);
        channelSet.add(channel3);
        channelVmsDefault.setMobileTerminal(mobileTerminal);
        channel2.setMobileTerminal(mobileTerminal);
        channel3.setMobileTerminal(mobileTerminal);
        mobileTerminal.setChannels(channelSet);
        mobileTerminal.setAsset(createAndRestBasicAsset());
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        MobileTerminal mobileTerminalCreated = createMobileTerminal(mobileTerminal);
        
        Channel channelVmsDefault2 = createBasicChannel(true, true, channelname);
        Set<Channel> channelSet2 = new HashSet<>();
        channelSet2.add(channelVmsDefault2);
        channelVmsDefault2.setMobileTerminal(mobileTerminalCreated);
        mobileTerminalCreated.getChannels().clear();
        mobileTerminalCreated.setChannels(channelSet2);
        mobileTerminalCreated.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        
        MobileTerminal mobileTerminalCreated2 = updateMobileTerminal(mobileTerminalCreated);
        
        Response response = getWebTargetExternal()
                .path("/internal")
                .path("/channelhistory")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);
        
        assertEquals(200, response.getStatus());
        assertNotNull(response);
        
        List<MobileTerminalDnidHistoryDto> listOfMobileTerminalDnidHistoryDto= response.readEntity(new GenericType<List<MobileTerminalDnidHistoryDto>>() {});
    
        System.out.println("listOfMobileTerminalDnidHistoryDto " + listOfMobileTerminalDnidHistoryDto.size());
        
        Predicate<MobileTerminalDnidHistoryDto> mth1 = mt -> mt.getChannelName().equals(channelname);

        assertTrue(listOfMobileTerminalDnidHistoryDto.stream().anyMatch(mth1));
        assertTrue(listOfMobileTerminalDnidHistoryDto.size() > 1);
    }

//    @Test
//    @OperateOnDeployment("normal")
//    public void getListOfMobileTerminalTest() throws InterruptedException {
//        
//        MobileTerminal mobileTerminal = createBasicMobileTerminal();
//        MobileTerminal mobileTerminalCreated = createMobileTerminal(mobileTerminal);
//        MobileTerminal mobileTerminal2 = createBasicMobileTerminal();
//        MobileTerminal mobileTerminalCreated2 = createMobileTerminal(mobileTerminal2);
//        
//        
//        Response response = getWebTargetExternal()
//                .path("/internal")
//                .path("/allmobileterminalhistory")
//                .request(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
//                .get(Response.class);
//        
//        assertEquals(200, response.getStatus());
//        assertNotNull(response);
//        
//        List<MobileTerminal> listOfMobileTerminalDnidHistoryDto= response.readEntity(new GenericType<List<MobileTerminal>>() {});
//    
//        System.out.println(listOfMobileTerminalDnidHistoryDto.toString());
//        Predicate<MobileTerminal> mt1 = mt -> mt.getId().equals(mobileTerminalCreated.getId());
//        Predicate<MobileTerminal> mt2 = mt -> mt.getId().equals(mobileTerminalCreated2.getId());
//
//        assertTrue(listOfMobileTerminalDnidHistoryDto.stream().anyMatch(mt1));
//        assertTrue(listOfMobileTerminalDnidHistoryDto.stream().anyMatch(mt2));
//    }
    /*
     * Helper methods below
     */
    
    
    private MobileTerminal createMobileTerminal(MobileTerminal mt){
        MobileTerminal created = getWebTargetExternal()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);
        assertNotNull(created);

        return  created;
    }
    
    private MobileTerminal getMobileTerminal(MobileTerminal mt){
        MobileTerminal fetched = getWebTargetExternal()
                .path("mobileterminal")
                .path(mt.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(MobileTerminal.class);
        assertNotNull(fetched);

        return  fetched;
    }
    
    private MobileTerminal updateMobileTerminal(MobileTerminal mt){
        MobileTerminal fetched = getWebTargetExternal()
                .path("/mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(mt), MobileTerminal.class);
        assertNotNull(fetched);

        return  fetched;
    }
    
    private Asset createAndRestBasicAsset() {
        Asset asset = createBasicAsset();

        Asset createdAsset = getWebTargetExternal()
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);

        return createdAsset;
    }
    
    private Asset createBasicAsset() {
        Asset assetEntity = new Asset();

        assetEntity.setName("Test asset "+ MobileTerminalTestHelper.generateARandomStringWithMaxLength(4));
        assetEntity.setActive(true);
        assetEntity.setExternalMarking("EXT123");
        assetEntity.setFlagStateCode("SWE");

        assetEntity.setCommissionDate(Instant.now());
        assetEntity.setCfr("CRF" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(9));
        assetEntity.setIrcs("F" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(7));
        assetEntity.setImo(MobileTerminalTestHelper.generateARandomStringWithMaxLength(7));
        assetEntity.setMmsi("M" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(8)); 
        assetEntity.setIccat("ICCAT" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(20));
        assetEntity.setUvi("UVI" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(20));
        assetEntity.setGfcm("GFCM" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(20));
        
        assetEntity.setNationalId((long) Math.round((Math.random() * (100000 - 1000)) + 1000));
        
        assetEntity.setGrossTonnage(10d);
        assetEntity.setPowerOfMainEngine(10d);
        
        assetEntity.setGearFishingType("Demersal");

        assetEntity.setOwnerName("Foo Bar");
        assetEntity.setOwnerAddress("Hacker st. 1337");

        assetEntity.setProdOrgCode("ORGCODE");
        assetEntity.setProdOrgName("ORGNAME");
        
        assetEntity.setUpdateTime(Instant.now());
        assetEntity.setUpdatedBy("TEST");

        return assetEntity;
    }
    
    private MobileTerminal createBasicMobileTerminal() {
        MobileTerminal mobileTerminal = new MobileTerminal();
        mobileTerminal.setSource(TerminalSourceEnum.INTERNAL);
        mobileTerminal.setMobileTerminalType(MobileTerminalTypeEnum.INMARSAT_C);
        mobileTerminal.setSerialNo(MobileTerminalTestHelper.generateARandomStringWithMaxLength(10));
        mobileTerminal.setInstalledBy("Mike Great");

        mobileTerminal.setSatelliteNumber("S" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(4));
        mobileTerminal.setAntenna("A");
        mobileTerminal.setTransceiverType("A");
        mobileTerminal.setSoftwareVersion("A");

        MobileTerminalPlugin plugin = new MobileTerminalPlugin();
        plugin.setPluginServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
        plugin.setName("Thrane&Thrane");
        plugin.setPluginSatelliteType("INMARSAT_C");
        plugin.setPluginInactive(false);
        mobileTerminal.setPlugin(plugin);

        return mobileTerminal;
    }

    private Channel createBasicChannel(boolean setDefault, boolean setActive, String setName){
        Channel channel = new Channel();
        channel.setName(setName);
        channel.setFrequencyGracePeriod(Duration.ofSeconds(54000));
        channel.setMemberNumber(Integer.parseInt(MobileTerminalTestHelper.generateARandomStringWithMaxLength(3)));
        channel.setExpectedFrequency(Duration.ofSeconds(7200));
        channel.setExpectedFrequencyInPort(Duration.ofSeconds(10800));
        channel.setLesDescription("Thrane&Thrane");
        channel.setDnid(Integer.parseInt("1" + MobileTerminalTestHelper.generateARandomStringWithMaxLength(4)));
        channel.setArchived(false);
        channel.setActive(setActive);
        channel.setConfigChannel(true);
        channel.setDefaultChannel(setDefault);
        channel.setPollChannel(true);
        return channel;
    }
    
    private Asset updateAsset(Asset asset){
        Asset updatedAsset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .put(Entity.json(asset), Asset.class);
        return updatedAsset;
    }
    
}
