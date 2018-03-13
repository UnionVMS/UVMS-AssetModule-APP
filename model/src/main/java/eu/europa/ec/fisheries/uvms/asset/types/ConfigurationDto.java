package eu.europa.ec.fisheries.uvms.asset.types;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationDto {

    private List<Config> configList = new ArrayList<>();

    public void addConfig(Config config) {
        configList.add(config);
    }

    public List<Config> getConfigList() {
        return configList;
    }


}
