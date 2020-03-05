package eu.europa.ec.fisheries.uvms.rest.asset.service;

import java.io.Reader;
import java.util.Set;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.adapter.JsonbAdapter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

public class AssetFilterRestResponseAdapter implements JsonbAdapter<AssetFilter, JsonObject> {
		 
    @Override
    public JsonObject adaptToJson(AssetFilter assetFilter) throws Exception {
    	
    	Set<AssetFilterQuery>  assetFilterQuerySet = assetFilter.getQueries();
    	JsonArrayBuilder jsonArraylistOfQueries = Json.createArrayBuilder();
    	  	
    	for(AssetFilterQuery assetFilterQuery : assetFilterQuerySet){ 
        	JsonArrayBuilder jsonValueArray = Json.createArrayBuilder();
    		Set<AssetFilterValue> assetFilterValues = assetFilterQuery.getValues();
    		
    		for(AssetFilterValue assetFilterValue : assetFilterValues) {
    			if( assetFilterQuery.getIsNumber() == false) {
    				jsonValueArray
    					.add(assetFilterValue.getValue());
        		}
    			else {
    				JsonObject jsonValueObject = Json.createObjectBuilder()
        				.add("operator", assetFilterValue.getOperator())
        				.add("value", assetFilterValue.getValue())
        				.build();
    				jsonValueArray
    					.add(jsonValueObject);
        		}
    		}
    		JsonObject jsonQueryBuilder =  Json.createObjectBuilder()
    			.add("inverse", assetFilterQuery.getInverse())
    			.add("isNumber", assetFilterQuery.getIsNumber())
    			.add("type", assetFilterQuery.getType())
    			.add("values",jsonValueArray.build())
    			.build();
    		
    		jsonArraylistOfQueries
    			.add(jsonQueryBuilder);
    	}
        return Json.createObjectBuilder()
	        		.add("assetFilterId", assetFilter.getId().toString())
	        		.add(assetFilter.getName(), jsonArraylistOfQueries.build())
	        		.build();
    }
    
    

	@Override
	public AssetFilter adaptFromJson(JsonObject adapted) throws Exception {
		AssetFilter assetFilter = new AssetFilter();
		assetFilter.setName(adapted.getString("name"));
		assetFilter.setId(UUID.fromString(adapted.getString("id")));
//		for(JsonValue array : adapted.asJsonObject().asJsonArray()) {
//			System.out.println("JsonObject: " + array.getValueType());
//		}
		AssetFilterQuery assetFilterQuery = JsonbBuilder.create().fromJson((Reader) adapted.getJsonObject(adapted.getString("name")), AssetFilterQuery.class);
		System.out.println("assetFilterQuery inverse: " + assetFilterQuery.getInverse());
	//	AssetFilterQuery assetFilterQuery = new AssetFilterQuery();
		assetFilterQuery.setAssetFilter(assetFilter);
		// assetFilterQuery.setInverse(adapted.);
		return assetFilter;
	}
}
