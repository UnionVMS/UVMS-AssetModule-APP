package eu.europa.ec.fisheries.uvms.asset.util;

import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchLeaf;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

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
                        //This part does not work bc it picks up the wrong jsonB implementation library in the tests, since we for some reason have two different   ;(
                        //JsonParser parser = Json.createParser(new StringReader(jsonValue.toString()));
                        //trunk.getFields().add(ctx.deserialize(A.class, parser));

                        SearchFields key = SearchFields.valueOf(jsonValue.asJsonObject().getJsonString("searchField").getString());//String("searchField"));
                        String value = jsonValue.asJsonObject().getJsonString("searchValue").getString();
                        trunk.getFields().add(new SearchLeaf(key, value));
                    }

                }
                return trunk;
            }catch (Exception e){
                LOG.error("Unparsable input string for asset list: {}", object.toString());
                throw new RuntimeException("Unparsable input string for asset list: " + object.toString(), e);
            }
        }
}
