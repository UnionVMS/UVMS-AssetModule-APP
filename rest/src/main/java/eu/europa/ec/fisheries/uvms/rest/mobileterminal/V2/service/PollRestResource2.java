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
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.V2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollMapper;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

@Path("/poll2")
@Stateless
@Consumes(value = { MediaType.APPLICATION_JSON })
@Produces(value = { MediaType.APPLICATION_JSON })
public class PollRestResource2 {

    private final static Logger LOG = LoggerFactory.getLogger(PollRestResource2.class);

    @Inject
    private PollServiceBean pollServiceBean;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @Inject
    private MobileTerminalServiceBean mobileTerminalServiceBean;

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public Response createPoll(PollRequestType createPoll) {
        LOG.info("Create poll invoked in rest layer:{}",createPoll);
        try {
            CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(createPoll);
            return Response.ok(createPollResultDto).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll {}] {}",createPoll, ex.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @POST
    @Path("createPollForAsset/{assetId}")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public Response createPollForAsset(@PathParam("assetId") String assetId, @QueryParam("comment") String comment) {
        try {
            UUID asset = UUID.fromString(assetId);
            String username = request.getRemoteUser();
            return Response.ok(pollServiceBean.createPollForAsset(asset, PollType.MANUAL_POLL, username, comment)).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll for {}] {}",assetId, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @GET
    @Path("/running")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public Response getRunningProgramPolls() {
        LOG.info("Get running program polls invoked in rest layer");
        try {
            List<PollDto> polls = pollServiceBean.getRunningProgramPolls();
            return Response.ok(polls).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting running program polls ] {}", (Object) ex.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @GET
    @Path("{pollProgramId}/start/")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public Response startProgramPoll(@PathParam("pollProgramId") String pollId) {
        LOG.info("Start poll invoked in rest layer:{}",pollId);
        try {
            PollResponseType pollResponse = pollServiceBean.startProgramPoll(pollId, request.getRemoteUser());
            PollDto poll = PollMapper.mapPoll(pollResponse);
            return Response.ok(poll).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when starting program poll {}] {}", pollId, ex.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @GET
    @Path("{pollProgramId}/stop/")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public Response stopProgramPoll(@PathParam("pollProgramId") String pollId) {
        LOG.info("Stop poll invoked in rest layer:{}",pollId);
        try {
            PollResponseType pollResponse = pollServiceBean.stopProgramPoll(pollId, request.getRemoteUser());
            PollDto poll = PollMapper.mapPoll(pollResponse);
            return Response.ok(poll).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when stopping program poll {} ] {}",pollId, ex.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @GET
    @Path("{pollProgramId}/archive/")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public Response archiveProgramPoll(@PathParam("pollProgramId") String pollId) { // This gives a poll the status "ARCHIVED"
        LOG.info("Archive poll invoked in rest layer:{}",pollId);
        try {
            PollResponseType pollResponse = pollServiceBean.inactivateProgramPoll(pollId, request.getRemoteUser());
            PollDto poll = PollMapper.mapPoll(pollResponse);
            return Response.ok(poll).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when inactivating program poll {}] {}",pollId, ex.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @POST
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public Response getPollBySearchCriteria(PollListQuery query) {
        LOG.info("Get poll by search criteria invoked in rest layer:{}",query);
        try {
        	PollChannelListDto pollChannelList = pollServiceBean.getPollBySearchCriteria(query);
            return Response.ok(pollChannelList).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting poll by search criteria {}] {}",query, ex.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @POST
    @Path("/getPollable")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public Response getPollableChannels(PollableQuery query) {
        LOG.info("Get pollables invoked in rest layer:{}",query);
        try {
            PollChannelListDto pollChannelList = mobileTerminalServiceBean.getPollableMobileTerminal(query);
            return Response.ok(pollChannelList).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            LOG.error("[ Error when getting poll by search criteria {}] {}", query, ex.getStackTrace());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();
        }
    }

    @GET
    @Path("/program/{pollProgramId}")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public Response getPollProgram(@PathParam("pollProgramId") String pollProgramId) {
        try {
            PollProgram pollProgram = pollProgramDao.getPollProgramByGuid(pollProgramId);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String returnString = objectMapper.writeValueAsString(pollProgram);
            return Response.ok(returnString).header("MDC", MDC.get("requestId")).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionUtils.getRootCause(ex)).build();

        }
    }
}
