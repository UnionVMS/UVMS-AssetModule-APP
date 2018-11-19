package eu.europa.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
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

import eu.europa.fisheries.uvms.tests.TransactionalTests;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class AssetGroupServiceBeanIntTest extends TransactionalTests {

    @EJB
    private AssetService assetService;

    @EJB
    private AssetGroupService assetGroupService;

    @Test
    @OperateOnDeployment("normal")
    public void createAssetGroup() {
        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        assertNotNull(createdAssetGroupEntity);
        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), "TEST");
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAssetGroupById() {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();
        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), createdAssetGroupEntity.getOwner());
        AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
        assertNotNull(fetchedAssetGroupEntity);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupById() throws HeuristicRollbackException, RollbackException, NotSupportedException, HeuristicMixedException, SystemException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();
        assertEquals(false, createdAssetGroupEntity.getArchived());

        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), createdAssetGroupEntity.getOwner());
        commit();

        AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
        assertEquals(fetchedAssetGroupEntity.getId(), guid);
        assertEquals(true, fetchedAssetGroupEntity.getArchived());
        em.createQuery("delete from AssetGroup ag where ag.id = :id").setParameter("id",guid).executeUpdate();
        userTransaction.commit();
        userTransaction.begin();
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroup() {
        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();
        String oldUserName = createdAssetGroupEntity.getOwner();
        String newUserName = "UPDATED_SERVICE_TEST";
        createdAssetGroupEntity.setOwner(newUserName);

        AssetGroup updatedAssetGroupEntity = assetGroupService.updateAssetGroup(createdAssetGroupEntity, newUserName);
        AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
        Assert.assertFalse(fetchedAssetGroupEntity.getOwner().equalsIgnoreCase(oldUserName));
        em.createQuery("delete from AssetGroup ag where ag.id = :id").setParameter("id",guid).executeUpdate();
    }

    @Test
    @OperateOnDeployment("normal")
    @Ignore
    public void getAssetGroupListByAssetGuid() {

        Asset asset = AssetTestsHelper.createBiggerAsset();
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
        assetGroupField.setAssetGroup(anAssetGroup);
        assetGroupField.setKey("GUID");
        assetGroupField.setValue(assetGuid.toString());
        assetGroupField.setUpdateTime(OffsetDateTime.now(Clock.systemUTC()));

        assetGroupService.createAssetGroupField(anAssetGroup.getId(), assetGroupField, "TEST");

        List<AssetGroup> fetchedEntityList = assetGroupService.getAssetGroupListByAssetId(assetGuid);
        List<UUID> fetchedList = new ArrayList<>();
        for (AssetGroup e : fetchedEntityList) {
            fetchedList.add(e.getId());
        }
        // the list from db MUST contain our created GUID:s
        boolean ok = false;
        if (createdList.contains(fetchedList.get(0))) {
            ok = true;
        }
        Assert.assertTrue(ok);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupList() {

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

        assertEquals(3, listUser1.size());
        assertEquals(8, listUser2.size());
        assertEquals(11, listUser3.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssetGroupField() {

        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        assertNotNull(fetchedAssetGroupField);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroupField() {

        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());

        fetchedAssetGroupField.setValue("CHANGEDVALUE");
        assetGroupService.updateAssetGroupField(fetchedAssetGroupField, "TEST");
        AssetGroupField fetchedAssetGroupField2 = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        assertEquals(fetchedAssetGroupField2.getValue(), "CHANGEDVALUE");
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupField() {
        // same as create . . .
        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        assertNotNull(fetchedAssetGroupField);
    }

    @Test
    @OperateOnDeployment("normal")
    public void deleteAssetGroupField() {
        AssetGroupField createdAssetGroupField = createAssetGroupFieldHelper();
        AssetGroupField fetchedAssetGroupField = assetGroupService.deleteAssetGroupField(createdAssetGroupField.getId(), "TESTER");
        fetchedAssetGroupField = assetGroupService.getAssetGroupField(createdAssetGroupField.getId());
        Assert.assertNull(fetchedAssetGroupField);
    }

    private AssetGroupField createAssetGroupFieldHelper() {
        AssetGroup anAssetGroup = createAndStoreAssetGroupEntity("TEST");
        AssetGroupField assetGroupField = new AssetGroupField();
        assetGroupField.setAssetGroup(anAssetGroup);
        assetGroupField.setKey("GUID");
        assetGroupField.setValue(UUID.randomUUID().toString());
        assetGroupField.setUpdateTime(OffsetDateTime.now(Clock.systemUTC()));
        return assetGroupService.createAssetGroupField(anAssetGroup.getId(), assetGroupField, "TEST");
    }

    private AssetGroup createAndStoreAssetGroupEntity(String user) {
        AssetGroup assetGroupEntity = createAssetGroupEntity(user);
        Assert.assertNull(assetGroupEntity.getId());

        AssetGroup createdAssetGroupEntity = assetGroupService.createAssetGroup(assetGroupEntity, user);
        Assert.assertNotNull(createdAssetGroupEntity.getId());
        return createdAssetGroupEntity;
    }

    private AssetGroup createAssetGroupEntity(String user) {
        AssetGroup ag = new AssetGroup();
        ag.setUpdatedBy("test");
        ag.setUpdateTime(OffsetDateTime.now(Clock.systemUTC()));
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
