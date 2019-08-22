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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.ExchangeModuleRestMock;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.UnionVMSMock;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.UserRestMock;
import eu.europa.ec.fisheries.uvms.rest.security.InternalRestTokenHandler;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import javax.ejb.EJB;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.File;

@ArquillianSuiteDeployment
public abstract class AbstractAssetRestTest {

    private String token;

    @EJB
    private InternalRestTokenHandler tokenHandler;

    protected WebTarget getWebTargetExternal() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Client client = ClientBuilder.newClient();
        client.register(new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
        return client.target("http://localhost:28080/test/rest");  //external
        //return client.target("http://localhost:8080/test/rest");    //internal
    }

    //jersey does not like sse so to fix this we need the sse test to reside on the server environment instead of @RunAsClient
    //also, if we switch from jersey to resteasy (like we have in all the other modules) for the client everything stops working with status code 405
    protected WebTarget getWebTargetInternal() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Client client = ClientBuilder.newClient();
        client.register(new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
        return client.target("http://localhost:8080/test/rest");    //internal
    }

    @Deployment(name = "normal", order = 2)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve()
                .withTransitivity().asFile();
        testWar.addAsLibraries(files);

        testWar.addAsLibraries(Maven.configureResolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.asset:asset-service").withTransitivity().asFile());


        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.rest.asset");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.rest.mobileterminal");

        testWar.delete("/WEB-INF/web.xml");
        testWar.addAsWebInfResource("mock-web.xml", "web.xml");

        return testWar;
    }

    @Deployment(name = "uvms", order = 1)
    public static Archive<?> createExchangeMock() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "unionvms.war");
        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.exchange:exchange-model:5.0.0-UVMS",
                        "eu.europa.ec.fisheries.uvms:usm4uvms",
                        "eu.europa.ec.fisheries.uvms.commons:uvms-commons-message:3.0.29").withTransitivity().asFile();

        testWar.addAsLibraries(files);


        testWar.addClass(UnionVMSMock.class);
        testWar.addClass(ExchangeModuleRestMock.class);
        testWar.addClass(UserRestMock.class);
        testWar.addClass(MovementMock.class);

        return testWar;
    }

    protected String getTokenExternal() {
        if (token == null) {
            token = ClientBuilder.newClient()
                    .target("http://localhost:28080/unionvms/user/token")
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
        }
        return token;
    }

    //see the comment above getWebbTargetInternal
    protected String getTokenInternal() {
        if (token == null) {
            token = ClientBuilder.newClient()
                    .target("http://localhost:8080/unionvms/user/token")
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);
        }
        return token;
    }

    protected String getTokenInternalRest() {
        return tokenHandler.createAndFetchToken("user");
    }
}
