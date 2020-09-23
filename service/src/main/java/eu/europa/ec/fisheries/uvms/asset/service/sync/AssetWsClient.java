package eu.europa.ec.fisheries.uvms.asset.service.sync;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import eu.europa.ec.mare.fisheries.vessel.common.v1.GetVesselCoreData;
import eu.europa.ec.mare.fisheries.vessel.common.v1.GetVesselCoreDataResponse;
import eu.europa.ec.mare.fisheries.vessel.common.v1.GetVesselExtendedData;
import eu.europa.ec.mare.fisheries.vessel.common.v1.GetVesselExtendedDataResponse;
import eu.europa.ec.mare.fisheries.vessel.common.v1.PagingType;
import eu.europa.ec.mare.fisheries.vessel.common.v1.PredicateType;
import eu.europa.ec.mare.fisheries.vessel.common.v1.PredicateTypeOperator;
import eu.europa.ec.mare.fisheries.vessel.common.v1.SelectorType;
import eu.europa.ec.mare.fisheries.vessel.common.v1_0.FleetDataServiceExceptionFault;
import eu.europa.ec.mare.fisheries.vessel.common.v1_0.VesselDataService_Service;

@ApplicationScoped
public class AssetWsClient {

    @Inject
    private VesselDataService_Service vesselDataService_service;

    public GetVesselCoreDataResponse getAssetPage(Integer pageNumber, Integer pageSize) {
        SelectorType selectorType = new SelectorType();
        PagingType pagingType = new PagingType();
        pagingType.setMaxResults(pageSize);
        pagingType.setOffset(pageNumber);
        selectorType.setPaging(pagingType);

        GetVesselCoreData getVesselCoreData = new GetVesselCoreData();
        getVesselCoreData.setSelector(selectorType);

        GetVesselCoreDataResponse vesselCoreDataResponse = null;
        try {
            vesselCoreDataResponse = vesselDataService_service.getVesselDataServicePort().getVesselCoreData(getVesselCoreData, "clientId", "partialFailure");
        } catch (FleetDataServiceExceptionFault fleetDataServiceExceptionFault) {
            fleetDataServiceExceptionFault.printStackTrace();
            return null;
        }
        return vesselCoreDataResponse;
    }


    public GetVesselExtendedDataResponse getExtendedDataForAssetByCfr(String cfr) {
        SelectorType selectorType = new SelectorType();
        PagingType pagingType = new PagingType();
        pagingType.setMaxResults(1);
        pagingType.setOffset(0);
        selectorType.setPaging(pagingType);

        PredicateType cfrPredicate = new PredicateType();
        cfrPredicate.setField("cfr");
        cfrPredicate.setOperator(PredicateTypeOperator.EQUALS);
        cfrPredicate.getValues().add(cfr);
        selectorType.getPredicates().add(cfrPredicate);

        GetVesselExtendedData getVesselExtendedData = new GetVesselExtendedData();
        getVesselExtendedData.setSelector(selectorType);

        GetVesselExtendedDataResponse vesselExtendedDataResponse = null;
        try {
            vesselExtendedDataResponse = vesselDataService_service.getVesselDataServicePort().getVesselExtendedData(getVesselExtendedData, "clientId", "partialFailure");
        } catch (FleetDataServiceExceptionFault fleetDataServiceExceptionFault) {
            fleetDataServiceExceptionFault.printStackTrace();
            return null;
        }

        return vesselExtendedDataResponse;
    }
}
