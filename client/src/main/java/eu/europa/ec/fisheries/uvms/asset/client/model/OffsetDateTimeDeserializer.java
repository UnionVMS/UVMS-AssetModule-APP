package eu.europa.ec.fisheries.uvms.asset.client.model;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;

import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializer extends InstantDeserializer {
    public OffsetDateTimeDeserializer(){
        super(InstantDeserializer.OFFSET_DATE_TIME ,DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
