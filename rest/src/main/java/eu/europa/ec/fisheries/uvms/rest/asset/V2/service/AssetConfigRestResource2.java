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
package eu.europa.ec.fisheries.uvms.rest.asset.V2.service;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.asset.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTMobileTerminalConfig;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.Map;

@Path("/config2")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class AssetConfigRestResource2 {

    private static final Logger LOG = LoggerFactory.getLogger(AssetConfigRestResource2.class);

    @Inject
    private ConfigServiceBean configService;

    @Inject
    ConfigServiceBeanMT configServiceMT;

    @GET
    @Path("/searchfields")
    public Response getConfigSearchFields() {
        try {
            return Response.ok(ConfigSearchField.values()).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }
    
    @GET
    @Path(value = "/parameters")
    public Response getParameters() {
        try {
        	Map<String, String> parameters = configService.getParameters();
        	return Response.ok(parameters).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(e)).build();
        }
    }

    //Stuff copied from MT
    @GET
    @Path("/MT/transponders")
    public Response getConfigTransponders() {
        try {
            LOG.info("Get config transponders invoked in rest layer.");
            List<TerminalSystemType> list = configServiceMT.getTerminalSystems();
            return Response.ok(MTMobileTerminalConfig.mapConfigTransponders(list)).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting configTransponders {} ] {}", ex.getMessage(), ex.getStackTrace());
            return Response.serverError().entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }
    
    @GET
    @Path("/MT/searchfields")
    public Response getMTConfigSearchFields() {
        LOG.info("Get config search fields invoked in rest layer.");
        try {
            return Response.ok(SearchKey.values()).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting config search fields ] {}", ex.getStackTrace());
            return Response.serverError().entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @GET
    @Path("/MT/")
    public Response getMTConfiguration() {
        try {
            List<ConfigList> config = configServiceMT.getConfigValues();
            return Response.ok(MTMobileTerminalConfig.mapConfigList(config)).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            return Response.serverError().entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }
}