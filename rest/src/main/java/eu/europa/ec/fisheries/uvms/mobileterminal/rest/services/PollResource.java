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
package eu.europa.ec.fisheries.uvms.mobileterminal.rest.service;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ErrorHandler;
import eu.europa.ec.fisheries.uvms.mobileterminal.rest.error.ResponseCode;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MappedPollServiceBean;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/poll")
@Stateless
@Consumes(value = { MediaType.APPLICATION_JSON })
@Produces(value = { MediaType.APPLICATION_JSON })
public class PollResource {

    private final static Logger LOG = LoggerFactory.getLogger(PollResource.class);

    @EJB
    private MappedPollServiceBean pollService;

    @Context
    private HttpServletRequest request;

    /**
     * @responseMessage 200 Poll successfully created
     * @responseMessage 500 Error when creating poll
     * @summary Creates polls of different types
     */
    @POST
    @Path("/")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public ResponseDto<CreatePollResultDto> createPoll(PollRequestType createPoll) {
        LOG.info("Create poll invoked in rest layer:{}",createPoll);
        try {
            CreatePollResultDto createPollResultDto = pollService.createPoll(createPoll, request.getRemoteUser());
            return new ResponseDto<>(createPollResultDto, ResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll {}] {}",createPoll, ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }

    /**
     * @responseMessage 200 Running polls successfully retreived
     * @responseMessage 500 Error when retrieveing running polls
     *
     * @summary Gets all program polls that are currently active
     */
    @GET
    @Path("/running")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public ResponseDto<List<PollDto>> getRunningProgramPolls() {
        LOG.info("Get running program polls invoked in rest layer");
        try {
            List<PollDto> polls = pollService.getRunningProgramPolls();
            return new ResponseDto<>(polls, ResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting running program polls ] {}", (Object) ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }

    /**
     * @responseMessage 200 Poll program successfully started
     * @responseMessage 500 Error when starting the program poll
     *
     * @summary Starts a polling program
     */
    @PUT
    @Path("/start/{id}")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public ResponseDto<PollDto> startProgramPoll(@PathParam("id") String pollId) {
        LOG.info("Start poll invoked in rest layer:{}",pollId);
        try {
            PollDto poll = pollService.startProgramPoll(pollId, request.getRemoteUser());
            return new ResponseDto<>(poll, ResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when starting program poll {}] {}", pollId, ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }

    /**
     *
     * @responseMessage 200 Poll program successfully stopped
     * @responseMessage 500 Error when stopping the program poll
     *
     * @summary Stops a polling program
     */
    @PUT
    @Path("/stop/{id}")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public ResponseDto<PollDto> stopProgramPoll(@PathParam("id") String pollId) {
        LOG.info("Stop poll invoked in rest layer:{}",pollId);
        try {
            PollDto poll = pollService.stopProgramPoll(pollId, request.getRemoteUser());
            return new ResponseDto<>(poll, ResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when stopping program poll {} ] {}",pollId, ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }

    /**
     * @responseMessage 200 Poll program successfully inactivated
     * @responseMessage 500 Error when inactivating the program poll
     *
     * @summary Inactivates and Archives a polling program
     */
    @PUT
    @Path("/inactivate/{id}")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public ResponseDto<PollDto> inactivateProgramPoll(@PathParam("id") String pollId) {
        LOG.info("Stop poll invoked in rest layer:{}",pollId);
        try {
            PollDto poll = pollService.inactivateProgramPoll(pollId, request.getRemoteUser());
            return new ResponseDto<>(poll, ResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when inactivating program poll {}] {}",pollId, ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }

    /**
     *
     * @responseMessage 200 Polls successfully retreived
     * @responseMessage 500 Error when getting polls by6 search criteria
     *
     * @summary Search for a polling program by query
     */
    @POST
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public ResponseDto<PollChannelListDto> getPollBySearchCriteria(PollListQuery query) {
        LOG.info("Get poll by search criteria invoked in rest layer:{}",query);
        try {
        	PollChannelListDto pollChannelList = pollService.getPollBySearchQuery(query);
            return new ResponseDto<>(pollChannelList, ResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting poll by search criteria {}] {}",query, ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }

    /**
     * @responseMessage 200 Pollable channels successfully retrieved
     * @responseMessage 500 Error when retrieving pollable channels
     *
     * @summary Search for pollable InmarsatC channels
     */
    @POST
    @Path("/pollable")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public ResponseDto<PollChannelListDto> getPollableChannels(PollableQuery query) {
        LOG.info("Get pollables invoked in rest layer:{}",query);
        try {
            PollChannelListDto pollChannelList = pollService.getPollableChannels(query);
            return new ResponseDto<>(pollChannelList, ResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting poll by search criteria {}] {}", query, ex.getStackTrace());
            return ErrorHandler.getFault(ex);
        }
    }
}
