/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.rest.asset;

import java.io.File;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

@ArquillianSuiteDeployment
public abstract class AbstractAssetRestTest {

    protected WebTarget getWebTarget() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Client client = ClientBuilder.newClient();
        client.register(new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
        //return client.target("http://localhost:28080/test/rest");  //external
        return client.target("http://localhost:8080/test/rest");    //internal
    }

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve()
                .withTransitivity().asFile();
        testWar.addAsLibraries(files);
            
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.rest.asset");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.rest.mobileterminal");

        testWar.addClass(AuthenticationFilterMock.class);

        testWar.delete("/WEB-INF/web.xml");
        testWar.addAsWebInfResource("mock-web.xml", "web.xml");

        return testWar;
    }


    protected int getReturnCode(String responsDto) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responsDto, ObjectNode.class).get("code").asInt();
    }

    protected static <T> T deserializeResponseDto(String responseDto, Class<T> clazz) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.readValue(responseDto, ObjectNode.class);
        JsonNode jsonNode = node.get("data");
        return objectMapper.readValue(objectMapper.writeValueAsString(jsonNode), clazz);
    }
}
