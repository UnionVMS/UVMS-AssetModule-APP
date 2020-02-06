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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europa.ec.fisheries.uvms.asset.bean.AssetFilterServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroupField;
import eu.europa.ec.fisheries.uvms.rest.asset.ObjectMapperContextResolver;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/filter")
@Stateless
@Api(value = "Asset Group Service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetFilterRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetFilterRestResource.class);

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private AssetFilterServiceBean assetFilterService;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    private ObjectMapper objectMapper() {
        ObjectMapperContextResolver omcr = new ObjectMapperContextResolver();
        ObjectMapper objectMapper = omcr.getContext(AssetGroup.class);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules();
        return objectMapper;
    }
    
    @GET
    @ApiOperation(value = "GetAssetFilterByAssetId", notes = "This works if field is stored with GUID and value pointing to AssetId", response = AssetFilter.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when getting AssetFilters"),
            @ApiResponse(code = 200, message = "AssetFilter success")})
    @Path("/asset/{assetId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterListByAssetId(@ApiParam(value = "Asset id", required = true) @PathParam(value = "assetId") UUID assetId) throws Exception {
        try {
            List<AssetFilter> assetFilters = assetFilterService.getAssetFilterListByAssetId(assetId);
            String response = objectMapper().writeValueAsString(assetFilters);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset filter list by user. {}", assetId, toString(), e);
            throw e;
        }
    }

    
    // Test endpoint Filter
    // public Response getAssetGroupListByUser(@ApiParam(value = "user", required = true) @QueryParam(value = "user") String user) throws Exception {
    
    @POST
    @ApiOperation(value = "CreateAssetFilterValue", notes = "CreateAssetFilterValue", response = AssetGroupField.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when create AssetGroupField"),
            @ApiResponse(code = 200, message = "AssetGroupField successfully deleted")})
    @Path("/assetFilter")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilter(@PathParam(value = "assetFId")
                                          @ApiParam(value = "The AssetFilterValue to be created", required = true) AssetFilter assetFilter) throws Exception {
        try {
            String user = servletRequest.getRemoteUser();
            AssetFilter createdAssetdFilter = assetFilterService.createAssetFilter(assetFilter, user);
            String response = objectMapper().writeValueAsString(createdAssetdFilter);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetFilterValue. ", e);
            throw e;
        }
    }

    @PUT
    @ApiOperation(value = "UpdateAssetFilterValue", notes = "UpdateAssetFilterValue", response = AssetGroupField.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when update AssetFilterValue"),
            @ApiResponse(code = 200, message = "AssetFilterValue successfully update")})
    @Path("/value")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetGroupField(@ApiParam(value = "Parent AssetFilterValue", required = true) AssetFilterValue assetFilterValue) throws Exception {

        try {
            String user = servletRequest.getRemoteUser();
            AssetFilterValue updatedAssetFilterValue = assetFilterService.updateAssetFilterValue(assetFilterValue, user);
            String response = objectMapper().writeValueAsString(updatedAssetFilterValue);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating updatedAssetFilterValue. ", e);
            throw e;
        }
    }

}
