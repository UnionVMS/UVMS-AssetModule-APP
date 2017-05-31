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
package eu.europa.ec.fisheries.uvms;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetDaoBean;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.entity.model.Carrier;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;

@RunWith(MockitoJUnitRunner.class)
public class AssetDaoBeanTest {

    @Mock
    EntityManager em;

    @InjectMocks
    private AssetDaoBean dao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateVessel() throws AssetDaoException {
        Carrier carrier = new Carrier();
        AssetEntity assetEntity = new AssetEntity();
        assetEntity.setCarrier(carrier);

        List<AssetHistory> assetHistoryList = new ArrayList<AssetHistory>();
        AssetHistory assetHistory = new AssetHistory();
        assetHistoryList.add(assetHistory);
		assetEntity.setHistories(assetHistoryList);

        dao.createAsset(assetEntity);
        verify(em).persist(assetEntity);
    }

    @Test
    public void testGetVesselById() throws AssetDaoException {
        Long id = 1L;
        AssetEntity entity = new AssetEntity();
        entity.setId(id);
        when(em.find(AssetEntity.class, id)).thenReturn(entity);

        AssetEntity result = dao.getAssetById(id);

        verify(em).find(AssetEntity.class, id);
        assertSame(id, result.getId());
    }

    @Test
    public void testUpdateVessel() throws AssetDaoException {
    	Long id = 11L;

        AssetEntity assetEntity = new AssetEntity();
        assetEntity.setId(id);
        List<AssetHistory> historyList = new ArrayList<AssetHistory>();
        AssetHistory history = new AssetHistory();
        historyList.add(history);

        AssetEntity result = new AssetEntity();
        result.setId(id);
        when(em.merge(assetEntity)).thenReturn(result);

        AssetEntity resultEntity = dao.updateAsset(assetEntity);

        verify(em).merge(assetEntity);
        assertSame(id, resultEntity.getId());
    }

    @Test
    public void testDeleteVessel() throws AssetDaoException {
        //em.remove(arg0);
    }

    @Test
    public void testGetVesselList() throws AssetDaoException {
        TypedQuery<AssetEntity> query = mock(TypedQuery.class);
        when(em.createNamedQuery(UvmsConstants.ASSET_FIND_ALL, AssetEntity.class)).thenReturn(query);

        List<AssetEntity> dummyResult = new ArrayList<AssetEntity>();
        when(query.getResultList()).thenReturn(dummyResult);

        List<AssetEntity> result = dao.getAssetListAll();

        verify(em).createNamedQuery(UvmsConstants.ASSET_FIND_ALL, AssetEntity.class);
        verify(query).getResultList();
        assertSame(dummyResult, result);
    }

    @Test
    public void testGetAssetListByGuids() throws AssetDaoException {
        TypedQuery<AssetHistory> query = mock(TypedQuery.class);
        List<String> ids = new ArrayList<>();
        ids.add("test-123");
        ids.add("test-456");
        query.setParameter("guids", ids);

        List<AssetHistory> dummyResult = new ArrayList<AssetHistory>();
        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setGuid("test-123");
        AssetHistory assetHistory2 = new AssetHistory();
        assetHistory2.setGuid("test-456");

        dummyResult.add(assetHistory);
        dummyResult.add(assetHistory2);


        when(em.createNamedQuery(UvmsConstants.ASSETHISTORY_FIND_BY_GUIDS, AssetHistory.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(dummyResult);

        when(dao.getAssetListByAssetGuids(ids)).thenReturn(dummyResult);
        List<AssetHistory> result = dao.getAssetListByAssetGuids(ids);

        verify(query).getResultList();
        assertSame(dummyResult, result);
    }
}