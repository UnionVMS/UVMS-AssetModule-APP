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
public class MTConfigResource {



    private final static Logger LOG = LoggerFactory.getLogger(MTConfigResource.class);

    /*

    @EJB
    private ConfigServiceBean configService;
    
   @GET
   @Path("/transponders")
	public ResponseDto<List<MobileTerminalDeviceConfig>> getConfigTransponders() {
       try {
           LOG.info("Get config transponders invoked in rest layer.");
           List<TerminalSystemType> list = configService.getTerminalSystems();
           return new ResponseDto<>(MobileTerminalConfig.mapConfigTransponders(list), ResponseCode.OK);
       } catch (Exception ex) {
           LOG.error("[ Error when getting configTransponders ] {}", ex.getStackTrace());
           return ErrorHandler.getFault(ex);
       }
   }

   @GET
   @Path("/searchfields")
   public ResponseDto<SearchKey[]> getConfigSearchFields() {
       LOG.info("Get config search fields invoked in rest layer.");
       try {
           return new ResponseDto<>(SearchKey.values(), ResponseCode.OK);
       } catch (Exception ex) {
           LOG.error("[ Error when getting config search fields ] {}", ex.getStackTrace());
           return ErrorHandler.getFault(ex);
       }
   }
    
    @GET
    @Path("/")
    public ResponseDto<Map<String, List<String>>>getConfiguration() {
        try {
        	List<ConfigList> config = configService.getConfig();
            return new ResponseDto<>(MobileTerminalConfig.mapConfigList(config), ResponseCode.OK);
        } catch (Exception ex) {
            return ErrorHandler.getFault(ex);
        }
    }

    */

}
