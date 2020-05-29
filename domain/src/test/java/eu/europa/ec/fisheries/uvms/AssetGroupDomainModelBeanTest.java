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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelException;
import eu.europa.ec.fisheries.uvms.bean.AssetGroupDomainModelBean;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import eu.europa.ec.fisheries.wsdl.asset.group.ListAssetGroupResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;

@RunWith(MockitoJUnitRunner.class)
public class AssetGroupDomainModelBeanTest {

    @Mock
    AssetGroupDao dao;
    
    @InjectMocks
    private AssetGroupDomainModelBean model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    
    private static final String TEST_USER = "testUser";
    
    @Test
    public void testGetVesselGroup() throws AssetGroupDaoException, AssetModelException {
        String id = "1";
        AssetGroup entity = new AssetGroup();
        entity.setGuid(id);
        entity.getFields().addAll(new ArrayList<>());
        
        when(dao.getAssetGroupByGuid(id)).thenReturn(entity);

        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup result = model.getAssetGroup(id);
        assertEquals(id, result.getGuid());
    }
    
    @Test
    public void testCreateVesselGroup() throws AssetGroupDaoException, AssetModelException {
        String id = "1";

        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup group = MockData.getAssetGroup(TEST_USER, id);
        
        AssetGroup groupEntity = new AssetGroup();
        groupEntity.setGuid(id);
        groupEntity.setName("GROUPNAME2");
        
        when(dao.createAssetGroup(groupEntity)).thenReturn(groupEntity);

        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup result = model.createAssetGroup(group, TEST_USER);
        assertEquals(group.getName(), result.getName());
    }
    
    @Test
    public void testVesselGroupListByUser() throws AssetGroupDaoException, AssetModelException {
    	String id = "1";
        
        List<AssetGroup> filterList = new ArrayList<>();
        when(dao.getAssetGroupByUser(TEST_USER)).thenReturn(filterList);

        List<eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup> groupList = model.getAssetGroupListByUser(TEST_USER);
        assertSame(filterList.size(), groupList.size());

        filterList = new ArrayList<>();
        AssetGroup filtergroup = new AssetGroup();
        filtergroup.setGuid(id);
        filtergroup.getFields().addAll(new ArrayList<>());
        filterList.add(filtergroup);
        
        when(dao.getAssetGroupByUser(TEST_USER)).thenReturn(filterList);

        groupList = model.getAssetGroupListByUser(TEST_USER);
        assertSame(filterList.size(), groupList.size());
        String resultGroupId = groupList.get(0).getGuid();
        assertEquals(id, resultGroupId);
    }
}