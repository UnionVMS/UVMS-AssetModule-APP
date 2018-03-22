package eu.europa.fisheries.uvms.asset.service.arquillian;

import java.io.File;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

@ArquillianSuiteDeployment
public abstract class BuildAssetServiceDeployment {

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        File[] files = Maven.configureResolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
        testWar.addAsLibraries(files);

        testWar.addPackages(true, "com.tocea.easycoverage.framework.api");
        testWar.addPackages(true, "eu.europa.fisheries.uvms.asset.service");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.asset.service");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.asset.dto");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.asset.exception");
        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.entity");

        return testWar;
    }
}