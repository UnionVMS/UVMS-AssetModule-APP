/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2020.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.asset.arquillian;

import static eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum.DERMERSAL;
import static eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum.PELAGIC;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Arquillian tests for the {@code AssetGroupDaoBean}.
 */
@RunWith(Arquillian.class)
public class AssetGroupDaoBeanIT extends TransactionalTests {

	private static final String ZERO_GUID = "00000000-0000-0000-0000-000000000000";

	private static final String STATIC_GROUP_A_UUID1 = "93b2105e-45ea-11e7-bec7-4c32759615eb";
	private static final String STATIC_GROUP_A_UUID2 = "1b2d9022-aca0-45eb-84a4-c7951911731d";

	private static final String THREE_ASSETS_UUID1 = "38b495d6-7bd7-4309-93d0-2146429a86b6";
	private static final String THREE_ASSETS_UUID2 = "445cf9a5-1146-4667-ae74-edc3f3be64df";

	private static final String STATIC_GROUPS_COMMON_UUID = "8221ee47-5a6d-4220-a107-eb2b71307d49";

	@Inject
	AssetGroupDao assetGroupDao;

	@Test
	public void testGetAssetGroupForAssetAndHistory() throws AssetGroupDaoException {
		AssetGroup gGreece = makeAssetGroup(true, "Greece");
		addField(gGreece, "FLAG_STATE", "GRC");
		AssetGroup gStaticGroupA = makeAssetGroup(false, "StaticGroupA");
		addField(gStaticGroupA, "GUID", STATIC_GROUP_A_UUID1);
		addField(gStaticGroupA, "GUID", STATIC_GROUP_A_UUID2);
		addField(gStaticGroupA, "GUID", STATIC_GROUPS_COMMON_UUID);
		AssetGroup gGreceOrPortugal = makeAssetGroup(true, "GreceOrPortugal");
		addField(gGreceOrPortugal, "FLAG_STATE", "GRC");
		addField(gGreceOrPortugal, "FLAG_STATE", "PRT");
		AssetGroup gGreeceOrPortugalShort = makeAssetGroup(true, "GreeceOrPortugalShort");
		addField(gGreeceOrPortugalShort, "FLAG_STATE", "GRC");
		addField(gGreeceOrPortugalShort, "FLAG_STATE", "PRT");
		addField(gGreeceOrPortugalShort, "MIN_LENGTH", "0");
		addField(gGreeceOrPortugalShort, "MAX_LENGTH", "11,99");
		AssetGroup gThreeAssets = makeAssetGroup(false, "ThreeAssets");
		addField(gThreeAssets, "GUID", THREE_ASSETS_UUID1);
		addField(gThreeAssets, "GUID", THREE_ASSETS_UUID2);
		addField(gThreeAssets, "GUID", STATIC_GROUPS_COMMON_UUID);
		AssetGroup gShort = makeAssetGroup(true, "Short");
		addField(gShort, "MIN_LENGTH", "0");
		addField(gShort, "MAX_LENGTH", "11,99");
		AssetGroup gPelagic = makeAssetGroup(true, "Pelagic");
		addField(gPelagic, "GEAR_TYPE", "PELAGIC");

		assetGroupDao.createAssetGroup(gGreece);
		assetGroupDao.createAssetGroup(gStaticGroupA);
		assetGroupDao.createAssetGroup(gGreceOrPortugal);
		assetGroupDao.createAssetGroup(gGreeceOrPortugalShort);
		assetGroupDao.createAssetGroup(gThreeAssets);
		assetGroupDao.createAssetGroup(gShort);
		assetGroupDao.createAssetGroup(gPelagic);

		assertGroupIds(
				makeAsset(ZERO_GUID), makeLongAsset(ZERO_GUID, "GRC", DERMERSAL),
				gGreece, gGreceOrPortugal
		);
		assertGroupIds(
				makeAsset(ZERO_GUID), makeShortAsset(ZERO_GUID, "GRC", DERMERSAL),
				gGreece, gGreceOrPortugal, gGreeceOrPortugalShort, gShort
		);
		assertGroupIds(
				makeAsset(ZERO_GUID), makeLongAsset(ZERO_GUID, "ITA", DERMERSAL)
				// NO GROUP
		);
		assertGroupIds(
				makeAsset(ZERO_GUID), makeShortAsset(ZERO_GUID, "PRT", DERMERSAL),
				gGreceOrPortugal, gGreeceOrPortugalShort, gShort
		);
		assertGroupIds(
				makeAsset(ZERO_GUID), makeShortAsset(ZERO_GUID, "ITA", DERMERSAL),
				gShort
		);
		assertGroupIds(
				makeAsset(STATIC_GROUP_A_UUID1), makeLongAsset(ZERO_GUID, "ITA", DERMERSAL),
				gStaticGroupA
		);
		assertGroupIds(
				makeAsset(STATIC_GROUP_A_UUID1), makeShortAsset(ZERO_GUID, "ITA", DERMERSAL),
				gStaticGroupA, gShort
		);
		assertGroupIds(
				makeAsset(STATIC_GROUPS_COMMON_UUID), makeShortAsset(ZERO_GUID, "PRT", DERMERSAL),
				gStaticGroupA, gShort, gGreceOrPortugal, gGreeceOrPortugalShort, gThreeAssets
		);
		assertGroupIds(
				makeAsset(ZERO_GUID), makeLongAsset(ZERO_GUID, "ITA", PELAGIC),
				gPelagic
		);
		assertGroupIds(
				makeAsset(ZERO_GUID), makeShortAsset(ZERO_GUID, "GRC", PELAGIC),
				gPelagic, gGreece, gGreceOrPortugal, gGreeceOrPortugalShort, gShort
		);
	}

