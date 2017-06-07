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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.Dao;
import eu.europa.ec.fisheries.uvms.dao.exception.NoAssetEntityFoundException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldMapper;
import eu.europa.ec.fisheries.uvms.mapper.SearchFieldType;
import eu.europa.ec.fisheries.uvms.mapper.SearchKeyValue;

/**
 **/
@Stateless
public class AssetDaoBean extends Dao implements AssetDao {

    private static final Logger LOG = LoggerFactory.getLogger(AssetDaoBean.class);

    @Override
    public AssetEntity createAsset(AssetEntity asset) throws AssetDaoException {
        try {
            LOG.debug("Create asset.");
            em.persist(asset);
            return asset;
        } catch (Exception e) {
            LOG.error("[ Error when creating asset. ] ");
            throw new AssetDaoException("[ Error when creating asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetEntity getAssetById(Long id) throws AssetDaoException {
        LOG.debug("Get asset by ID.");
        try {
            return em.find(AssetEntity.class, id);
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by ID. ] ");
            throw new NoAssetEntityFoundException("No asset found for " + id);
        }
    }

    @Override
    public AssetEntity updateAsset(AssetEntity asset) throws AssetDaoException {
        LOG.debug("Update asset.");
        try {
            em.merge(asset);
            em.flush();
            return asset;
        } catch (Exception e) {
            LOG.error("[ Error when updating asset. ] ");
            throw new AssetDaoException("[ update asset, id: " + asset.getId() + " ] " + e.getMessage());
        }
    }

    @Override
    public void deleteAsset(Long assetId) throws AssetDaoException {
        LOG.debug("Delete asset.");
        throw new AssetDaoException("Not implemented yet");
    }

    @Override
    public List<AssetEntity> getAssetListAll() throws AssetDaoException {
        try {
            LOG.debug("Get asset list.");
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_ALL, AssetEntity.class);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            LOG.error("[ Error when getting asset list. ] ");
            throw new AssetDaoException("[ get all asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetEntity getAssetByCfr(String cfr) throws NoAssetEntityFoundException, AssetDaoException {
        LOG.debug("Get asset by CFR.");
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_CFR, AssetEntity.class);
            query.setParameter("cfr", cfr);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by CFR. ] ");
            throw new NoAssetEntityFoundException("No asset found for " + cfr);
        }
    }

    @Override
    public AssetEntity getAssetByIrcs(String ircs) throws NoAssetEntityFoundException, AssetDaoException {
        LOG.debug("Get asset by IRCS.");
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IRCS, AssetEntity.class);
            query.setParameter("ircs", ircs);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by IRCS. ]");
            throw new NoAssetEntityFoundException("No asset found for " + ircs);
        }
    }

    @Override
    public AssetEntity getAssetByGuid(String guid) throws AssetDaoException {
        LOG.debug("Get asset by GUID.");
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_GUID, AssetEntity.class);
            query.setParameter("guid", guid);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by GUID. ]");
            throw new NoAssetEntityFoundException("No asset found for " + guid);
        }
    }

	@Override
	public AssetEntity getAssetByImo(String imo) throws AssetDaoException {
		LOG.debug("Get asset by IMO");
		try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IMO, AssetEntity.class);
            query.setParameter("imo", imo);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by IMO. ]");
            throw new NoAssetEntityFoundException("No asset found for " + imo);
        }
	}

	@Override
	public AssetEntity getAssetByMmsi(String mmsi) throws AssetDaoException {
		LOG.debug("Get asset by MMSI");
		try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_MMSI, AssetEntity.class);
            query.setParameter("mmsi", mmsi);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by MMSI. ]");
            throw new NoAssetEntityFoundException("No asset found for " + mmsi);
        }
	}
    
    @Override
    public AssetHistory getAssetHistoryByGuid(String guid) throws AssetDaoException {
        LOG.debug("Get assethistory by GUID.");
        try {
            TypedQuery<AssetHistory> query = em.createNamedQuery(UvmsConstants.ASSETHISTORY_FIND_BY_GUID, AssetHistory.class);
            query.setParameter("guid", guid);
            AssetHistory singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting assethistory by GUID. ] ");
            throw new NoAssetEntityFoundException("No asset history found for " + guid);
        }
    }

    @Override
    public Long getAssetCount(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException {
        TypedQuery<Long> query = em.createQuery(sql, Long.class);

        for (SearchKeyValue field : searchFields) {
            if (SearchFieldMapper.useLike(field)) {
                int containsCount = 0;
                for (String searchValue : field.getSearchValues()) {
                    containsCount++;
                    String tmpValue = searchValue.replace("*", "");
                    query.setParameter(field.getSearchField().getValueName() + containsCount, "%" + tmpValue + "%");
                }
            } else {
                if (field.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                    List<Integer> parameter = new ArrayList<>();
                    for (String param : field.getSearchValues()) {
                        parameter.add(Integer.parseInt(param));
                    }
                    query.setParameter(field.getSearchField().getValueName(), parameter);
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                	query.setParameter(field.getSearchField().getValueName(), field.getSearchValues());
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) { //BOOLEAN only one value
                	query.setParameter(field.getSearchField().getValueName(), Boolean.parseBoolean(field.getSearchValues().get(0)));
                } else { //DECIMAL, only one value
                    query.setParameter(field.getSearchField().getValueName(), new BigDecimal(field.getSearchValues().get(0)));
                }
            }
        }

        return query.getSingleResult();
    }

    @Override
    public List<AssetHistory> getAssetListSearchPaginated(Integer pageNumber, Integer pageSize, String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException {
        TypedQuery<AssetHistory> query = getVesselHistoryQuery(sql, searchFields, isDynamic);
        query.setFirstResult(pageSize * (pageNumber - 1));
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public List<AssetHistory> getAssetListSearchNotPaginated(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) throws AssetDaoException {
        return getVesselHistoryQuery(sql, searchFields, isDynamic).getResultList();
    }

    private TypedQuery<AssetHistory> getVesselHistoryQuery(String sql, List<SearchKeyValue> searchFields, boolean isDynamic) {
        TypedQuery<AssetHistory> query = em.createQuery(sql, AssetHistory.class);

        for (SearchKeyValue field : searchFields) {
            if (SearchFieldMapper.useLike(field)) {
                int containsCount = 0;
                for (String searchValue : field.getSearchValues()) {
                    containsCount++;
                    String tmpValue = searchValue.replace("*", "");
                    query.setParameter(field.getSearchField().getValueName() + containsCount, "%" + tmpValue + "%");
                }
            } else {
                if (field.getSearchField().getFieldType().equals(SearchFieldType.NUMBER)) {
                    List<Integer> parameter = new ArrayList<>();
                    for (String param : field.getSearchValues()) {
                        parameter.add(Integer.parseInt(param));
                    }
                    query.setParameter(field.getSearchField().getValueName(), parameter);
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.LIST)) {
                	query.setParameter(field.getSearchField().getValueName(), field.getSearchValues());
                } else if (field.getSearchField().getFieldType().equals(SearchFieldType.BOOLEAN)) { //BOOLEAN only one value
                	query.setParameter(field.getSearchField().getValueName(), Boolean.parseBoolean(field.getSearchValues().get(0)));
                } else { //DECIMAL, only one value
                    query.setParameter(field.getSearchField().getValueName(), new BigDecimal(field.getSearchValues().get(0)));
                }
            }
        }
        return query;
    }

    @Override
    public List<AssetHistory> getAssetListByAssetGuids(List<String> assetGuids) throws AssetDaoException {
        try {
            LOG.debug("Get asset list by asset guids.");
            TypedQuery<AssetHistory> query = em.createNamedQuery(UvmsConstants.ASSETHISTORY_FIND_BY_GUIDS, AssetHistory.class);
            query.setParameter("guids", assetGuids);
            return query.getResultList();
        } catch (IllegalArgumentException e) {
            LOG.error("[ Error when getting asset list. ] ");
            throw new AssetDaoException("[ get all asset ] " + e.getMessage());
        }
    }

    @Override
    public AssetEntity getAssetByCfrExcludeArchived(String cfr) throws NoAssetEntityFoundException, AssetDaoException {
        LOG.debug("Get asset by CFR.");
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_CFR_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("cfr", cfr);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by CFR. ] ");
            throw new NoAssetEntityFoundException("No asset found for " + cfr);
        }
    }

    @Override
    public AssetEntity getAssetByIrcsExcludeArchived(String ircs) throws NoAssetEntityFoundException, AssetDaoException {
        LOG.debug("Get asset by IRCS.");
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IRCS_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("ircs", ircs);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by IRCS. ]");
            throw new NoAssetEntityFoundException("No asset found for " + ircs);
        }
    }

    @Override
    public AssetEntity getAssetByImoExcludeArchived(String imo) throws AssetDaoException {
        LOG.debug("Get asset by IMO");
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_IMO_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("imo", imo);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by IMO. ]");
            throw new NoAssetEntityFoundException("No asset found for " + imo);
        }
    }

    @Override
    public AssetEntity getAssetByMmsiExcludeArchived(String mmsi) throws AssetDaoException {
        LOG.debug("Get asset by MMSI");
        try {
            TypedQuery<AssetEntity> query = em.createNamedQuery(UvmsConstants.ASSET_FIND_BY_MMSI_EXCLUDE_ARCHIVED, AssetEntity.class);
            query.setParameter("mmsi", mmsi);
            AssetEntity singleResult = query.getSingleResult();
            return singleResult;
        } catch (NoResultException e) {
            LOG.error("[ Error when getting asset by MMSI. ]");
            throw new NoAssetEntityFoundException("No asset found for " + mmsi);
        }
    }

    @Override
    public List<NotesActivityCode> getNoteActivityCodes() {
        TypedQuery<NotesActivityCode> query = em.createNamedQuery(UvmsConstants.ASSET_NOTE_ACTIVITY_CODE_FIND_ALL, NotesActivityCode.class);
        return query.getResultList();
    }
}