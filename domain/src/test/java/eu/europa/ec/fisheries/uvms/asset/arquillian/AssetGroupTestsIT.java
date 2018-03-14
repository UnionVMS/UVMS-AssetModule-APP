package eu.europa.ec.fisheries.uvms.asset.arquillian;


import eu.europa.ec.fisheries.uvms.asset.types.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetGroupFieldDaoBean;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@RunWith(Arquillian.class)
public class AssetGroupTestsIT extends TransactionalTests {


    private Random rnd = new Random();

    @EJB
    private AssetGroupDao assetGroupDao;

    @EJB
    AssetGroupFieldDao assetGroupFieldDaoBean;


    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupAll() throws AssetGroupDaoException {

        List<UUID> createdList = new ArrayList<>();
        List<UUID> fetchedList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            AssetGroupEntity createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
            createdList.add(createdAssetGroupEntity.getId());
        }
        List<AssetGroupEntity> rs = assetGroupDao.getAssetGroupAll();
        for (AssetGroupEntity e : rs) {
            fetchedList.add(e.getId());
        }

        // the list from db MUST contain our created Id:s
        Boolean ok = true;
        for (UUID aCreated : createdList) {
            if (!fetchedList.contains(aCreated)) {
                ok = false;
                break;
            }
        }
        Assert.assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetGroup() throws AssetGroupDaoException {

        AssetGroupEntity createdAssetGroupEntity1 = createAndStoreAssetGroupEntity("TEST",1);
        Assert.assertTrue(createdAssetGroupEntity1 != null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupByUser() throws AssetGroupDaoException {

        String user1 = UUID.randomUUID().toString();
        String user2 = UUID.randomUUID().toString();
        String user3 = UUID.randomUUID().toString();

        for (int i = 0; i < 3; i++) {
            createAndStoreAssetGroupEntity(user1,1);
        }
        for (int i = 0; i < 8; i++) {
            createAndStoreAssetGroupEntity(user2,1);
        }
        for (int i = 0; i < 11; i++) {
            createAndStoreAssetGroupEntity(user3,1);
        }

        List<AssetGroupEntity> listUser1 = assetGroupDao.getAssetGroupByUser(user1);
        List<AssetGroupEntity> listUser2 = assetGroupDao.getAssetGroupByUser(user2);
        List<AssetGroupEntity> listUser3 = assetGroupDao.getAssetGroupByUser(user3);

        Assert.assertTrue(listUser1.size() == 3);
        Assert.assertTrue(listUser2.size() == 8);
        Assert.assertTrue(listUser3.size() == 11);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupByGuid() throws AssetGroupDaoException {

        AssetGroupEntity createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
        UUID guid = createdAssetGroupEntity.getId();
        Assert.assertTrue(guid != null);

        AssetGroupEntity fetchedAssetGroupEntity = assetGroupDao.getAssetGroupByGuid(guid);
        Assert.assertEquals(guid, fetchedAssetGroupEntity.getId());
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupByGUIDS() throws AssetGroupDaoException {

        List<UUID> createdList = new ArrayList<>();
        List<UUID> fetchedList = new ArrayList<>();
        List<AssetGroupEntity> fetchedEntityList;
        for (int i = 0; i < 5; i++) {
            AssetGroupEntity createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
            createdList.add(createdAssetGroupEntity.getId());
        }

        fetchedEntityList = assetGroupDao.getAssetGroupsByGroupGuidList(createdList);
        for (AssetGroupEntity e : fetchedEntityList) {
            fetchedList.add(e.getId());
        }

        // the list from db MUST contain our created GUIDS:s
        Boolean ok = true;
        for (UUID aCreatedGUID : createdList) {
            if (!fetchedList.contains(aCreatedGUID)) {
                ok = false;
                break;
            }
        }
        Assert.assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAssetGroup() throws AssetGroupDaoException {

        AssetGroupEntity assetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
        UUID uuid = assetGroupEntity.getId();
        assetGroupDao.deleteAssetGroup(assetGroupEntity);

            AssetGroupEntity fetchedGroup = assetGroupDao.getAssetGroupByGuid(uuid);
            Assert.assertTrue(fetchedGroup == null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroup() throws AssetGroupDaoException {

        AssetGroupEntity assetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
        UUID uuid = assetGroupEntity.getId();

        assetGroupEntity.setOwner("NEW OWNER");
        assetGroupDao.updateAssetGroup(assetGroupEntity);
        em.flush();

        AssetGroupEntity fetchedGroup = assetGroupDao.getAssetGroupByGuid(uuid);
        Assert.assertTrue(fetchedGroup.getOwner().equalsIgnoreCase("NEW OWNER"));
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroupAndFields() throws AssetGroupDaoException {




        AssetGroupEntity assetGroupEntity = createAndStoreAssetGroupEntity("TEST",42);
        UUID uuid = assetGroupEntity.getId();

        assetGroupEntity.setOwner("NEW OWNER");
        List<AssetGroupField>  newLines = createAssetGroupFields( assetGroupEntity,  assetGroupEntity.getUpdateTime(), assetGroupEntity.getOwner(), 17);

        assetGroupDao.updateAssetGroup(assetGroupEntity);
        assetGroupFieldDaoBean.syncFields(assetGroupEntity, newLines);
        em.flush();

        AssetGroupEntity fetchedGroup = assetGroupDao.getAssetGroupByGuid(uuid);
        Assert.assertTrue(fetchedGroup.getOwner().equalsIgnoreCase("NEW OWNER"));
    }



    private AssetGroupEntity createAndStoreAssetGroupEntity(String user, int numberOfGroupFields) throws AssetGroupDaoException {

        AssetGroupEntity assetGroupEntity = createAssetGroupEntity(user,numberOfGroupFields);
        AssetGroupEntity createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroupEntity);
        return createdAssetGroupEntity;
    }


    private AssetGroupEntity createAssetGroupEntity(String user, int numberOfGroupFields) {
        AssetGroupEntity ag = new AssetGroupEntity();

        LocalDateTime dt = LocalDateTime.now(Clock.systemUTC());

        ag.setUpdatedBy("test");
        ag.setUpdateTime(dt);
        ag.setArchived(false);
        ag.setName("The Name");
        ag.setOwner(user);
        ag.setDynamic(false);
        ag.setGlobal(true);

        List<AssetGroupField> groupFields = createAssetGroupFields(ag,dt,user, numberOfGroupFields);
        return ag;
    }


    private  List<AssetGroupField> createAssetGroupFields(AssetGroupEntity assetGroupEntity, LocalDateTime dt, String user, int n) {

        List<AssetGroupField> groupFields = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String uuid = UUID.randomUUID().toString();
            AssetGroupField field = createAssetGroupField(assetGroupEntity, ConfigSearchFieldEnum.GUID, uuid, dt, user);
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
