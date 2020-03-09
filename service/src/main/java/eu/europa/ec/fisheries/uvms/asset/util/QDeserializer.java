package eu.europa.ec.fisheries.uvms.asset.util;

import eu.europa.ec.fisheries.uvms.asset.domain.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.A;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.Q;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import java.lang.reflect.Type;

public class QDeserializer implements JsonbDeserializer<Q> {

    private static final Logger LOG = LoggerFactory.getLogger(QDeserializer.class);

        @Override
        public Q deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

            JsonObject object = parser.getObject();
            return recurse(object);
        }

        private Q recurse(JsonObject object){
            try {
                Q trunk = new Q();
                trunk.setLogicalAnd(object.getBoolean("logicalAnd",true));
                JsonArray fields = object.getJsonArray("fields");
                for (JsonValue jsonValue : fields) {
                    if (jsonValue.asJsonObject().containsKey("fields")) {
                        trunk.getFields().add(recurse(jsonValue.asJsonObject()));

                    } else {
                        //This part does not work bc it picks up the wrong jsonB implementation library in the tests, since we for some reason have two different   ;(
                        //JsonParser parser = Json.createParser(new StringReader(jsonValue.toString()));
                        //trunk.getFields().add(ctx.deserialize(A.class, parser));

                        SearchFields key = SearchFields.valueOf(jsonValue.asJsonObject().getString("searchField"));
                        String value = jsonValue.asJsonObject().get("searchValue").toString();
                        trunk.getFields().add(new A(key, value));
                    }

                }
                return trunk;
            }catch (Exception e){
                LOG.error("Unparsable input string for asset list: {}", object.toString());
                throw new RuntimeException("Unparsable input string for asset list: " + object.toString(), e);
            }
        }
}
