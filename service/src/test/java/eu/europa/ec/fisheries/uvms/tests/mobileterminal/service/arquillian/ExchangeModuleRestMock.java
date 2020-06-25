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
package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;

@Path("exchange/rest/unsecured/api")
@Stateless
public class ExchangeModuleRestMock {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeModuleRestMock.class);

    @POST
    @Path("serviceList")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public GetServiceListResponse getServiceList(GetServiceListRequest request) {
        try {

            LOG.debug("READING MESSAGE IN EXCHANGE MOCK: " + request.toString());

            List<ServiceResponseType> serviceResponse = new ArrayList<>();
            ServiceResponseType serviceResponseType = new ServiceResponseType();
            serviceResponseType.setServiceClassName("eu.europa.ec.fisheries.uvms.plugins.test");
            serviceResponseType.setName("Test&Test");
            serviceResponseType.setSatelliteType("INMARSAT_D");
            serviceResponseType.setActive(true);
            CapabilityListType capabilityList = new CapabilityListType();
            CapabilityType capabilityType = new CapabilityType();
            capabilityType.setType(CapabilityTypeType.POLLABLE);
            capabilityType.setValue("TRUE");
            capabilityList.getCapability().add(capabilityType);
            serviceResponseType.setCapabilityList(capabilityList);

            serviceResponse.add(serviceResponseType);

            GetServiceListResponse response = new GetServiceListResponse();
            response.getService().addAll(serviceResponse);

            return response;
        } catch (Exception e) {
            LOG.error("Mock error", e);
            return null;
        }
    }

    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Path("/pluginCommand")
    public Response sendCommandToPlugin(SetCommandRequest request) {
        return Response.ok().build();
    }
}
