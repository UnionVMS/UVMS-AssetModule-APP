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
import java.util.Map;

public class SearchBranchDeserializer implements JsonbDeserializer<SearchBranch> {

    private static final Logger LOG = LoggerFactory.getLogger(SearchBranchDeserializer.class);
    private static final List<String> OPERATOR_WHITE_LIST = new ArrayList<>(Arrays.asList(">=", "<=", "!=", "="));
    private static final Map<String,SearchFields> MAP_OF_SEARCH_FIELDS = SearchFields.getMapOfEnums();
    
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
                	SearchFields mappedValue = MAP_OF_SEARCH_FIELDS.get(jsonSerachFieldValue.toLowerCase());
                	SearchFields key =  mappedValue != null ? mappedValue : SearchFields.valueOf(jsonSerachFieldValue);  	
                	String value;
                    if (jsonValue.asJsonObject().get("searchValue").getValueType() == ValueType.STRING) {
                        value = jsonValue.asJsonObject().getJsonString("searchValue").getString();
                    } else {
                    	value = jsonValue.asJsonObject().get("searchValue").toString();
                    }
                    
                    String operator = null;
                    if (jsonValue.asJsonObject().containsKey("operator")) {
                    	String operatorFromJson = jsonValue.asJsonObject().getJsonString("operator").getString();
                        operator = OPERATOR_WHITE_LIST.contains(operatorFromJson) ? operatorFromJson : "=";
                    }
                    trunk.getFields().add(new SearchLeaf(key, value, operator));
                }
            }
            return trunk;
        }catch (Exception e){
            LOG.error("Unparsable input string for asset list: {}", object);
            throw new RuntimeException("Unparsable input string for asset list: " + object, e);
        }
    }
}
