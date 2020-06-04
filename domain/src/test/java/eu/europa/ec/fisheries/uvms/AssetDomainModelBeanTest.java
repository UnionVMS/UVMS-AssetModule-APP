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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetDaoException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.asset.model.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.bean.AssetDomainModelBean;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetGroupsForAssetQueryElement;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
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
        when(ModelToEntityMapper.mapToNewAssetEntity(asset, new ArrayList<String>())).thenReturn(vesselEntity);
        when(vesselDao.createAsset(any(VesselEntity.class))).thenReturn(vesselEntity);
        when(EntityToModelMapper.toVesselFromEntity(any(VesselEntity.class))).thenReturn(asset);

        Vessel result = model.createAsset(asset);
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
        when(mapper.toVessel(any(VesselEntity.class))).thenReturn(asset);
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
        when(mapper.toVessel(any(VesselEntity.class))).thenReturn(asset);
        when(vesselDao.getAssetByCfr(cfr)).thenReturn(entity);

        Vessel result = model.getAssetById(assetId);
        String id = result.getVesselId().getValue();
        assertEquals(cfr, id);*/
    }

    @Test(expected = InputArgumentException.class)
    public void findAssetGroupsForAssetsShouldThrowAnExceptionForNULLRefUuid() throws AssetException {

        List<AssetGroupsForAssetQueryElement> assetGroupsForAssetQueryElementList = new ArrayList<>();
        AssetGroupsForAssetQueryElement element = new AssetGroupsForAssetQueryElement();
        element.setConnectId("ABC");
        AssetId assetId = new AssetId();
        assetId.setValue("Test");
        element.getAssetId().add(assetId);
        assetGroupsForAssetQueryElementList.add(element);
        model.findAssetGroupsForAssets(assetGroupsForAssetQueryElementList);
    }

    @Test(expected = InputArgumentException.class)
    public void findAssetGroupsForAssetsShouldThrowAnExceptionForNULLAssets() throws AssetException {

        List<AssetGroupsForAssetQueryElement> assetGroupsForAssetQueryElementList = new ArrayList<>();
        AssetGroupsForAssetQueryElement element = new AssetGroupsForAssetQueryElement();
        element.setRefUuid("ABC");
        assetGroupsForAssetQueryElementList.add(element);
        model.findAssetGroupsForAssets(assetGroupsForAssetQueryElementList);
    }

    @Test
    public void findHighestFromLowestDateShouldReturnTheFirstLowestDateFromOccurrence() throws AssetException {

        List<AssetHistory> assetHistories = new ArrayList<>();
        Date occurrenceDate = new Date();
        AssetHistory historyBeforeOccurrence1 = addHoursToDate(assetHistories,occurrenceDate,-1);
        addHoursToDate(assetHistories,occurrenceDate,-2);
        addHoursToDate(assetHistories,occurrenceDate,-3);
        addHoursToDate(assetHistories,occurrenceDate,+1);
        addHoursToDate(assetHistories,occurrenceDate,+2);

        AssetHistory highestFromLowestDate = model.findHighestFromLowestDate(occurrenceDate, assetHistories);
        Assert.assertTrue(highestFromLowestDate.getDateOfEvent().equals(historyBeforeOccurrence1.getDateOfEvent()));
    }

    @Test(expected = AssetException.class)
    public void findHighestFromLowestDateShouldThrowExceptionIfNoHistoryLowerThanOccurrenceIsFound() throws AssetException {

        List<AssetHistory> assetHistories = new ArrayList<>();
        Date occurrenceDate = new Date();

        addHoursToDate(assetHistories,occurrenceDate,+1);
        addHoursToDate(assetHistories,occurrenceDate,+2);

        model.findHighestFromLowestDate(occurrenceDate, assetHistories);
    }

    @Test(expected = AssetException.class)
    public void findHighestFromLowestDateShouldThrowExceptionIfOccurrenceDateIsNUll() throws AssetException {

        List<AssetHistory> assetHistories = new ArrayList<>();
        Date occurrenceDate = null;
        model.findHighestFromLowestDate(occurrenceDate, assetHistories);
    }

    @Test(expected = AssetException.class)
    public void findHighestFromLowestDateShouldThrowExceptionIfOnlyOneHistoryHigherThanOccurrenceIsFound() throws AssetException {

        List<AssetHistory> assetHistories = new ArrayList<>();
        Date occurrenceDate = new Date();

        addHoursToDate(assetHistories,occurrenceDate,+1);

        model.findHighestFromLowestDate(occurrenceDate, assetHistories);
    }

    @Test
    public void findHighestFromLowestDateShouldReturnTheFirstLowestDateFromOccurrenceForListSizeOne() throws AssetException {

        List<AssetHistory> assetHistories = new ArrayList<>();
        Date occurrenceDate = new Date();
        AssetHistory historyBeforeOccurrence = addHoursToDate(assetHistories,occurrenceDate,-1);
        AssetHistory highestFromLowestDate = model.findHighestFromLowestDate(occurrenceDate, assetHistories);
        Assert.assertTrue(highestFromLowestDate.getDateOfEvent().equals(historyBeforeOccurrence.getDateOfEvent()));
    }

    private AssetHistory addHoursToDate(List<AssetHistory> assetHistories,Date occurrenceDate,int hoursToAdd){
        AssetHistory history = new AssetHistory();
        history.setDateOfEvent(DateUtils.addHours(new Date(occurrenceDate.getTime()), hoursToAdd));
        assetHistories.add(history);
        return history;
    }
}