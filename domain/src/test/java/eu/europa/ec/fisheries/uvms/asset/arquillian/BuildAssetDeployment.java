package eu.europa.ec.fisheries.uvms.asset.arquillian;

import java.io.File;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * Created by andreasw on 2017-02-13.
 */
@ArquillianSuiteDeployment
public abstract class BuildAssetDeployment {


    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        // Embedding war package which contains the test class is needed
        // So that Arquillian can invoke test class through its servlet test runner
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        // Import Maven runtime dependencies
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
        testWar.addAsLibraries(files);

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.bean");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.constant");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.entity");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.dao");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mapper");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.util");
        testWar.addPackages(true, "eu.europa.ec.fisheries.wsdl.asset");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.model.exception");
        testWar.addPackages(true, "com.tocea.easycoverage.framework.api");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.model");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.asset.arquillian");
        testWar.addPackages(true, "eu.europa.ec.fisheries.schema");

        testWar.addClass(TransactionalTests.class);

        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");

        return testWar;
    }
}
