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
package eu.europa.fisheries.uvms.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.DNIDListDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.bean.OceanRegionDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.ConfigDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.InputArgumentException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.NoEntityFoundException;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.exception.TerminalDaoException;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.DNIDList;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity2.OceanRegion;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PluginMapper;
import eu.europa.ec.fisheries.uvms.mobileterminal.model.exception.MobileTerminalModelException;
import eu.europa.ec.fisheries.uvms.mobileterminal.service.bean.ConfigServiceBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginMapper.class, ConfigServiceBean.class})
public class ConfigModelTest {


	@Mock
	private OceanRegionDaoBean oceanRegionDao;

	@Mock
	private MobileTerminalPluginDaoBean mobileTerminalPluginDao;

	@Mock
	private PluginService pluginType;

	@Mock
    private MobileTerminalPlugin siriusone;

	@Mock
    private MobileTerminalPlugin twostage;

	@Mock
	private ChannelDaoBean channelDao;

	@Mock
	private DNIDListDaoBean dnidListDao;

	@InjectMocks
    private ConfigServiceBean testModelBean;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
        when(siriusone.getPluginSatelliteType()).thenReturn("IRIDIUM");
        when(twostage.getPluginSatelliteType()).thenReturn("INMARSAT_C");
	}


	@Test
	public void testGetAllTerminalSystemsEmpty() throws MobileTerminalModelException {
		List<MobileTerminalPlugin> pluginList = new ArrayList<>();
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);
		List<OceanRegion> oceanRegionList = new ArrayList<>();
		when(oceanRegionDao.getOceanRegionList()).thenReturn(oceanRegionList);

		List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();

		assertEquals(0, terminalSystems.size());
	}



	@Test
	public void testGetAllTerminalSystems() throws MobileTerminalModelException {
		List<MobileTerminalPlugin> pluginList = new ArrayList<>();
		pluginList.add(siriusone);
		pluginList.add(twostage);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);
		List<OceanRegion> oceanRegionList = new ArrayList<>();
		when(oceanRegionDao.getOceanRegionList()).thenReturn(oceanRegionList);

		List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();

		assertEquals(2, terminalSystems.size());

		for(TerminalSystemType system : terminalSystems) {
			assertNotNull(system.getType());
		}
	}


	@Test
	public void testGetAllTerminalSystemsException() throws MobileTerminalModelException {

		List<MobileTerminalPlugin> list = mobileTerminalPluginDao.getPluginList();
		Assert.assertNotNull(list);
		List<TerminalSystemType> list2 = testModelBean.getAllTerminalSystems();
		Assert.assertNotNull(list2);
	}

	@Test
	public void testGetConfigValues() {
		List<ConfigList> configValues = testModelBean.getConfigValues();
		assertNotNull(configValues);
		assertEquals(3, configValues.size());

		for(ConfigList config : configValues) {
			assertNotNull(config.getName());
		}
	}

	@Test
	public void testUpsertPluginsEmptyInput() throws MobileTerminalModelException {
		List<PluginService> pluginList = new ArrayList<>();
		List<Plugin> plugins = testModelBean.upsertPlugins(pluginList);
		assertEquals(pluginList.size(), plugins.size());
	}

	@Test(expected=InputArgumentException.class)
	public void testUpsertPluginsNull() throws MobileTerminalModelException {
		testModelBean.upsertPlugins(null);
	}

	@Test(expected=InputArgumentException.class)
	public void testUpsertPluginsNonValidInput() throws MobileTerminalModelException {
		List<PluginService> pluginList = new ArrayList<>();
		pluginList.add(pluginType);
		testModelBean.upsertPlugins(pluginList);
	}

	@Test
	public void testUpdatePluginEquals() throws TerminalDaoException {
		String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusone);
		mockStatic(PluginMapper.class);
		when(PluginMapper.equals(siriusone, pluginType)).thenReturn(true);

		MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);

		assertNotNull(resEntity);
	}

	@Test
	public void testUpdatePluginUpdate() throws TerminalDaoException {
		String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusone);
		mockStatic(PluginMapper.class);
		when(PluginMapper.equals(siriusone, pluginType)).thenReturn(false);
		mockStatic(PluginMapper.class);
		when(PluginMapper.mapModelToEntity(siriusone, pluginType)).thenReturn(siriusone);

		when(mobileTerminalPluginDao.updateMobileTerminalPlugin(any(MobileTerminalPlugin.class))).thenReturn(siriusone);

		MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
		assertNotNull(resEntity);
	}

	@Test
	public void testUpdateNoPluginFound() throws TerminalDaoException {
		String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenThrow(new NoEntityFoundException("No plugin to update"));
		MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);

		assertNull(resEntity);
	}

	@Test
	public void testUpsertPluginsCreate() throws MobileTerminalModelException {
		String pluginLabelName = "serviceLabelName";
		String pluginServiceName = "serviceName";
		String pluginSatelliteType = "satelliteType";
		List<PluginService> pluginList = new ArrayList<>();
		when(pluginType.getLabelName()).thenReturn(pluginLabelName);
		when(pluginType.getServiceName()).thenReturn(pluginServiceName);
		when(pluginType.getSatelliteType()).thenReturn(pluginSatelliteType);
		pluginList.add(pluginType);

		when(mobileTerminalPluginDao.getPluginByServiceName(pluginServiceName)).thenThrow(new NoEntityFoundException("No plugin to update"));

		mockStatic(PluginMapper.class);
		when(PluginMapper.mapModelToEntity(any(PluginService.class))).thenReturn(siriusone);
		when(mobileTerminalPluginDao.createMobileTerminalPlugin(siriusone)).thenReturn(siriusone);

		List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);

		List<Plugin> plugins = testModelBean.upsertPlugins(pluginList);

		assertEquals(pluginList.size(), plugins.size());
	}

	@Test
	public void testInactivatePluginsException() throws ConfigDaoException {

		List<MobileTerminalPlugin> list = mobileTerminalPluginDao.getPluginList();
		Assert.assertNotNull(list);

		Map<String, PluginService> map = new HashMap<>();
		testModelBean.inactivatePlugins(map);
	}

	@Test
	public void testInactivatePluginsNoPlugin() throws ConfigDaoException {
		Map<String, PluginService> map = new HashMap<>();
		List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);

		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}

	@Test
	public void testInactivatePluginsInactive() throws ConfigDaoException {
		String serviceName = "serviceName";
		Map<String, PluginService> map = new HashMap<>();
		List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(false);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);

		List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(1, resEntityList.size());
		for(Plugin p : resEntityList) {
			assertFalse(p.isInactive());
		}
	}

	@Test
	public void testInactivePluginsExsist() throws ConfigDaoException {
		String serviceName = "serviceName";
		Map<String, PluginService> map = new HashMap<>();
		List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(false);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);
		map.put(serviceName, pluginType);

		List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}

	@Test
	public void testInactivePluginsAlreadyInactive() throws ConfigDaoException {
		String serviceName = "serviceName";
		Map<String, PluginService> map = new HashMap<>();
		List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(true);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);

		List<Plugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}

	@Test
	public void testCheckDNIDListChangeNotChanged() throws Exception {
		String serviceName = "serviceName";
		List<String> activeDnidList = new ArrayList<>();
		List<DNIDList> dnidLists = new ArrayList<>();

		when(channelDao.getActiveDNID(serviceName)).thenReturn(activeDnidList);
		when(dnidListDao.getDNIDList(serviceName)).thenReturn(dnidLists);

		boolean isChanged = testModelBean.checkDNIDListChange(serviceName);
		assertFalse(isChanged);
	}

	@Test
	public void testCheckDNIDListChangeChanged() throws Exception {
		String serviceName = "serviceName";
		List<String> activeDnidList = Collections.singletonList("TEST");
		List<DNIDList> dnidLists = new ArrayList<>();
		DNIDList dnidList = new DNIDList();

		when(channelDao.getActiveDNID(serviceName)).thenReturn(activeDnidList);
		when(dnidListDao.getDNIDList(serviceName)).thenReturn(dnidLists);

		doNothing().when(dnidListDao).removeByPluginName(serviceName);

		when(dnidListDao.create(dnidList)).thenReturn(new DNIDList());

		boolean isChanged = testModelBean.checkDNIDListChange(serviceName);
		assertTrue(isChanged);
	}

}
