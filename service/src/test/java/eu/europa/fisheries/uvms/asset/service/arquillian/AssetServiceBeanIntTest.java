package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.asset.types.AssetDTO;
import eu.europa.ec.fisheries.asset.types.AssetIdType;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Random;

/**
 * Created by thofan on 2017-06-01.
 */

@RunWith(Arquillian.class)
public class AssetServiceBeanIntTest extends TransactionalTests {

    Random rnd = new Random();


    @EJB
    AssetService assetService;


    @Test
    @OperateOnDeployment("normal")
    public void crtAssert() {

        /*

        // this test is to ensure that create actually works
        AssetDTO createdAsset = null;
        try {
            // create an Asset
            createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();
            Assert.assertTrue(createdAsset != null);
        } catch (AssetException e) {
            Assert.fail();
        }

        */
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetById_INTERNAL_TYPE_GUID() {

        /*

        AssetDTO createdAsset = null;
        AssetDTO fetched_asset = null;
        try {
            // create an Asset
            createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();
            // fetch it and compare guid to verify
            fetched_asset = assetService.getAssetById(createdAsset.getAssetId(), AssetDataSourceQueue.INTERNAL);
// @formatter:off
            boolean ok = fetched_asset != null &&
                    fetched_asset.getAssetId() != null &&
                    fetched_asset.getAssetId().getGuid() != null;
// @formatter:on
            if (ok) {
                Assert.assertTrue(createdAsset.getAssetId().getGuid().equals(fetched_asset.getAssetId().getGuid()));
            } else {
                Assert.fail();
            }
        } catch (AssetException e) {
            Assert.fail();
        }

        */
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetById_INTERNAL_TYPE_INTERNAL_ID() {

/*

        try {
            // create an Asset
            AssetDTO createdAsset = assetService.createAsset(AssetHelper.helper_createAsset(AssetIdType.INTERNAL_ID), "test");
            em.flush();
            // fetch it and compare guid to verify
            AssetDTO fetched_asset = assetService.getAssetById(createdAsset.getAssetId(), AssetDataSourceQueue.INTERNAL);
// @formatter:off
            boolean ok = fetched_asset != null &&
                    fetched_asset.getAssetId() != null &&
                    fetched_asset.getAssetId().getGuid() != null;
// @formatter:on
            if (ok) {
                Assert.assertTrue(createdAsset.getAssetId().getGuid().equals(fetched_asset.getAssetId().getGuid()));
            } else {
                Assert.fail();
            }
        } catch (AssetException e) {
            Assert.fail();
        }


        */

    }


    @Test
    @OperateOnDeployment("normal")
    public void upsert_createVersion() {

        /*

        AssetDTO createdAsset = null;
        AssetDTO fetched_asset = null;
        try {
            // create an Asset
            createdAsset = assetService.upsertAsset(AssetHelper.helper_createAsset(AssetIdType.GUID), "test");
            em.flush();
            // fetch it and compare guid to verify
            fetched_asset = assetService.getAssetById(createdAsset.getAssetId(), AssetDataSourceQueue.INTERNAL);
// @formatter:off
            boolean ok = fetched_asset != null &&
                    fetched_asset.getAssetId() != null &&
                    fetched_asset.getAssetId().getGuid() != null;
// @formatter:on
            if (ok) {
                Assert.assertTrue(createdAsset.getAssetId().getGuid().equals(fetched_asset.getAssetId().getGuid()));
            } else {
                Assert.fail();
            }
        } catch (AssetException e) {
            Assert.fail();
        }

        */
    }





}
