package eu.europa.ec.fisheries.uvms.rest.asset.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;


@RunWith(Arquillian.class)
@RunAsClient
public class AssetFilterRestResourceTest extends AbstractAssetRestTest{
	
	private String testName;
	private AssetFilter assetFilter;
	private AssetFilterQuery assetFilterQuery;
	private AssetFilterValue assetFilterValue;
	
	@Before
	public void init() {
		this.testName = "Test name";
		this.assetFilter = AssetHelper.createBasicAssetFilter(this.testName);
		this.assetFilter = createAssetFilter(this.assetFilter);
		this.assetFilterQuery = createAssetFilterQuery(assetFilter);
		Set<AssetFilterQuery> queries = new HashSet<AssetFilterQuery>();
		queries.add(assetFilterQuery);
		assetFilter.setQueries(queries);
		this.assetFilterValue = createAssetFilterValue(assetFilterQuery);
		Set<AssetFilterValue> values = new HashSet<AssetFilterValue>();
		values.add(assetFilterValue);
	}
	@After
	public void afterTest() {
		deleteAssetFilter(this.assetFilter);
		deleteAssetFilterQuery(assetFilterQuery);
		deleteAssetFilterValue(assetFilterValue);
	}
	
	
	@Test
    @OperateOnDeployment("normal")
    public void createAssetFilterTest() {
		
		AssetFilter testAssetFilter = new AssetFilter();
		testAssetFilter.setName("Mr Wirde");
		
		testAssetFilter = createAssetFilter(testAssetFilter);
		
        assertEquals(testAssetFilter.getName(),"Mr Wirde");
        assertNotNull(testAssetFilter.getId());
        
        Response deleteresp = getWebTargetExternal()
	        .path("filter")
	        .path(testAssetFilter.getId().toString())
	        .request(MediaType.APPLICATION_JSON)
	        .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	        .delete();
        
        assertThat(deleteresp.getStatus(), is(Status.OK.getStatusCode()));
    }
	
	@Test
    @OperateOnDeployment("normal")
    public void getAssetFilterTest() throws InterruptedException {
	   
		JsonbConfig config = new JsonbConfig().withAdapters(new AssetFilterRestResponseAdapter());
		Jsonb jsonb = JsonbBuilder.create(config);
    	
    	String fetchedAssetFilter = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

    	AssetFilter fetchedAssetFilterJsonAdapter = jsonb.fromJson(fetchedAssetFilter, AssetFilter.class);
        assertEquals(fetchedAssetFilterJsonAdapter.getName(), testName);
        assertNotNull(fetchedAssetFilter);
        assertThat(fetchedAssetFilterJsonAdapter.getId(), is(assetFilter.getId()));
        assertThat(fetchedAssetFilterJsonAdapter.getName(), is(assetFilter.getName()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterListByUserNoUserParamTest() {
        Response response = getWebTargetExternal()
                .path("filter")
                .path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get();
        assertThat(response.getStatus(), is(Status.OK.getStatusCode()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterListByUserTest() {
        
        Response response = getWebTargetExternal()
                .path("filter")
                .path("list")
                .queryParam("user", assetFilter.getOwner())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(Response.class);
        
        assertNotNull(response);
        assertThat(response.getStatus(), is(Status.OK.getStatusCode()));
        assertTrue(response.getEntity().toString().length() > 1);
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void createAssetFilterQueryTest() {
       
        AssetFilterQuery assetQuery = new AssetFilterQuery();
        assetQuery.setType("GUID");
        assetQuery.setIsNumber(false);
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
		
		JsonbConfig config = new JsonbConfig().withAdapters(new AssetFilterRestResponseAdapter());
		Jsonb jsonb = JsonbBuilder.create(config);
		
		AssetFilter fetchedAssetFilter = jsonb.fromJson(fetchedAssetFilterJsonString, AssetFilter.class);
		
		assertEquals("Test name",  fetchedAssetFilter.getName());
		assertEquals(assetFilter.getId(),  fetchedAssetFilter.getId());
	 }
	
	@Test
    @OperateOnDeployment("normal")
    public void updateAssetFilterFromJson() {
		
		String afId = assetFilter.getId().toString();
		String afjson = "{\"id\":\""+afId+"\", \"fromJsonName\": [{\"values\":[{\"value\":23, \"operator\":\"this is a operator\"}],\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";
		
		getWebTargetExternal()
	            .path("filter")
	            .path("updateAssetFilter")
	            .request(MediaType.APPLICATION_JSON)
	            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	            .put(Entity.json(afjson), String.class);
		 
		String assetFilterResp = getWebTargetExternal()
					.path("filter")
					.path(afId)
		    		.request(MediaType.APPLICATION_JSON)
		    		.header(HttpHeaders.AUTHORIZATION, getTokenExternal())
		    		.get(String.class);
		 
		 Jsonb jsonb = new JsonBConfigurator().getContext(null);
		 JsonbConfig config = new JsonbConfig().withAdapters(new AssetFilterRestResponseAdapter());
		 jsonb = JsonbBuilder.create(config);
		 assetFilter = jsonb.fromJson(assetFilterResp, AssetFilter.class);
		 assertNotNull(assetFilter.getId());
		 assertEquals("fromJsonName", assetFilter.getName());
		 assertTrue(assetFilterResp.contains(afId));
	 }
	
	
	 private AssetFilter createAssetFilter(AssetFilter assetFilter) {
		 return getWebTargetExternal()
		            .path("filter")
		            .request(MediaType.APPLICATION_JSON)
		            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
		            .post(Entity.json(assetFilter), AssetFilter.class);
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
	
	 private void deleteAssetFilterQuery(AssetFilterQuery assetFilterQueryToDelete) {
		 getWebTargetExternal()
            .path("filter")
            .path(assetFilterQueryToDelete.getId().toString())
            .path("query")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .delete();
	 }
	 
	 private void deleteAssetFilterValue(AssetFilterValue assetFilterValueToDelete) { 
		 getWebTargetExternal()
            .path("filter")
            .path(assetFilterValueToDelete.getId().toString())
            .path("value")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .delete();
	 }
}
