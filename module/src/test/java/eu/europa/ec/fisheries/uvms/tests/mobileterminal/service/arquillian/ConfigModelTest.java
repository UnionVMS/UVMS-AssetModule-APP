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
package eu.europa.ec.fisheries.uvms.tests.mobileterminal.service.arquillian;

import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.ConfigList;
import eu.europa.ec.fisheries.schema.mobileterminal.config.v1.TerminalSystemType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.PluginService;
import eu.europa.ec.fisheries.uvms.asset.bean.ConfigServiceBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.bean.ConfigServiceBeanMT;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.ChannelDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.dao.MobileTerminalPluginDaoBean;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.MobileTerminalPlugin;
import eu.europa.ec.fisheries.uvms.mobileterminal.mapper.PluginMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PowerMockIgnore({"javax.management.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*"})		//magic line to fix powermock java 11 issues
@RunWith(PowerMockRunner.class)
@PrepareForTest({PluginMapper.class, ConfigServiceBean.class})
public class ConfigModelTest {

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

	@InjectMocks
    private ConfigServiceBeanMT testModelBean;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
        when(siriusone.getPluginSatelliteType()).thenReturn("IRIDIUM");
        when(twostage.getPluginSatelliteType()).thenReturn("INMARSAT_C");
	}

	@Test
	public void testGetAllTerminalSystemsEmpty()  {
		List<MobileTerminalPlugin> pluginList = new ArrayList<>();
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);

		List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();

		assertEquals(0, terminalSystems.size());
	}

	@Test
	public void testGetAllTerminalSystems()  {
		List<MobileTerminalPlugin> pluginList = new ArrayList<>();
		pluginList.add(siriusone);
		pluginList.add(twostage);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(pluginList);

		List<TerminalSystemType> terminalSystems = testModelBean.getAllTerminalSystems();
		assertEquals(2, terminalSystems.size());

		for(TerminalSystemType system : terminalSystems) {
			assertNotNull(system.getType());
		}
	}

	@Test
	public void testGetAllTerminalSystemsException()  {
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
	public void testUpdatePluginEquals()  {
		String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);
		when(mobileTerminalPluginDao.getPluginByServiceName(serviceName)).thenReturn(siriusone);
		mockStatic(PluginMapper.class);
		when(PluginMapper.equals(siriusone, pluginType)).thenReturn(true);

		MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);

		assertNotNull(resEntity);
	}

	@Test
	public void testUpdatePluginUpdate()  {
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
	public void testUpdateNoPluginFound()  {
		String serviceName = "serviceName";
		when(pluginType.getServiceName()).thenReturn(serviceName);

		try{
			MobileTerminalPlugin fetched  = mobileTerminalPluginDao.getPluginByServiceName(serviceName);
			MobileTerminalPlugin resEntity = testModelBean.updatePlugin(pluginType);
			assertNull(resEntity);
		}
		catch (Throwable t) {
			Assert.fail();
		}
	}

	@Test
	public void testInactivatePluginsException()  {
		List<MobileTerminalPlugin> list = mobileTerminalPluginDao.getPluginList();
		Assert.assertNotNull(list);

		Map<String, PluginService> map = new HashMap<>();
		testModelBean.inactivatePlugins(map);
	}

	@Test
	public void testInactivatePluginsNoPlugin()  {
		Map<String, PluginService> map = new HashMap<>();
		List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);

		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}

	@Test
	public void testInactivatePluginsInactive()  {
		String serviceName = "serviceName";
		Map<String, PluginService> map = new HashMap<>();
		List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(false);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);

		List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(1, resEntityList.size());
		for(MobileTerminalPlugin p : resEntityList) {
			assertFalse(p.getPluginInactive());
		}
	}

	@Test
	public void testInactivePluginsExsist()  {
		String serviceName = "serviceName";
		Map<String, PluginService> map = new HashMap<>();
		List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(false);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);
		map.put(serviceName, pluginType);

		List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}

	@Test
	public void testInactivePluginsAlreadyInactive()  {
		String serviceName = "serviceName";
		Map<String, PluginService> map = new HashMap<>();
		List<MobileTerminalPlugin> entityList = new ArrayList<>();
		when(siriusone.getPluginServiceName()).thenReturn(serviceName);
		when(siriusone.getPluginInactive()).thenReturn(true);
		entityList.add(siriusone);
		when(mobileTerminalPluginDao.getPluginList()).thenReturn(entityList);

		List<MobileTerminalPlugin> resEntityList = testModelBean.inactivatePlugins(map);
		assertNotNull(resEntityList);
		assertEquals(0, resEntityList.size());
	}

}
