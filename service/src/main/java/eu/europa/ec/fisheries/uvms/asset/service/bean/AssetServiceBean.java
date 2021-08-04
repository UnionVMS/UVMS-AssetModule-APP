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
package eu.europa.ec.fisheries.uvms.asset.service.bean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.AssetMessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMarshallException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.GetAssetListResponseDto;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.uvms.bean.AssetDomainModelBean;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class AssetServiceBean implements AssetService {

    private final static Logger LOG = LoggerFactory.getLogger(AssetServiceBean.class);

    @EJB
    private AssetMessageProducer messageProducer;

    @EJB
    private ReportingProducerBean reportingProducer;

    @EJB
    private AssetQueueConsumer receiver;

    @EJB
    private AssetDomainModelBean assetDomainModel;

    @EJB
    private AssetDao assetDao;

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Asset createAsset(Asset asset, String username) throws AssetException {
        LOG.debug("Creating asset.");
        Asset createdAsset = assetDomainModel.createAsset(asset, username);
        sendAssetUpdateToReporting(createdAsset);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetCreated(createdAsset.getAssetId().getGuid(), username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AssetMessageException e) {
            LOG.warn("Failed to send audit log message! Asset with guid {} was created ", createdAsset.getAssetId().getGuid());
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was created ", createdAsset.getAssetId().getGuid());
        }

        return createdAsset;
    }

    /**
     * {@inheritDoc}
     *
     * @param requestQuery
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public ZeroBasedIndexListAssetResponse getZeroBasedAssetList(AssetListQuery requestQuery) throws AssetException {
        LOG.debug("Getting AssetList.");
        GetAssetListResponseDto assetList = assetDomainModel.getAssetList(requestQuery);
        ZeroBasedIndexListAssetResponse zeroBasedIndexListAssetResponse = new ZeroBasedIndexListAssetResponse();
        zeroBasedIndexListAssetResponse.setCurrentPage(assetList.getCurrentPage());
        zeroBasedIndexListAssetResponse.setTotalResults(assetList.getTotalCount());
        zeroBasedIndexListAssetResponse.setTotalNumberOfPages(assetList.getTotalNumberOfPages());
        zeroBasedIndexListAssetResponse.getAsset().addAll(assetList.getAssetList());
        return zeroBasedIndexListAssetResponse;
    }

    /**
     * {@inheritDoc}
     *
     * @param requestQuery
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public ListAssetResponse getAssetList(AssetListQuery requestQuery) throws AssetException {
        LOG.debug("Getting AssetList.");
        GetAssetListResponseDto assetList = assetDomainModel.getAssetList(requestQuery);
        ListAssetResponse listAssetResponse = new ListAssetResponse();
        listAssetResponse.setCurrentPage(assetList.getCurrentPage());
        listAssetResponse.setTotalNumberOfPages(assetList.getTotalNumberOfPages());
        listAssetResponse.getAsset().addAll(assetList.getAssetList());
        return listAssetResponse;
    }

    /**
     * {@inheritDoc}
     * <p>
     * TODO : This is just a first iteration of a batch service! For loop should be avoided somehow!
     *
     * @param requestQuery
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public List<BatchAssetListResponseElement> getAssetListBatch(List<AssetListQuery> requestQuery) throws AssetException {
        LOG.debug("Getting AssetList Batch.");
        List<BatchAssetListResponseElement> batchListList = new ArrayList<>();
        for (AssetListQuery assetListQuery : requestQuery) {
            GetAssetListResponseDto assetList = assetDomainModel.getAssetList(assetListQuery);
            BatchAssetListResponseElement batchElement = new BatchAssetListResponseElement();
            batchElement.setCurrentPage(assetList.getCurrentPage());
            batchElement.setTotalNumberOfPages(assetList.getTotalNumberOfPages());
            batchElement.getAsset().addAll(assetList.getAssetList());
            batchListList.add(batchElement);
        }
        return batchListList;
    }

    /**
     * {@inheritDoc}
     *
     * @param requestQuery
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Long getAssetListCount(AssetListQuery requestQuery) throws AssetException {
        LOG.debug("Getting AssetList.");
        return assetDomainModel.getAssetListCount(requestQuery);
    }

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Asset updateAsset(Asset asset, String username, String comment) throws AssetException {
        Asset updatedAsset = updateAssetInternal(asset, username);
        sendAssetUpdateToReporting(updatedAsset);
        logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    @Override
    public Asset archiveAsset(Asset asset, String username, String comment) throws AssetException {
        Asset archivedAsset = updateAssetInternal(asset, username);
        sendAssetUpdateToReporting(archivedAsset);
        logAssetArchived(archivedAsset, comment, username);
        return archivedAsset;
    }

    private void logAssetUpdated(Asset asset, String comment, String username) throws AssetMessageException {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetUpdated(asset.getAssetId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was updated ", asset.getAssetId().getGuid());
        }
    }

    private void sendAssetUpdateToReporting(Asset asset) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("mainTopic", "reporting");
            params.put("subTopic", "asset");
            reportingProducer.sendMessageToSpecificQueueSameTx(AssetModuleRequestMapper.createUpsertAssetModuleResponse(asset), reportingProducer.getDestination(), null, params);
        } catch (MessageException | AssetModelMarshallException e) {
            LOG.error("Could not send asset update to reporting", e);
        }
    }

    private void logAssetArchived(Asset asset, String comment, String username) throws AssetMessageException {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetArchived(asset.getAssetId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was archived ", asset.getAssetId().getGuid());
        }
    }

    private Asset updateAssetInternal(Asset asset, String username) throws AssetException {
        LOG.debug("Updating Asset");
        Asset updatedAsset;

        if (asset == null) {
            throw new InputArgumentException("No asset to update");
        }

        if (asset.getAssetId().getValue() == null) {
            throw new InputArgumentException("No id on asset to update");
        }

        Asset storedAsset = assetDomainModel.getAssetById(asset.getAssetId());
        switch (storedAsset.getSource()) {
            case INTERNAL:
                updatedAsset = assetDomainModel.updateAsset(asset, username);
                break;
            default:
                throw new AssetServiceException("Not allowed to update");
        }
        return updatedAsset;
    }

    @Override
    public Asset upsertAsset(Asset asset, String username) throws AssetException {
        if (asset == null) {
            throw new InputArgumentException("No asset to upsert");
        }
        return assetDomainModel.upsertAsset(asset, username);

    }

    /**
     * {@inheritDoc}
     *
     * @param assetId
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Asset getAssetById(AssetId assetId, AssetDataSourceQueue source) throws AssetException {
        Asset assetById;

        if (assetId == null) {
            throw new InputArgumentException("AssetId object is null");
        }

        if (assetId.getValue() == null || assetId.getType() == null) {
            throw new InputArgumentException("AssetId value or type is null");
        }

        if (source == null) {
            throw new InputArgumentException("AssetDataSourceQueue is null");
        }

        LOG.debug("GETTING ASSET BY ID: {} : {} at {}.", assetId.getType(), assetId.getValue(), source.name());

        switch (source) {
            case INTERNAL:
                assetById = assetDomainModel.getAssetById(assetId);
                break;
            default:
                String data = AssetDataSourceRequestMapper.mapGetAssetById(assetId.getValue(), assetId.getType());
                String messageId = messageProducer.sendDataSourceMessage(data, source);
                TextMessage response = receiver.getMessageOv(messageId, TextMessage.class);
                assetById = AssetDataSourceResponseMapper.mapToAssetFromResponse(response, messageId);
                break;
        }
        return assetById;

    }

    /**
     * @param guid
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Asset getAssetByGuid(String guid) throws AssetException {
        LOG.debug("Getting asset by ID.");
        if (guid == null || guid.isEmpty()) {
            throw new InputArgumentException("AssetId is null");
        }

        AssetId assetId = new AssetId();
        assetId.setType(AssetIdType.GUID);
        assetId.setValue(guid);
        return assetDomainModel.getAssetById(assetId);
    }

    /**
     * @param groups
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups) throws AssetException {
        LOG.debug("Getting asset by ID.");
        if (groups == null || groups.isEmpty()) {
            throw new InputArgumentException("No groups in query");
        }

        return assetDomainModel.getAssetListByAssetGroup(groups);
    }

    @Override
    public AssetListGroupByFlagStateResponse getAssetListGroupByFlagState(List assetIds) throws AssetException {
        LOG.debug("Getting asset list by asset ids group by flags State.");
        List assetListGroupByFlagState = assetDomainModel.getAssetListGroupByFlagState(assetIds);
        AssetListGroupByFlagStateResponse assetListGroupByFlagStateResponse = new AssetListGroupByFlagStateResponse();
        assetListGroupByFlagStateResponse.getNumberOfAssetsGroupByFlagState().addAll(assetListGroupByFlagState);
        return assetListGroupByFlagStateResponse;

    }

    public NoteActivityCode getNoteActivityCodes() {
        return assetDomainModel.getNoteActivityCodes();
    }

    @Override
    public void deleteAsset(AssetId assetId) throws AssetException {
        assetDomainModel.deleteAsset(assetId);
    }

    @Override
    public List<Asset> getAssetHistoryListByAssetId(AssetId assetId, Integer maxResults) throws AssetModelException {
        try {
            return assetDomainModel.getAssetHistoryListByAssetId(assetId, maxResults);
        } catch (NoAssetEntityFoundException e) {
            return Lists.newArrayList();
        }
    }


    @Override
    public List<AssetGroupsForAssetResponseElement> findAssetGroupsForAssets(List<AssetGroupsForAssetQueryElement> assetGroupsForAssetQueryElementList) throws AssetException {
        return assetDomainModel.findAssetGroupsForAssets(assetGroupsForAssetQueryElementList);
    }

    @Override
    public List<AssetHistory> findAssetHistoriesByGuidAndOccurrenceDate(String guid, Date occurrenceDate, int page, int listSize) throws AssetException {
        return assetDomainModel.getAssetListSearchPaginated(guid, occurrenceDate, page, listSize);
    }

    @Override
    public Asset getAssetByIdentifierPrecedence(AssetListCriteria assetListCriteria) {
        return assetDomainModel.findAssetByIdentifierPrecedence(assetListCriteria);
    }
}