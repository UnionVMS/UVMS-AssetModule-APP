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

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollableQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.source.v1.MobileTerminalListResponse;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset;
import eu.europa.ec.fisheries.uvms.asset.dto.AssetMTEnrichmentRequest;
import eu.europa.ec.fisheries.uvms.asset.mapper.AssetDtoMapper;
import eu.europa.ec.fisheries.uvms.asset.message.AuditProducer;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.AssetDto;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.ChangeHistoryRow;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.TerminalDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.MTListResponse;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.dto.PollChannelListDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.Channel;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminal;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.MobileTerminalStatus;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.MobileTerminalDtoMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PollDtoMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.constants.TerminalSourceEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.dto.ListResponseDto;
import eu.europa.ec.fisheries.uvms.mobileterminal.search.MTSearchKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@LocalBean
public class MobileTerminalServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(MobileTerminalServiceBean.class);

    @Inject
    private AuditProducer auditProducer;

    @EJB
    private PollServiceBean pollModel;

    @EJB
    private TerminalDaoBean terminalDao;

    @Inject
    private AssetDao assetDao;

    public MobileTerminal createMobileTerminal(MobileTerminal mobileTerminal, String username) {
        if (mobileTerminal.getChannels().isEmpty()) {
            throw new IllegalArgumentException("A mobile Terminal needs to have at least one channel attached to it.");
        }
        Set<Channel> channels = mobileTerminal.getChannels();
        channels.forEach(channel -> channel.setMobileTerminal(mobileTerminal));
        mobileTerminal.setUpdateuser(username);
        MobileTerminal createdMobileTerminal = terminalDao.createMobileTerminal(mobileTerminal);
        sortChannels(createdMobileTerminal);
        String pluginServiceName = createdMobileTerminal.getPlugin().getPluginServiceName();

        //send stuff to audit
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalCreated(createdMobileTerminal.getId().toString(), username);
            auditProducer.sendModuleMessage(auditData);
        } catch (JMSException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was created", createdMobileTerminal.getId().toString());
        }
        return createdMobileTerminal;
    }

    public MTListResponse getMobileTerminalList(List<MTSearchKeyValue> searchFields, int page, int listSize, boolean isDynamic, boolean includeArchived) {
        return getTerminalListByQuery(searchFields, page, listSize, isDynamic, includeArchived);
    }

    public MobileTerminal upsertMobileTerminal(MobileTerminal data, TerminalSourceEnum source, String username) {
        nullValidation(data, "No Mobile terminal to update [ NULL ]");
        data.setSource(source);
        return upsertMobileTerminal(data, username);
    }

    public MobileTerminal updateMobileTerminal(MobileTerminal mobileTerminal, String comment, String username) {
        nullValidation(mobileTerminal, "No terminal to update");
        nullValidation(mobileTerminal.getId(), "Non valid id of terminal to update");

        MobileTerminal oldTerminal = getMobileTerminalEntityById(mobileTerminal.getId());
        MobileTerminalPlugin updatedPlugin = null;

        if (mobileTerminal.getPlugin() == null || mobileTerminal.getPlugin().getName() == null) {
            updatedPlugin = oldTerminal.getPlugin();
        }

        if (updatedPlugin == null) {
            updatedPlugin = oldTerminal.getPlugin();
        }

        mobileTerminal.setUpdateuser(username);
        mobileTerminal.setComment(comment);
        mobileTerminal.setPlugin(updatedPlugin);

        mobileTerminal.getChannels().forEach(channel -> channel.setMobileTerminal(mobileTerminal));     //this is here to take care of the back reference since jsonb does not do that automatically

        //TODO check type
        MobileTerminal updatedTerminal;
        if (mobileTerminal.getMobileTerminalType() != null) {
            updatedTerminal = terminalDao.updateMobileTerminal(mobileTerminal);
            sortChannels(updatedTerminal);
            Asset asset = updatedTerminal.getAsset();
            if (asset != null) {
                asset.setUpdateTime(Instant.now());
            }
        } else {
            throw new UnsupportedOperationException("Update - Not supported mobile terminal type");
        }

        //send to audit
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUpdated(updatedTerminal.getId().toString(), comment, username);
            auditProducer.sendModuleMessage(auditData);
        } catch (JMSException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was updated", updatedTerminal.getId().toString());
        }

        return updatedTerminal;
    }

    public MobileTerminal populateAssetInMT(MobileTerminal mt) {
        if (mt.getAssetUUID() != null) {
            Asset asset = assetDao.getAssetById(UUID.fromString(mt.getAssetUUID()));
            checkIfAssetAlreadyHasAnActiveMTBeforeAddingANewOne(asset, mt);
            mt.setAsset(asset);
        }
        return mt;
    }

    private void checkIfAssetAlreadyHasAnActiveMTBeforeAddingANewOne(Asset asset, MobileTerminal mt) {
        if (mt.getActive() && asset != null && asset.getMobileTerminals().stream().anyMatch(m -> m.getActive() && !m.getId().equals(mt.getId()))) {
            throw new IllegalArgumentException("An asset can not have more then one active MT. Asset " + asset.getName() + " already has at least one active MT");
        }
    }

    public MobileTerminal assignMobileTerminal(UUID connectId, UUID mobileTerminalId, String comment, String username) {
        MobileTerminal terminalAssign = assignMobileTerminalToCarrier(connectId, mobileTerminalId, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalAssigned(terminalAssign.getId().toString(), comment, username);
            auditProducer.sendModuleMessage(auditData);
        } catch (JMSException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was assigned", terminalAssign.getId().toString());
        }
        return terminalAssign;
    }

    public MobileTerminal unAssignMobileTerminal(UUID connectId, UUID guid, String comment, String username) {
        MobileTerminal terminalUnAssign = unAssignMobileTerminalFromCarrier(connectId, guid, comment, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnassigned(terminalUnAssign.getId().toString(), comment, username);
            auditProducer.sendModuleMessage(auditData);
        } catch (JMSException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was unassigned", terminalUnAssign.getId().toString());
        }
        return terminalUnAssign;
    }

    public MobileTerminal setStatusMobileTerminal(UUID guid, String comment, MobileTerminalStatus status, String username) {
        MobileTerminal terminalStatus = getMobileTerminalEntityById(guid);

        terminalStatus = changeUpdateMobileTerminalStatus(terminalStatus, comment, status, username);

        //audit stuff
        try {
            String auditData = null;
            switch (status) {
                case ACTIVE:
                    auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalActivated(terminalStatus.getId().toString(), comment, username);
                    break;
                case INACTIVE:
                    auditData = AuditModuleRequestMapper
                            .mapAuditLogMobileTerminalInactivated(terminalStatus.getId().toString(), comment, username);
                    break;
                case ARCHIVE:
                    auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalArchived(terminalStatus.getId().toString(), comment, username);
                    break;
                case UNARCHIVE:
                    auditData = AuditModuleRequestMapper.mapAuditLogMobileTerminalUnarchived(terminalStatus.getId().toString(), comment, username);
                    break;
                default:
                    break;
            }
            auditProducer.sendModuleMessage(auditData);
        } catch (JMSException e) {
            LOG.error("Failed to send audit log message! Mobile Terminal with guid {} was set to status {}", terminalStatus.getId().toString(), status);
        }
        return terminalStatus;
    }

    private MobileTerminal changeUpdateMobileTerminalStatus(MobileTerminal mobileTerminal, String comment, MobileTerminalStatus status, String username) {
        nullValidation(mobileTerminal, "No Mobile Terminal");
        nullValidation(status, "No terminal status to set");

        switch (status) {
            case ACTIVE:
                mobileTerminal.setActive(true);
                checkIfAssetAlreadyHasAnActiveMTBeforeAddingANewOne(mobileTerminal.getAsset(), mobileTerminal);
                break;
            case INACTIVE:
                mobileTerminal.setActive(false);
                break;
            case ARCHIVE:
                mobileTerminal.setArchived(true);
                mobileTerminal.setActive(false);
                mobileTerminal.setAsset(null);
                break;
            case UNARCHIVE:
                mobileTerminal.setArchived(false);
                break;
            default:
                LOG.error("[ Non valid status to set ] {}", status);
                throw new IllegalArgumentException("Non valid status to set");
        }
        mobileTerminal.setUpdateuser(username);
        mobileTerminal.setComment(comment);
        mobileTerminal = terminalDao.updateMobileTerminal(mobileTerminal);
        sortChannels(mobileTerminal);
        return mobileTerminal;
    }

    public List<MobileTerminal> getMobileTerminalRevisions(UUID mobileTerminalId, int maxNbr) {
        List<MobileTerminal> revisions = terminalDao.getMobileTerminalRevisionById(mobileTerminalId);
        revisions.sort(Comparator.comparing(MobileTerminal::getUpdatetime));
        revisions.forEach(this::sortChannels);
        if (revisions.size() > maxNbr) {
            return revisions.subList(revisions.size() - maxNbr, revisions.size());  //we should get the latest ones right?
        }
        return revisions;
    }

    public MobileTerminal getActiveMTForAsset(UUID assetId) {
        Asset asset = assetDao.getAssetById(assetId);
        return asset.getMobileTerminals()
                .stream()
                .filter(MobileTerminal::getActive)
                .findAny()
                .orElse(null);
    }

    public Channel getPollableChannel(MobileTerminal mt) {
        return mt.getChannels()
                .stream()
                .filter(Channel::isPollChannel)
                .findAny()
                .orElse(null);
    }

    public PollChannelListDto getPollableMobileTerminal(PollableQuery query) {
        PollChannelListDto channelListDto = new PollChannelListDto();

        ListResponseDto listResponse = pollModel.getMobileTerminalPollableList(query);
        MobileTerminalListResponse response = new MobileTerminalListResponse();
        response.setCurrentPage(listResponse.getCurrentPage());
        response.setTotalNumberOfPages(listResponse.getTotalNumberOfPages());
        response.getMobileTerminal().addAll(listResponse.getMobileTerminalList());

        channelListDto.setCurrentPage(response.getCurrentPage());
        channelListDto.setTotalNumberOfPages(response.getTotalNumberOfPages());

        ArrayList<PollChannelDto> pollChannelList = new ArrayList<>();
        for (MobileTerminalType terminalType : response.getMobileTerminal()) {
            PollChannelDto terminal = PollDtoMapper.mapPollChannel(terminalType);
            pollChannelList.add(terminal);
        }
        channelListDto.setPollableChannels(pollChannelList);
        return channelListDto;
    }

    /*****************************************************************************************************************/

    public MobileTerminal getMobileTerminalEntityById(UUID id) {
        nullValidation(id, "Non valid MobileTerminal ID: NULL");
        MobileTerminal mt = terminalDao.getMobileTerminalById(id);
        sortChannels(mt);
        return mt;
    }

    public List<MobileTerminal> getMobileTerminalListNotConnectedToAsset() {
        List<MobileTerminal> mtList = terminalDao.getMobileTerminalListWithNoActiveAsset();
        mtList.forEach(this::sortChannels);
        return mtList;
    }

    public MobileTerminal getMobileTerminalEntityBySerialNo(String serialNo) {
        if (serialNo == null || serialNo.isEmpty())
            throw new NullPointerException("Non valid serial no");
        return terminalDao.getMobileTerminalBySerialNo(serialNo);
    }

    public void assertTerminalHasSerialNumber(MobileTerminal mobileTerminal) {
        String serialNumber = mobileTerminal.getSerialNo();
        nullValidation(serialNumber, "Cannot create mobile terminal without serial number");
        nullValidation(mobileTerminal.getPlugin(), "Cannot create Mobile terminal when plugin is null");
    }

    public void assertTerminalNotExists(UUID mobileTerminalGUID, String serialNr) {
        MobileTerminal terminal = getMobileTerminalEntityById(mobileTerminalGUID);
        if (terminal != null) {
            throw new IllegalArgumentException("Mobile terminal already exists in database for id: " + mobileTerminalGUID.toString());
        }

        MobileTerminal terminalBySerialNo = getMobileTerminalEntityBySerialNo(serialNr);
        if (terminalBySerialNo == null) {  //aka the serial number does not exist in the db
            return;
        }
        if (!terminalBySerialNo.getArchived()) {
            throw new IllegalArgumentException("Mobile terminal already exists in database for serial number: " + serialNr);
        }
    }

    public MobileTerminal assignMobileTerminalToCarrier(UUID connectId, UUID mobileTerminalId, String comment, String username) {
        nullValidation(mobileTerminalId, "No Mobile terminalId in request");
        nullValidation(connectId, "No connect id in request");

        Asset asset = assetDao.getAssetById(connectId);
        nullValidation(asset, "No Asset with ID " + connectId + " found. Can not link Mobile Terminal.");

        MobileTerminal terminal = getMobileTerminalEntityById(mobileTerminalId);
        if (terminal.getAsset() != null) {
            throw new IllegalArgumentException("Terminal " + mobileTerminalId + " is already linked to an asset with guid " + connectId);
        }
        checkIfAssetAlreadyHasAnActiveMTBeforeAddingANewOne(asset, terminal);

        asset.getMobileTerminals().add(terminal);
        asset.setUpdateTime(Instant.now());
        terminal.setAsset(asset);
        terminal.setUpdateuser(username);
        terminal.setComment(comment);
        terminal = terminalDao.updateMobileTerminal(terminal);
        sortChannels(terminal);
        return terminal;
    }

    public MobileTerminal unAssignMobileTerminalFromCarrier(UUID connectId, UUID guid, String comment, String username) {
        nullValidation(connectId, "No connect id in request");

        MobileTerminal terminal = getMobileTerminalEntityById(guid);
        terminal.setUpdateuser(username);

        Asset asset = terminal.getAsset();

        boolean remove = asset.getMobileTerminals().remove(terminal);
        if (!remove) {
            throw new IllegalArgumentException("Terminal " + guid + " is not linked to an asset with ID " + asset.getId());
        }
        asset.setUpdateTime(Instant.now());
        terminal.setAsset(null);
        terminal.setComment(comment);
        terminal = terminalDao.updateMobileTerminal(terminal);
        sortChannels(terminal);
        return terminal;
    }

    public MobileTerminal upsertMobileTerminal(MobileTerminal mobileTerminal, String username) {
        nullValidation(mobileTerminal, "RequestQuery is null");

        MobileTerminal upsertedMT;
        if (mobileTerminal.getId() == null) {
            upsertedMT = createMobileTerminal(mobileTerminal, username);
        } else {
            upsertedMT = updateMobileTerminal(mobileTerminal, "Upserted by external module", username);
        }
        return upsertedMT;
    }

    public MTListResponse getTerminalListByQuery(List<MTSearchKeyValue> searchFields, int page, int listSize, boolean isDynamic, boolean includeArchived) {
        nullValidation(searchFields, "No list query");

        MTListResponse response = new MTListResponse();

        int startIndex = (page - 1) * listSize;
        int stopIndex = startIndex + listSize;
        LOG.debug("page: " + page + ", listSize: " + listSize + ", startIndex: " + startIndex);

        List<MobileTerminal> terminals = terminalDao.getMTListSearchPaginated(page, listSize, searchFields, isDynamic, includeArchived);

        terminals.sort(Comparator.comparing(MobileTerminal::getId));
        terminals.forEach(this::sortChannels);

        int totalMatches = terminals.size();
        LOG.debug("totalMatches: " + totalMatches);

        int numberOfPages = totalMatches / listSize;
        if (totalMatches % listSize != 0) {
            numberOfPages += 1;
        }
        response.setMobileTerminalList(MobileTerminalDtoMapper.mapToMobileTerminalDtos(terminals));

        if ((totalMatches - 1) > 0) {
            if (stopIndex >= totalMatches) {
                stopIndex = totalMatches;
            }
            LOG.debug("stopIndex: " + stopIndex);
            response.setMobileTerminalList(new ArrayList<>(MobileTerminalDtoMapper.mapToMobileTerminalDtos(terminals.subList(startIndex, stopIndex))));
        }
        response.setTotalNumberOfPages(numberOfPages);
        response.setCurrentPage(page);

        return response;
    }

    public MobileTerminal findMobileTerminalByAsset(UUID assetid) {
        return terminalDao.findMobileTerminalByAsset(assetid);
    }

    public MobileTerminal getMobileTerminalByAssetMTEnrichmentRequest(AssetMTEnrichmentRequest request) {
        if (request.getDnidValue() == null && request.getMemberNumberValue() == null
                && request.getSerialNumberValue() == null) {
            return null;
        }
        return terminalDao.getMobileTerminalByRequest(request);
    }

    public void inactivateAndUnlink(Asset asset, String comment, String username) {
        asset.getMobileTerminals().forEach(mt -> {
            mt.setUpdateuser(username);
            mt.setAsset(null);
            mt.setComment(comment);
            mt.setUpdatetime(Instant.now());
            setStatusMobileTerminal(mt.getId(), comment, MobileTerminalStatus.INACTIVE, username);
        });
        asset.getMobileTerminals().clear();
    }

    public Map<UUID, ChangeHistoryRow> getMobileTerminalRevisionsByAssetId(UUID assetId, int maxNbr) {
        Map<UUID, ChangeHistoryRow> revisionsMap = new HashMap<>();
        List<UUID> mtList = terminalDao.getAllMobileTerminalIdsWithARelationToAsset(assetId);

        mtList.forEach(terminalID -> {
            List<MobileTerminal> revisions = terminalDao.getMobileTerminalRevisionsRelevantToAsset(terminalID, assetId);
            revisions.sort(Comparator.comparing(MobileTerminal::getCreateTime));
            revisions.forEach(this::sortChannels);
            if (revisions.size() > maxNbr) {
                revisions = revisions.subList(0, maxNbr);
            }
            revisionsMap.putAll(MobileTerminalDtoMapper.mapToMobileTerminalRevisionsMap(revisions));
        });
        return revisionsMap;
    }

    public List<AssetDto> getAssetRevisionsByMobileTerminalId(UUID mobileTerminalId) {
        List<MobileTerminal> mtRevisions = terminalDao.getMobileTerminalRevisionById(mobileTerminalId);

        return AssetDtoMapper.mapToAssetDtos(
                mtRevisions
                        .stream()
                        .filter(mt -> mt.getAsset() != null)
                        .map(MobileTerminal::getAsset)
                        .collect(Collectors.toList()));
    }

    private void sortChannels(MobileTerminal mt) {
        if (mt.getChannels() != null && !mt.getChannels().isEmpty()) {
            List<Channel> asList = new ArrayList<>(mt.getChannels());
            asList.sort(Comparator.comparing(Channel::getName));
            Set<Channel> sorted = new LinkedHashSet<>(asList);
            mt.getChannels().clear();
            mt.getChannels().addAll(sorted);
        }
    }

    private void nullValidation(Object obj, String message) {
        if (obj == null) throw new IllegalArgumentException(message);
    }

}
