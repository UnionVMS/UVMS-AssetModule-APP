package eu.europa.ec.fisheries.uvms.rest.asset.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
import eu.europa.ec.fisheries.uvms.rest.asset.filter.AppError;


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

        // You really could argue that this should be a bad request but the server was returning 400 for everything,
        // if there is only one thing returned for every error it is better if it is a 500
       // Integer code  = response.readEntity(JsonNode.class).path("code").intValue();
        Integer code  = response.readEntity(AppError.class).code;
        assertThat(code, is(Status.INTERNAL_SERVER_ERROR.getStatusCode()));
        assertThat(response.getStatus(), is(Status.OK.getStatusCode()));
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
        
       String assetF = getWebTargetExternal()
                .path("filter")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .post(Entity.json(assetFilter), String.class);
       
//        AssetFilterQuery assetQuery = new AssetFilterQuery();
//        assetQuery.setType("GUID");
//        assetQuery.setNumber(false);
//       // assetQuery  
//        String r = getWebTargetExternal()
//                .path("filter")
//                .path(assetFilter.getId().toString())
//                .path("query")
//                .request(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
//                .post(Entity.json(assetQuery),String.class);
//               // .readEntity(AssetFilterQuery.class);
//
//        
//        System.out.println(r);
//        assertNotNull(assetQuery.getId());;
    }
 
	
	@Test
    @OperateOnDeployment("normal")
    public void getAssetFilterByIdTest() {
		
		AssetFilter assetFilter = AssetHelper.createBasicAssetFilter("testName");
//		assetFilter = getWebTargetExternal()
//                .path("filter")
//                .request(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
//                .post(Entity.json(assetFilter), AssetFilter.class);
		
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

		// System.out.println("assetFilterValue: " + assetFilterValue.getId().toString() );
    	System.out.println("assetFilter: " + assetFilter.getId().toString() );
    	
        String fetchedAssetFilter = getWebTargetExternal()
                .path("filter")
                .path(assetFilter.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
                .get(String.class);

        System.out.println("fetchedAssetFilter: " + fetchedAssetFilter );
	 }
	
	 private AssetFilter createAssetFilter(AssetFilter assetFilter) {
		 assetFilter = getWebTargetExternal()
            .path("filter")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
            .post(Entity.json(assetFilter), AssetFilter.class);
		 return assetFilter;
	}
	 
	 
    private String createAssetFilterReturnAssetFilterId() {
    	AssetFilter assetFilter = AssetHelper.createBasicAssetFilter(testName);
        assetFilter = getWebTargetExternal()
		    .path("/filter")
		    .request(MediaType.APPLICATION_JSON)
		    .header(HttpHeaders.AUTHORIZATION, getTokenExternal())
		    .post(Entity.json(assetFilter), AssetFilter.class);
		return assetFilter.getId().toString();
    }
}
