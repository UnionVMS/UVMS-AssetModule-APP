package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.dao.bean.AssetSEDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetSE;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class Asset_SE_Tests extends TransactionalTests {

    @Inject
    AssetSEDao assetDao;

    @Test
    @OperateOnDeployment("normal")
    public void createAndGetAssetTest() {

        AssetSE asset = new AssetSE();
        //asset = assetDao.createAsset(asset);

        //AssetSE fetchedAsset = assetDao.find(asset.getId());
        //assertEquals(asset.getId(), fetchedAsset.getId());
    }
}
