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
package eu.europa.ec.fisheries.uvms.mobileterminal.bean;

import eu.europa.ec.fisheries.schema.exchange.common.v1.AcknowledgeTypeType;
import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.*;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginCapabilityType;
import eu.europa.ec.fisheries.uvms.asset.bean.AssetServiceBean;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.message.AuditProducer;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.PollProgramDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollTypeEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.*;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.SimpleCreatePoll;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.PollSearchKeyValue;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.poll.PollSearchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Stateless
@LocalBean
public class PollServiceBean {

    private static final String FAILED_TO_SEND_AUDIT_LOG = "Failed to send audit log message due tue: ";

    private static final Logger LOG = LoggerFactory.getLogger(PollServiceBean.class);

    @Inject
    private AuditProducer auditProducer;

    @Inject
    private MobileTerminalServiceBean mobileTerminalServiceBean;

    @EJB
    private PluginServiceBean sendPollService;

    @EJB
    private PollDaoBean pollDao;

    @EJB
    private PollProgramDaoBean pollProgramDao;

    @EJB
    private TerminalDaoBean terminalDao;

    @EJB
    private ChannelDaoBean channelDao;

    @Inject
    private AssetServiceBean assetServiceBean;

    public CreatePollResultDto createPollForAsset(UUID assetId, SimpleCreatePoll createPoll, String username){
        MobileTerminal mt = mobileTerminalServiceBean.getActiveMTForAsset(assetId);

        if(mt == null) {
            Asset assetById = assetServiceBean.getAssetById(assetId);
            if(assetById == null) {
                throw new IllegalArgumentException("No asset with id: " + assetId + " found, unable to poll");
            }
            throw new IllegalArgumentException("No active MT for asset: " + assetById.getName() + " (" + assetById.getIrcs() + ") , unable to poll");
        }
        Channel channel = mobileTerminalServiceBean.getPollableChannel(mt);
        if(channel == null) {
            throw new IllegalArgumentException("No pollable channel for this active MT: " + mt.getSerialNo() + " , unable to poll");
        }

        PollRequestType prt = buildPollRequest(createPoll, username, mt, channel);

        return createPoll(prt);
    }

    private PollRequestType buildPollRequest(SimpleCreatePoll createPoll, String username,
                                             MobileTerminal mt, Channel channel) {
        PollRequestType prt = new PollRequestType();
        prt.setPollType(createPoll.getPollType() != null ? createPoll.getPollType() : PollType.MANUAL_POLL);
        PollMobileTerminal pollMobileTerminal = new PollMobileTerminal();
        pollMobileTerminal.setMobileTerminalId(mt.getId().toString());
        pollMobileTerminal.setComChannelId(channel.getId().toString());
        prt.setUserName(username);
        prt.setComment(createPoll.getComment());
        prt.getMobileTerminals().add(pollMobileTerminal);

        setPollRequestAttributes(prt, createPoll);

        return prt;
    }

    private void setPollRequestAttributes(PollRequestType pollRequest, SimpleCreatePoll createPoll){
        switch (createPoll.getPollType()){
            case PROGRAM_POLL:
                pollRequest.getAttributes().add(createPollAttribute(PollAttributeType.FREQUENCY, "" + createPoll.getFrequency()));
                pollRequest.getAttributes().add(createPollAttribute(PollAttributeType.START_DATE, "" + createPoll.getStartDate().toEpochMilli()));
                pollRequest.getAttributes().add(createPollAttribute(PollAttributeType.END_DATE, "" + createPoll.getEndDate().toEpochMilli()));
                break;
            default:
        }
    }

    private PollAttribute createPollAttribute(PollAttributeType key, String value){
        PollAttribute attribute = new PollAttribute();
        attribute.setKey(key);
        attribute.setValue(value);
        return attribute;
    }

