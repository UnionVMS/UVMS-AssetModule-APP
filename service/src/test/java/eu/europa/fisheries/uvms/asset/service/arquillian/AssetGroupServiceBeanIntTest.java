package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.types.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;


@RunWith(Arquillian.class)
public class AssetGroupServiceBeanIntTest extends TransactionalTests {


    Random rnd = new Random();


    @EJB
    AssetGroupService assetGroupService;



    @Test
    @OperateOnDeployment("normal")
    public void createAssertGroup() throws AssetException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        Assert.assertTrue(createdAssetGroupEntity != null);
    }

    @Test
    @OperateOnDeployment("normal")
    public void createAssertGroupAndFields() throws AssetException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        Assert.assertTrue(createdAssetGroupEntity != null);

    }





    @Test
    @OperateOnDeployment("normal")
    public void deleteAssetGroupById() throws AssetException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();

        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), createdAssetGroupEntity.getOwner());

        try {
            AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
            Assert.assertTrue(fetchedAssetGroupEntity != null);
        }catch (AssetException s)
        {
            Assert.assertTrue(false);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupById() throws AssetException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();

        assetGroupService.deleteAssetGroupById(createdAssetGroupEntity.getId(), createdAssetGroupEntity.getOwner());

        try {
            AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
            Assert.assertTrue(fetchedAssetGroupEntity.getId().equals(guid));
        }catch (AssetException s)
        {
            Assert.assertTrue(false);
        }
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroup() throws AssetException {

        AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("SERVICE_TEST");
        UUID guid = createdAssetGroupEntity.getId();
        String oldUserName = createdAssetGroupEntity.getOwner();
        String newUserName = "UPDATED_SERVICE_TEST";
        createdAssetGroupEntity.setOwner(newUserName);

        try {
            AssetGroup updatedAssetGroupEntity =  assetGroupService.updateAssetGroup(createdAssetGroupEntity, newUserName);
            AssetGroup fetchedAssetGroupEntity = assetGroupService.getAssetGroupById(guid);
            Assert.assertFalse(fetchedAssetGroupEntity.getOwner().equalsIgnoreCase(oldUserName));
        }catch (AssetException s)
        {
            Assert.assertTrue(false);
        }
    }


    @Test
    @OperateOnDeployment("normal")
    @Ignore
    public void getAssetGroupListByAssetGuid() throws AssetException {

        List<UUID> createdList = new ArrayList<>();
        List<UUID> fetchedList = new ArrayList<>();
        List<AssetGroup> fetchedEntityList ;
        for (int i = 0; i < 5; i++) {
            AssetGroup createdAssetGroupEntity = createAndStoreAssetGroupEntity("TEST");
            createdList.add(createdAssetGroupEntity.getId());
        }

        UUID assetGuid = UUID.randomUUID();

        fetchedEntityList = assetGroupService.getAssetGroupListByAssetGuid(assetGuid);
        for(AssetGroup e : fetchedEntityList){
            fetchedList.add(e.getId());
        }

        // the list from db MUST contain our created GUIDS:s
        Boolean ok = true;
        for (UUID aCreatedGUID : createdList) {
            if(!fetchedList.contains(aCreatedGUID)){
                ok = false;
                break;
            }
        }
        Assert.assertTrue(ok);
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetGroupList() throws AssetException {



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




    private List<AssetGroupField> createAssetGroupLines( int n) throws AssetException {

        List<AssetGroupField> ret = new ArrayList<>();

        for(int i = 0 ; i < n ; i++){
            AssetGroupField field = new AssetGroupField();
            field.setField(ConfigSearchFieldEnum.GUID.value());
            field.setValue(UUID.randomUUID().toString());
            ret.add(field);
        }
        return ret;
    }



    private AssetGroup createAndStoreAssetGroupEntity(String user) throws AssetException {

        AssetGroup assetGroupEntity = createAssetGroupEntity(user);
        Assert.assertTrue(assetGroupEntity.getId() == null);


        List<AssetGroupField> fields =  createAssetGroupLines(5);

        assetGroupEntity.setFields(fields);

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




}
