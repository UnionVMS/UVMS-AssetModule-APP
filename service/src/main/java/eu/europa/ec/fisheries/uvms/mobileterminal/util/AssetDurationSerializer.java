package eu.europa.ec.fisheries.uvms.mobileterminal.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Duration;

public class AssetDurationSerializer extends StdSerializer<Duration> {

    public AssetDurationSerializer() {
        this(null);
    }

    public AssetDurationSerializer(Class<Duration> t) {
        super(t);
    }

    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString("" + duration.getSeconds());
    }
}
