package eu.europa.ec.fisheries.uvms.tests.asset.service.arquillian.arquillian;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetFilterValueType;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.asset.bean.AssetFilterServiceBean;
import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;

@RunWith(Arquillian.class)
public class AssetFilterServiceBeanTest extends TransactionalTests{

    @EJB
    private AssetServiceBean assetService;

    @EJB
    private AssetFilterServiceBean assetFilterService;

    private AssetFilter assetFilter;
    private AssetFilterQuery assetFilterQuery;
    private AssetFilterValue assetFilterValue;
    
    @Before
    public void initAssets() {
    	assetFilter = createAssetFilterEntity("Test Testblom");
    	assetFilterQuery = createAssetFilterQueryEntity(assetFilter);
    	assetFilterValue = createAssetFilterValueEntity(assetFilterQuery.getId(),"test value");
    }
    
    private AssetFilter createAssetFilterEntity(String user) {
    	AssetFilter af = new AssetFilter();
    	af.setUpdatedBy("test");
    	af.setUpdateTime(Instant.now());
    	af.setName("The Name");
    	af.setOwner(user);
        return assetFilterService.createAssetFilter(af, user);
    }
    private AssetFilterQuery createAssetFilterQueryEntity(AssetFilter af) {
    	AssetFilterQuery assetFilterQuery = new AssetFilterQuery();
        assetFilterQuery.setAssetFilter(af);
        assetFilterQuery.setType("GUID");
        assetFilterQuery.setValueType(AssetFilterValueType.NUMBER);
        assetFilterQuery.setInverse(false);
        return assetFilterService.createAssetFilterQuery(af.getId(), assetFilterQuery);
    }
	private AssetFilterValue createAssetFilterValueEntity(UUID parentAssetFilterQueryId, String testValue) {
	    AssetFilterValue afv = new AssetFilterValue();
	    afv.setValueString(testValue);
	    return assetFilterService.createAssetFilterValue(parentAssetFilterQueryId, afv);
	}

	@Test
    @OperateOnDeployment("normal")
    public void deleteAssetFilterById() {
		AssetFilter createdAssetFilterEntity = createAssetFilterEntity("jkldsfajfd");
        UUID guid = createdAssetFilterEntity.getId();
        assetFilterService.deleteAssetFilterById(createdAssetFilterEntity.getId(), createdAssetFilterEntity.getOwner());
        AssetFilter fetchedAssetFilterEntity = assetFilterService.getAssetFilterById(guid);
        assertNull(fetchedAssetFilterEntity);
        // assertNotNull(fetchedAssetFilterEntity);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterById()  {
    	AssetFilter createdAssetFilterEntity = assetFilter;
        UUID guid = createdAssetFilterEntity.getId();
        AssetFilter fetchedAssetFilterEntity = assetFilterService.getAssetFilterById(guid);
        assertEquals(fetchedAssetFilterEntity.getId(), guid);
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByAssetId()  {
        UUID assetId = UUID.randomUUID();

        AssetFilter createdAssetFilterEntity = assetFilter;

        AssetFilterValue assetFilterValue = createAssetFilterValueEntity(assetFilterQuery.getId(), assetId.toString());

        List<AssetFilter> fetchedAssetFilterEntity = assetFilterService.getAssetFilterListByAssetId(assetId);

        assertTrue(fetchedAssetFilterEntity.stream().anyMatch(filter -> filter.getId().equals(createdAssetFilterEntity.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByAssetId_TwoAssetFilters()  {    //Since you create one in the init
        UUID assetId = UUID.randomUUID();

        AssetFilter createdAssetFilter = createAssetFilterEntity("Filter Tester");
        AssetFilterQuery createdAssetFilterQuery = createAssetFilterQueryEntity(createdAssetFilter);
        AssetFilterValue assetFilterValue = createAssetFilterValueEntity(createdAssetFilterQuery.getId(), assetId.toString());

        List<AssetFilter> fetchedAssetFilterEntity = assetFilterService.getAssetFilterListByAssetId(assetId);

        assertTrue(fetchedAssetFilterEntity.stream().anyMatch(filter -> filter.getId().equals(createdAssetFilter.getId())));
        assertFalse(fetchedAssetFilterEntity.stream().anyMatch(filter -> filter.getId().equals(assetFilter.getId())));
    }

    @Test
    @OperateOnDeployment("normal")
    public void updateAssetFilter() {
    	AssetFilter createdAssetFilterEntity = assetFilter;
        UUID guid = createdAssetFilterEntity.getId();
        String oldUserName = createdAssetFilterEntity.getOwner();
        String newUserName = "UPDATED_SERVICE_TEST";
        createdAssetFilterEntity.setOwner(newUserName);

        AssetFilter updatedAssetFilterEntity = assetFilterService.updateAssetFilter(createdAssetFilterEntity, newUserName);
        AssetFilter fetchedAssetFilterEntity = assetFilterService.getAssetFilterById(updatedAssetFilterEntity.getId());
        assertFalse(fetchedAssetFilterEntity.getOwner().equalsIgnoreCase(oldUserName));
        assertEquals(guid, updatedAssetFilterEntity.getId());
    }


    @Test
    @OperateOnDeployment("normal")
    public void getAssetFieldList() {
        String user1 = UUID.randomUUID().toString();
        String user2 = UUID.randomUUID().toString();
        String user3 = UUID.randomUUID().toString();

        for (int i = 0; i < 3; i++) {
        	createAssetFilterEntity(user1);
        }
        for (int i = 0; i < 8; i++) {
        	createAssetFilterEntity(user2);
        }
        for (int i = 0; i < 11; i++) {
        	createAssetFilterEntity(user3);
        }

        List<AssetFilter> listUser1 = assetFilterService.getAssetFilterList(user1);
        List<AssetFilter> listUser2 = assetFilterService.getAssetFilterList(user2);
        List<AssetFilter> listUser3 = assetFilterService.getAssetFilterList(user3);

        assertEquals(3, listUser1.size());
        assertEquals(8, listUser2.size());
        assertEquals(11, listUser3.size());
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void updateAssetFilterValue() {
    	AssetFilterValue createdAssetFilterValue = assetFilterValue;
    	AssetFilterValue fetchedAssetFilterValue = assetFilterService.getAssetFilterValue(createdAssetFilterValue.getId());
    	fetchedAssetFilterValue.setValueString("CHANGEDVALUE");
        assetFilterService.updateAssetFilterValue(fetchedAssetFilterValue, "TEST");
        AssetFilterValue fetchedAssetFilterValue2 = assetFilterService.getAssetFilterValue(createdAssetFilterValue.getId());
        assertEquals("CHANGEDVALUE", fetchedAssetFilterValue2.getValueString());
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterValue() {
    	AssetFilterValue fetchedAssetFilterValue = assetFilterService.getAssetFilterValue(assetFilterValue.getId());
        assertNotNull(fetchedAssetFilterValue);
    }

}
