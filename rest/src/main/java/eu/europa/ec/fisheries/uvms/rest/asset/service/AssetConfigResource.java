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
package eu.europa.ec.fisheries.uvms.rest.asset.service;

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

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.ConfigServiceBeanMT;
import eu.europa.ec.fisheries.uvms.rest.asset.mapper.ConfigMapper;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTMobileTerminalConfig;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTMobileTerminalDeviceConfig;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTResponseDto;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTErrorHandler;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

@Path("/config")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class AssetConfigResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetConfigResource.class);

    @Inject
    private ConfigServiceBean configService;

    @GET
    @Path("/searchfields")
    public Response getConfigSearchFields() {
        try {
            return Response.ok(ConfigSearchField.values()).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }

    @GET
    @Path(value = "/")
    public Response getConfiguration() {
        try {
        	List<Object> configuration = configService.getConfiguration();
        	return Response.ok(ConfigMapper.mapConfiguration(configuration)).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }
    
    @GET
    @Path(value = "/parameters")
    public Response getParameters() {
        try {
        	Map<String, String> parameters = configService.getParameters();
        	return Response.ok(parameters).build();
        } catch (Exception e) {
            LOG.error("Error when getting config search fields.");
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }
    //Stuff copied from MT

    @Inject
    ConfigServiceBeanMT configServiceMT;

    @GET
    @Path("/MT/transponders")
    public MTResponseDto<List<MTMobileTerminalDeviceConfig>> getConfigTransponders() {
        try {
            LOG.info("Get config transponders invoked in rest layer.");
            List<TerminalSystemType> list = configServiceMT.getTerminalSystems();
            return new MTResponseDto<>(MTMobileTerminalConfig.mapConfigTransponders(list), MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting configTransponders ] {}", ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/MT/searchfields")
    public MTResponseDto<SearchKey[]> getMTConfigSearchFields() {
        LOG.info("Get config search fields invoked in rest layer.");
        try {
            return new MTResponseDto<>(SearchKey.values(), MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting config search fields ] {}", ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/MT/")
    public MTResponseDto<Map<String, List<String>>>getMTConfiguration() {
        try {
            List<ConfigList> config = configServiceMT.getConfig();
            return new MTResponseDto<>(MTMobileTerminalConfig.mapConfigList(config), MTResponseCode.OK);
        } catch (Exception ex) {
            return MTErrorHandler.getFault(ex);
        }
    }
}