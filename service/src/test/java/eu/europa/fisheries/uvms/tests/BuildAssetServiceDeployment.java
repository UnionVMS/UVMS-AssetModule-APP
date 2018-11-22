package eu.europa.fisheries.uvms.tests;

import java.io.File;

import eu.europa.ec.fisheries.uvms.asset.bean.AssetConfigHelper;
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

        File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies().resolve()
                .withTransitivity().asFile();
        testWar.addAsLibraries(files);

        /*File[]*/ /*files = Maven.configureResolver().loadPomFromFile("pom.xml")
                /*.importRuntimeAndTestDependencies()*/
                /*.resolve(/*"eu.europa.ec.fisheries.uvms.asset:deprecated-asset-message",

                        "eu.europa.ec.fisheries.uvms.audit:audit-model",
                        "eu.europa.ec.fisheries.uvms:config-library",
                        "eu.europa.ec.fisheries.uvms.config:config-model:4.0.0")*/
                /*.withTransitivity().asFile();
        testWar.addAsLibraries(files);*/
        

        testWar.addPackages(true, "eu.europa.fisheries.uvms.tests");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.mobileterminal");

        testWar.addClass(AssetConfigHelper.class);

        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");

        return testWar;
    }
}
