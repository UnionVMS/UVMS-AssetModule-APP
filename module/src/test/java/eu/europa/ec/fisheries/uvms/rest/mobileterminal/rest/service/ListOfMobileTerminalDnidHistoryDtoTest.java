package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.MobileTerminalDnidHistoryDto;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
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
import java.util.List;

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
                .path("/mobileterminals")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);
        
        assertEquals(200, response.getStatus());

        assertNotNull(response);
        
        List<MobileTerminalDnidHistoryDto> listOfMobileTerminalDnidHistoryDto= response.readEntity(new GenericType<List<MobileTerminalDnidHistoryDto>>() {});
        System.out.println("listOfMobileTerminalDnidHistoryDto size " +listOfMobileTerminalDnidHistoryDto.size());
        for (MobileTerminalDnidHistoryDto mobileTerminalDnidHistoryDto : listOfMobileTerminalDnidHistoryDto) {
            System.out.println("mobileTerminalDnidHistoryDto asset id" +mobileTerminalDnidHistoryDto.getAssetId());
            System.out.println("mobileTerminalDnidHistoryDto DNID" +mobileTerminalDnidHistoryDto.getDnid());
            System.out.println("mobileTerminalDnidHistoryDto getStartDate" +mobileTerminalDnidHistoryDto.getStartDate());
            System.out.println("mobileTerminalDnidHistoryDto getEndDate" +mobileTerminalDnidHistoryDto.getEndDate());
        }
        
        assertNotNull(listOfMobileTerminalDnidHistoryDto);
    }
    
    private MobileTerminal createMobileTerminal(MobileTerminal mt){
        MobileTerminal created = getWebTargetExternal()
                //.path("/internal")
                .path("/mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(mt), MobileTerminal.class);
        assertNotNull(created);

        return  created;
    }
    
    private Asset createAndRestBasicAsset() {
        Asset asset = AssetHelper.createBasicAsset();

        Asset createdAsset = getWebTargetExternal()
              //  .path("/internal")
                .path("/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        assertNotNull(createdAsset);

        return createdAsset;
    }
    
}
