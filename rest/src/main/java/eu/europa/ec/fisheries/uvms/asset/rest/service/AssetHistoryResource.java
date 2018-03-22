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

import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;

@Path("/history")
@Stateless
@RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
public class AssetHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetHistoryResource.class);

    @Inject
    private AssetService assetService;

    /**
     *
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     *
     * @summary Gets a list of all history recordings for a specific asset
     *
     */
    @GET
    @Path("/asset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetHistoryListByAssetId(@QueryParam("assetId") String assetId, @QueryParam("maxNbr") Integer maxNbr) {
        try {
//            Asset assetHistories = assetService.getAssetHistoryListByAssetId(assetId, maxNbr);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting asset history list by asset ID. {}]", assetId, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/assetflagstate")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getFlagStateByIdAndDate(@QueryParam("assetGuid") String assetGuid, @QueryParam("date") String dateStr ) {
        try {
//            Date date = DateUtils.parseToUTCDate( dateStr, DateUtils.FORMAT);
//            FlagState flagState = assetService.getFlagStateByIdAndDate(assetGuid, date );
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting asset flagstate by asset ID. {}]",assetGuid);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/assetFromAssetIdAndDate")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAssetFromAssetIdAndDate(@QueryParam("type") String type,@QueryParam("value") String value,  @QueryParam("date") String dateStr ) {
        try {
//            Date date = DateUtils.parseToUTCDate(dateStr,DateUtils.FORMAT);
//            Asset asset = assetService.getAssetByIdAndDate(type, value , date);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting asset. Type: {}, Value: {}, Date: {}", type, value, dateStr, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
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
   public Response getAssetHistoryByAssetHistGuid(@PathParam("guid") UUID guid) {
        try {
//            Asset asset = assetService.getAssetHistoryByAssetHistGuid(guid);
            return Response.ok().build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by asset history guid. {}] ", guid, e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}