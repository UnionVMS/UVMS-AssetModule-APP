package eu.europa.ec.fisheries.uvms.rest.asset.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;
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
        				.add("value", Integer.parseInt(assetFilterValue.getValue()))
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
		assetFilter.setId(UUID.fromString(adapted.getString("assetFilterId")));
		/**
		 *  Här kommer en ful och inte helt stabil lösning för att sätta "name" värdet som key på objectet. 
		 *  Jag kollar om det inte är "assetFilterId", det funkar sålänge det bara är två värden som skickas...
		 */
		String nameValue = "";
		for (String keyStr : adapted.keySet()) {
	        if(!keyStr.equalsIgnoreCase("assetFilterId") ) {
	        	nameValue = keyStr;
	        }
	    }
		assetFilter.setName(nameValue);
		
		JsonArray queryJsonArray = adapted.getJsonArray(nameValue);
		Set<AssetFilterQuery> queriesFromJson = new HashSet<AssetFilterQuery>();
		
		for(JsonValue jsonQuery : queryJsonArray) {
			
			JsonObject jsonQueryObject = jsonQuery.asJsonObject();
			Set<AssetFilterValue> valuesFromJson = new HashSet<AssetFilterValue>();
			boolean isNumberValues = jsonQueryObject.getBoolean("isNumber");
			
			AssetFilterQuery assetFilterQuery = new AssetFilterQuery();
			assetFilterQuery.setAssetFilter(assetFilter);
			assetFilterQuery.setInverse(jsonQueryObject.getBoolean("inverse"));
			assetFilterQuery.setIsNumber(isNumberValues);
			assetFilterQuery.setType(jsonQueryObject.getString("type"));
			
			JsonArray valuesJsonArray = jsonQueryObject.getJsonArray("values");
			
			for(JsonValue jsonValue : valuesJsonArray) {
				
				AssetFilterValue assetFilterValue = new AssetFilterValue();
				if(isNumberValues == false) {
					assetFilterValue.setValue(jsonValue.toString());
				}else {
					JsonObject jsonValueObject = jsonValue.asJsonObject();
					assetFilterValue.setOperator(jsonValueObject.getString("operator"));
				    if(jsonValueObject.get("value").getClass().getTypeName().contains("Number")) {
				    	Integer number = (Integer)jsonValueObject.getInt("value");
				    	assetFilterValue.setValue(number.toString());
				    }else{
				    	assetFilterValue.setValue(jsonValueObject.getString("value"));
				    }
				}
				valuesFromJson.add(assetFilterValue);
			}
			assetFilterQuery.setValues(valuesFromJson);
			queriesFromJson.add(assetFilterQuery);
		}
		assetFilter.setQueries(queriesFromJson);
		return assetFilter;
	}
}
