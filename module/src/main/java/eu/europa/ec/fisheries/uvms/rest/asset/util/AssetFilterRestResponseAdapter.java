package eu.europa.ec.fisheries.uvms.rest.asset.util;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.json.*;
import javax.json.bind.adapter.JsonbAdapter;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetFilterValueType;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;

public class AssetFilterRestResponseAdapter implements JsonbAdapter<AssetFilter, JsonObject> {

    @Override
    public JsonObject adaptToJson(AssetFilter assetFilter) throws Exception {

    	Set<AssetFilterQuery>  assetFilterQuerySet = assetFilter.getQueries();
    	JsonArrayBuilder jsonArrayListOfQueries = Json.createArrayBuilder();

    	for(AssetFilterQuery assetFilterQuery : assetFilterQuerySet) {
        	JsonArrayBuilder jsonValueArray = Json.createArrayBuilder();
    		Set<AssetFilterValue> assetFilterValues = assetFilterQuery.getValues();

    		for(AssetFilterValue assetFilterValue : assetFilterValues) {
    			if(assetFilterQuery.getValueType().equals(AssetFilterValueType.STRING)){
					JsonReader jsonReader = Json.createReader(new StringReader(assetFilterValue.getValueString()));
					JsonValue object = jsonReader.readValue();
					jsonValueArray.add(object);
					jsonReader.close();
				} else if (assetFilterQuery.getValueType().equals(AssetFilterValueType.BOOLEAN)){
    				Boolean b = Boolean.valueOf(assetFilterValue.getValueString());
					jsonValueArray.add(b);
				} else {
					JsonObject jsonValueObject = Json.createObjectBuilder()
							.add("operator", assetFilterValue.getOperator())
							.add("value", assetFilterValue.getValueNumber())
							.build();

					jsonValueArray.add(jsonValueObject);
				}
    		}
    		JsonObject jsonQueryBuilder =  Json.createObjectBuilder()
    			.add("inverse", assetFilterQuery.getInverse())
    			.add("valueType", assetFilterQuery.getValueType().name())
    			.add("type", assetFilterQuery.getType())
    			.add("values", jsonValueArray.build())
    			.build();

    		jsonArrayListOfQueries.add(jsonQueryBuilder);
    	}
        return Json.createObjectBuilder()
	        		.add("id", assetFilter.getId().toString())
	        		.add("name", assetFilter.getName())
	        		.add("filter", jsonArrayListOfQueries.build())
	        		.build();
    }

	@Override
	public AssetFilter adaptFromJson(JsonObject adapted) throws Exception {
		AssetFilter assetFilter = new AssetFilter();

		for (String keyStr : adapted.keySet()) {
			if(keyStr.equalsIgnoreCase("id") ) {
	        	assetFilter.setId(UUID.fromString(adapted.getString("id")));
	        }
	    }
		assetFilter.setName(adapted.getString("name"));

		JsonArray queryJsonArray = adapted.getJsonArray("filter");
		Set<AssetFilterQuery> queriesFromJson = new HashSet<AssetFilterQuery>();

		for(JsonValue jsonQuery : queryJsonArray) {

			JsonObject jsonQueryObject = jsonQuery.asJsonObject();
			Set<AssetFilterValue> valuesFromJson = new HashSet<AssetFilterValue>();
			AssetFilterValueType valueType = AssetFilterValueType.valueOf(jsonQueryObject.getString("valueType").toUpperCase());

			AssetFilterQuery assetFilterQuery = new AssetFilterQuery();
			assetFilterQuery.setAssetFilter(assetFilter);
			assetFilterQuery.setInverse(jsonQueryObject.getBoolean("inverse"));
			assetFilterQuery.setValueType(valueType);
			assetFilterQuery.setType(jsonQueryObject.getString("type"));

			JsonArray valuesJsonArray = jsonQueryObject.getJsonArray("values");

			for(JsonValue jsonValue : valuesJsonArray) {

				AssetFilterValue assetFilterValue = new AssetFilterValue();
				if(valueType.equals(AssetFilterValueType.STRING)) {
					assetFilterValue.setValueString(jsonValue.toString());
				} else if(valueType.equals(AssetFilterValueType.BOOLEAN)) {
					assetFilterValue.setValueString(jsonValue.getValueType().toString());
				} else {
					JsonObject jsonValueObject = jsonValue.asJsonObject();
					assetFilterValue.setOperator(jsonValueObject.getString("operator"));
				    if(jsonValueObject.get("value").getClass().getTypeName().contains("Number")) {
				    	assetFilterValue.setValueNumber(jsonValueObject.getJsonNumber("value").doubleValue());
				    }else{
				    	assetFilterValue.setValueString(jsonValueObject.getString("value"));
				    }
				}
				assetFilterValue.setAssetFilterQuery(assetFilterQuery);
				valuesFromJson.add(assetFilterValue);
			}
			assetFilterQuery.setValues(valuesFromJson);
			queriesFromJson.add(assetFilterQuery);
		}
		assetFilter.setQueries(queriesFromJson);
		return assetFilter;
	}
}
