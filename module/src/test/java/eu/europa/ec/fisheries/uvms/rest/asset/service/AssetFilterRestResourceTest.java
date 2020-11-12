package eu.europa.ec.fisheries.uvms.rest.asset.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetFilterValueType;
import eu.europa.ec.fisheries.uvms.rest.asset.util.AssetFilterListRestResourceAdapter;
import eu.europa.ec.fisheries.uvms.rest.asset.util.AssetFilterRestResponseAdapter;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;

@RunWith(Arquillian.class)
@RunAsClient
public class AssetFilterRestResourceTest extends AbstractAssetRestTest{

	private AssetFilter assetFilter;
	private Jsonb jsonb;
	
   @Before
    public void setup() {
	   JsonbConfig config = new JsonbConfig().withAdapters(new AssetFilterRestResponseAdapter(), new AssetFilterListRestResourceAdapter());
       jsonb = JsonbBuilder.create(config);
	   assetFilter = AssetHelper.createBasicAssetFilter("Test name");
	   assetFilter = createAssetFilter(assetFilter);
	   AssetFilterQuery assetFilterQuery = AssetHelper.createBasicAssetFilterQuery(assetFilter);
	   AssetHelper.createBasicAssetFilterValue(assetFilterQuery);
	   assetFilterQuery = createAssetFilterQuery(assetFilter);
	   createAssetFilterValue(assetFilterQuery);
    }
 
    @After
    public void tearDown() {
    	deleteAssetFilter(assetFilter);
    }
	
	@Test
    @OperateOnDeployment("normal")
    public void createAssetFilterFromJsonTest() {
		
		String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";
		
        String assetFilterCreateResp = getWebTargetExternal()
            .path("filter")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .post(Entity.json(afjson), String.class);
        
		AssetFilter assetFilter2 = jsonb.fromJson(assetFilterCreateResp, AssetFilter.class);
        
		assertNotNull(assetFilter2.getId().toString());
		
        Response deleteresp = getWebTargetExternal()
	        .path("filter")
	        .path(assetFilter2.getId().toString())
	        .request(MediaType.APPLICATION_JSON)
	        .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	        .delete();
        
        assertTrue(deleteresp.getStatus() == Status.OK.getStatusCode() );
    }

	@Test
	@OperateOnDeployment("normal")
	public void createAssetFilterFromJsonMoreComplexQuery() {

		String afjson = "{\"name\":\"VMS båtar\",\"filter\":[{\"type\":\"flagstate\",\"values\":[\"SWE\"],\"inverse\":false,\"valueType\":\"STRING\"},{\"type\":\"vesselType\",\"values\":[\"Fishing\"],\"inverse\":false,\"valueType\":\"STRING\"},{\"type\":\"lengthOverAll\",\"values\":[{\"operator\":\"greater than or equal\",\"value\":12}],\"inverse\":false,\"valueType\":\"NUMBER\"},{\"type\":\"hasLicence\",\"values\":[true],\"inverse\":false,\"valueType\":\"BOOLEAN\"}]}";

		String assetFilterCreateResp = getWebTargetExternal()
				.path("filter")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getTokenExternal())
				.post(Entity.json(afjson), String.class);

		AssetFilter assetFilter2 = jsonb.fromJson(assetFilterCreateResp, AssetFilter.class);

		assertNotNull(assetFilter2.getId().toString());

