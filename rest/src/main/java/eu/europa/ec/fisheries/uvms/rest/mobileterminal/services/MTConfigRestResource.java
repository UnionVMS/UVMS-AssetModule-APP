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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.services;

import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @apiDescription Handles all Polls
 */
@Path("/config")
@Stateless
@Consumes(value = {MediaType.APPLICATION_JSON})
@Produces(value = {MediaType.APPLICATION_JSON})
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class MTConfigRestResource {



    private final static Logger LOG = LoggerFactory.getLogger(MTConfigRestResource.class);

    /*

    @EJB
    private ConfigServiceBean configService;
    
   @GET
   @Path("/transponders")
	public MTResponseDto<List<MTMobileTerminalDeviceConfig>> getConfigTransponders() {
       try {
           LOG.info("Get config transponders invoked in rest layer.");
           List<TerminalSystemType> list = configService.getTerminalSystems();
           return new MTResponseDto<>(MTMobileTerminalConfig.mapConfigTransponders(list), MTResponseCode.OK);
       } catch (Exception ex) {
           LOG.error("[ Error when getting configTransponders ] {}", ex.getStackTrace());
           return MTErrorHandler.getFault(ex);
       }
   }

   @GET
   @Path("/searchfields")
   public MTResponseDto<SearchKey[]> getConfigSearchFields() {
       LOG.info("Get config search fields invoked in rest layer.");
       try {
           return new MTResponseDto<>(SearchKey.values(), MTResponseCode.OK);
       } catch (Exception ex) {
           LOG.error("[ Error when getting config search fields ] {}", ex.getStackTrace());
           return MTErrorHandler.getFault(ex);
       }
   }
    
    @GET
    @Path("/")
    public MTResponseDto<Map<String, List<String>>>getConfiguration() {
        try {
        	List<ConfigList> config = configService.getConfig();
            return new MTResponseDto<>(MTMobileTerminalConfig.mapConfigList(config), MTResponseCode.OK);
        } catch (Exception ex) {
            return MTErrorHandler.getFault(ex);
        }
    }

    */

}
