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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.*;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.MobileTerminalModelToEntityMapper;
import eu.europa.ec.fisheries.uvms.rest.asset.ObjectMapperContextResolver;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTResponseDto;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTErrorHandler;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/mobileterminal")
@Stateless
@Consumes(value = { MediaType.APPLICATION_JSON })
@Produces(value = { MediaType.APPLICATION_JSON })
public class MobileTerminalRestResource {


    private final static Logger LOG = LoggerFactory.getLogger(MobileTerminalRestResource.class);

    @EJB
    private MobileTerminalServiceBean mobileTerminalService;

    @Inject
    private MobileTerminalPluginDaoBean pluginDao;

    @Inject
    private AssetDao assetDao;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public MTResponseDto<MobileTerminalType> createMobileTerminal(MobileTerminalType mobileTerminalType) {
        LOG.info("Create mobile terminal invoked in rest layer.");
        LOG.info("MobileTerminalType: SHORT_PREFIX_STYLE", mobileTerminalType.toString());
        try {
            mobileTerminalType.setSource(MobileTerminalSource.INTERNAL);
            String serialNumber = mobileTerminalService.assertTerminalHasSerialNumber(mobileTerminalType);
            UUID id = null;
            if(mobileTerminalType.getMobileTerminalId() != null
                    && mobileTerminalType.getMobileTerminalId().getGuid() != null
                    && !mobileTerminalType.getMobileTerminalId().getGuid().isEmpty()) {
                id = UUID.fromString(mobileTerminalType.getMobileTerminalId().getGuid());
                mobileTerminalService.assertTerminalNotExists(id, serialNumber);
            }

            MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(mobileTerminalType.getPlugin().getServiceName());
            if(plugin == null){
                plugin = pluginDao.initAndGetPlugin(mobileTerminalType.getPlugin().getServiceName());
            }
            Asset asset = null;
            if(mobileTerminalType.getConnectId() != null){
                asset = assetDao.getAssetById(UUID.fromString(mobileTerminalType.getConnectId()));
            }
            MobileTerminal mobileTerminalEntity = MobileTerminalModelToEntityMapper.mapNewMobileTerminalEntity(mobileTerminalType, serialNumber, plugin, request.getRemoteUser());
            if(asset != null)
                mobileTerminalEntity.setAsset(asset);
            mobileTerminalEntity = mobileTerminalService.createMobileTerminal(mobileTerminalEntity, request.getRemoteUser());
//            if(asset != null) {
//                asset.getMobileTerminals().add(mobileTerminalEntity);
//                assetDao.updateAsset(asset);
//            }
            return new MTResponseDto<>(MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminalEntity), MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when creating mobile terminal ] {}", ex, ex.getStackTrace());

            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public MTResponseDto<MobileTerminalType> getMobileTerminalById(@PathParam("id") String mobileterminalId) {
        LOG.info("Get mobile terminal by id invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminalService.getMobileTerminalEntityById(UUID.fromString(mobileterminalId)));
            return new MTResponseDto<>(mobileTerminalType, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when creating mobile terminal ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    //needed since eager fetch is not supported by AuditQuery et al, so workaround is to serialize while we still have a DB session active
    private ObjectMapper objectMapper(){
        ObjectMapperContextResolver omcr = new ObjectMapperContextResolver();
        return omcr.getContext(Asset.class);
    }

    @GET
    @Path("/entity/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalEntityById(@PathParam("id") String mobileterminalId) {
        LOG.info("Get mobile terminal by id invoked in rest layer.");
        try {
            MobileTerminal mobileTerminal = mobileTerminalService.getMobileTerminalEntityById(UUID.fromString(mobileterminalId));
            String returnString = objectMapper().writeValueAsString(mobileTerminal);
            return Response.status(200).entity(returnString).type(MediaType.APPLICATION_JSON )
                    .header("MDC", MDC.get("requestId")).build();
        } catch (Exception e) {
            LOG.error("[ Error when creating mobile terminal ] {}", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @PUT
    @Path("/")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public MTResponseDto<MobileTerminalType> updateMobileTerminal(@QueryParam("comment") String comment, MobileTerminalType mobileTerminalType) {
        LOG.info("Update mobile terminal by id invoked in rest layer.");
        try {
            mobileTerminalType.setSource(MobileTerminalSource.INTERNAL);
            String serialNumber = mobileTerminalService.assertTerminalHasSerialNumber(mobileTerminalType);
            MobileTerminalPlugin plugin = pluginDao.getPluginByServiceName(mobileTerminalType.getPlugin().getServiceName());
            if(plugin == null){
                plugin = pluginDao.initAndGetPlugin(mobileTerminalType.getPlugin().getServiceName());
            }
            Asset asset = null;
            if(mobileTerminalType.getConnectId() != null){
                asset = assetDao.getAssetById(UUID.fromString(mobileTerminalType.getConnectId()));
            }
            MobileTerminal mobileTerminal = MobileTerminalModelToEntityMapper.mapMobileTerminalEntity(
                    mobileTerminalService.getMobileTerminalEntityById(mobileTerminalType.getMobileTerminalId()),
                    mobileTerminalType, serialNumber, plugin, request.getRemoteUser(), EventCodeEnum.MODIFY);
            if(asset != null)
                mobileTerminal.setAsset(asset);
            mobileTerminal = mobileTerminalService.updateMobileTerminal(mobileTerminal, comment, request.getRemoteUser());

            MobileTerminalType updatedMobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal);
            return new MTResponseDto<>(updatedMobileTerminalType , MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when updating mobile terminal ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    @POST
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public MTResponseDto<MobileTerminalListResponse> getMobileTerminalList(MobileTerminalListQuery query) {
        LOG.info("Get mobile terminal list invoked in rest layer.");
        try {
            return new MTResponseDto<>(mobileTerminalService.getMobileTerminalList(query), MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal list ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    @POST
    @Path("/assign")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public MTResponseDto<MobileTerminalType> assignMobileTerminal(@QueryParam("comment") String comment, MobileTerminalAssignQuery query) {
        LOG.info("Assign mobile terminal invoked in rest layer.");
        try {
            MobileTerminal mobileTerminal = mobileTerminalService.assignMobileTerminal(query, comment, request.getRemoteUser());
            MobileTerminalType mobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal);
            return new MTResponseDto<>(mobileTerminalType, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when assigning mobile terminal ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    @POST
    @Path("/unassign")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public MTResponseDto<MobileTerminalType> unAssignMobileTerminal(@QueryParam("comment") String comment, MobileTerminalAssignQuery query) {
        LOG.info("Unassign mobile terminal invoked in rest layer.");
        try {
            MobileTerminal mobileTerminal = mobileTerminalService.unAssignMobileTerminal(query, comment, request.getRemoteUser());
            MobileTerminalType mobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminal);
            return new MTResponseDto<>(mobileTerminalType, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when unassigning mobile terminal ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    @PUT
    @Path("/status/activate")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public MTResponseDto<MobileTerminalType> setStatusActive(@QueryParam("comment") String comment, MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status active invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.ACTIVE, request.getRemoteUser()));
            return new MTResponseDto<>(mobileTerminalType, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when activating mobile terminal ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    @PUT
    @Path("/status/inactivate")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public MTResponseDto<MobileTerminalType> setStatusInactive(@QueryParam("comment") String comment, MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status inactive invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.INACTIVE, request.getRemoteUser()));
            return new MTResponseDto<>(mobileTerminalType, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when inactivating mobile terminal ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    @PUT
    @Path("/status/remove")
    @RequiresFeature(UnionVMSFeature.manageMobileTerminals)
    public MTResponseDto<MobileTerminalType> setStatusRemoved(@QueryParam("comment") String comment, MobileTerminalId terminalId) {
        LOG.info("Set mobile terminal status removed invoked in rest layer.");
        try {
            MobileTerminalType mobileTerminalType = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(mobileTerminalService.setStatusMobileTerminal(terminalId, comment, MobileTerminalStatus.ARCHIVE, request.getRemoteUser()));
            return new MTResponseDto<>(mobileTerminalType, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when removing mobile terminal ] {}", ex);
            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/history/{id}")
    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
    public Response getMobileTerminalHistoryListByMobileTerminalId(@PathParam("id") String id) {
        LOG.info("Get mobile terminal history by mobile terminal id invoked in rest layer.");
        try {
            MobileTerminal mt = mobileTerminalService.getMobileTerminalEntityById(UUID.fromString(id));
            List<MobileTerminal> mobileTerminalRevisions = mobileTerminalService.getMobileTerminalRevisions(mt.getHistoryId());
            return Response.ok().entity(mobileTerminalRevisions).status(Response.Status.OK).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting mobile terminal history by terminalId ] {}", ex);
//            return MTErrorHandler.getFault(ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GET
//    @Path("/history/{id}")
//    @RequiresFeature(UnionVMSFeature.viewVesselsAndMobileTerminals)
//    public MTResponseDto<MobileTerminalHistory> getMobileTerminalHistoryListByMobileTerminalId(@PathParam("id") String guid) {
//        LOG.info("Get mobile terminal history by mobile terminal id invoked in rest layer.");
//        try {
//            return new MTResponseDto<>(mobileTerminalService.getMobileTerminalHistoryList(guid), MTResponseCode.OK);
//        } catch (Exception ex) {
//            LOG.error("[ Error when getting mobile terminal history by terminalId ] {}", ex);
//            return MTErrorHandler.getFault(ex);
//        }
//    }


}
