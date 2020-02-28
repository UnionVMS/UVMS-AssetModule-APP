package eu.europa.ec.fisheries.uvms.rest.asset.service;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.adapter.JsonbAdapter;

import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

public class AssetFilterRestResponseAdapter implements JsonbAdapter<AssetFilter, JsonObject> {
		 
    @Override
    public JsonObject adaptToJson(AssetFilter assetFilter) throws Exception {
    	
    	Set<AssetFilterQuery>  assetFilterQuerySet = assetFilter.getQueries();
    	JsonArrayBuilder jsonArraylistOfQueries = Json.createArrayBuilder();
    	JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    	JsonObjectBuilder jsonQueryBuilder =  Json.createObjectBuilder();
    	
    	for (AssetFilterQuery assetFilterQuery : assetFilterQuerySet){ 
        	JsonArrayBuilder jsonValueArray = Json.createArrayBuilder();
        	JsonObjectBuilder jsonValueObject = Json.createObjectBuilder();
    		Set<AssetFilterValue> assetFilterValues = assetFilterQuery.getValues();
    		
    		for(AssetFilterValue assetFilterValue : assetFilterValues) {
    			if( assetFilterQuery.getIsNumber() == true) {
    				jsonValueArray
    						.add(assetFilterValue.getValue());
        		}
    			else {
    				jsonValueObject
        					.add("operator", assetFilterValue.getOperator())
        					.add("value", assetFilterValue.getValue());
        		}
    		}
    		if( assetFilterQuery.getIsNumber() == true) {
    			jsonQueryBuilder
    					.add("values", jsonValueArray.build());
    		}else {
    			jsonQueryBuilder
    					.add("inverse", assetFilterQuery.getInverse())
    					.add("isNumber", assetFilterQuery.getIsNumber())
    					.add("values", jsonValueObject);
    		}
    		jsonArraylistOfQueries.add(jsonQueryBuilder.build());
    	}
        return jsonObjectBuilder
          .add(assetFilter.getName(), jsonArraylistOfQueries.build())
          .build();
    }

	@Override
	public AssetFilter adaptFromJson(JsonObject adapted) throws Exception {
		AssetFilter assetFilter = new AssetFilter();
		assetFilter.setName(adapted.getString("name"));
	        return assetFilter;
	}
	
//	private UUID id;
//    @Column(name = "name")
//    private String name;
//    private Instant updateTime;
//    private String updatedBy;
//    private String owner;
//    @OneToMany(mappedBy="assetFilter", cascade = CascadeType.ALL)
//    @Fetch(FetchMode.SELECT)
//    @Column(name="queries")
//    private Set<AssetFilterQuery> queries;

}
