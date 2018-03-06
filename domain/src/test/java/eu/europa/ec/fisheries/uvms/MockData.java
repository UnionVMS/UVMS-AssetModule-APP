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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.Carrier;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupEntity;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;

import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetDTO;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;

public class MockData {

	private static final String TRUE = "TRUE";
	private static final String FALSE = "FALSE";
	
	public static AssetDTO getAsset(int id) {
		AssetDTO dto = new AssetDTO();
		AssetId assetId = new AssetId();
		assetId.setType(AssetIdType.INTERNAL_ID);
		assetId.setValue(""+id);
		dto.setAssetId(assetId);
		
        dto.setCfr("CFR" + id);
        dto.setCountryCode("SWE" + id);
        dto.setExternalMarking("MARKING" + 1);
        dto.setGrossTonnage(new BigDecimal(id + 0.5));
        dto.setHasIrcs("Y");
        dto.setHasLicense(true);
        dto.setHomePort("PORT" + id);
        
        dto.setIrcs("IRCS-" + id);
        dto.setLengthBetweenPerpendiculars(new BigDecimal(0.5 + id));
        dto.setLengthOverAll(new BigDecimal(2.5 + id));
        dto.setName("ASSET-" + id);
        dto.setOtherGrossTonnage(new BigDecimal(11.5 + id));
        dto.setPowerAux(new BigDecimal(123.4 + id));
        dto.setPowerMain(new BigDecimal(586.2 + id));
        dto.setSafetyGrossTonnage(new BigDecimal(54.3 + id));
        dto.setSource(CarrierSource.INTERNAL);
        dto.setActive(true);

        dto.setGearType("ASSET-TYPE: " + id);
        return dto;
	}
	
	public static AssetEntity getAssetEntity(int id) {
		AssetEntity entity = new AssetEntity();
		entity.setGuid(""+id);
		entity.setId(new Long(id));
		Carrier carrier = new Carrier();
		entity.setCarrier(carrier);
		//entity.setHullmaterial(hullmaterial);
		entity.setCFR("CFR: " + id);
		//entity.setVessDayofcommissioning(vessDayofcommissioning);
		List<AssetHistory> assethistories = new ArrayList<AssetHistory>();
		assethistories.add(getAssethistory(entity, id + 1));
		
		entity.setHistories(assethistories);
		entity.setIMO(String.valueOf(id + 10));
		entity.setIRCS("IRCS: " + id);
		entity.setIrcsIndicator("Y");
		entity.setMMSI(String.valueOf(id + 20));
		//entity.setVessMonthcommissioning(vessMonthcommissioning);
		entity.setUpdateTime(new Date());
		entity.setUpdatedBy("MOCK");
		//entity.setVessYearofcommissioning(vessYearofcommissioning);
		//entity.setVessYearofconstruction(vessYearofconstruction);
		
		return entity;
	}

	public static AssetHistory getAssethistory(AssetEntity entity, int historyId) {
		AssetHistory assetHistory = new AssetHistory();
		assetHistory.setAsset(entity);
		assetHistory.setId(new Long(historyId));
		assetHistory.setName("NAME: " + historyId);
		assetHistory.setPortOfRegistration("Port" + historyId);
		assetHistory.setCountryOfRegistration("SWE");
		assetHistory.setDateOfEvent(new Date());
		return assetHistory;
	}
	
	public static AssetGroupEntity getFiltergroup(String user, String id) {
		AssetGroupEntity group = new AssetGroupEntity();
		group.setGlobal(false);
		group.setGuid(id);
		group.setName("GROUPNAME");
		group.setUpdateTime(new Date());
		group.setUpdatedBy("DAOTEST");
		group.setOwner(user);
		
		List<AssetGroupField> filters = new ArrayList<AssetGroupField>();
		AssetGroupField filter = new AssetGroupField();
		//filter.setId(id+10);
		filter.setAssetGroup(group);
		filter.setField(ConfigSearchField.NAME.name());
		filter.setValue("ASSET-NAME");
		
		filter.setUpdatedBy("DAOTEST");
		filter.setUpdateTime(new Date());
		
		group.getFields().addAll(filters);
		
		return group;
	}
	
	public static eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupWSDL getAssetGroup(String user, String id) {
        eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupWSDL group = new eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupWSDL();
		group.setGuid(id);
		group.setName("GROUPNAME2");
		group.setUser(user);
		AssetGroupSearchField field1 = new AssetGroupSearchField();
		field1.setKey(ConfigSearchField.NAME);
		field1.setValue("ASSET-NAME");
		group.getSearchFields().add(field1);
		return group;
	}
}