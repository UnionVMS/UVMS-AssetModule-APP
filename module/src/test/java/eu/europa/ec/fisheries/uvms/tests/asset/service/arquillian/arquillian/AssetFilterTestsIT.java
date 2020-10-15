package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetFilterDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class AssetFilterTestsIT extends TransactionalTests {

    @Inject
    private AssetFilterDao assetFilterDao;

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterAll() {

        List<UUID> createdList = new ArrayList<>();
        List<UUID> fetchedList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            AssetFilter createdAssetFilterEntity = createAndStoreAssetFilterEntity("TEST");
            createdList.add(createdAssetFilterEntity.getId());
        }
        List<AssetFilter> rs = assetFilterDao.getAssetFilterAll();
        for (AssetFilter e : rs) {
            fetchedList.add(e.getId());
        }

        // the list from db MUST contain our created Id:s
        boolean ok = true;
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
    	AssetFilter createdAssetGroupEntity1 = createAndStoreAssetFilterEntity("TEST");
        assertNotNull(createdAssetGroupEntity1);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByUser() {
        String user1 = UUID.randomUUID().toString();
        String user2 = UUID.randomUUID().toString();
        String user3 = UUID.randomUUID().toString();

        for (int i = 0; i < 3; i++) {
            createAndStoreAssetFilterEntity(user1);
        }
        for (int i = 0; i < 8; i++) {
            createAndStoreAssetFilterEntity(user2);
        }
        for (int i = 0; i < 11; i++) {
            createAndStoreAssetFilterEntity(user3);
        }

        List<AssetFilter> listUser1 = assetFilterDao.getAssetFilterByUser(user1);
        List<AssetFilter> listUser2 = assetFilterDao.getAssetFilterByUser(user2);
        List<AssetFilter> listUser3 = assetFilterDao.getAssetFilterByUser(user3);

        assertEquals(3, listUser1.size());
        assertEquals(8, listUser2.size());
        assertEquals(11, listUser3.size());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByGuid() {

    	AssetFilter createdAssetFilterEntity = createAndStoreAssetFilterEntity("TEST");
        UUID guid = createdAssetFilterEntity.getId();
        assertNotNull(guid);

        AssetFilter fetchedAssetFilterEntity = assetFilterDao.getAssetFilterByGuid(guid);
        assertEquals(guid, fetchedAssetFilterEntity.getId());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByGUIDS() {

        List<UUID> createdList = new ArrayList<>();
        List<UUID> fetchedList = new ArrayList<>();
        List<AssetFilter> fetchedEntityList;
        for (int i = 0; i < 5; i++) {
        	AssetFilter createdAssetGroupEntity = createAndStoreAssetFilterEntity("TEST");
            createdList.add(createdAssetGroupEntity.getId());
        }

        fetchedEntityList = assetFilterDao.getAssetFiltersByValueGuidList(createdList);
        for (AssetFilter e : fetchedEntityList) {
            fetchedList.add(e.getId());
        }

        // the list from db MUST contain our created GUIDS:s
        boolean ok = true;
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
    public void deleteAssetFilter() {
    	AssetFilter assetFilterEntity = createAndStoreAssetFilterEntity("TEST");
        UUID uuid = assetFilterEntity.getId();
        assetFilterDao.deleteAssetFilter(assetFilterEntity);

        AssetFilter fetchedFilter = assetFilterDao.getAssetFilterByGuid(uuid);
        Assert.assertNull(fetchedFilter);
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetGroup() throws Exception {
    	AssetFilter assetFilterEntity = createAndStoreAssetFilterEntity("TEST");
        UUID uuid = assetFilterEntity.getId();

        assetFilterEntity.setOwner("NEW OWNER");
        assetFilterDao.updateAssetFilter(assetFilterEntity);
        commit();
        em.flush();

        AssetFilter fetchedFilter = assetFilterDao.getAssetFilterByGuid(uuid);
        Assert.assertTrue(fetchedFilter.getOwner().equalsIgnoreCase("NEW OWNER"));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetFilterAndValues() {
    	AssetFilter assetFilter = createAndStoreAssetFilterEntity("TEST");
        UUID uuid = assetFilter.getId();

        assetFilter.setOwner("NEW OWNER");
       // List<AssetFilterValue> newLines = createAssetFilterValue( assetFilter,  assetFilter.getUpdateTime(), assetFilter.getOwner(), 17);
        List<AssetFilterQuery> filterQueryss = createAssetFilterQuery(assetFilter, 17);
        
        assetFilterDao.updateAssetFilter(assetFilter);
        em.flush();

        AssetFilter fetchedFilter = assetFilterDao.getAssetFilterByGuid(uuid);
        Assert.assertTrue(fetchedFilter.getOwner().equalsIgnoreCase("NEW OWNER"));
    }

    private AssetFilter createAndStoreAssetFilterEntity(String user) {

    	AssetFilter assetFilterEntity = createAssetFilterEntity(user);
    	AssetFilter createAssetFilterEntity = assetFilterDao.createAssetFilter(assetFilterEntity);
        return createAssetFilterEntity;
    }

    private AssetFilter createAssetFilterEntity(String user) {
    	AssetFilter af = new AssetFilter();
        Instant date = Instant.now();
        
        af.setUpdatedBy("test");
        af.setUpdateTime(date);
        af.setName("The Name");
        af.setOwner(user);

        return af;
    }

    private  List<AssetFilterQuery> createAssetFilterQuery(AssetFilter assetFilterEntity, int n) {
        List<AssetFilterQuery> assetFilterQueryList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            AssetFilterQuery field = AssetTestsHelper.createAssetFilterQuery(assetFilterEntity);
            assetFilterQueryList.add(field);
        }
        return assetFilterQueryList;
    }
    
    private void commit() throws Exception {
        userTransaction.commit();
        userTransaction.begin();
    }
}
