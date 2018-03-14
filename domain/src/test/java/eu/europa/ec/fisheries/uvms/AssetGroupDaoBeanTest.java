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

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import eu.europa.ec.fisheries.uvms.asset.types.ConfigSearchFieldEnum;
import eu.europa.ec.fisheries.uvms.dao.bean.AssetGroupDaoBean;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import eu.europa.ec.fisheries.uvms.entity.model.AssetGroupEntity;


@RunWith(MockitoJUnitRunner.class)
public class AssetGroupDaoBeanTest {

	@Mock
	EntityManager em;
	
	@InjectMocks
	private AssetGroupDaoBean dao;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	private static final String TEST_USER = "testUser";



	@Test
	public void testCreateVesselGroup() throws AssetGroupDaoException {
		AssetGroupEntity group = getFiltergroup(TEST_USER, 1);
		
		dao.createAssetGroup(group);
		verify(em).persist(group);
	}
	
	@Test
	public void testVesselGroupList() throws AssetGroupDaoException {
		TypedQuery<AssetGroupEntity> query = mock(TypedQuery.class);
		when(em.createNamedQuery(UvmsConstants.GROUP_ASSET_FIND_ALL, AssetGroupEntity.class)).thenReturn(query);
		
		List<AssetGroupEntity> dummyResult = new ArrayList<AssetGroupEntity>();
		when(query.getResultList()).thenReturn(dummyResult);
		List<AssetGroupEntity> result = dao.getAssetGroupAll();
		
		verify(em).createNamedQuery(UvmsConstants.GROUP_ASSET_FIND_ALL, AssetGroupEntity.class);
		verify(query).getResultList();
		assertSame(dummyResult, result);
	}
	
	@Test
	public void testVesselGroupListByUser() throws AssetGroupDaoException {
		TypedQuery<AssetGroupEntity> query = mock(TypedQuery.class);
		when(em.createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER, AssetGroupEntity.class)).thenReturn(query);
		
		List<AssetGroupEntity> dummyResult = new ArrayList<AssetGroupEntity>();
		when(query.getResultList()).thenReturn(dummyResult);
		
		List<AssetGroupEntity> result = dao.getAssetGroupByUser(TEST_USER);
		
		verify(em).createNamedQuery(UvmsConstants.GROUP_ASSET_BY_USER, AssetGroupEntity.class);
		verify(query).getResultList();
		assertSame(dummyResult, result);
	}




	public static AssetGroupEntity getFiltergroup(String user, long id) {
		AssetGroupEntity group = new AssetGroupEntity();
		group.setGlobal(false);
		group.setName("GROUPNAME");
		group.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));
		group.setUpdatedBy("DAOTEST");
		group.setOwner(user);

		List<AssetGroupField> filters = new ArrayList<AssetGroupField>();
		AssetGroupField filter = new AssetGroupField();
		filter.setId(id+10);
		filter.setAssetGroup(group);
		filter.setField(ConfigSearchFieldEnum.NAME.name());
		filter.setValue("ASSET-NAME");

		filter.setUpdatedBy("DAOTEST");
		filter.setUpdateTime(LocalDateTime.now(Clock.systemUTC()));


		return group;
	}





}