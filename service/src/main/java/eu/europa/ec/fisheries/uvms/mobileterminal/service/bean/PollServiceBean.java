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
package eu.europa.ec.fisheries.uvms.mobileterminal.service.bean;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.mobileterminal.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.MTMessageProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.message.event.ModuleQueue;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.PollService;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types.MobileTerminalTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.exception.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.mapper.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.search.poll.PollSearchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.*;

@Stateless
@LocalBean
public class PollServiceBean implements PollService {

    private final static Logger LOG = LoggerFactory.getLogger(PollServiceBean.class);

    @EJB
    private MTMessageProducer MTMessageProducer;

    @EJB
    private PluginServiceBean sendPollService;

    @EJB
    private MobileTerminalPollTimerServiceBean timerService;

    @EJB
    private PollDaoBean pollDao;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @EJB
    private TerminalDaoBean terminalDao;

    @EJB
    private ChannelDaoBean channelDao;


    public CreatePollResultDto createPoll(PollRequestType poll, String username) throws MobileTerminalServiceException {
        try {
            List<PollResponseType> createdPolls = createPolls(poll, username);

            boolean triggerTimer = false;
            List<String> unsentPolls = new ArrayList<>();
            List<String> sentPolls = new ArrayList<>();
            for (PollResponseType createdPoll : createdPolls) {
                triggerTimer = PollType.PROGRAM_POLL.equals(createdPoll.getPollType());

                AcknowledgeTypeType ack = sendPollService.sendPoll(createdPoll, username);
                switch (ack) {
                    case NOK:
                        unsentPolls.add(createdPoll.getPollId().getGuid());
                        break;
                    case OK:
                        sentPolls.add(createdPoll.getPollId().getGuid());
                        break;
                }

                try {
                    String auditData = AuditModuleRequestMapper.mapAuditLogPollCreated(createdPoll.getPollType(), createdPoll.getPollId().getGuid(), createdPoll.getComment(), username);
                    MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
                } catch (AuditModelMarshallException | MobileTerminalException e) {
                    LOG.error("Failed to send audit log message! Poll with guid {} was created", createdPoll.getPollId().getGuid());
                }
            }

            if (triggerTimer) {
                timerService.timerTimeout();
            }

            CreatePollResultDto result = new CreatePollResultDto();
            result.setSentPolls(sentPolls);
            result.setUnsentPolls(unsentPolls);
            result.setUnsentPoll(!unsentPolls.isEmpty());
            return result;
        } catch (MobileTerminalModelException e) {
            LOG.error("Failed to create poll", e);
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    public List<PollResponseType> getRunningProgramPolls()  {
            return getPollProgramList();
    }

    public PollResponseType startProgramPoll(String pollId, String username) throws MobileTerminalServiceException {
        try {
            PollId pollIdType = new PollId();
            pollIdType.setGuid(pollId);
            PollResponseType startedPoll = setStatusPollProgram(pollIdType, PollStatus.STARTED);
            try {
                String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollStarted(startedPoll.getPollId().getGuid(), username);
                MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
            } catch (AuditModelMarshallException | MobileTerminalException e) {
                LOG.error("Failed to send audit log message! Poll with guid {} was started", startedPoll.getPollId().getGuid());
            }

            return startedPoll;
        } catch (MobileTerminalModelException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    public PollResponseType stopProgramPoll(String pollId, String username) throws MobileTerminalServiceException {
        try {
            PollId pollIdType = new PollId();
            pollIdType.setGuid(pollId);
            PollResponseType stoppedPoll = setStatusPollProgram(pollIdType, PollStatus.STOPPED);
            try {
                String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollStopped(stoppedPoll.getPollId().getGuid(), username);
                MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
            } catch (AuditModelMarshallException | MobileTerminalException e) {
                LOG.error("Failed to send audit log message! Poll with guid {} was stopped", stoppedPoll.getPollId().getGuid());
            }

            return stoppedPoll;
        } catch (MobileTerminalModelException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    public PollResponseType inactivateProgramPoll(String pollId, String username) throws MobileTerminalServiceException {
        try {
            PollId pollIdType = new PollId();
            pollIdType.setGuid(pollId);
            PollResponseType inactivatedPoll = setStatusPollProgram(pollIdType, PollStatus.ARCHIVED);
            try {
                String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollInactivated(inactivatedPoll.getPollId().getGuid(), username);
                MTMessageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
            } catch (AuditModelMarshallException e) {
                LOG.error("Failed to send audit log message! Poll with guid {} was inactivated", inactivatedPoll.getPollId().getGuid());
            }

            return inactivatedPoll;
        } catch (MobileTerminalModelException  | MobileTerminalException e) {
            throw new MobileTerminalServiceException(e.getMessage());
        }
    }

    public PollListResponse getPollBySearchCriteria(PollListQuery query) {
        try {
            return getPollList(query);
        } catch (RuntimeException | SearchMapperException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<PollResponseType> timer() {

        return getPollProgramRunningAndStarted();
    }

    private MobileTerminalType mapPollableTerminalType(MobileTerminalTypeEnum type, String guid) {
        MobileTerminal terminal = terminalDao.getMobileTerminalByGuid(guid);
        return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal);
    }

    private MobileTerminalType getPollableTerminalType(String guid, String channelGuid) throws MobileTerminalModelException {
        MobileTerminal terminal = terminalDao.getMobileTerminalByGuid(guid);
        checkPollable(terminal);

        if (channelGuid != null && !channelGuid.isEmpty()) {
            for (Channel channel : terminal.getChannels()) {
                if (channel.getId().toString().equalsIgnoreCase(channelGuid)) {
                    if (!channel.getMobileTerminal().getId().toString().equalsIgnoreCase(guid)) {
                        throw new MobileTerminalModelException("Channel " + channel.getId() + " can not be polled, because it is not part of terminal " + terminal.getId());
                    }
                    return MobileTerminalEntityToModelMapper.mapToMobileTerminalType(terminal, channel);
                }
            }

        }
        throw new MobileTerminalModelException("Could not find channel " + channelGuid + " based on");
    }

    private void checkPollable(MobileTerminal terminal) throws MobileTerminalModelException {
        if (terminal.getArchived()) {
            throw new MobileTerminalModelException("Terminal is archived");
        }
        if (terminal.getInactivated()) {
            throw new MobileTerminalModelException("Terminal is inactive");
        }
        if (terminal.getPlugin() != null && terminal.getPlugin().getPluginInactive()) {
            throw new MobileTerminalModelException("Terminal connected to no longer active Plugin (LES)");
        }
    }

    public List<PollResponseType> createPolls(PollRequestType pollRequest, String username) throws MobileTerminalModelException {
        if (pollRequest == null || pollRequest.getPollType() == null) {
            throw new IllegalArgumentException("No polls to create");
        }

        if (pollRequest.getComment() == null || pollRequest.getUserName() == null) {
            throw new IllegalArgumentException("Cannot create without comment and user");
        }

        if (pollRequest.getMobileTerminals().isEmpty()) {
            throw new IllegalArgumentException("No mobile terminals for " + pollRequest.getPollType());
        }

        List<PollResponseType> responseList;
        Map<Poll, MobileTerminalType> pollMobileTerminalTypeMap;
        switch (pollRequest.getPollType()) {
            case PROGRAM_POLL:
                Map<PollProgram, MobileTerminalType> pollProgramMobileTerminalTypeMap = validateAndMapToProgramPolls(pollRequest, username);
                responseList = createPollPrograms(pollProgramMobileTerminalTypeMap, username);
                break;
            case CONFIGURATION_POLL:
            case MANUAL_POLL:
            case SAMPLING_POLL:
                pollMobileTerminalTypeMap = validateAndMapToPolls(pollRequest, username);
                responseList = createPolls(pollMobileTerminalTypeMap, pollRequest.getPollType());
                break;
            default:
                LOG.error("[ Could not decide poll type ] {}", pollRequest.getPollType());
                throw new IllegalArgumentException("Could not decide Poll Type when creating polls");
        }
        return responseList;
    }

    private Map<PollProgram, MobileTerminalType> validateAndMapToProgramPolls(PollRequestType pollRequest, String username) throws MobileTerminalModelException {
        Map<PollProgram, MobileTerminalType> map = new HashMap<>();

        for (PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            MobileTerminal mobileTerminalEntity = terminalDao.getMobileTerminalByGuid(pollTerminal.getMobileTerminalId());
            String connectId = mobileTerminalEntity.getCurrentEvent().getConnectId();
            if (!pollTerminal.getConnectId().equals(connectId)) {
                throw new MobileTerminalModelException("Terminal " + mobileTerminalEntity.getId() + " can not be polled, because it is not linked to asset " + connectId);
            }
            MobileTerminalType terminalType = getPollableTerminalType(pollTerminal.getMobileTerminalId(), pollTerminal.getComChannelId());
            PollProgram pollProgram = PollModelToEntityMapper.mapToProgramPoll(mobileTerminalEntity, connectId, pollTerminal.getComChannelId(), pollRequest, username);
            map.put(pollProgram, terminalType);
        }
        return map;
    }

    private Map<Poll, MobileTerminalType> validateAndMapToPolls(PollRequestType pollRequest, String username) throws MobileTerminalModelException {
        Map<Poll, MobileTerminalType> map = new HashMap<>();

        for (PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            MobileTerminal mobileTerminalEntity = terminalDao.getMobileTerminalByGuid(pollTerminal.getMobileTerminalId());
            String connectId = mobileTerminalEntity.getCurrentEvent().getConnectId();
            if (pollTerminal.getConnectId() == null || !pollTerminal.getConnectId().equals(connectId)) {
                throw new MobileTerminalModelException("Terminal " + mobileTerminalEntity.getId() + " can not be polled, because it is not linked to asset " + connectId);
            }

            if (pollRequest.getPollType() != PollType.MANUAL_POLL) {
                validateMobileTerminalPluginCapability(mobileTerminalEntity.getPlugin().getCapabilities(), pollRequest.getPollType(), mobileTerminalEntity.getPlugin().getPluginServiceName());
            }
            MobileTerminalType terminalType = getPollableTerminalType(pollTerminal.getMobileTerminalId(), pollTerminal.getComChannelId());
            Poll poll = PollModelToEntityMapper.mapToPoll(mobileTerminalEntity, connectId, pollTerminal.getComChannelId(), pollRequest, username);
            map.put(poll, terminalType);
        }
        return map;
    }

    private void validateMobileTerminalPluginCapability(Set<MobileTerminalPluginCapability> capabilities, PollType pollType, String pluginServiceName) throws MobileTerminalModelException {
        PluginCapabilityType pluginCapabilityType;
        switch (pollType) {
            case CONFIGURATION_POLL:
                pluginCapabilityType = PluginCapabilityType.CONFIGURABLE;
                break;
            case SAMPLING_POLL:
                pluginCapabilityType = PluginCapabilityType.SAMPLING;
                break;
            default:
                throw new MobileTerminalModelException("Cannot create " + pollType.name() + "  poll when plugin: " + pluginServiceName);
        }
        if (!validatePluginHasCapabilityConfigurable(capabilities, pluginCapabilityType)) {
            throw new MobileTerminalModelException("Cannot create " + pollType.name() + "  poll when plugin: " + pluginServiceName + " has not capability " + pluginCapabilityType.name() + " set");
        }
    }

    private boolean validatePluginHasCapabilityConfigurable(Set<MobileTerminalPluginCapability> capabilities, PluginCapabilityType pluginCapability) {
        for (MobileTerminalPluginCapability pluginCap : capabilities) {
            if (pluginCapability.name().equalsIgnoreCase(pluginCap.getName())) {
                return true;
            }
        }
        return false;
    }

    private List<PollResponseType> createPollPrograms(Map<PollProgram, MobileTerminalType> map, String username) {
        List<PollResponseType> responseList = new ArrayList<>();
        for (Map.Entry<PollProgram, MobileTerminalType> next : map.entrySet()) {
            PollProgram pollProgram = next.getKey();
            MobileTerminalType mobileTerminalType = next.getValue();
            pollProgramDao.createPollProgram(pollProgram);
            responseList.add(PollEntityToModelMapper.mapToPollResponseType(pollProgram, mobileTerminalType));
        }
        return responseList;
    }

    private List<PollResponseType> createPolls(Map<Poll, MobileTerminalType> map, PollType pollType) {
        List<PollResponseType> responseList = new ArrayList<>();
        for (Map.Entry<Poll, MobileTerminalType> next : map.entrySet()) {
            Poll poll = next.getKey();
            MobileTerminalType mobileTerminalType = next.getValue();
            pollDao.createPoll(poll);
            responseList.add(PollEntityToModelMapper.mapToPollResponseType(poll, mobileTerminalType, pollType));
        }
        return responseList;
    }

    public PollListResponse getPollList(PollListQuery query) throws SearchMapperException {
        if (query == null) {
            throw new NullPointerException("Cannot get poll list because no query.");
        }

        if (query.getPagination() == null) {
            throw new NullPointerException("Cannot get poll list because no list pagination.");
        }

        if (query.getPollSearchCriteria() == null || query.getPollSearchCriteria().getCriterias() == null) {
            throw new NullPointerException("Cannot get poll list because criteria are null.");
        }
        PollListResponse response = new PollListResponse();
        List<PollResponseType> pollResponseList = new ArrayList<>();

        Integer page = query.getPagination().getPage();
        Integer listSize = query.getPagination().getListSize();
        boolean isDynamic = query.getPollSearchCriteria().isIsDynamic();
        List<PollSearchKeyValue> searchKeys = PollSearchMapper.createSearchFields(query.getPollSearchCriteria().getCriterias());

        String countSql = PollSearchMapper.createCountSearchSql(searchKeys, isDynamic);
        String sql = PollSearchMapper.createSelectSearchSql(searchKeys, isDynamic);

        Long numberMatches = pollDao.getPollListSearchCount(countSql, searchKeys);
        List<Poll> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, searchKeys);

        for (Poll poll : pollList) {
            try {
                MobileTerminal mobileTerminalEntity = poll.getPollBase().getMobileterminal();
                MobileTerminalType mobileTerminalType = mapPollableTerminalType(mobileTerminalEntity.getMobileTerminalType(), mobileTerminalEntity.getId().toString());
                PollResponseType pollType = PollEntityToModelMapper.mapToPollResponseType(poll, mobileTerminalType, EnumMapper.getPollModelFromType(poll.getPollType()));
                pollResponseList.add(pollType);
            } catch (RuntimeException e) {
                LOG.error("[ Poll " + poll.getId() + "  couldn't map type ]");
                throw new RuntimeException(e);
            }
        }

        int numberOfPages = (int) (numberMatches / listSize);
        if (numberMatches % listSize != 0) {
            numberOfPages += 1;
        }

        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(query.getPagination().getPage());
        response.getPollList().addAll(pollResponseList);
        return response;
    }

    public List<PollResponseType> getPollProgramList()  {
        List<PollProgram> pollPrograms = pollProgramDao.getProgramPollsAlive();
        return getResponseList(pollPrograms);
    }


    public List<PollResponseType> getPollProgramRunningAndStarted() {
        List<PollProgram> pollPrograms = pollProgramDao.getPollProgramRunningAndStarted();
        return getResponseList(pollPrograms);
    }

    public PollResponseType setStatusPollProgram(PollId id, PollStatus state) throws MobileTerminalModelException {
        if (id == null || id.getGuid() == null || id.getGuid().isEmpty()) {
            throw new IllegalArgumentException("No poll id given");
        }
        if (state == null) {
            throw new IllegalArgumentException("No status to set");
        }

        try {
            PollProgram program = pollProgramDao.getPollProgramByGuid(id.getGuid());
            MobileTerminal terminal = program.getPollBase().getMobileterminal();
            MobileTerminalType terminalType = mapPollableTerminalType(terminal.getMobileTerminalType(), terminal.getId().toString());

            switch (program.getPollState()) {
                case ARCHIVED:
                    throw new MobileTerminalModelException("Can not change status of archived program poll, id: [ " + id.getGuid() + " ]");
                case STARTED:
                case STOPPED:
            }

            // TODO: check terminal/comchannel?

            program.setPollState(EnumMapper.getPollStateTypeFromModel(state));

            return PollEntityToModelMapper.mapToPollResponseType(program, terminalType);
        } catch (MobileTerminalModelException e) {
            LOG.error("[ Error when setting poll program status. ] {}", e.getMessage());
            throw new MobileTerminalModelException(e.getMessage());
        }
    }

    public ListResponseDto getMobileTerminalPollableList(PollableQuery query) {
        if (query == null) {
            throw new NullPointerException("No query");
        }

        if (query.getPagination() == null) {
            throw new NullPointerException("No list pagination");
        }

        ListResponseDto response = new ListResponseDto();
        List<MobileTerminalType> mobileTerminalList = new ArrayList<>();

        Integer page = query.getPagination().getPage();
        Integer listSize = query.getPagination().getListSize();
        int startIndex = (page - 1) * listSize;
        int stopIndex = startIndex + listSize;
        LOG.debug("page: " + page + ", listSize: " + listSize + ", startIndex: " + startIndex);

        List<String> idList = query.getConnectIdList();
        long in = System.currentTimeMillis();

        //Long numberMatches = channelInmarsatCDao.getPollableListSearchCount(countSql, idList);
        List<Channel> channels = channelDao.getPollableListSearch(idList);

        for (Channel comchannel : channels) {
            //TODO slim response from Pollable
            MobileTerminalType terminal = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(comchannel.getMobileTerminal(), comchannel);
            mobileTerminalList.add(terminal);
        }

        int numberMatches = mobileTerminalList.size();

        int numberOfPages = (numberMatches / listSize);
        if (numberMatches % listSize != 0) {
            numberOfPages += 1;
        }

        if ((numberMatches - 1) <= 0) {
            response.setMobileTerminalList(mobileTerminalList);
        } else {
            if (stopIndex >= numberMatches) {
                stopIndex = numberMatches;
            }
            LOG.debug("stopIndex: " + stopIndex);
            List<MobileTerminalType> newList = new ArrayList<>(mobileTerminalList.subList(startIndex, stopIndex));
            response.setMobileTerminalList(newList);
        }

        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(query.getPagination().getPage());

        long out = System.currentTimeMillis();
        LOG.debug("Get pollable channels " + (out - in) + " ms");
        return response;
    }

    private List<PollResponseType> getResponseList(List<PollProgram> pollPrograms)  {
        List<PollResponseType> responseList = new ArrayList<>();
        for (PollProgram pollProgram : pollPrograms) {
                MobileTerminal terminal = pollProgram.getPollBase().getMobileterminal();
            MobileTerminalType terminalType = null;
            terminalType = mapPollableTerminalType(terminal.getMobileTerminalType(), terminal.getId().toString());
            responseList.add(PollEntityToModelMapper.mapToPollResponseType(pollProgram, terminalType));
        }
        return responseList;
    }
}
