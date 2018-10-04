package eu.europa.ec.fisheries.uvms.rest.asset;


import eu.europa.ec.fisheries.uvms.rest.asset.service.SpatialEnrichmentResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/assetinternal")
public class RestActivatorMovementRulesEnrich  extends Application {


    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivatorMovementRulesEnrich() {
        set.add(SpatialEnrichmentResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }



}
