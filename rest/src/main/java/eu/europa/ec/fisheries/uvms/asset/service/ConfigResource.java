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
package eu.europa.ec.fisheries.uvms.asset.service;

import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.mapper.ConfigMapper;
import eu.europa.ec.fisheries.uvms.asset.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

@Path("/config")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class ConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

    @Inject
    private ConfigServiceBean configService;

    @GET
    @Path("/searchfields")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getConfigSearchFields() {
        try {
            return Response.ok(ConfigSearchField.values()).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/")
    public Response getConfiguration() {
        try {
        	List<Object> configuration = configService.getConfiguration();
        	return Response.ok(ConfigMapper.mapConfiguration(configuration)).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/parameters")
    public Response getParameters() {
        try {
        	Map<String, String> parameters = configService.getParameters();
        	return Response.ok(parameters).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}