package eu.europa.fisheries.uvms.asset.service.arquillian;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.ejb.EJB;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import eu.europa.ec.fisheries.uvms.asset.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.AssetService;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;


@RunWith(Arquillian.class)
public class AssetGroupServiceBeanIntTest extends TransactionalTests {


    Random rnd = new Random();


    @EJB
    AssetService assetService;


    @EJB
    AssetGroupService assetGroupService;


    @Test
    @OperateOnDeployment("normal")
    public void createAssetGroup() throws AssetServiceException {
        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        Assert.assertTrue(createdAssetGroupEntity != null);
        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), "TEST");
    }


    @Test
    @OperateOnDeployment("normal")
    public void deleteAssetGroupById() throws AssetServiceException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();
        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), createdAssetGroupEntity.getOwner());
        AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
        Assert.assertTrue(fetchedAssetGroupEntity != null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupById() throws AssetServiceException, HeuristicRollbackException, RollbackException, NotSupportedException, HeuristicMixedException, SystemException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();
        Assert.assertTrue(createdAssetGroupEntity.getArchived().equals(false));

        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), createdAssetGroupEntity.getOwner());
        commit();

        AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
        Assert.assertTrue(fetchedAssetGroupEntity.getId().equals(guid));
        Assert.assertTrue(fetchedAssetGroupEntity.getArchived().equals(true));
        em.createQuery("delete from AssetGroup ag where ag.id = :id").setParameter("id",guid).executeUpdate();
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroup() throws AssetServiceException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();
        String oldUserName = createdAssetGroupEntity.getOwner();
        String newUserName = "UPDATED_SERVICE_TEST";
        createdAssetGroupEntity.setOwner(newUserName);

        AssetGroup updatedAssetGroupEntity = assetGroupService.updateAssetGroup(createdAssetGroupEntity, newUserName);
        AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
        Assert.assertFalse(fetchedAssetGroupEntity.getOwner().equalsIgnoreCase(oldUserName));
//            Assert.assertTrue(false);
        em.createQuery("delete from AssetGroup ag where ag.id = :id").setParameter("id",guid).executeUpdate();

    }


    @Test
    @OperateOnDeployment("normal")
    @Ignore
    public void getAssetGroupListByAssetGuid() throws AssetServiceException, HeuristicRollbackException, RollbackException, NotSupportedException, HeuristicMixedException, SystemException {


        Asset asset = AssetHelper.createBiggerAsset();
        Asset createdAsset = assetService.createAsset(asset, "test");
        // commit();
        UUID assetGuid = createdAsset.getId();

        List<UUID> createdList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST");
            createdList.add(createdAssetGroupEntity.getId());
        }

        // HÄR GÖR NÅGOT VETTIGT  skapa en rad med ett GUID från en asset
        UUID uuidAssetGroup = createdList.get(3);
        AssetGroup anAssetGroup = assetGroupService.getAssetGroupById(uuidAssetGroup);

        AssetGroupField assetGroupField = new AssetGroupField();
        assetGroupField.setAssetGroup(anAssetGroup.getId());
        assetGroupField.setField("GUID");
        assetGroupField.setValue(assetGuid.toString());
        assetGroupField.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));

        assetGroupService.createAssetGroupField(anAssetGroup.getId(), assetGroupField, "TEST");
        //       commit();


        List<AssetGroup> fetchedEntityList = assetGroupService.getAssetGroupListByAssetId(assetGuid);
        List<UUID> fetchedList = new ArrayList<>();
        for (AssetGroup e : fetchedEntityList) {
            fetchedList.add(e.getId());
        }
        // the list from db MUST contain our created GUID:s
        Boolean ok = false;
        if (createdList.contains(fetchedList.get(0))) {
            ok = true;
        }
        Assert.assertTrue(ok);
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupList() throws AssetServiceException {

        String user1 = UUID.randomUUID().toString();
        String user2 = UUID.randomUUID().toString();
        String user3 = UUID.randomUUID().toString();

        for (int i = 0; i < 3; i++) {
            createAndStoreAssetGroupEntity(user1);
        }
        for (int i = 0; i < 8; i++) {
            createAndStoreAssetGroupEntity(user2);
        }
        for (int i = 0; i < 11; i++) {
            createAndStoreAssetGroupEntity(user3);
        }

        List<AssetGroup> listUser1 = assetGroupService.getAssetGroupList(user1);
        List<AssetGroup> listUser2 = assetGroupService.getAssetGroupList(user2);
        List<AssetGroup> listUser3 = assetGroupService.getAssetGroupList(user3);

        Assert.assertTrue(listUser1.size() == 3);
        Assert.assertTrue(listUser2.size() == 8);
        Assert.assertTrue(listUser3.size() == 11);

    }


/*
    void removeFieldsForGroup(AssetGroup assetGroup)  throws InputArgumentException;
*/


    @Test
    @OperateOnDeployment("normal")
    public void createAssetGroupField() throws AssetServiceException {

        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        Assert.assertNotNull(fetchedAssetGroupField);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroupField() throws AssetServiceException {

        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());

        fetchedAssetGroupField.setValue("CHANGEDVALUE");
        assetGroupService.updateAssetGroupField(fetchedAssetGroupField, "TEST");
        AssetGroupField fetchedAssetGroupField2 = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        Assert.assertEquals(fetchedAssetGroupField2.getValue(), "CHANGEDVALUE");

    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupField() throws AssetServiceException {

        // same as create . . .
        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        Assert.assertNotNull(fetchedAssetGroupField);

    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAssetGroupField() throws AssetServiceException {

        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.deleteAssetGroupField(createdAssetGroupField.getId(), "TESTER");
        fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        Assert.assertNull(fetchedAssetGroupField);

    }


    private AssetGroupField createAssetGroupFieldHelper() throws AssetServiceException {

        AssetGroup anAssetGroup = createAndStoreAssetGroupEntity("TEST");
        AssetGroupField assetGroupField = new AssetGroupField();
        assetGroupField.setAssetGroup(anAssetGroup.getId());
        assetGroupField.setField("GUID");
        assetGroupField.setValue(UUID.randomUUID().toString());
        assetGroupField.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
        return assetGroupService.createAssetGroupField(anAssetGroup.getId(), assetGroupField, "TEST");

    }


    private AssetGroup createAndStoreAssetGroupEntity(String user) throws AssetServiceException {

        AssetGroup assetGroupEntity = createAssetGroupEntity(user);
        Assert.assertTrue(assetGroupEntity.getId() == null);

        AssetGroup createdAssetGroupEntity = assetGroupService.createAssetGroup(assetGroupEntity, user);
        Assert.assertTrue(createdAssetGroupEntity.getId() != null);
        return createdAssetGroupEntity;
    }


    private AssetGroup createAssetGroupEntity(String user) {
        AssetGroup ag = new AssetGroup();
        ag.setUpdatedBy("test");
        ag.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
        ag.setArchived(false);
        ag.setName("The Name");
        ag.setOwner(user);
        ag.setDynamic(false);
        ag.setGlobal(true);

        return ag;
    }


    private void commit() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {

        userTransaction.commit();
        userTransaction.begin();


    }


}