	private AssetGroup makeAssetGroup(boolean isDynamic, String name) throws AssetGroupDaoException {
		AssetGroup g = new AssetGroup();
		g.setDynamic(isDynamic);
		g.setName(name);
		g.setOwner("rep_power");
		return g;
	}

	private void addField(AssetGroup group, String field, String value) {
		AssetGroupField f = new AssetGroupField();
		f.setAssetGroup(group);
		f.setField(field);
		f.setValue(value);
		group.getFields().add(f);
	}

	private AssetEntity makeAsset(String guid) {
		AssetEntity a = new AssetEntity();
		a.setGuid(guid);
		a.setCFR("CFR");
		a.setGfcm("GFCM");
		a.setIccat("ICCAT");
		a.setIMO("123");
		a.setIRCS("IRCS");
		a.setMMSI("456");
		a.setUvi("UVI");
		a.setHistories(new ArrayList<>());
		return a;
	}

	private AssetHistory makeAssetHistory(String guid, String countryOfRegistration, GearFishingTypeEnum gearType) {
		AssetHistory h = new AssetHistory();
		h.setGuid(guid);
		h.setCountryOfRegistration(countryOfRegistration);
		h.setName("Name");
		h.setPortOfRegistration("port");
		h.setExternalMarking("em");
		h.setPowerOfMainEngine(BigDecimal.valueOf(100L));
		h.setType(GearFishingTypeEnum.UNKNOWN);
		h.setLicenceType("LT");
		h.setType(gearType);
		return h;
	}

	private AssetHistory makeShortAsset(String guid, String countryOfRegistration, GearFishingTypeEnum gearType) {
		AssetHistory h = makeAssetHistory(guid, countryOfRegistration, gearType);
		h.setLengthOverAll(BigDecimal.valueOf(5L));
		return h;
	}

	private AssetHistory makeLongAsset(String guid, String countryOfRegistration, GearFishingTypeEnum gearType) {
		AssetHistory h = makeAssetHistory(guid, countryOfRegistration, gearType);
		h.setLengthOverAll(BigDecimal.valueOf(25L));
		return h;
	}

	private void assertGroupIds(AssetEntity a, AssetHistory h, AssetGroup... expectedGroups) {
		List<String> actualIds = assetGroupDao.getAssetGroupForAssetAndHistory(a,h);
		Set<String> actual = new HashSet<>(actualIds);
		Set<String> expected = Arrays.stream(expectedGroups).map(AssetGroup::getGuid).collect(Collectors.toSet());
		assertEquals(expected, actual);
	}
}
