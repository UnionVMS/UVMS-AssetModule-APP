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
package eu.europa.ec.fisheries.uvms.mapper;

import eu.europa.ec.fisheries.uvms.MockData;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetDaoMappingException;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetGroupMapperTest {
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	private static final String TEST_USER = "testUser";
	
	@Test
	public void testEntityToGroup() {
		String id = "1";
		AssetGroup entity = MockData.getFiltergroup(TEST_USER, id);

        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup result = AssetGroupMapper.toAssetGroup(entity);
		
		assertEquals(id.toString(), result.getGuid());
		assertSame(entity.getName(), result.getName());
		assertSame(entity.getFields().size(), result.getSearchFields().size());
	}
	
	@Test
	public void testGroupToEntity() throws AssetDaoMappingException {
		String id = "1";
        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup group = MockData.getAssetGroup(TEST_USER, id);
		
		AssetGroup result = AssetGroupMapper.toGroupEntity(group, TEST_USER);
		
		assertSame(group.getName(), result.getName());
		assertSame(group.getUser(), result.getOwner());
		assertSame(group.getSearchFields().size(), result.getFields().size());
		
		List<AssetGroupSearchField> searchFieldList = group.getSearchFields();
		List<AssetGroupField> filterList = result.getFields();
		
		for(AssetGroupSearchField searchField : searchFieldList) {
			ConfigSearchField field = searchField.getKey();
			String value = searchField.getValue();
			for(AssetGroupField f : filterList) {
				if(f.getField().equalsIgnoreCase(field.name())) {
					assertSame(f.getValue(), value);
				}
			}
		}
	}
	
	@Test
	public void testEntityAndGroupToEntity() throws AssetDaoMappingException {
		String id = "2";
		AssetGroup entity = MockData.getFiltergroup(TEST_USER, id);
		String beforeName = entity.getName();
        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup updateWithThis = MockData.getAssetGroup(TEST_USER, id);
		
		AssetGroup newEntity = AssetGroupMapper.toGroupEntity(entity, updateWithThis, TEST_USER);
		
		assertNotSame(beforeName, newEntity.getName());
		assertSame(newEntity.getName(), updateWithThis.getName());
	}
}