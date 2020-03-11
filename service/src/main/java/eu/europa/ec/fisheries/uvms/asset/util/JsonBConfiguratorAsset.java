package eu.europa.ec.fisheries.uvms.asset.util;

import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;

public class JsonBConfiguratorAsset extends JsonBConfigurator {

    public JsonBConfiguratorAsset() {
        super();
        config.withDeserializers(new SearchBranchDeserializer());
    }
}
