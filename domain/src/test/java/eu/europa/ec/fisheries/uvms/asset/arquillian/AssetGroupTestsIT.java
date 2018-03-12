package eu.europa.ec.fisheries.uvms.asset.arquillian;


import eu.europa.ec.fisheries.uvms.asset.types.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(Arquillian.class)
public class AssetGroupTestsIT extends TransactionalTests  {


    private Random rnd = new Random();

    @EJB
    private AssetGroupDao assetGroupDao;



    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupAll() throws AssetGroupDaoException {

        List<AssetGroupEntity> rs = assetGroupDao.getAssetGroupAll();


        System.out.println(rs);


    }



    @Test
    @OperateOnDeployment("normal")
    public void createAssetGroup() throws AssetGroupDaoException {


        AssetGroupEntity assetGroupEntity = createAssetGroupEntity();

        AssetGroupEntity createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroupEntity);


    }

    private AssetGroupEntity createAssetGroupEntity() {
        AssetGroupEntity ag = new AssetGroupEntity();

        ag.setUpdatedBy("test");
        ag.setUpdateTime(new Date(System.currentTimeMillis()));
        ag.setArchived(false);
        ag.setName("Donald the Trumpier Duck");
        ag.setOwner("Earth");
        ag.setDynamic(false);
        ag.setGlobal(true);

        return ag;
    }


}
