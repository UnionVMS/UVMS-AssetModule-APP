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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetExceptionRuntimeException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.config.service.ParameterService;
import eu.europa.ec.mare.fisheries.vessel.common.v1_0.VesselDataService;
import eu.europa.ec.mare.fisheries.vessel.common.v1_0.VesselDataService_Service;

@ApplicationScoped
public class VesselDataServiceClientProducer {

    @Inject
    private ParameterService parameterService;

    private VesselDataService_Service vesselDataService_service;

    private String portName = "VesselDataServicePort";
    private String namespace = "http://ec.europa.eu/mare/fisheries/vessel/common/v1.0";

    @PostConstruct
    public void init() {
        try {
            portName = parameterService.getParamValueById("asset.fleetsync.wsdl.port.name");
            namespace = parameterService.getParamValueById("asset.fleetsync.wsdl.namespace");
            String service = parameterService.getParamValueById("asset.fleetsync.wsdl.service.name");
            String url = parameterService.getParamValueById("asset.fleetsync.wsdl.location.url");
            vesselDataService_service = new VesselDataService_Service(new URL(url), new QName(namespace, service));
        } catch (ConfigServiceException e) {
            throw new AssetExceptionRuntimeException("Error configuring fleet sync client", e);
        } catch (MalformedURLException e) {
            throw new AssetExceptionRuntimeException("Invalid Url for fleet sync wsdl location", e);
        }
    }

    public VesselDataService getVesselDataService() {
        return vesselDataService_service.getPort(new QName(namespace, portName), VesselDataService.class);
    }
}
