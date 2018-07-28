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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseCodeConstant;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.asset.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.asset.service.AssetHistoryService;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

import java.net.URLDecoder;
import java.util.Date;

/**
 **/
@Path("/history")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class AssetHistoryResource {

    @EJB
    AssetHistoryService assetHistoryService;

    final static Logger LOG = LoggerFactory.getLogger(AssetHistoryResource.class);

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Gets a list of all history recordings for a specific transportMeans
     *
     */
    @GET
    @Path("/transportMeans")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public ResponseDto getAssetHistoryListByAssetId(@QueryParam("assetId") String assetId, @QueryParam("maxNbr") Integer maxNbr) {
        try {
            LOG.info("Getting transportMeans history list by transportMeans ID: {}",assetId);
            return new ResponseDto(assetHistoryService.getAssetHistoryListByAssetId(assetId, maxNbr), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting transportMeans history list by transportMeans ID. {}]",assetId);
            return ErrorHandler.getFault(e);
        }
    }

    @GET
    @Path("/assetflagstate")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public ResponseDto getFlagStateByIdAndDate(@QueryParam("assetGuid") String assetGuid, @QueryParam("date") String dateStr ) {
        try {
            LOG.info("Getting transportMeans history list by transportMeans GUID: {} Date: {}",assetGuid, dateStr);
            return new ResponseDto(assetHistoryService.getFlagStateByIdAndDate(assetGuid, DateUtils.parseToUTCDate( dateStr, DateUtils.FORMAT) ), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting transportMeans history list by transportMeans ID. {}]",assetGuid);
            return ErrorHandler.getFault(e);
        }
    }



    @GET
    @Path("/assetFromAssetIdAndDate")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public ResponseDto getAssetFromAssetIdAndDate(@QueryParam("type") String type,@QueryParam("value") String value,  @QueryParam("date") String dateStr ) {
        try {
            return new ResponseDto(assetHistoryService.getAssetByIdAndDate(type, value , DateUtils.parseToUTCDate(dateStr,DateUtils.FORMAT)), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting transportMeans {}{}{} ]from cfr and date", type, value, dateStr );
            return ErrorHandler.getFault(e);
        }
    }



    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Gets a specific history recording by guid
     *
     */
    @GET
    @Path("/{guid}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
   public ResponseDto getAssetHistoryByAssetHistGuid(@PathParam("guid") String guid) {
        try {
            LOG.info("Getting transportMeans history by transportMeans history guid: {}",guid);
            return new ResponseDto(assetHistoryService.getAssetHistoryByAssetHistGuid(guid), ResponseCodeConstant.OK);
        } catch (Exception e) {
            LOG.error("[ Error when getting transportMeans history by transportMeans history guid. {}] ",guid);
            return ErrorHandler.getFault(e);
        }
    }
}