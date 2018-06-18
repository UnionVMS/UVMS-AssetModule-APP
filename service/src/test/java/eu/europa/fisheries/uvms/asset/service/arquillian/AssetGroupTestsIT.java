package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import eu.europa.fisheries.uvms.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(Arquillian.class)
public class AssetGroupTestsIT extends TransactionalTests {

    @Inject
    private AssetGroupDao assetGroupDao;

    @Inject
    private AssetGroupFieldDao assetGroupFieldDaoBean;

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupAll() {

        List<UUID> createdList = new ArrayList<>();
        List<UUID> fetchedList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
            createdList.add(createdAssetGroupEntity.getId());
        }
        List<AssetGroup> rs = assetGroupDao.getAssetGroupAll();
        for (AssetGroup e : rs) {
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
    public void createAssetGroup() {

        AssetGroup createdAssetGroupEntity1 = createAndStoreAssetGroupEntity("TEST",1);
        Assert.assertTrue(createdAssetGroupEntity1 != null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupByUser() {

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

        List<AssetGroup> listUser1 = assetGroupDao.getAssetGroupByUser(user1);
        List<AssetGroup> listUser2 = assetGroupDao.getAssetGroupByUser(user2);
        List<AssetGroup> listUser3 = assetGroupDao.getAssetGroupByUser(user3);

        Assert.assertTrue(listUser1.size() == 3);
        Assert.assertTrue(listUser2.size() == 8);
        Assert.assertTrue(listUser3.size() == 11);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupByGuid() {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
        UUID guid = createdAssetGroupEntity.getId();
        Assert.assertTrue(guid != null);

        AssetGroup fetchedAssetGroupEntity = assetGroupDao.getAssetGroupByGuid(guid);
        Assert.assertEquals(guid, fetchedAssetGroupEntity.getId());
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupByGUIDS() {

        List<UUID> createdList = new ArrayList<>();
        List<UUID> fetchedList = new ArrayList<>();
        List<AssetGroup> fetchedEntityList;
        for (int i = 0; i < 5; i++) {
            AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
            createdList.add(createdAssetGroupEntity.getId());
        }

        fetchedEntityList = assetGroupDao.getAssetGroupsByGroupGuidList(createdList);
        for (AssetGroup e : fetchedEntityList) {
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
    public void deleteAssetGroup() {

        AssetGroup assetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
        UUID uuid = assetGroupEntity.getId();
        assetGroupDao.deleteAssetGroup(assetGroupEntity);

            AssetGroup fetchedGroup = assetGroupDao.getAssetGroupByGuid(uuid);
            Assert.assertTrue(fetchedGroup == null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroup() {

        AssetGroup assetGroupEntity = createAndStoreAssetGroupEntity("TEST",1);
        UUID uuid = assetGroupEntity.getId();

        assetGroupEntity.setOwner("NEW OWNER");
        assetGroupDao.updateAssetGroup(assetGroupEntity);
        em.flush();

        AssetGroup fetchedGroup = assetGroupDao.getAssetGroupByGuid(uuid);
        Assert.assertTrue(fetchedGroup.getOwner().equalsIgnoreCase("NEW OWNER"));
    }


    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroupAndFields() {




        AssetGroup assetGroupEntity = createAndStoreAssetGroupEntity("TEST",42);
        UUID uuid = assetGroupEntity.getId();

        assetGroupEntity.setOwner("NEW OWNER");
        List<AssetGroupField>  newLines = createAssetGroupFields( assetGroupEntity,  assetGroupEntity.getUpdateTime(), assetGroupEntity.getOwner(), 17);

        assetGroupDao.updateAssetGroup(assetGroupEntity);
        em.flush();

        AssetGroup fetchedGroup = assetGroupDao.getAssetGroupByGuid(uuid);
        Assert.assertTrue(fetchedGroup.getOwner().equalsIgnoreCase("NEW OWNER"));
    }



    private AssetGroup createAndStoreAssetGroupEntity(String user, int numberOfGroupFields) {

        AssetGroup assetGroupEntity = createAssetGroupEntity(user,numberOfGroupFields);
        AssetGroup createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroupEntity);
        return createdAssetGroupEntity;
    }


    private AssetGroup createAssetGroupEntity(String user, int numberOfGroupFields) {
        AssetGroup ag = new AssetGroup();

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


    private  List<AssetGroupField> createAssetGroupFields(AssetGroup assetGroupEntity, LocalDateTime dt, String user, int n) {

        List<AssetGroupField> groupFields = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String uuid = UUID.randomUUID().toString();
            AssetGroupField field = createAssetGroupField(assetGroupEntity, "GUID", uuid, dt, user);
            groupFields.add(field);
        }
        return groupFields;
    }


    private AssetGroupField createAssetGroupField(AssetGroup assetGroupEntity, String key, String keyFieldValue, LocalDateTime dt, String user) {

        AssetGroupField ag = new AssetGroupField();
        ag.setAssetGroup(assetGroupEntity.getId());
        ag.setUpdatedBy(user);
        ag.setUpdateTime(dt);
        ag.setField(key);
        ag.setValue(keyFieldValue);


        return ag;
    }


}
