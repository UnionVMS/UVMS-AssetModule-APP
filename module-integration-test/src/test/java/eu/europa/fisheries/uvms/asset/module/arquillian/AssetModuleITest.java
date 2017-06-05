package eu.europa.fisheries.uvms.asset.module.arquillian;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class AssetModuleITest extends BuildAssetModuleTestDeployment {

    @Test
    @OperateOnDeployment("assetmodule")
    public void validateEarModuleDeploymentTest()  {
    }

}
