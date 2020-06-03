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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.uvms.constant.SearchFields;
import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.DynamicQueryGenerator;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static eu.europa.ec.fisheries.uvms.constant.SearchFields.GEAR_TYPE;

@Stateless
public class AssetGroupDaoBean extends Dao implements AssetGroupDao {

    @Inject
    DynamicQueryGenerator dynamicQueryGenerator;

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupDaoBean.class);

    @Override
    public AssetGroup createAssetGroup(AssetGroup group) throws AssetGroupDaoException {
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
    public AssetGroup getAssetGroupByGuid(String groupId) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID, AssetGroup.class);
            query.setParameter("guid", groupId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset group by guid. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset group, guid: " + groupId + " ] " + e.getMessage());
        }
    }

    @Override
    public AssetGroup updateAssetGroup(AssetGroup group) throws AssetGroupDaoException {
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
    public AssetGroup deleteAssetGroup(AssetGroup group) throws AssetGroupDaoException {
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
    public List<AssetGroup> getAssetGroupAll() throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_FIND_ALL, AssetGroup.class);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get all asset groups ] " + e.getMessage());
        } 
    }

    @Override
    public List<AssetGroup> getAssetGroupByUser(String user) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER, AssetGroup.class);
            query.setParameter("owner", user);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups by user. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset groups, by user: " + user + " ] " + e.getMessage(), e);
        }
    }

    @Override
    public List<AssetGroup> getAssetGroupByUserPaginated(String user, Integer pageNumber, Integer pageSize) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER, AssetGroup.class);
            query.setParameter("owner", user);
            query.setFirstResult(pageSize * (pageNumber - 1));
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups by user. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset groups, by user: " + user + " ] " + e.getMessage(), e);
        }
    }

    public Long getAssetGroupByUserCount(String user) throws AssetGroupDaoException {
        try {
            TypedQuery<Long> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER_COUNT, Long.class);
            query.setParameter("owner", user);
            return query.getSingleResult();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups by user. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset groups, by user: " + user + " ] " + e.getMessage(), e);
        }
    }

	@Override
	public List<AssetGroup> getAssetGroupsByGroupGuidList(List<String> guidList) throws AssetGroupDaoException {
        try {
            TypedQuery<AssetGroup> query = em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_GUID_LIST, AssetGroup.class);
            query.setParameter("guidList", guidList);
            return query.getResultList();
        } catch (Exception e) {
            LOG.error("[ Error when getting asset groups by idlist. ] {}", e.getMessage());
            throw new AssetGroupDaoException("[ get asset groups by idlist ] " + e.getMessage());
        }
	}

    @Override
    public List<String> getAssetGroupForAssetAndHistory(AssetEntity asset, AssetHistory assetHistory){
        String jpql = dynamicQueryGenerator.findAssetGroupByAssetAndHistory();
        TypedQuery<String> query = em.createQuery(jpql, String.class);
        Map<SearchFields, String> searchFieldsStringMap = dynamicQueryGenerator.searchFieldValueMapper(asset, assetHistory);
        searchFieldsStringMap.forEach((key, value) -> {
            if (key == GEAR_TYPE) {
                query.setParameter(key.getValueName(), value);
                return;
            }
            switch (key.getFieldType()) {
                case NUMBER:
                    query.setParameter(key.getValueName(), Integer.valueOf(value));
                    break;
                case MAX_DECIMAL:
                case MIN_DECIMAL:
                    query.setParameter(key.getValueName(), Double.valueOf(value));
                    break;
                default:
                    query.setParameter(key.getValueName(), value);
            }
        });
        return query.getResultList();
    }
}