    public CreatePollResultDto createPoll(PollRequestType poll) {

        List<PollResponseType> createdPolls = validateAndCreatePolls(poll);

        List<String> unsentPolls = new ArrayList<>();
        List<String> sentPolls = new ArrayList<>();
        for (PollResponseType createdPoll : createdPolls) {
            if (PollType.PROGRAM_POLL.equals(createdPoll.getPollType())) {
                unsentPolls.add(createdPoll.getPollId().getGuid());
            } else {
                AcknowledgeTypeType ack = sendPollService.sendPoll(createdPoll);
                if (ack == AcknowledgeTypeType.NOK ) {
                    unsentPolls.add(createdPoll.getPollId().getGuid());
                } else if (ack == AcknowledgeTypeType.OK) {
                    sentPolls.add(createdPoll.getPollId().getGuid());
                }
            }
            try {
                String auditData = AuditModuleRequestMapper.mapAuditLogPollCreated(createdPoll.getPollType(), createdPoll.getPollId().getGuid(), createdPoll.getComment(), createdPoll.getUserName());
                auditProducer.sendModuleMessage(auditData);
            } catch (Exception e) {
                LOG.error("Failed to send audit log message! Poll with guid {} was created", createdPoll.getPollId().getGuid());
            }
        }

        CreatePollResultDto result = new CreatePollResultDto();
        result.setSentPolls(sentPolls);
        result.setUnsentPolls(unsentPolls);
        result.setUnsentPoll(!unsentPolls.isEmpty());
        return result;
    }

    public List<PollDto> getRunningProgramPolls() {
        List<ProgramPoll> pollPrograms = pollProgramDao.getProgramPollsAlive();
        List<PollResponseType> pollResponse = getResponseList(pollPrograms);

        return PollDtoMapper.mapPolls(pollResponse);
    }

