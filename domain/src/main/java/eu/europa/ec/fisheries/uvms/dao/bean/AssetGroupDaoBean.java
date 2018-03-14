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
package eu.europa.ec.fisheries.uvms.dao.bean;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.*;

import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;

@Stateless
public class AssetGroupDaoBean extends Dao implements AssetGroupDao {

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupDaoBean.class);

    @Override
    public AssetGroupEntity createAssetGroup(AssetGroupEntity group) throws AssetGroupDaoException {
        try {
            em.persist(group);
            return group;
        } catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when creating asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset group ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when creating asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetGroupEntity getAssetGroupByGuid(String groupId) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID, AssetGroupEntity.class);
            query.setParameter("guid", groupId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset group by guid. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset group, guid: " + groupId + " ] " + e.getMessage());
        }
    }

    @Override
    public AssetGroupEntity updateAssetGroup(AssetGroupEntity group) throws AssetGroupDaoException {
        try {
            em.merge(group);
            return group;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when updating asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ update asset group, id: " + group.getGuid() + " ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when updating asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetGroupEntity deleteAssetGroup(AssetGroupEntity group) throws AssetGroupDaoException {
        try {
            em.remove(group);
            return group;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when deleting asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ delete asset group, id: " + group.getGuid() + " ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when deleting asset group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create asset ] " + e.getMessage());
        }

    }

    @Override
    public List<AssetGroupEntity> getAssetGroupAll() throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_FIND_ALL, AssetGroupEntity.class);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get all asset groups ] " + e.getMessage());
        } 
    }

    @Override
    public List<AssetGroupEntity> getAssetGroupByUser(String user) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER, AssetGroupEntity.class);
            query.setParameter("owner", user);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups by user. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset groups, by user: " + user + " ] " + e.getMessage());
        }
    }

	@Override
	public List<AssetGroupEntity> getAssetGroupsByGroupGuidList(List<String> guidList) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroupEntity> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID_LIST, AssetGroupEntity.class);
            query.setParameter("guidList", guidList);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups by idlist. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset groups by idlist ] " + e.getMessage());
        }
	}

}