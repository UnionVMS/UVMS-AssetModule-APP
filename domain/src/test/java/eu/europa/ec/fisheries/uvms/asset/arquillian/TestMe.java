package eu.europa.ec.fisheries.uvms.asset.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;

/**
 * Created by thofan on 2017-05-31.
 */

@RunWith(Arquillian.class)
public class TestMe extends TransactionalTests {

    final static Logger LOG = LoggerFactory.getLogger(TestMe.class);

    @EJB
    AssetDao assetDao;


    @Test
    @OperateOnDeployment("normal")
    public void test()  {

        try {
            List<AssetEntity> rs = assetDao.getAssetListAll();
            Assert.assertTrue(rs != null);
        } catch (AssetDaoException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }







}
