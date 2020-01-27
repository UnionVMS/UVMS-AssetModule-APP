package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetGroupFieldDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetGroupFieldTestsIT extends TransactionalTests {

    @Inject
    private AssetGroupDao assetGroupDao;

    @Inject
    private AssetGroupFieldDao assetGroupFieldDaoBean;

    @Test
    @OperateOnDeployment("normal")
    public void create() {

        String user = "test";
        AssetGroup assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        UUID createdId = createdAssetGroupField.getId();
        AssetGroupField fetchedAssetGroupField =  getField(createdId);
        assertNotNull(fetchedAssetGroupField);
    }

    @Test
    @OperateOnDeployment("normal")
    public void get() {
        String user = "test";
        AssetGroup assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        UUID createdId = createdAssetGroupField.getId();
        AssetGroupField fetchedAssetGroupField =  getField(createdId);
        assertEquals(createdId, fetchedAssetGroupField.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void delete() {
        String user = "test";
        AssetGroup assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        UUID createdId = createdAssetGroupField.getId();
        assetGroupFieldDaoBean.delete(createdAssetGroupField);
        AssetGroupField fetchedAssetGroupField =  getField(createdId);
        assertNull(fetchedAssetGroupField);
    }

    @Test
    @OperateOnDeployment("normal")
    public void update() {
        String user = "test";
        AssetGroup assetGroup = createAndStoreAssetGroupEntity(user);
        AssetGroupField createdAssetGroupField = createAndStoreAssetGroupFieldEntity(assetGroup);
        UUID createdId = createdAssetGroupField.getId();
        String createdUpdatedBy = createdAssetGroupField.getUpdatedBy();
        createdAssetGroupField.setUpdatedBy("PEKKA");
        assetGroupFieldDaoBean.update(createdAssetGroupField);
        AssetGroupField fetchedAssetGroupField =  getField(createdId);

        assertNotNull(fetchedAssetGroupField);
        assertNotEquals(createdUpdatedBy, fetchedAssetGroupField.getUpdatedBy());
    }

    @Test
    @OperateOnDeployment("normal")
    public void removeFieldsForGroup() {

        String user = "test";
        AssetGroup assetGroup1 = createAndStoreAssetGroupEntity(user);
        AssetGroup assetGroup2 = createAndStoreAssetGroupEntity(user);
        List<AssetGroupField> createdAssetGroupFields1 = createAndStoreAssetGroupFieldEntityList(assetGroup1, 50);
        List<AssetGroupField> createdAssetGroupFields2 = createAndStoreAssetGroupFieldEntityList(assetGroup2, 25 );

        assetGroupFieldDaoBean.removeFieldsForGroup(assetGroup1);

        List<AssetGroupField> retrievedAssetGroupFields1 =  assetGroupFieldDaoBean.retrieveFieldsForGroup(assetGroup1);
        List<AssetGroupField> retrievedAssetGroupFields2 =  assetGroupFieldDaoBean.retrieveFieldsForGroup(assetGroup2);

        assertEquals(retrievedAssetGroupFields1.size(), 0);
        assertEquals(retrievedAssetGroupFields2.size(), 25);
    }

    private List<AssetGroupField>  createAndStoreAssetGroupFieldEntityList(AssetGroup assetGroup, int n) {
        Instant dt = Instant.now();
        List<AssetGroupField> groupFields = createAssetGroupFields(assetGroup,dt,assetGroup.getOwner(), n);
        return groupFields;
    }

    private AssetGroupField getField(UUID id) {
        AssetGroupField assetGroupField =  assetGroupFieldDaoBean.get(id);
        return assetGroupField;
    }

    private AssetGroupField createAndStoreAssetGroupFieldEntity(AssetGroup assetGroup) {
        Instant dt = Instant.now();
        List<AssetGroupField> groupFields = createAssetGroupFields(assetGroup,dt,assetGroup.getOwner(), 1);
        AssetGroupField assetGroupField = groupFields.get(0);
        AssetGroupField createdAssetGroupField = assetGroupFieldDaoBean.create(assetGroupField);
        return createdAssetGroupField;
    }

    private AssetGroup createAndStoreAssetGroupEntity(String user) {
        AssetGroup assetGroupEntity = createAssetGroupEntity(user);
        AssetGroup createdAssetGroupEntity = assetGroupDao.createAssetGroup(assetGroupEntity);
        return createdAssetGroupEntity;
    }

    private AssetGroup createAssetGroupEntity(String user) {
        AssetGroup ag = new AssetGroup();

        Instant dt = Instant.now();

        ag.setUpdatedBy("test");
        ag.setUpdateTime(dt);
        ag.setArchived(false);
        ag.setName("The Name");
        ag.setOwner(user);
        ag.setDynamic(false);
        ag.setGlobal(true);
        return ag;
    }

    private  List<AssetGroupField> createAssetGroupFields(AssetGroup assetGroupEntity, Instant dt, String user, int n) {
        List<AssetGroupField> groupFields = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String uuid = UUID.randomUUID().toString();
            AssetGroupField field = AssetTestsHelper.createAssetGroupField(assetGroupEntity, "GUID", uuid, dt, user);
            assetGroupFieldDaoBean.create(field);
            groupFields.add(field);
        }
        return groupFields;
    }
}
