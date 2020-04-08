package eu.europa.ec.fisheries.uvms.asset.util;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;

public class JsonBConfiguratorAsset extends JsonBConfigurator {

    public JsonBConfiguratorAsset() {
        super();
        config.withDeserializers(new SearchBranchDeserializer());
    }
    
    @Override
    public Jsonb getContext(Class<?> type) {
        return JsonbBuilder.newBuilder()
                .withConfig(config)
                .build();
    }
}
