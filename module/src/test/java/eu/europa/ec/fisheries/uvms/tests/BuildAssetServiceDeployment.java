package eu.europa.ec.fisheries.uvms.tests;

import java.io.File;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.ExchangeModuleRestMock;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.rest.UserRestMock;
import eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian.helper.UnionVMSMock;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@ArquillianSuiteDeployment
public abstract class BuildAssetServiceDeployment {

    @Deployment(name = "normal", order = 2)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve()
                .withTransitivity().asFile();
        testWar.addAsLibraries(files);

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.tests");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.rest.asset");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.rest.mobileterminal");

        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");

        testWar.delete("/WEB-INF/web.xml");
        testWar.addAsWebInfResource("mock-web.xml", "web.xml");
        
        return testWar;
    }

    @Deployment(name = "uvms", order = 1)
    public static Archive<?> createExchangeMock(){

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "unionvms.war");
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.exchange:exchange-client",
                        "eu.europa.ec.fisheries.uvms:usm4uvms",
                        "eu.europa.ec.fisheries.uvms.commons:uvms-commons-message")
                .withTransitivity().asFile();

        testWar.addAsLibraries(files);


        testWar.addClass(UnionVMSMock.class);
        testWar.addClass(ExchangeModuleRestMock.class);
        testWar.addClass(MovementMock.class);
        testWar.addClass(UserRestMock.class);

        return testWar;
    }
}
