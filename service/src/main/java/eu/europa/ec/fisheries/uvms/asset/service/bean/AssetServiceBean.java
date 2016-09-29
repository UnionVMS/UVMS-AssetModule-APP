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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.remote.AssetDomainModel;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.GetAssetListResponseDto;
import eu.europa.ec.fisheries.uvms.asset.service.constants.ServiceConstants;
import eu.europa.ec.fisheries.uvms.audit.model.exception.AuditModelMarshallException;
import eu.europa.ec.fisheries.wsdl.asset.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.asset.exception.AssetServiceException;
import eu.europa.ec.fisheries.uvms.asset.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.consumer.AssetQueueConsumer;
import eu.europa.ec.fisheries.uvms.asset.message.exception.AssetMessageException;
import eu.europa.ec.fisheries.uvms.asset.message.mapper.AuditModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetDataSourceResponseMapper;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;

@Stateless
public class AssetServiceBean implements AssetService {

    final static Logger LOG = LoggerFactory.getLogger(AssetServiceBean.class);

    @EJB
    MessageProducer messageProducer;

    @EJB
    AssetQueueConsumer reciever;

    @EJB(lookup = ServiceConstants.DB_ACCESS_ASSET_DOMAIN_MODEL)
    AssetDomainModel assetDomainModel;

    /**
     * {@inheritDoc}
     *
     * @param asset
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Asset createAsset(Asset asset, String username) throws AssetException {
        LOG.info("Creating asset.");
        Asset createdAsset = assetDomainModel.createAsset(asset, username);
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetCreated(createdAsset.getAssetId().getGuid(), username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
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
    public ListAssetResponse getAssetList(AssetListQuery requestQuery) throws AssetException {
        LOG.info("Getting AssetList.");
        GetAssetListResponseDto assetList = assetDomainModel.getAssetList(requestQuery);
        ListAssetResponse listAssetResponse = new ListAssetResponse();
        listAssetResponse.setCurrentPage(assetList.getCurrentPage());
        listAssetResponse.setTotalNumberOfPages(assetList.getTotalNumberOfPages());
        listAssetResponse.getAsset().addAll(assetList.getAssetList());
        return listAssetResponse;
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
        LOG.info("Getting AssetList.");
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
        logAssetUpdated(updatedAsset, comment, username);
        return updatedAsset;
    }

    @Override
    public Asset archiveAsset(Asset asset, String username, String comment) throws AssetException {
        Asset archivedAsset = updateAssetInternal(asset, username);
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

    private void logAssetArchived(Asset asset, String comment, String username) throws AssetMessageException {
        try {
            String auditData = AuditModuleRequestMapper.mapAuditLogAssetArchived(asset.getAssetId().getGuid(), comment, username);
            messageProducer.sendModuleMessage(auditData, ModuleQueue.AUDIT);
        } catch (AuditModelMarshallException e) {
            LOG.error("Failed to send audit log message! Asset with guid {} was archived ", asset.getAssetId().getGuid());
        }
    }

    private Asset updateAssetInternal(Asset asset, String username) throws AssetException {
        LOG.info("Updating Asset");
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
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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

        LOG.info("GETTING ASSET BY ID: {} : {} at {}.", assetId.getType(), assetId.getValue(), source.name());

        switch (source){
            case INTERNAL:
                assetById = assetDomainModel.getAssetById(assetId);
                break;
            default:
                String data = AssetDataSourceRequestMapper.mapGetAssetById(assetId.getValue(), assetId.getType());
                String messageId = messageProducer.sendDataSourceMessage(data, source);
                TextMessage response = reciever.getMessage(messageId, TextMessage.class);
                assetById = AssetDataSourceResponseMapper.mapToAssetFromResponse(response, messageId);
                break;
        }
        return assetById;

    }

    /**
     *
     * @param guid
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public Asset getAssetByGuid(String guid) throws AssetException {
        LOG.info("Getting asset by ID.");
        if (guid == null || guid.isEmpty()) {
            throw new InputArgumentException("AssetId is null");
        }

        AssetId assetId = new AssetId();
        assetId.setType(AssetIdType.GUID);
        assetId.setValue(guid);
        return assetDomainModel.getAssetById(assetId);
    }

    /**
     *
     * @param groups
     * @return
     * @throws eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException
     */
    @Override
    public List<Asset> getAssetListByAssetGroups(List<AssetGroup> groups) throws AssetException {
        LOG.info("Getting asset by ID.");
        if (groups == null || groups.isEmpty()) {
            throw new InputArgumentException("No groups in query");
        }

        return assetDomainModel.getAssetListByAssetGroup(groups);
    }

    @Override
    public AssetListGroupByFlagStateResponse getAssetListGroupByFlagState(List assetIds) throws AssetException {
        LOG.info("Getting asset list by asset ids group by flags State.");
        List assetListGroupByFlagState = assetDomainModel.getAssetListGroupByFlagState(assetIds);
        AssetListGroupByFlagStateResponse assetListGroupByFlagStateResponse = new AssetListGroupByFlagStateResponse();
        assetListGroupByFlagStateResponse.getNumberOfAssetsGroupByFlagState().addAll(assetListGroupByFlagState);
        return assetListGroupByFlagStateResponse;

    }

    public NoteActivityCode getNoteActivityCodes() {
        return assetDomainModel.getNoteActivityCodes();
    }
}