    public ProgramPoll startProgramPoll(String pollId, String username) {

        PollId pollIdType = new PollId();
        pollIdType.setGuid(pollId);
        ProgramPoll startedPoll = setStatusPollProgram(pollIdType, ProgramPollStatus.STARTED);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollStarted(startedPoll.getId().toString(), username);
            auditProducer.sendModuleMessage(auditData);
        } catch (Exception e) {
            LOG.error(FAILED_TO_SEND_AUDIT_LOG + e + "! Poll with guid {} was started", startedPoll.getId().toString());
        }
        return startedPoll;
    }

    public ProgramPoll stopProgramPoll(String pollId, String username){

        PollId pollIdType = new PollId();
        pollIdType.setGuid(pollId);
        ProgramPoll stoppedPoll = setStatusPollProgram(pollIdType, ProgramPollStatus.STOPPED);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollStopped(stoppedPoll.getId().toString(), username);
            auditProducer.sendModuleMessage(auditData);
        } catch (Exception e) {
            LOG.error(FAILED_TO_SEND_AUDIT_LOG + e + "! Poll with guid {} was stopped", stoppedPoll.getId().toString());
        }
        return stoppedPoll;
    }

    public ProgramPoll inactivateProgramPoll(String pollId, String username){

        PollId pollIdType = new PollId();
        pollIdType.setGuid(pollId);
        ProgramPoll inactivatedPoll = setStatusPollProgram(pollIdType, ProgramPollStatus.ARCHIVED);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogProgramPollInactivated(inactivatedPoll.getId().toString(), username);
            auditProducer.sendModuleMessage(auditData);
        } catch (Exception e) {
            LOG.error(FAILED_TO_SEND_AUDIT_LOG + e + "! Poll with guid {} was inactivated", inactivatedPoll.getId().toString());
        }
        return inactivatedPoll;
    }

    public PollChannelListDto getPollBySearchCriteria(PollListQuery query) {
        PollListResponse pollResponse = getPollList(query);
        return PollDtoMapper.pollListResponseToPollChannelListDto(pollResponse);
    }

    private MobileTerminal getMobileTerminalById(UUID guid) {
        return terminalDao.getMobileTerminalById(guid);
    }

    private List<PollResponseType> validateAndCreatePolls(PollRequestType pollRequest) {
        validatePollRequest(pollRequest);
        List<PollResponseType> responseList;
        switch (pollRequest.getPollType()) {
            case PROGRAM_POLL:
                Map<ProgramPoll, MobileTerminal> programPollMap = validateAndMapToProgramPolls(pollRequest);
                responseList = createProgramPolls(programPollMap);
                break;
            case CONFIGURATION_POLL:
                Map<ConfigurationPoll, MobileTerminal> configurationPollMap = validateAndMapToConfigurationPolls(pollRequest);
                responseList = createPolls(configurationPollMap);
                break;
            case MANUAL_POLL:
            case AUTOMATIC_POLL:
                Map<PollBase, MobileTerminal> basePollMap = validateAndMapToBasePolls(pollRequest);
                responseList = createPolls(basePollMap);
                break;
            case SAMPLING_POLL:
                Map<SamplingPoll, MobileTerminal> samplingPollMap = validateAndMapToSamplingPolls(pollRequest);
                responseList = createPolls(samplingPollMap);
                break;
            default:
                LOG.error("[ Could not decide poll type ] {}", pollRequest.getPollType());
                throw new IllegalArgumentException("Could not decide Poll Type when creating polls");
        }
        return responseList;
    }

    private Map<PollBase, MobileTerminal> validateAndMapToBasePolls(PollRequestType pollRequest) {
        Map<PollBase, MobileTerminal> map = new HashMap<>();
        for (PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            MobileTerminal mobileTerminalEntity = validateAndGetMobileTerminal(pollTerminal);
            checkPollable(mobileTerminalEntity);
            PollBase poll = PollModelToEntityMapper.createPoll(mobileTerminalEntity, pollTerminal.getComChannelId(), pollRequest, PollBase.class);
            map.put(poll, mobileTerminalEntity);
        }
        return map;
    }

    private Map<SamplingPoll, MobileTerminal> validateAndMapToSamplingPolls(PollRequestType pollRequest) {
        Map<SamplingPoll, MobileTerminal> map = new HashMap<>();

        for (PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            MobileTerminal mobileTerminalEntity = validateAndGetMobileTerminal(pollTerminal);
            validateMobileTerminalPluginCapability(mobileTerminalEntity.getPlugin().getCapabilities(), pollRequest.getPollType(), mobileTerminalEntity.getPlugin().getPluginServiceName());
            checkPollable(mobileTerminalEntity);
            SamplingPoll poll = PollModelToEntityMapper.mapToSamplingPoll(mobileTerminalEntity, pollTerminal.getComChannelId(), pollRequest);
            map.put(poll, mobileTerminalEntity);
        }
        return map;
    }

    private Map<ConfigurationPoll, MobileTerminal> validateAndMapToConfigurationPolls(PollRequestType pollRequest) {
        Map<ConfigurationPoll, MobileTerminal> map = new HashMap<>();

        for (PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            MobileTerminal mobileTerminalEntity = validateAndGetMobileTerminal(pollTerminal);

            validateMobileTerminalPluginCapability(mobileTerminalEntity.getPlugin().getCapabilities(), pollRequest.getPollType(), mobileTerminalEntity.getPlugin().getPluginServiceName());
            checkPollable(mobileTerminalEntity);
            ConfigurationPoll poll = PollModelToEntityMapper.mapToConfigurationPoll(mobileTerminalEntity, pollTerminal.getComChannelId(), pollRequest);
            map.put(poll, mobileTerminalEntity);
        }
        return map;
    }

    private MobileTerminal validateAndGetMobileTerminal(PollMobileTerminal pollTerminal) {
        MobileTerminal mobileTerminalEntity = terminalDao.getMobileTerminalById(UUID.fromString(pollTerminal.getMobileTerminalId()));
        if (mobileTerminalEntity == null) {
            throw new IllegalArgumentException("No mobile terminal connected to this poll request or the mobile terminal can not be found, for mobile terminal id: " + pollTerminal.getMobileTerminalId());
        }
        if(mobileTerminalEntity.getAsset() == null){
            throw new IllegalArgumentException("This mobile terminal is not connected to any asset");
        }
        return mobileTerminalEntity;
    }

    private void validatePollRequest(PollRequestType pollRequest) {
        if (pollRequest == null || pollRequest.getPollType() == null) {
            throw new IllegalArgumentException("No polls to create");
        }
        if (pollRequest.getUserName() == null || pollRequest.getUserName().isEmpty()) {
            throw new IllegalArgumentException("Cannot create poll without a user");
        }
        if (pollRequest.getComment() == null || pollRequest.getComment().isEmpty()) {
            throw new IllegalArgumentException("Cannot create poll without a comment");
        }
        if (pollRequest.getMobileTerminals().isEmpty()) {
            throw new IllegalArgumentException("No mobile terminals for " + pollRequest.getPollType());
        }
    }

    private Map<ProgramPoll, MobileTerminal> validateAndMapToProgramPolls(PollRequestType pollRequest) {
        Map<ProgramPoll, MobileTerminal> map = new HashMap<>();

        for (PollMobileTerminal pollTerminal : pollRequest.getMobileTerminals()) {
            MobileTerminal mobileTerminalEntity = terminalDao.getMobileTerminalById(UUID.fromString(pollTerminal.getMobileTerminalId()));
            if(mobileTerminalEntity == null){
                throw new IllegalArgumentException("No mobile terminal connected to this poll request or the mobile terminal can not be found, for mobile terminal id: " + pollTerminal.getMobileTerminalId());
            }
            if(mobileTerminalEntity.getAsset() == null){
                throw new IllegalArgumentException("This mobile terminal is not connected to any asset");
            }
            checkPollable(mobileTerminalEntity);
            ProgramPoll programPoll = PollModelToEntityMapper.mapToProgramPoll(mobileTerminalEntity, pollTerminal.getComChannelId(), pollRequest);
            map.put(programPoll, mobileTerminalEntity);
        }
        return map;
    }

    private void checkPollable(MobileTerminal terminal){
        if (terminal.getArchived()) {
            throw new IllegalStateException("Terminal is archived");
        }
        if (!terminal.getActive()) {
            throw new IllegalStateException("Terminal is inactive");
        }
        if (terminal.getPlugin() != null && terminal.getPlugin().getPluginInactive()) {
            throw new IllegalStateException("Terminal connected to no longer active Plugin (LES)");
        }
    }

    private void validateMobileTerminalPluginCapability (Set<MobileTerminalPluginCapability> capabilities, PollType pollType, String pluginServiceName) {
        PluginCapabilityType pluginCapabilityType;
        switch (pollType) {
            case CONFIGURATION_POLL:
                pluginCapabilityType = PluginCapabilityType.CONFIGURABLE;
                break;
            case SAMPLING_POLL:
                pluginCapabilityType = PluginCapabilityType.SAMPLING;
                break;
            default:
                throw new IllegalArgumentException("Cannot create " + pollType.name() + "  poll when plugin: " + pluginServiceName);
        }
        if (!validatePluginHasCapabilityConfigurable(capabilities, pluginCapabilityType)) {
            throw new IllegalArgumentException("Cannot create " + pollType.name() + "  poll when plugin: " + pluginServiceName + " has not capability " + pluginCapabilityType.name() + " set");
        }
    }

    private boolean validatePluginHasCapabilityConfigurable (Set<MobileTerminalPluginCapability> capabilities, PluginCapabilityType pluginCapability) {
        for (MobileTerminalPluginCapability pluginCap : capabilities) {
            if (pluginCapability.name().equalsIgnoreCase(pluginCap.getName())) {
                return true;
            }
        }
        return false;
    }

    private List<PollResponseType> createProgramPolls(Map<ProgramPoll, MobileTerminal> map) {
        List<PollResponseType> responseList = new ArrayList<>();
        for (Map.Entry<ProgramPoll, MobileTerminal> next : map.entrySet()) {
            ProgramPoll pollProgram = next.getKey();
            pollProgramDao.createProgramPoll(pollProgram);
            responseList.add(PollEntityToModelMapper.mapToPollResponseType(pollProgram));
        }
        return responseList;
    }

    private List<PollResponseType> createPolls(Map<? extends PollBase, MobileTerminal> map) {
        List<PollResponseType> responseList = new ArrayList<>();
        for (Map.Entry<? extends PollBase, MobileTerminal> next : map.entrySet()) {
            MobileTerminal mobileTerminal = next.getValue();
            PollResponseType pollResponseType;
            switch (next.getKey().getPollTypeEnum()) {
                case SAMPLING_POLL:
                    SamplingPoll samplingPoll = (SamplingPoll) next.getKey();
                    pollDao.createPoll(samplingPoll);
                    pollResponseType = PollEntityToModelMapper.mapToPollResponseType(samplingPoll, mobileTerminal);
                    responseList.add(pollResponseType);
                    break;
                case MANUAL_POLL:
                case AUTOMATIC_POLL:
                    PollBase pollBase = next.getKey();
                    pollDao.createPoll(pollBase);
                    pollResponseType = PollEntityToModelMapper.mapToPollResponseType(pollBase, mobileTerminal);
                    responseList.add(pollResponseType);
                    break;
                case CONFIGURATION_POLL:
                    ConfigurationPoll configurationPoll = (ConfigurationPoll) next.getKey();
                    pollDao.createPoll(configurationPoll);
                    pollResponseType = PollEntityToModelMapper.mapToPollResponseType(configurationPoll, mobileTerminal);
                    responseList.add(pollResponseType);
                    break;
                default:
                    throw new RuntimeException("Invalid Poll Type. Poll not created!");
            }
        }
        return responseList;
    }

    public PollListResponse getPollList(PollListQuery query) {
        validatePollListQuery(query);
        PollListResponse response = new PollListResponse();
        List<PollResponseType> pollResponseList = new ArrayList<>();

        Integer page = query.getPagination().getPage();
        Integer listSize = query.getPagination().getListSize();
        boolean isDynamic = query.getPollSearchCriteria().isIsDynamic();
        List<PollSearchKeyValue> searchKeys = PollSearchMapper.createSearchFields(query.getPollSearchCriteria().getCriterias());

        PollTypeEnum pollTypeEnum = getPollTypeFromQuery(query);

        String countSql = PollSearchMapper.createCountSearchSql(searchKeys, isDynamic, pollTypeEnum);
        String sql = PollSearchMapper.createSelectSearchSql(searchKeys, isDynamic, pollTypeEnum);

        Long numberMatches = pollDao.getPollListSearchCount(countSql, searchKeys);
        List<PollBase> pollList = pollDao.getPollListSearchPaginated(page, listSize, sql, searchKeys);

        for (PollBase poll : pollList) {
            try {
                MobileTerminal mobileTerminalEntity = poll.getMobileterminal();
                MobileTerminal mobileTerminal = getMobileTerminalById(mobileTerminalEntity.getId());

                PollResponseType pollType = PollEntityToModelMapper.mapToPollResponseType(poll, mobileTerminal);
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

    private PollTypeEnum getPollTypeFromQuery(PollListQuery query) {
        Optional<ListCriteria> optional = query.getPollSearchCriteria().getCriterias()
                .stream().filter(c -> c.getKey().equals(SearchKey.POLL_TYPE)).findAny();
        return optional.map(listCriteria -> PollTypeEnum.valueOf(listCriteria.getValue())).orElse(PollTypeEnum.BASE_POLL);
    }

    private void validatePollListQuery(PollListQuery query) {
        if (query == null) {
            throw new NullPointerException("Cannot get poll list because no query.");
        }
        if (query.getPagination() == null) {
            throw new NullPointerException("Cannot get poll list because no list pagination.");
        }
        if (query.getPollSearchCriteria() == null || query.getPollSearchCriteria().getCriterias() == null) {
            throw new NullPointerException("Cannot get poll list because criteria are null.");
        }
    }

    public List<ProgramPoll> getPollProgramRunningAndStarted() {
        return pollProgramDao.getProgramPollRunningAndStarted();
    }

    public ProgramPoll setStatusPollProgram(PollId id, ProgramPollStatus state) {
        if (id == null || id.getGuid() == null || id.getGuid().isEmpty()) {
            throw new NullPointerException("No poll id given");
        }
        if (state == null) {
            throw new NullPointerException("No status to set");
        }

        ProgramPoll program = pollProgramDao.getProgramPollByGuid(id.getGuid());

        switch (program.getPollState()) {
            case ARCHIVED:
                throw new IllegalArgumentException("Can not change status of archived program poll, id: [ " + id.getGuid() + " ]");
            case STARTED:
            case STOPPED:
        }

        // TODO: check terminal/comchannel?

        program.setPollState(state);

        return program;
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

        int page = query.getPagination().getPage();
        int listSize = query.getPagination().getListSize();
        int startIndex = (page - 1) * listSize;
        int stopIndex = startIndex + listSize;
        LOG.debug("page: " + page + ", listSize: " + listSize + ", startIndex: " + startIndex);

        List<String> idList = query.getConnectIdList();
        long in = System.currentTimeMillis();

        List<Channel> channels = channelDao.getPollableListSearch(idList);

        for (Channel comchannel : channels) {
            //TODO slim response from Pollable
            MobileTerminalType terminal = MobileTerminalEntityToModelMapper.mapToMobileTerminalType(comchannel.getMobileTerminal());
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

    private List<PollResponseType> getResponseList(List<ProgramPoll> pollPrograms)  {
        List<PollResponseType> responseList = new ArrayList<>();
        for (ProgramPoll pollProgram : pollPrograms) {
            responseList.add(PollEntityToModelMapper.mapToPollResponseType(pollProgram));
        }
        return responseList;
    }

    public List<PollBase> getAllPollsForAssetForTheLastDay(UUID assetId){
        return pollDao.findByAssetInTimespan(assetId, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now());

    }
}
