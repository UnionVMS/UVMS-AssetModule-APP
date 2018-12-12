package eu.europa.ec.fisheries.uvms.mobileterminal.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Duration;

public class AssetDurationDeserializer extends StdDeserializer<Duration> {

    public AssetDurationDeserializer() {
        this(null);
    }

    public AssetDurationDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return Duration.ofSeconds(Long.valueOf(jsonParser.getText()));
    }
}
