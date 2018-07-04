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
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollRequestType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollResponseType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.MobileTerminalServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.PollServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.PollProgram;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.PollMapper;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.dto.MTResponseDto;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTErrorHandler;
import eu.europa.ec.fisheries.uvms.rest.mobileterminal.error.MTResponseCode;
import eu.europa.ec.fisheries.uvms.rest.security.RequiresFeature;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/poll")
@Stateless
@Consumes(value = { MediaType.APPLICATION_JSON })
@Produces(value = { MediaType.APPLICATION_JSON })
public class PollRestResource {

    private final static Logger LOG = LoggerFactory.getLogger(PollRestResource.class);


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
    public MTResponseDto<CreatePollResultDto> createPoll(PollRequestType createPoll) {
        LOG.info("Create poll invoked in rest layer:{}",createPoll);
        try {
            CreatePollResultDto createPollResultDto = pollServiceBean.createPoll(createPoll, request.getRemoteUser());
            return new MTResponseDto<>(createPollResultDto, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when creating poll {}] {}",createPoll, ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/running")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public MTResponseDto<List<PollDto>> getRunningProgramPolls() {
        LOG.info("Get running program polls invoked in rest layer");
        try {
            List<PollDto> polls = pollServiceBean.getRunningProgramPolls();
            return new MTResponseDto<>(polls, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting running program polls ] {}", (Object) ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/start/{id}")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public MTResponseDto<PollDto> startProgramPoll(@PathParam("id") String pollId) {
        LOG.info("Start poll invoked in rest layer:{}",pollId);
        try {
            PollResponseType pollResponse = pollServiceBean.startProgramPoll(pollId, request.getRemoteUser());
            PollDto poll = PollMapper.mapPoll(pollResponse);
            return new MTResponseDto<>(poll, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when starting program poll {}] {}", pollId, ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/stop/{id}")
    @RequiresFeature(UnionVMSFeature.managePolls)
    public MTResponseDto<PollDto> stopProgramPoll(@PathParam("id") String pollId) {
        LOG.info("Stop poll invoked in rest layer:{}",pollId);
        try {
            //PollDto poll = pollService.stopProgramPoll(pollId, request.getRemoteUser());
            PollResponseType pollResponse = pollServiceBean.stopProgramPoll(pollId, request.getRemoteUser());
            PollDto poll = PollMapper.mapPoll(pollResponse);
            return new MTResponseDto<>(poll, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when stopping program poll {} ] {}",pollId, ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @GET
    @Path("/inactivate/{id}")                       //TODO: change this to /archive/{id} when we have a moment to change it in frontend
    @RequiresFeature(UnionVMSFeature.managePolls)
    public MTResponseDto<PollDto> archiveProgramPoll(@PathParam("id") String pollId) {       //This gives a poll the status "ARCHIVED"
        LOG.info("Archive poll invoked in rest layer:{}",pollId);
        try {
            //PollDto poll = pollService.inactivateProgramPoll(pollId, request.getRemoteUser());
            PollResponseType pollResponse = pollServiceBean.inactivateProgramPoll(pollId, request.getRemoteUser());
            PollDto poll = PollMapper.mapPoll(pollResponse);
            return new MTResponseDto<>(poll, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when inactivating program poll {}] {}",pollId, ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @POST
    @Path("/list")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public MTResponseDto<PollChannelListDto> getPollBySearchCriteria(PollListQuery query) {
        LOG.info("Get poll by search criteria invoked in rest layer:{}",query);
        try {
        	PollChannelListDto pollChannelList = pollServiceBean.getPollBySearchCriteria(query);
            return new MTResponseDto<>(pollChannelList, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting poll by search criteria {}] {}",query, ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }

    @POST
    @Path("/pollable")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public MTResponseDto<PollChannelListDto> getPollableChannels(PollableQuery query) {
        LOG.info("Get pollables invoked in rest layer:{}",query);
        try {
            PollChannelListDto pollChannelList = mobileTerminalServiceBean.getPollableMobileTerminal(query);
            return new MTResponseDto<>(pollChannelList, MTResponseCode.OK);
        } catch (Exception ex) {
            LOG.error("[ Error when getting poll by search criteria {}] {}", query, ex.getStackTrace());
            return MTErrorHandler.getFault(ex);
        }
    }


    @GET
    @Path("/program/{id}")
    @RequiresFeature(UnionVMSFeature.viewMobileTerminalPolls)
    public MTResponseDto<String> getPollProgram(@PathParam("id") String pollProgramId) {
        try {
            PollProgram pollProgram = pollProgramDao.getPollProgramByGuid(pollProgramId);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            String json = objectMapper.writeValueAsString(pollProgram);
            return new MTResponseDto<>(json, MTResponseCode.OK);
        } catch (Exception e) {

            return MTErrorHandler.getFault(e);

        }
    }


}
