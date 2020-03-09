package eu.europa.ec.fisheries.uvms.rest.asset.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;
import eu.europa.ec.fisheries.uvms.rest.asset.AbstractAssetRestTest;
import eu.europa.ec.fisheries.uvms.rest.asset.AssetHelper;


@RunWith(Arquillian.class)
@RunAsClient
public class AssetFilterRestResourceTest extends AbstractAssetRestTest{
	
	private String testName = "Test name";
	
	@Test
    @OperateOnDeployment("normal")
    public void createAssetFilterTest() {
		
        AssetFilter assetFilter = AssetHelper.createBasicAssetFilter(testName);
        assetFilter = getWebTargetExternal()
                .path("/filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilter), AssetFilter.class);
        
        assertEquals(assetFilter.getName(), testName);
        assertNotNull(assetFilter.getId());
    }
	
   @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterTest() {

    	AssetFilter assetFilter = AssetHelper.createBasicAssetFilter(testName);
    	AssetFilter createdAssetFilter = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilter), AssetFilter.class);
        
    	AssetFilter fetchedAssetFilter = getWebTargetExternal()
                .path("filter")
                .path(createdAssetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(AssetFilter.class);

        assertEquals(assetFilter.getName(), testName);
        assertNotNull(fetchedAssetFilter);
        assertThat(fetchedAssetFilter.getId(), is(createdAssetFilter.getId()));
        assertThat(fetchedAssetFilter.getName(), is(createdAssetFilter.getName()));
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
//        Integer code  = response.readEntity(AppError.class).code;
//        assertThat(code, is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(response.getStatus(), is(Status.OK.getStatusCode()));
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void getAssetFilterListByUserTest() {
    	AssetFilter assetFilter = AssetHelper.createBasicAssetFilter(testName);
        assetFilter.setName("Mr Wirde");
        assetFilter = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilter), AssetFilter.class);

        System.out.println("owner: " + assetFilter.getOwner() );
        
        String response = getWebTargetExternal()
                .path("filter")
                .path("list")
                .queryParam("user", assetFilter.getOwner())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);
        
       System.out.println("response: " + response );
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void createAssetFilterQueryTest() {
        Asset asset = AssetHelper.createBasicAsset();
        asset = getWebTargetExternal()
                .path("asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(asset), Asset.class);

        AssetFilter assetFilter = AssetHelper.createBasicAssetFilter(testName);
        
        assetFilter = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilter), AssetFilter.class);
       
        AssetFilterQuery assetQuery = new AssetFilterQuery();
        assetQuery.setType("GUID");
        assetQuery.setIsNumber(false);
        
        assetQuery = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetQuery), AssetFilterQuery.class);
        assertNotNull(assetQuery.getId());;
    }
 
	
	@Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByIdTest() {
		
		AssetFilter assetFilter = AssetHelper.createBasicAssetFilter("testName");
		
		assetFilter = createAssetFilter(assetFilter);
		
		AssetFilterQuery assetFilterQuery = AssetHelper.createBasicAssetFilterQuery(assetFilter);
		
		assetFilterQuery = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilterQuery), AssetFilterQuery.class);
		
		AssetFilterValue assetFilterValue = AssetHelper.createBasicAssetFilterValue(assetFilterQuery);
		assetFilterValue.setAssetFilterQuery(assetFilterQuery);
		
		assetFilterValue = getWebTargetExternal()
				.path("filter")
				.path(assetFilterQuery.getId().toString())
	    		.path("value")
	    		.request(MediaType.APPLICATION_JSON)
	    		.header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	    		.post(Entity.json(assetFilterValue), AssetFilterValue.class);
    	
        String fetchedAssetFilter = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        System.out.println("fetchedAssetFilter: " + fetchedAssetFilter );
	 }
	
	@Test
    @OperateOnDeployment("normal")
    public void updateAssetFilterFromJson() {
		
		AssetFilter assetFilter = AssetHelper.createBasicAssetFilter("Mr.updateAssetFilterFromJsonTestNaME");
		assetFilter = createAssetFilter(assetFilter);
		
		AssetFilterQuery assetFilterQuery = AssetHelper.createBasicAssetFilterQuery(assetFilter);
		assetFilterQuery = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilterQuery), AssetFilterQuery.class);
		
		AssetFilterValue assetFilterValue = AssetHelper.createBasicAssetFilterValue(assetFilterQuery);
		assetFilterValue.setAssetFilterQuery(assetFilterQuery);
		
		System.out.println(assetFilterValue);
		
		assetFilterValue = getWebTargetExternal()
				.path("filter")
				.path(assetFilterQuery.getId().toString())
	    		.path("value")
	    		.request(MediaType.APPLICATION_JSON)
	    		.header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	    		.post(Entity.json(assetFilterValue), AssetFilterValue.class);
		
		String afId = assetFilter.getId().toString();
//		String afjson = "{\n" + 
//				"		\"assetFilterId\": [\n" + afId + 
//				"		\"fromJsonName\": [\n" + 
//				"			{\n" + 
//				"				\"type\": \"fromJsonTest\",\n" + 
//				"				\"inverse\": false,\n" + 
//				"				\"isNumber\": true,\n" + 
//				"				\"values\": [\n" + 
//				"					\"TEST\"\n" + 
//				"				]\n" + 
//				"			}\n" + 
//				"		]\n" + 
//				"	}";
		
		String afjson = "{\"assetFilterId\":\""+afId+"\", \"fromJsonName\": [{\"values\":[{\"value\":\"23\", \"operator\":\"this is a operator\"}],\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";
	//	String afjson = "{\"assetFilterId\":\""+afId+"\", \"fromJsonName\": [{\"values\":[\"sdhuyds\"],\"type\": \"dsad\", \"inverse\": false,\"isNumber\": false}] }";
		System.out.println("afjson: "+afjson);
		System.out.println("afId: "+afId);
	//	AssetFilter assetFilterFromJson = new AssetFilter();
		
		 String assetFilterFromJson = getWebTargetExternal()
	            .path("filter")
	          //  .path(afId)
	            .path("updateAssetFilter")
	            .request(MediaType.APPLICATION_JSON)
	            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
	            .put(Entity.json(afjson), String.class);
		
		System.out.println("assetFilter: " + assetFilter.getName() );
	 System.out.println("assetFilterFromJson: " +assetFilterFromJson);
//		System.out.println("assetFilterFromJson: " + assetFilterFromJson.getId() );
		
	 }
	
	
	 private AssetFilter createAssetFilter(AssetFilter assetFilter) {
		 assetFilter = getWebTargetExternal()
            .path("filter")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .post(Entity.json(assetFilter), AssetFilter.class);
		 return assetFilter;
	}
}
