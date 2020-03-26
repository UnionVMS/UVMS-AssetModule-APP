package eu.europa.ec.fisheries.uvms.asset.util;

import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchLeaf;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchBranchDeserializer implements JsonbDeserializer<SearchBranch> {

    private static final Logger LOG = LoggerFactory.getLogger(SearchBranchDeserializer.class);

        @Override
        public SearchBranch deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

            JsonObject object = parser.getObject();
            return recurse(object);
        }

        private SearchBranch recurse(JsonObject object){
            try {
                SearchBranch trunk = new SearchBranch();
                trunk.setLogicalAnd(object.getBoolean("logicalAnd",true));
                JsonArray fields = object.getJsonArray("fields");
                for (JsonValue jsonValue : fields) {
                    if (jsonValue.asJsonObject().containsKey("fields")) {
                        trunk.getFields().add(recurse(jsonValue.asJsonObject()));

                    } else {
                    	String jsonSerachFieldValue = jsonValue.asJsonObject().getJsonString("searchField").getString();
                    	if(jsonSerachFieldValue.equalsIgnoreCase("flagState")) {
                    		jsonSerachFieldValue = "FLAG_STATE";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("name")) {
                    		jsonSerachFieldValue = "NAME";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("externalMarking")) {
                    		jsonSerachFieldValue = "EXTERNAL_MARKING";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("ircs")) {
                    		jsonSerachFieldValue = "IRCS";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("cfr")) {
                    		jsonSerachFieldValue = "CFR";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("mmsi")) {
                    		jsonSerachFieldValue = "MMSI";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("imo")) {
                    		jsonSerachFieldValue = "IMO";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("iccat")) {
                    		jsonSerachFieldValue = "ICCAT";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("uvi")) {
                    		jsonSerachFieldValue = "UVI";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("gfcm")) {
                    		jsonSerachFieldValue = "GFCM";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("portOfRegistration")) {
                    		jsonSerachFieldValue = "HOMEPORT";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("licenceType")) {
                    		jsonSerachFieldValue = "LICENSE";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("vesselType")) {
                    		jsonSerachFieldValue = "VESSEL_TYPE";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("id")) {
                    		jsonSerachFieldValue = "GUID";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("historyId")) {
                    		jsonSerachFieldValue = "HIST_GUID";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("gearFishingType")) {
                    		jsonSerachFieldValue = "GEAR_TYPE";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("lengthOverAll")) {
                        	//jsonSerachFieldValue = "MAX_LENGTH";
                    		 jsonSerachFieldValue = "LENGTH_OVER_ALL";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("powerOfMainEngine")) {
                        	//jsonSerachFieldValue = "MAX_POWER";
                    		jsonSerachFieldValue = "ENGINE_POWER";
                        }else if(jsonSerachFieldValue.equalsIgnoreCase("producerName")) {
                    		jsonSerachFieldValue = "PRODUCER_NAME";
                        }
                    	SearchFields key = SearchFields.valueOf(jsonSerachFieldValue);
                    	String value;
                        if (jsonValue.asJsonObject().get("searchValue").getValueType() == ValueType.STRING) {
                            value = jsonValue.asJsonObject().getJsonString("searchValue").getString();
                        } else {
                        	value = jsonValue.asJsonObject().get("searchValue").toString();
                        }
                        List<String> operatorWhiteList = new ArrayList<String>(Arrays.asList(">=", "<=", "!=", "="));
                        String operator = null;
                        if (jsonValue.asJsonObject().containsKey("operator")) {
                        	String operatorFromJson = jsonValue.asJsonObject().getJsonString("operator").getString();
                            operator = operatorWhiteList.contains(operatorFromJson) ? operatorFromJson : "=";
                        }
                        trunk.getFields().add(new SearchLeaf(key, value, operator));
                    }

                }
                return trunk;
            }catch (Exception e){
                LOG.error("Unparsable input string for asset list: {}", object.toString());
                throw new RuntimeException("Unparsable input string for asset list: " + object.toString(), e);
            }
        }
}
