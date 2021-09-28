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
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterList;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterQuery;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetFilterValue;
import eu.europa.ec.fisheries.uvms.rest.asset.util.AssetFilterRestResponseAdapter;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/filter")
@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssetFilterRestResource {

    private static final String REQUSTID = "requestId";

    private static final Logger LOG = LoggerFactory.getLogger(AssetFilterRestResource.class);

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private AssetFilterServiceBean assetFilterService;
    
    private Jsonb jsonb;

    @PostConstruct
    public void init() {
    	JsonbConfig config = new JsonbConfig().withAdapters(new AssetFilterRestResponseAdapter());
        jsonb = JsonbBuilder.create(config);
    }
    
    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Get asset Filter list by user
     */
    @GET
    @Path("/listbyuser")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterListByUser(@QueryParam(value = "user") String user) throws Exception {
        try {
        	if(user == null) {
        		user = servletRequest.getRemoteUser();
        	}
            List<AssetFilter> assetFilterList = assetFilterService.getAssetFilterList(user);
            String response =  jsonb.toJson(assetFilterList);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
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
    @Path("/{assetFilterId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterById(@PathParam(value = "assetFilterId") final UUID id) throws Exception {
        try {
            AssetFilter assetFilter = assetFilterService.getAssetFilterById(id);
            String response = jsonb.toJson(assetFilter);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
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
    @Path("/createFilter")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilterIn(final AssetFilter assetFilter) throws Exception {
        try {
            String user = servletRequest.getRemoteUser();
            AssetFilter createdAssetFilter = assetFilterService.createAssetFilter(assetFilter, user);
            return Response.ok(createdAssetFilter).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetFilter: {}", assetFilter, e);
            throw e;
        }
    }

    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Delete a asset group
     */
    @DELETE
    @Path("/{assetFilterId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteAssetFilter(@PathParam(value = "assetFilterId") final UUID id) throws Exception {
        try {
            String user = servletRequest.getRemoteUser();
            assetFilterService.deleteAssetFilterById(id, user);
            return Response.ok().header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when deleting asset filter by id: {}", id, e);
            throw e;
        }
    }

    @GET
    @Path("/asset/{assetId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterListByAssetId(@PathParam(value = "assetId") UUID assetId) throws Exception {
        try {
            List<AssetFilter> assetFilters = assetFilterService.getAssetFilterListByAssetId(assetId);
            String response = jsonb.toJson(assetFilters);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when getting assetFilters list by user. {}", assetId, e);
            throw e;
        }
    }

    @POST
    @Path("/{assetFilterId}/query")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilterQuery(@PathParam(value = "assetFilterId") UUID parentAssetFilterId,
                                          AssetFilterQuery assetFilterQuery) throws Exception {
        try {
            AssetFilterQuery createdAssetFilterQuery = assetFilterService.createAssetFilterQuery(parentAssetFilterId, assetFilterQuery);//  createAssetFilterQuery(parentAssetFilterId, assetFilterQuery, user);
            String response = jsonb.toJson(createdAssetFilterQuery);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when creating createdAssetFilterQuery. ", e);
            throw e;
        }
    }

    @POST
    @Path("/{assetFilterQueryId}/value")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilterValue(@PathParam(value = "assetFilterQueryId") UUID parentAssetFilterQueryId,
                                          AssetFilterValue assetFilterValue) throws Exception {
        try {
            AssetFilterValue createdAssetFilterValue = assetFilterService.createAssetFilterValue(parentAssetFilterQueryId, assetFilterValue);
            
            String response = jsonb.toJson(createdAssetFilterValue);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when creating createdAssetFilterValue. ", e);
            throw e;
        }
    }
    
    @PUT
    @Path("/value")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetFilterValue(AssetFilterValue assetFilterValue) throws Exception {

        try {
            String user = servletRequest.getRemoteUser();
            AssetFilterValue updatedAssetFilterValue = assetFilterService.updateAssetFilterValue(assetFilterValue, user);
            String response = jsonb.toJson(updatedAssetFilterValue);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetFilterValue. ", e);
            throw e;
        }
    }

    @GET
    @Path("/value/{assetFilterValueId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getAssetFilterValue(@PathParam(value = "assetFilterValueId") UUID id) throws Exception {

        try {
        	AssetFilterValue fetchedAssetFilterValue = assetFilterService.getAssetFilterValue(id);
            String response = jsonb.toJson(fetchedAssetFilterValue);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when getting AssetFilterValue. ", e);
            throw e;
        }
    }

    @DELETE
    @Path("/value/{assetFilterValueId}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response deleteAssetFilterValue(@PathParam(value = "assetFilterValueId") UUID assetFilterValueId) throws Exception {

        try {
            String user = servletRequest.getRemoteUser();
            AssetFilterValue fetchedAssetGroupField = assetFilterService.deleteAssetFilterValue(assetFilterValueId, user);
            String response = jsonb.toJson(fetchedAssetGroupField);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when delete AssetFilterValue. ", e);
            throw e;
        }
    }
    
    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Create a asset filter
     */
    @POST
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response createAssetFilter(String jsonAssetFilter) throws Exception {
        try {
            String user = servletRequest.getRemoteUser();
            AssetFilter assetFilter = jsonb.fromJson(jsonAssetFilter, AssetFilter.class);
            AssetFilter createdAssetFilter = assetFilterService.createAssetFilter(assetFilter, user);
            String resp = jsonb.toJson(createdAssetFilter);
            return Response.ok(resp).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when creating AssetFilter from json: {}", jsonAssetFilter, e);
            throw e;
        }
    }
    
    @PUT
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response updateAssetFilter(String jsonAssetFilter) throws Exception {

        try {
            AssetFilter mappedAssetFilter = jsonb.fromJson(jsonAssetFilter, AssetFilter.class);
            String user = servletRequest.getRemoteUser();
            AssetFilter updatedAssetFilter = assetFilterService.updateAllAssetFilter(mappedAssetFilter, user); 
            String response = jsonb.toJson(updatedAssetFilter);
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when updating AssetFilter. ", e);
            throw e;
        }
    }
    
    /**
     * @responseMessage 200 Success
     * @responseMessage 500 Error
     * @summary Get asset Filter list by user
     */
    @GET
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getListOfAssetFilterByUser() throws Exception {
        try {
        	String user = servletRequest.getRemoteUser();
        	List<AssetFilter> assetFilterList = assetFilterService.getAssetFilterList(user);
        	AssetFilterList assetFilterListresp = new AssetFilterList();
            Map<UUID, AssetFilter> filterMap = assetFilterList.stream().collect(Collectors.toMap(AssetFilter::getId, Function.identity()));
            assetFilterListresp.setSavedFilters(filterMap);
            String response =  jsonb.toJson(assetFilterListresp);
            
            return Response.ok(response).header("MDC", MDC.get(REQUSTID)).build();
        } catch (Exception e) {
            LOG.error("Error when retrieving assetFilter list {}", e);
            throw e;
        }
    }
}
