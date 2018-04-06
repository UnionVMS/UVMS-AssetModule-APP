package eu.europa.ec.fisheries.uvms.asset.arquillian;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Created by andreasw on 2017-02-13.
 */
@ArquillianSuiteDeployment
public abstract class BuildAssetDeployment {

    @Deployment(name = "normal", order = 1)
    public static Archive<?> createDeployment() {

        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "test.war");

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.domain");
        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.asset.arquillian");

        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");

        return testWar;
    }
}
