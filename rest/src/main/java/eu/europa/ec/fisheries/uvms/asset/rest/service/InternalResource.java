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
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import eu.europa.ec.fisheries.uvms.asset.domain.constant.AssetIdentifier;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.domain.mapper.SearchKeyValue;
import eu.europa.ec.fisheries.uvms.asset.rest.dto.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.rest.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetGroupService;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.asset.service.dto.AssetListResponse;

@Path("internal")
@Stateless
public class InternalResource {

    @Inject
    private AssetService assetService;
    
    @Inject
    private AssetGroupService assetGroupService;
    
    @GET
    @Path("asset/{idType : (guid|cfr|ircs|imo|mmsi|iccat|uvi|gfcm)}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetById(@PathParam("idType") String type, @PathParam("id") String id) {
        AssetIdentifier assetId = AssetIdentifier.valueOf(type.toUpperCase());
        Asset asset = assetService.getAssetById(assetId, id);
        return Response.ok(asset).build();
    }
    
    @POST
    @Path("query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetList(@DefaultValue("1") @QueryParam("page") int page,
                                 @DefaultValue("100") @QueryParam("size") int size,
                                 @DefaultValue("true") @QueryParam("dynamic") boolean dynamic,
                                 AssetQuery query) {
        List<SearchKeyValue> searchFields = SearchFieldMapper.createSearchFields(query);
        AssetListResponse assetList = assetService.getAssetList(searchFields, page, size, dynamic);
        return Response.ok(assetList).build();
    }
    
    @GET
    @Path("group/user/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetGroupByUser(@PathParam("user") String user) {
        List<AssetGroup> assetGroups = assetGroupService.getAssetGroupList(user);
        return Response.ok(assetGroups).build();
    }
    
    @GET
    @Path("group/asset/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetGroupByUser(@PathParam("id") UUID assetId) {
        List<AssetGroup> assetGroups = assetGroupService.getAssetGroupListByAssetId(assetId);
        return Response.ok(assetGroups).build();
    }
    
    @GET
    @Path("asset/group/{ids}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssetByGroupIds(@PathParam("ids") List<UUID> groupIds) {
        List<AssetGroup> assetGroups = groupIds.stream()
                                            .map(assetGroupService::getAssetGroupById)
                                            .collect(Collectors.toList());
        List<Asset> assets = assetService.getAssetListByAssetGroups(assetGroups);
        return Response.ok(assets).build();
    }
    
    @POST
    @Path("asset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response upsertAsset(Asset asset) {
        Asset upsertedAsset = assetService.upsertAsset(asset, "Internal REST resource");
        return Response.ok(upsertedAsset).build();
    }
    
    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {
        return Response.ok("pong").build();
    }
}
