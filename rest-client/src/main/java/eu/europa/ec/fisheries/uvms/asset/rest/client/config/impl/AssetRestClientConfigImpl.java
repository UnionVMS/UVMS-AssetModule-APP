package eu.europa.ec.fisheries.uvms.asset.rest.client.config.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import eu.europa.ec.fisheries.uvms.asset.rest.client.config.AssetRestClientConfig;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AssetRestClientConfigImpl implements AssetRestClientConfig {

    public static final String ASSET_GATEWAY_ENDPOINT_PARAMETER_KEY = "asset.gateway.endpoint";

    private ParameterService parameterService;

    @Inject
    public AssetRestClientConfigImpl(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @SneakyThrows
    @Override
    public String getAssetGatewayEndpoint() {
        try {
            return parameterService.getParamValueById(ASSET_GATEWAY_ENDPOINT_PARAMETER_KEY);
        } catch (ConfigServiceException e) {
            log.error("Could not retrieve configuration parameter for key {}", ASSET_GATEWAY_ENDPOINT_PARAMETER_KEY);
            throw new ConfigServiceException("Could not retrieve configuration parameter for key " + ASSET_GATEWAY_ENDPOINT_PARAMETER_KEY, e);
        }
    }

    @Override
    public boolean isEndpointUpdateEvent(String endpointSettingKey) {
        if (endpointSettingKey == null) {
            return false;
        }

        return endpointSettingKey.equalsIgnoreCase(ASSET_GATEWAY_ENDPOINT_PARAMETER_KEY);
    }

}
