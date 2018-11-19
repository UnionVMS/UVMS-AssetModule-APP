package eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

/**
 * Created by thofan on 2017-06-08.
 */

@RunWith(Arquillian.class)
public class DeleteAssetIntTest extends TransactionalTests {

    @EJB
    private AssetService assetService;

    @Test
    @OperateOnDeployment("normal")
    public void deleteAsset() {

    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAsset_FAIL_NonsenseKey() {

    }
}
