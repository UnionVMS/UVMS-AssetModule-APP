package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.fisheries.uvms.TransactionalTests;
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
        /*

        // simplest possible test
        // Since it uses AssetEntity internally cascaded deletion should be automatically handled

        try {
            AssetDTO createdAsset = null;
            // create an Asset
            createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();

            AssetId assetId = createdAsset.getAssetId();

            assetService.deleteAsset(assetId);
            Assert.assertTrue(createdAsset != null);
        } catch (AssetException e) {
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.fail();
        }

        */
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAsset_FAIL_NonsenseKey() {
        /*

        // simplest possible test
        // Since it uses AssetEntity internally cascaded deletion should be automatically handled

        try {
            AssetDTO createdAsset = null;
            // create an Asset
            createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();

            AssetId assetId = createdAsset.getAssetId();
            assetId.setValue("NONSENS");

            assetService.deleteAsset(assetId);
            Assert.fail();
        } catch (AssetException e) {
            Assert.assertTrue(true);
        }

        */
    }
}
