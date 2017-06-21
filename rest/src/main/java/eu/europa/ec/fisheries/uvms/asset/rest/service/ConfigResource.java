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
package eu.europa.ec.fisheries.uvms.asset.rest.service;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseCodeConstant;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.asset.rest.mapper.ConfigMapper;
import eu.europa.ec.fisheries.uvms.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.wsdl.asset.config.Config;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

/**
 **/
@Path("/config")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class ConfigResource {

    @EJB
    ConfigServiceBean configService;
    
    final static Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/searchfields")
    public ResponseDto getConfigSearchFields() {
        try {
            return new ResponseDto(ConfigSearchField.values(), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. ]");
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/")
    public ResponseDto getConfiguration() {
        try {
        	List<Config> configuration = configService.getConfiguration();
            return new ResponseDto(ConfigMapper.mapConfiguration(configuration), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. ] ");
            return ErrorHandler.getFault(e);
        }
    }
    
    @GET
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Path(value = "/parameters")
    public ResponseDto getParameters() {
        try {
        	Map<String, String> parameters = configService.getParameters();
            return new ResponseDto(parameters, ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting config search fields. ] ");
            return ErrorHandler.getFault(e);
        }
    }
}