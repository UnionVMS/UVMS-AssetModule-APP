package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.UUID;

@RunWith(Arquillian.class)
public class AssetHistoryTestsIT  extends TransactionalTests {

    private AssetTestsHelper assetTestsHelper = new AssetTestsHelper();

    @EJB
    private AssetDao assetDao;


    @Test
    @OperateOnDeployment("normal")
    public void asset_flagstates_tests() throws AssetDaoException {

    }










    private AssetEntity create(AssetIdType key, String value, Date date) {

        AssetEntity assetEntity = assetTestsHelper.createAssetHelper(key, value, date);
        try {
            AssetEntity createdAsset = assetDao.createAsset(assetEntity);
            em.flush();
            String guid = createdAsset.getGuid();
            AssetEntity fetchedAsset = assetDao.getAssetByGuid(guid);
            Assert.assertEquals(createdAsset.getId(), fetchedAsset.getId());
            return fetchedAsset;
        } catch (AssetDaoException e) {
            Assert.fail();
            return null;
        }
    }



}