		Response deleteresp = getWebTargetExternal()
				.path("filter")
				.path(assetFilter2.getId().toString())
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getTokenExternal())
				.delete();

		assertTrue(deleteresp.getStatus() == Status.OK.getStatusCode() );
	}

	
	@Test
    @OperateOnDeployment("normal")
    public void createAssetFilterTest() {
		
		AssetFilter testAssetFilter = new AssetFilter();
		testAssetFilter = createAssetFilter(testAssetFilter);
        assertNotNull(testAssetFilter.getId());
        
        Response deleteresp = getWebTargetExternal()
	        .path("filter")
	        .path(testAssetFilter.getId().toString())
	        .request(MediaType.APPLICATION_JSON)
	        .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	        .delete();
        assertTrue(deleteresp.getStatus() == Status.OK.getStatusCode() );
    }
	
	@Test
    @OperateOnDeployment("normal")
    public void getAssetFilterTest() throws InterruptedException {
	   
    	String fetchedAssetFilter = getWebTargetExternal()
            .path("filter")
            .path(assetFilter.getId().toString())
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .get(String.class);

    	AssetFilter fetchedAssetFilterJsonAdapter = jsonb.fromJson(fetchedAssetFilter, AssetFilter.class);
        assertEquals(fetchedAssetFilterJsonAdapter.getName(), assetFilter.getName());
        assertNotNull(fetchedAssetFilter);
        assertEquals(fetchedAssetFilterJsonAdapter.getId(), assetFilter.getId());
        assertEquals(fetchedAssetFilterJsonAdapter.getName(), assetFilter.getName());
    }  
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterListByUserNoUserParamTest() {
    	
    	Response response = getWebTargetExternal()
            .path("filter")
            .path("list")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .get(Response.class);
    	
    	assertTrue(response.getStatus() == Status.OK.getStatusCode() );
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterListByUserTest() {
        
        Response response = getWebTargetExternal()
            .path("filter")
            .path("listAssetFiltersByUser")
            .queryParam("user", assetFilter.getOwner())
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .get(Response.class);
        
        assertNotNull(response);
        assertTrue(response.getStatus() == Status.OK.getStatusCode());
        assertTrue(response.getEntity().toString().length() > 1);
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void createAssetFilterQueryTest() {
       
        AssetFilterQuery assetQuery = new AssetFilterQuery();
        assetQuery.setType("GUID");
        assetQuery.setValueType(AssetFilterValueType.STRING);
        assetQuery.setAssetFilter(assetFilter);
        
        assetQuery = getWebTargetExternal()
            .path("filter")
            .path(assetFilter.getId().toString())
            .path("query")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .post(Entity.json(assetQuery), AssetFilterQuery.class);
        
        assertNotNull(assetQuery.getId());
        
        getWebTargetExternal()
        .path("filter")
        .path(assetQuery.getId().toString())
        .path("query")
        .request(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
        .delete();
    }
	
	@Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByIdTest() {
    	
		String fetchedAssetFilterJsonString = getWebTargetExternal()
            .path("filter")
            .path(assetFilter.getId().toString())
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .get(String.class);
		
		AssetFilter fetchedAssetFilter = jsonb.fromJson(fetchedAssetFilterJsonString, AssetFilter.class);
		
		assertEquals(assetFilter.getName(),  fetchedAssetFilter.getName());
		assertEquals(assetFilter.getId(),  fetchedAssetFilter.getId());
	 }
	
	@Test
    @OperateOnDeployment("normal")
    public void updateAssetFilterFromJson() {
		
		String afId = assetFilter.getId().toString(); 
		String afjson = "{\"id\":\""+afId+"\",\"name\":\"Nya Båtar och Update Test\", \"filter\": [{\"values\":[{\"value\":23, \"operator\":\"Not an Operator\"}, {\"value\":10100, \"operator\":\"bla bla bla\"}],\"type\": \"lapad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";
		
		String assetFilterResp = getWebTargetExternal()
            .path("filter")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .put(Entity.json(afjson), String.class);	 
		
		AssetFilter assetFilterRespString = getWebTargetExternal()
			.path("filter")
			.path(afId)
    		.request(MediaType.APPLICATION_JSON)
    		.header(HttpHeaders.AUTHORIZATION, getTokenExternal())
    		.get(AssetFilter.class);
		
		 assetFilter = jsonb.fromJson(assetFilterResp, AssetFilter.class);
		 assertNotNull(assetFilter.getId());
		 assertTrue(new ArrayList<AssetFilterQuery>(assetFilter.getQueries()).get(0).getValues().size() == 2);
		 assertTrue(assetFilterResp.contains(afId));
		 assertEquals(assetFilterRespString.getName(), assetFilter.getName());
		 assertEquals(assetFilterRespString.getOwner(), assetFilter.getOwner());
		 
		 String assetFilterList = getWebTargetExternal()
					.path("filter")
					.path("list")
		    		.request(MediaType.APPLICATION_JSON)
		    		.header(HttpHeaders.AUTHORIZATION, getTokenExternal())
		    		.get(String.class);

		 assertTrue(assetFilterList.contains(assetFilter.getId().toString()));
		 assertTrue(assetFilterList.contains("Not an Operator"));
	 }
	
	
	 private AssetFilter createAssetFilter(AssetFilter assetFilterToCreate) {
		 String assetFilterString = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";
		 assetFilterToCreate = jsonb.fromJson(assetFilterString, AssetFilter.class);
		 String assetFilterJson =  getWebTargetExternal()
		            .path("filter")
		            .request(MediaType.APPLICATION_JSON)
		            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
		            .post(Entity.json(assetFilterString), String.class);
		 assetFilterToCreate = jsonb.fromJson(assetFilterJson, AssetFilter.class);
		 return assetFilterToCreate;
	}
	 
	 private AssetFilterQuery createAssetFilterQuery(AssetFilter assetFilterforQuery) {
		 AssetFilterQuery assetFilterQuery = AssetHelper.createBasicAssetFilterQuery(assetFilterforQuery);
			return getWebTargetExternal()
	                .path("filter")
	                .path(assetFilterforQuery.getId().toString())
	                .path("query")
	                .request(MediaType.APPLICATION_JSON)
	                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	                .post(Entity.json(assetFilterQuery), AssetFilterQuery.class);
	 }
	 
	 private AssetFilterValue createAssetFilterValue(AssetFilterQuery assetFilterQueryForValue) {
		 AssetFilterValue assetFilterValue = AssetHelper.createBasicAssetFilterValue(assetFilterQueryForValue);
			return getWebTargetExternal()
	                .path("filter")
	                .path(assetFilterQueryForValue.getId().toString())
	                .path("value")
	                .request(MediaType.APPLICATION_JSON)
	                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	                .post(Entity.json(assetFilterValue), AssetFilterValue.class);
	 }
	 private void deleteAssetFilter(AssetFilter assetFilter) {
		getWebTargetExternal()
            .path("filter")
            .path(assetFilter.getId().toString())
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .delete();
	}
	 
	
}
