package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.enums.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.transaction.*;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
    public void createAssert() {

        // this test is to ensure that create actually works
        AssetSE createdAsset = null;
        try {
            // create an Asset
            AssetSE asset = AssetHelper.createBiggerAsset();
            createdAsset = assetService.createAsset(asset, "test");
            commit();
            Assert.assertTrue(createdAsset != null);
        } catch (AssetException e) {
            Assert.fail();
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAsset() throws AssetException {

        // create an asset
        AssetSE asset = AssetHelper.createBiggerAsset();
        AssetSE createdAsset = assetService.createAsset(asset, "test");
        commit();
        // change it and store it
        createdAsset.setName("ÄNDRAD");
        AssetSE changedAsset = assetService.updateAsset(createdAsset, "CHG_USER", "En changekommentar");
        commit();

        // fetch it and check name
        AssetSE fetchedAsset = assetService.getAssetById(createdAsset.getId());
        Assert.assertEquals(createdAsset.getName(), fetchedAsset.getName());
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAsset() throws AssetException {

        // create an asset
        AssetSE asset = AssetHelper.createBiggerAsset();
        AssetSE createdAsset = assetService.createAsset(asset, "test");
        commit();

        // change it to get an audit
        createdAsset.setName("ÄNDRAD_1");
        AssetSE changedAsset1 = assetService.updateAsset(createdAsset, "CHG_USER_1", "En changekommentar");
        commit();

        // delete  it and flush
        AssetId assetId = new AssetId();
        assetId.setType(AssetIdTypeEnum.INTERNAL_ID);
        assetId.setValue(createdAsset.getId().toString());
        assetId.setGuid(createdAsset.getId());

        assetService.deleteAsset(assetId);
        commit();

        // fetch it and it should be null
        AssetSE fetchedAsset = assetService.getAssetById(createdAsset.getId());
        Assert.assertEquals(fetchedAsset, null);
    }



    @Test
    @OperateOnDeployment("normal")
    public void updateAssetThreeTimesAndCheckRevisionsAndValues() throws AssetException {

        // create an asset
        AssetSE asset = AssetHelper.createBiggerAsset();
        AssetSE createdAsset = assetService.createAsset(asset, "test");
        commit();
        // change it and store it
        createdAsset.setName("ÄNDRAD_1");
        AssetSE changedAsset1 = assetService.updateAsset(createdAsset, "CHG_USER_1", "En changekommentar");
        commit();
        UUID historyId1 = changedAsset1.getHistoryId();

        // change it and store it
        createdAsset.setName("ÄNDRAD_2");
        AssetSE changedAsset2 = assetService.updateAsset(createdAsset, "CHG_USER_2", "En changekommentar");
        commit();
        UUID historyId2 = changedAsset2.getHistoryId();

        // change it and store it
        createdAsset.setName("ÄNDRAD_3");
        AssetSE changedAsset3 = assetService.updateAsset(createdAsset, "CHG_USER_3", "En changekommentar");
        commit();
        UUID historyId3 = changedAsset3.getHistoryId();

        List<AssetSE> assetVersions = assetService.getRevisionsForAsset(asset);
        Assert.assertEquals(assetVersions.size(), 4);
        commit();


        AssetSE  fetchedAssetAtRevision = assetService.getAssetRevisionForRevisionId(changedAsset2, historyId2);

        Assert.assertEquals(historyId2, fetchedAssetAtRevision.getHistoryId());






    }






    private void commit() throws AssetException {

        try {
            userTransaction.commit();
            userTransaction.begin();
        } catch (RollbackException |HeuristicMixedException | HeuristicRollbackException |SystemException |  NotSupportedException e) {
            throw new AssetException(e);
        }
    }



}
