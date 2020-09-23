package eu.europa.ec.fisheries.uvms.asset.service.sync;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.mare.fisheries.vessel.common.v1_0.VesselDataService_Service;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class VesselDataServiceClientProducer {

    @Inject
    private ParameterService parameterService;

    private VesselDataService_Service vesselDataService_service;

    @PostConstruct
    void init() throws AssetException {
        try {
            String url = parameterService.getParamValueById("asset.fleetsync.wsdl.location.url");
            vesselDataService_service = new VesselDataService_Service(new URL(url));
        } catch (ConfigServiceException e) {
            log.error("Error configuring fleet sync client", e);
            throw new AssetException("Error configuring fleet sync client");
        } catch (MalformedURLException e) {
            log.error("Invalid Url for fleet sync wsdl location", e);
            throw new AssetException("Invalid Url for fleet sync wsdl location");
        }
    }

    @Produces
    @ApplicationScoped
    public VesselDataService_Service getVesselDataServiceClient() {
        return vesselDataService_service;
    }
}
