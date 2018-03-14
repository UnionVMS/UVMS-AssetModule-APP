package eu.europa.ec.fisheries.uvms.asset.arquillian;


import eu.europa.ec.fisheries.uvms.asset.types.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetGroupFieldDaoBean;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(Arquillian.class)
public class AssetGroupFieldTestsIT extends TransactionalTests {

    private Random rnd = new Random();



    @EJB
    private AssetGroupDao assetGroupDao;

    @EJB
    AssetGroupFieldDao assetGroupFieldDaoBean;


    @Test
    public void create() throws AssetGroupDaoException {

        String user = "test";
        AssetGroupEntity assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        Long createdId = createdAssetGroupField.getId();
        AssetGroupField fetchedAssetGroupField =  getField(createdId);
        Assert.assertTrue(fetchedAssetGroupField != null);

    }

    @Test
    public void get() throws AssetGroupDaoException {

        String user = "test";
        AssetGroupEntity assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        Long createdId = createdAssetGroupField.getId();
        AssetGroupField fetchedAssetGroupField =  getField(createdId);
        Assert.assertEquals(createdId, fetchedAssetGroupField.getId());
    }

    @Test
    public void delete() throws AssetGroupDaoException {

        String user = "test";
        AssetGroupEntity assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        Long createdId = createdAssetGroupField.getId();
        assetGroupFieldDaoBean.delete(createdAssetGroupField);
        AssetGroupField fetchedAssetGroupField =  getField(createdId);
        Assert.assertTrue(fetchedAssetGroupField == null);
    }

    @Test
    public void update() throws AssetGroupDaoException {

        String user = "test";
        AssetGroupEntity assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        Long createdId = createdAssetGroupField.getId();
        String createdUpdatedBy = createdAssetGroupField.getUpdatedBy();
        createdAssetGroupField.setUpdatedBy("PEKKA");
        assetGroupFieldDaoBean.update(createdAssetGroupField);
        AssetGroupField fetchedAssetGroupField =  getField(createdId);

        Assert.assertNotNull(fetchedAssetGroupField);
        Assert.assertNotEquals(createdUpdatedBy, fetchedAssetGroupField.getUpdatedBy());

    }

    @Test
    public void removeFieldsForGroup() throws AssetGroupDaoException {

        String user = "test";
        AssetGroupEntity assetGroup1 = createAndStoreAssetGroupEntity(user);
        AssetGroupEntity assetGroup2 = createAndStoreAssetGroupEntity(user);
        List<AssetGroupField> createdAssetGroupFields1 = createAndStoreAssetGroupFieldEntityList(assetGroup1, 50);
        List<AssetGroupField> createdAssetGroupFields2 = createAndStoreAssetGroupFieldEntityList(assetGroup2, 25 );


        assetGroupFieldDaoBean.removeFieldsForGroup(assetGroup1);

        List<AssetGroupField> retrievedAssetGroupFields1 =  assetGroupFieldDaoBean.retrieveFieldsForGroup(assetGroup1);
        List<AssetGroupField> retrievedAssetGroupFields2 =  assetGroupFieldDaoBean.retrieveFieldsForGroup(assetGroup2);

        Assert.assertEquals(retrievedAssetGroupFields1.size(), 0);
        Assert.assertEquals(retrievedAssetGroupFields2.size(), 25);




    }



    private List<AssetGroupField>  createAndStoreAssetGroupFieldEntityList(AssetGroupEntity assetGroup, int n)
    {
        LocalDateTime dt = LocalDateTime.now(Clock.systemUTC());
        List<AssetGroupField> groupFields = createAssetGroupFields(assetGroup,dt,assetGroup.getOwner(), n);
        return groupFields;

    }


    private AssetGroupField getField(Long id) throws AssetGroupDaoException {

        AssetGroupField assetGroupField =  assetGroupFieldDaoBean.get(id);
        return assetGroupField;
    }


    private AssetGroupField createAndStoreAssetGroupFieldEntity(AssetGroupEntity assetGroup) throws AssetGroupDaoException {

        LocalDateTime dt = LocalDateTime.now(Clock.systemUTC());
        List<AssetGroupField> groupFields = createAssetGroupFields(assetGroup,dt,assetGroup.getOwner(), 1);
        AssetGroupField assetGroupField = groupFields.get(0);
        AssetGroupField createdAssetGroupField = assetGroupFieldDaoBean.create(assetGroupField);
        return createdAssetGroupField;
    }



    private AssetGroupEntity createAndStoreAssetGroupEntity(String user) throws AssetGroupDaoException {

        AssetGroupEntity assetGroupEntity = createAssetGroupEntity(user);
        AssetGroupEntity createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroupEntity);
        return createdAssetGroupEntity;
    }

    private AssetGroupEntity createAssetGroupEntity(String user) {
        AssetGroupEntity ag = new AssetGroupEntity();

        LocalDateTime dt = LocalDateTime.now(Clock.systemUTC());

        ag.setUpdatedBy("test");
        ag.setUpdateTime(dt);
        ag.setArchived(false);
        ag.setName("The Name");
        ag.setOwner(user);
        ag.setDynamic(false);
        ag.setGlobal(true);
        return ag;

    }

    private  List<AssetGroupField> createAssetGroupFields(AssetGroupEntity assetGroupEntity, LocalDateTime dt, String user, int n) {

        List<AssetGroupField> groupFields = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String uuid = UUID.randomUUID().toString();
            AssetGroupField field = createAssetGroupField(assetGroupEntity, ConfigSearchFieldEnum.GUID, uuid, dt, user);
            assetGroupFieldDaoBean.create(field);
            groupFields.add(field);
        }
        return groupFields;
    }


    private AssetGroupField createAssetGroupField(AssetGroupEntity assetGroupEntity, ConfigSearchFieldEnum key, String keyFieldValue, LocalDateTime dt, String user) {

        AssetGroupField ag = new AssetGroupField();
        ag.setAssetGroup(assetGroupEntity);
        ag.setUpdatedBy(user);
        ag.setUpdateTime(dt);
        ag.setField(key.value());
        ag.setValue(keyFieldValue);


        return ag;
    }




}
