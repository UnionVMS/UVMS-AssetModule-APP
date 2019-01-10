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
package eu.europa.ec.fisheries.uvms.asset.client;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetCommandRequest;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityListType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.CapabilityTypeType;
import eu.europa.ec.fisheries.schema.exchange.service.v1.ServiceResponseType;

@Path("exchange/rest/api")
@Stateless
public class ExchangeModuleMock {

    @POST
    @Path("serviceList")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public GetServiceListResponse getServiceList(GetServiceListRequest request) {
        try {
            List<ServiceResponseType> serviceResponse = new ArrayList<ServiceResponseType>();
            ServiceResponseType serviceResponseType = new ServiceResponseType();
            serviceResponseType.setServiceClassName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
            serviceResponseType.setName("Thrane&Thrane");
            serviceResponseType.setSatelliteType("INMARSAT_C");
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
