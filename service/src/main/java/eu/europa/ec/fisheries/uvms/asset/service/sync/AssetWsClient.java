/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
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
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class AssetWsClient {

    @Inject
    private VesselDataServiceClientProducer vesselDataServiceClientProducer;

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
            vesselCoreDataResponse = vesselDataServiceClientProducer.getVesselDataService().getVesselCoreData(getVesselCoreData, "clientId", "partialFailure");
        } catch (FleetDataServiceExceptionFault e) {
            log.warn("Error getting page {} with page size {} from fleet server", pageNumber, pageSize, e);
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
            vesselExtendedDataResponse = vesselDataServiceClientProducer.getVesselDataService().getVesselExtendedData(getVesselExtendedData, "clientId", "partialFailure");
        } catch (FleetDataServiceExceptionFault e) {
            log.warn("Error getting extended data for asset with cfr {} from fleet server", cfr, e);
            return null;
        }

        return vesselExtendedDataResponse;
    }
}
