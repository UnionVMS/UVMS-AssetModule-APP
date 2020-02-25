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

import eu.europa.ec.fisheries.uvms.asset.bean.AssetFilterServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilter;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/filter")
@Stateless
@Api(value = "Asset Filter Service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetFilterRestResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetFilterRestResource.class);

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private AssetFilterServiceBean assetFilterService;
    
    private Jsonb jsonb;

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    @PostConstruct
    public void init() {
        jsonb =  new JsonBConfigurator().getContext(null);
    }
    
    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Get asset Filter list by user
     */
    @GET
    @ApiOperation(value = "Get a list of Assetfilters for user", notes = "Get a list of Assetfilters for user", response = AssetFilter.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving AssetFilter list"),
            @ApiResponse(code = 200, message = "AssetFilter list successfully retrieved")})
    @Path("list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterListByUser(@ApiParam(value = "user", required = true) @QueryParam(value = "user") String user) throws Exception {
        try {
            List<AssetFilter> assetFilter = assetFilterService.getAssetFilterList(user);
            String response = jsonb.toJson(assetFilter);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when retrieving assetFilter list {}", user, e);
            throw e;
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Get asset filter by ID
     */
    @GET
    @ApiOperation(value = "Get an AssetFilter by its id", notes = "Get a an AssetFilter by its id", response = AssetFilter.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving AssetGroup list"),
            @ApiResponse(code = 200, message = "AssetFilter list successfully retrieved")})
    @Path("/{assetFilterId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterById(@ApiParam(value = "AssetFilter Id", required = true) @PathParam(value = "assetFilterId") final UUID id) throws Exception {
        try {
            AssetFilter assetFilter = assetFilterService.getAssetFilterById(id);
            String response = jsonb.toJson(assetFilter);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting asset by ID. ", id, e);
            throw e;
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Create a asset filter
     */
    @POST
    @ApiOperation(value = "Create an AssetFilter", notes = "Create an AssetFilter", response = AssetFilter.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when creating AssetFilter"),
            @ApiResponse(code = 200, message = "AssetFilter successfully created")})
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilter(@ApiParam(value = "AssetFilter", required = true) final AssetFilter assetFilter) throws Exception {
        try {
            String user = servletRequest.getRemoteUser();
            AssetFilter createdAssetFilter = assetFilterService.createAssetFilter(assetFilter, user);
            return Response.ok(createdAssetFilter).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating asset group: {}", assetFilter, e);
            throw e;
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Update a asset group
     */
    @PUT
    @ApiOperation(value = "Update an AssetFilter", notes = "Update an AssetFilter", response = AssetFilter.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when update AssetGroup"),
            @ApiResponse(code = 200, message = "AssetGroup successfully updated")})
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetFilter(@ApiParam(value = "AssetFilter", required = true) final AssetFilter assetFilter) throws Exception {
        try {
            String user = servletRequest.getRemoteUser();
            AssetFilter updatedAssetFilter = assetFilterService.updateAssetFilter(assetFilter, user);
            String response = jsonb.toJson(assetFilter);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when updating asset group. {}", assetFilter, e);
            throw e;
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Delete a asset group
     */
    @DELETE
    @ApiOperation(value = "Delete an AssetFilter", notes = "Delete an AssetFilter", response = AssetFilter.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete AssetFilter"),
            @ApiResponse(code = 200, message = "AssetFilter successfully deleted")})
    @Path("/{assetFilterId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteAssetFilter(@ApiParam(value = "AssetFilter id", required = true) @PathParam(value = "assetFilterId") final UUID id) throws Exception {
        try {
            String user = servletRequest.getRemoteUser();
            assetFilterService.deleteAssetFilterById(id, user);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when deleting asset filter by id: {}", id, e);
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "GetAssetFilterByAssetId", notes = "This works if field is stored with GUID and value pointing to AssetId", response = AssetFilter.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete assetFilters"),
            @ApiResponse(code = 200, message = "assetFilters successfully deleted")})
    @Path("/asset/{assetId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterListByAssetId(@ApiParam(value = "Asset id", required = true) @PathParam(value = "assetId") UUID assetId) throws Exception {
        try {
            List<AssetFilter> assetFilters = assetFilterService.getAssetFilterListByAssetId(assetId);
            String response = jsonb.toJson(assetFilters);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when getting assetFilters list by user. {}", assetId, toString(), e);
            throw e;
        }
    }

    @POST
    @ApiOperation(value = "CreateAssetFilterQuery", notes = "CreateAssetFilterQuery", response = AssetFilterQuery.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when create AssetFilterQuery"),
            @ApiResponse(code = 200, message = "AssetFilterQuery successfully deleted")})
    @Path("/{assetFilterId}/query")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilterQuery(@ApiParam(value = "Parent AssetFilter id", required = true) @PathParam(value = "assetFilterId") UUID parentAssetFilterId,
                                          @ApiParam(value = "The AssetFilterQuery to be created", required = true) AssetFilterQuery assetFilterQuery) throws Exception {
        try {
            AssetFilterQuery createdAssetFilterQuery = assetFilterService.createAssetFilterQuery(parentAssetFilterId, assetFilterQuery);//  createAssetFilterQuery(parentAssetFilterId, assetFilterQuery, user);
            String response = jsonb.toJson(createdAssetFilterQuery);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating createdAssetFilterQuery. ", e);
            throw e;
        }
    }

    @POST
    @ApiOperation(value = "CreateAssetFilterValue", notes = "CreateAssetFilterValue", response = AssetFilterQuery.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when create AssetFilterValue"),
            @ApiResponse(code = 200, message = "AssetFilterValue successfully deleted")})
    @Path("/{assetFilterQueryId}/value")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilterValue(@ApiParam(value = "Parent AssetFilterQuery id", required = true) @PathParam(value = "assetFilterQueryId") UUID parentAssetFilterQueryId,
                                          @ApiParam(value = "The AssetFilterValue to be created", required = true) AssetFilterValue assetFilterValue) throws Exception {
        try {
            AssetFilterValue createdAssetFilterValue = assetFilterService.createAssetFilterValue(parentAssetFilterQueryId, assetFilterValue);
            String response = jsonb.toJson(createdAssetFilterValue);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating createdAssetFilterValue. ", e);
            throw e;
        }
    }
    
    @PUT
    @ApiOperation(value = "UpdateAssetFilterValue", notes = "UpdateAssetFilterValue", response = AssetFilterValue.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when update AssetFilterValue"),
            @ApiResponse(code = 200, message = "AssetFilterValue successfully update")})
    @Path("/value")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetFilterValue(@ApiParam(value = "Parent AssetFilterValue", required = true) AssetFilterValue assetFilterValue) throws Exception {

        try {
            String user = servletRequest.getRemoteUser();
            AssetFilterValue updatedAssetFilterValue = assetFilterService.updateAssetFilterValue(assetFilterValue, user);
            String response = jsonb.toJson(updatedAssetFilterValue);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetFilterValue. ", e);
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "GetAssetFilterValue by id", notes = "GetAssetFilterValue by id", response = AssetFilterValue.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when get AssetFilterValue"),
            @ApiResponse(code = 200, message = "AssetFilterValue successfully fetched")})
    @Path("/value/{assetFilterValueId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterValue(@ApiParam(value = "AssetFilterValue id", required = true) @PathParam(value = "assetFilterValueId") UUID id) throws Exception {

        try {
        	AssetFilterValue fetchedAssetFilterValue = assetFilterService.getAssetFilterValue(id);
            String response = jsonb.toJson(fetchedAssetFilterValue);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetFilterValue. ", e);
            throw e;
        }
    }

    @DELETE
    @ApiOperation(value = "DeleteAssetFilterValue by id", notes = "DeleteAssetFilterValue by id", response = AssetFilterValue.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete AssetFilterValue"),
            @ApiResponse(code = 200, message = "AssetFilterValue successfully deleted")})
    @Path("/value/{assetFilterValueId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteAssetFilterValue(@ApiParam(value = "AssetFilterValue id", required = true) @PathParam(value = "assetFilterValueId") UUID assetFilterValueId) throws Exception {

        try {
            String user = servletRequest.getRemoteUser();
            AssetFilterValue fetchedAssetGroupField = assetFilterService.deleteAssetFilterValue(assetFilterValueId, user);
            String response = jsonb.toJson(fetchedAssetGroupField);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when delete AssetFilterValue. ", e);
            throw e;
        }
    }

    @GET
    @ApiOperation(value = "Retrieve AssetFilterQuerys  by AssetFilterId", response = AssetFilterValue.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when retrieving AssetFilterValues"),
            @ApiResponse(code = 200, message = "AssetFilterValues successfully retrieved")})
    @Path("/{assetFilterId}/querysForFilter")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response retrieveValuesForFilters(@ApiParam(value = "AssetFilter id", required = true) @PathParam(value = "assetFilterId") UUID assetFilterId) throws Exception {

        try {
            List<AssetFilterQuery> fetchedAssetFilterQueries = assetFilterService.retrieveQuerysForFilter(assetFilterId);
            String response = jsonb.toJson(fetchedAssetFilterQueries);
            return Response.ok(response).header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when fetching AssetFilterValues. ", e);
            throw e;
        }

    }

    @DELETE
    @ApiOperation(value = "Delete AssetFilterValues  for AssetFilterId", response = AssetFilterValue.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Error when delete AssetFilterValues"),
            @ApiResponse(code = 200, message = "AssetFilterValues successfully deleted")})
    @Path("/{assetFilterId}/valuesForFilter")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteQuerysFromFilters(@ApiParam(value = "AssetFilter id", required = true) @PathParam(value = "assetFilterId") UUID assetFilterId) throws Exception {

        try {
        	assetFilterService.removeQuerysFromFilter(assetFilterId);
            return Response.ok().header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("Error when fetching AssetGroupFields. ", e);
            throw e;
        }

    }

}
