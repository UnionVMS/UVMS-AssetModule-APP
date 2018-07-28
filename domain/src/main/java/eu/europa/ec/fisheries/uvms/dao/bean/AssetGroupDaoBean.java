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

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;

@Stateless
public class AssetGroupDaoBean extends Dao implements AssetGroupDao {

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupDaoBean.class);

    @Override
    public AssetGroup createAssetGroup(AssetGroup group) throws AssetGroupDaoException {
        try {
            em.persist(group);
            return group;
        } catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when creating transportMeans group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create transportMeans group ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when creating transportMeans group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create transportMeans ] " + e.getMessage());
        }
    }

    @Override
    public AssetGroup getAssetGroupByGuid(String groupId) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID, AssetGroup.class);
            query.setParameter("guid", groupId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting transportMeans group by guid. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get transportMeans group, guid: " + groupId + " ] " + e.getMessage());
        }
    }

    @Override
    public AssetGroup updateAssetGroup(AssetGroup group) throws AssetGroupDaoException {
        try {
            em.merge(group);
            return group;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when updating transportMeans group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ update transportMeans group, id: " + group.getGuid() + " ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when updating transportMeans group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create transportMeans ] " + e.getMessage());
        }
    }

    @Override
    public AssetGroup deleteAssetGroup(AssetGroup group) throws AssetGroupDaoException {
        try {
            em.remove(group);
            return group;
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            LOG.error("[ Error when deleting transportMeans group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ delete transportMeans group, id: " + group.getGuid() + " ] " + e.getMessage());
        } catch (Exception e) {
            LOG.error("[ Error when deleting transportMeans group. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ create transportMeans ] " + e.getMessage());
        }

    }

    @Override
    public List<AssetGroup> getAssetGroupAll() throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_FIND_ALL, AssetGroup.class);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting transportMeans groups. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get all transportMeans groups ] " + e.getMessage());
        } 
    }

    @Override
    public List<AssetGroup> getAssetGroupByUser(String user) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER, AssetGroup.class);
            query.setParameter("owner", user);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting transportMeans groups by user. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get transportMeans groups, by user: " + user + " ] " + e.getMessage());
        }
    }

	@Override
	public List<AssetGroup> getAssetGroupsByGroupGuidList(List<String> guidList) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID_LIST, AssetGroup.class);
            query.setParameter("guidList", guidList);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting transportMeans groups by idlist. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get transportMeans groups by idlist ] " + e.getMessage());
        }
	}

}