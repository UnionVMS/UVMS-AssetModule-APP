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

import java.util.UUID;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.bean.AssetDomainModelBean;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.dao.AssetDao;

@RunWith(MockitoJUnitRunner.class)
public class AssetDomainModelBeanTest {

    @Mock
    AssetDao assetDao;

    @InjectMocks
    private AssetDomainModelBean model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Ignore
    public void testCreateAsset() throws AssetModelException, AssetDaoException {
        Long id = 1L;

        Asset asset = MockData.getAsset(id.intValue());

        AssetEntity assetEntity = new AssetEntity();
        assetEntity.setId(id);
/*
        when(ModelToEntityMapper.mapToNewAssetEntity(transportMeans, new ArrayList<String>())).thenReturn(vesselEntity);
        when(vesselDao.createAsset(any(VesselEntity.class))).thenReturn(vesselEntity);
        when(EntityToModelMapper.toVesselFromEntity(any(VesselEntity.class))).thenReturn(transportMeans);

        Vessel result = model.createAsset(transportMeans);
        assertEquals(id.toString(), result.getVesselId().getValue());*/
    }

    @Test
    @Ignore
    public void testGetAssetList() throws AssetModelException, AssetDaoException {
        String id = "2jsdfljk";
        Asset dto = new Asset();
        AssetId assetId = new AssetId();
        assetId.setType(AssetIdType.GUID);
        assetId.setValue(id);
        dto.setAssetId(assetId);
/*
        when(mapper.toVessel(any(VesselHistorySearch.class))).thenReturn(dto);
        when(vesselDao.getAssetCount(any(String.class), any(List.class), any(Boolean.class))).thenReturn(100L);

        List<VesselHistorySearch> allVesselList = new ArrayList<>();

        VesselHistorySearch vessel = new VesselHistorySearch();
        VesselEntity mockedVessel = MockData.getVesselEntity(1);
        vessel.setAsset(mockedVessel);
        vessel.setGuid(id);
        allVesselList.add(vessel);

        when(vesselDao.getAssetListSearchPaginated(any(Integer.class), any(Integer.class), any(String.class), any(List.class), any(Boolean.class))).thenReturn(allVesselList);

        VesselListQuery query = new VesselListQuery();
        VesselListCriteria criteria = new VesselListCriteria();
        criteria.setIsDynamic(true);
        VesselListPagination pagination = new VesselListPagination();
        pagination.setListSize(new BigInteger("1"));
        pagination.setPage(new BigInteger("1"));
        query.setPagination(pagination);
        query.setVesselSearchCriteria(criteria);

        GetVesselListResponseDto response = model.getAssetList(query);
        List<Vessel> vesselList = response.getAssetList();
        assertSame(allVesselList.size(), vesselList.size());

        when(vesselDao.getAssetListSearchPaginated(any(Integer.class), any(Integer.class), any(String.class), any(List.class), any(Boolean.class))).thenReturn(allVesselList);

        response = model.getAssetList(query);
        vesselList = response.getAssetList();
        assertSame(allVesselList.size(), vesselList.size());
        VesselId resultVesselId = vesselList.get(0).getVesselId();
        assertEquals(id.toString(), resultVesselId.getValue());*/
    }

    @Test
    @Ignore
    public void testGetAssetById() throws AssetDaoException, AssetModelException {
        String id = "ajs32";
        AssetEntity entity = new AssetEntity();
        entity.setGuid(UUID.randomUUID().toString());

        AssetId assetId = new AssetId();
        assetId.setType(AssetIdType.GUID);
        assetId.setValue(id);
        Asset asset = new Asset();
        asset.setAssetId(assetId);
/*
        when(mapper.toVessel(any(VesselEntity.class))).thenReturn(transportMeans);
        when(vesselDao.getAssetById(id.toString())).thenReturn(entity);

        Vessel result = model.getAssetById(assetId);
        String resultGuid = result.getVesselId().getValue();
        assertEquals(id.toString(), resultGuid);*/
    }

    @Test
    @Ignore
    public void testGetAssetByCfr() throws AssetDaoException, AssetModelException {
        String cfr = "cfr001";
        AssetEntity entity = new AssetEntity();
        entity.setCFR(cfr);

        AssetId assetId = new AssetId();
        assetId.setType(AssetIdType.CFR);
        assetId.setValue(cfr);

        Asset asset = new Asset();
        asset.setAssetId(assetId);
/*
        when(mapper.toVessel(any(VesselEntity.class))).thenReturn(transportMeans);
        when(vesselDao.getAssetByCfr(cfr)).thenReturn(entity);

        Vessel result = model.getAssetById(assetId);
        String id = result.getVesselId().getValue();
        assertEquals(cfr, id);*/
    }
}