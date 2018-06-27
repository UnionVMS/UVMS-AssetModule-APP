package eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.CreatePollResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollMobileTerminal;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.AbstractMTRestTest;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.MobileTerminalTestHelper;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
//@RunAsClient
public class PollRestResourceTest extends AbstractAssetRestTest {

    @Test
    public void getRunningProgramPollsTest() {

        String response = getWebTarget()
                .path("/poll/running")
                .request(MediaType.APPLICATION_JSON)
                .get()
                .readEntity(String.class);

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jsonObject = jsonReader.readObject();

        assertThat(jsonObject.getInt("code"), CoreMatchers.is(MTResponseCode.OK.getCode()));
    }


    @Test
    public void createPollTest() throws Exception {
        PollRequestType input = new PollRequestType();
        MobileTerminalType mt = MobileTerminalTestHelper.createBasicMobileTerminal();
        mt.setConnectId("Test Boat");

        String response = getWebTarget()
                .path("mobileterminal")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(mt), String.class);


        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        MobileTerminalType createdMT = deserializeResponseDto(response, MobileTerminalType.class);

        PollMobileTerminal pmt = new PollMobileTerminal();
        pmt.setComChannelId(createdMT.getChannels().get(0).getGuid());
        pmt.setConnectId(createdMT.getConnectId());
        pmt.setMobileTerminalId(createdMT.getMobileTerminalId().getGuid());
        input.getMobileTerminals().add(pmt);

        input.setPollType(PollType.MANUAL_POLL);
        input.setComment("Test Comment");
        input.setUserName("Test User");

        response = getWebTarget()
                .path("/poll")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(input), String.class);

        assertEquals(MTResponseCode.OK.getCode(), getReturnCode(response));
        CreatePollResultDto output = deserializeResponseDto(response, CreatePollResultDto.class);

        //TODO: Change when we get the message system working in a sane way
        assertEquals(1, output.getSentPolls().size() + output.getUnsentPolls().size());
        //assertFalse(output.isUnsentPoll());
        //assertTrue(output.getUnsentPolls().isEmpty());

    }
}
