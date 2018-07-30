package eu.europa.ec.fisheries.uvms.asset.arquillian;


import java.io.File;

import eu.europa.ec.fisheries.uvms.bean.ConfigDomainModelBean;
import eu.europa.ec.fisheries.uvms.config.service.entity.Parameter;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * Created by andreasw on 2017-02-13.
 */
@ArquillianSuiteDeployment
public abstract class BuildAssetDeployment {

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        // Import Maven runtime dependencies
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();

        // Embedding war package which contains the test class is needed
        // So that Arquillian can invoke test class through its servlet test runner
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");


        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.bean");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.constant");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.entity");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.dao");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mapper");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.util");

        testWar.addPackages(true, "com.tocea.easycoverage.framework.api");

        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.model");

        testWar.addPackages(true,"eu.europa.ec.fisheries.uvms.asset.arquillian");

        testWar.addPackages(true, "eu.europa.ec.fisheries.schema");
        testWar.addClass(TransactionalTests.class);
        testWar.addClass(ConfigDomainModelBean.class);
        testWar.addClass(AssetConfigHelperTest.class);
        testWar.addClass(Parameter.class);




        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");
        // Empty beans for EE6 CDI
        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsLibraries(files);



        return testWar;
    }

}
