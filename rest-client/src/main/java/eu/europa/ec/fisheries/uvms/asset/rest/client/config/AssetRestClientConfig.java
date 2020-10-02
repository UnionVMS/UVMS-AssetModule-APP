package eu.europa.ec.fisheries.uvms.asset.rest.client.config;


public interface AssetRestClientConfig {

    String getAssetGatewayEndpoint();

    boolean isEndpointUpdateEvent(String endpointSettingKey);

}
