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
package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.message.event.AssetMessageEvent;
import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetException;
import eu.europa.ec.fisheries.uvms.asset.service.bean.AssetIdsForGroupGuidEventBean;
import eu.europa.ec.fisheries.uvms.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.dao.AssetGroupDao;
import eu.europa.ec.fisheries.uvms.dao.exception.AssetGroupDaoException;
import eu.europa.ec.fisheries.uvms.entity.asset.types.CarrierSourceEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.ContactInfoSourceEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.NotesSourceEnum;
import eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroupField;
import eu.europa.ec.fisheries.uvms.entity.model.AssetEntity;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.Carrier;
import eu.europa.ec.fisheries.uvms.entity.model.ContactInfo;
import eu.europa.ec.fisheries.uvms.entity.model.Notes;
import eu.europa.ec.fisheries.uvms.entity.model.NotesActivityCode;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetIdsForGroupRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdsForGroupGuidQueryElement;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListPagination;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum.DERMERSAL;

@RunWith(Arquillian.class)
public class AssetIdsForGroupGuidEventBeanIT extends TransactionalTests {

    @EJB
    private AssetIdsForGroupGuidEventBean assetIdsForGroupGuidEventBean;

    @EJB
    private AssetDao assetDao;

    @Inject
    AssetGroupDao assetGroupDao;

    private final LocalDate todayLD = LocalDate.now();
    private final Date today = Date.from(todayLD.atStartOfDay(ZoneId.systemDefault()).toInstant());
    private final Date tomorrow = Date.from(todayLD.plus(1, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault()).toInstant());

    @Inject
    InterceptorForTest interceptorForTests;

    @After
    public void teardown() {
        interceptorForTests.recycle();
    }


    @Test
    @OperateOnDeployment("normal")
    public void testSetup() {
        Assert.assertNotNull(assetIdsForGroupGuidEventBean);
    }


    @Test
    @OperateOnDeployment("normal")
    public void testFindAndSendAssetIdsForGroupGuid() throws AssetException {


        AssetHistory ah1 = makeLongAsset( "GRC", DERMERSAL) ;
        AssetHistory ah2 = makeShortAsset("GRC", DERMERSAL);

        AssetEntity assetEntity = makeAsset();
        List<AssetHistory> assetHistoryList = new ArrayList<>();
        assetHistoryList.add(ah1);
        assetHistoryList.add(ah2);
        assetEntity.setHistories(assetHistoryList);
        ah1.setAsset(assetEntity);
        ah2.setAsset(assetEntity);

        assetEntity = assetDao.createAsset(assetEntity);
        Assert.assertNotNull(assetEntity);

        eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup gStaticGroupA = makeAssetGroup(false, "StaticGroupA");
        addField(gStaticGroupA, "GUID", assetEntity.getGuid());
        assetGroupDao.createAssetGroup(gStaticGroupA);

        AssetIdsForGroupGuidQueryElement assetIdsForGroupGuidQueryElement = new AssetIdsForGroupGuidQueryElement();
        assetIdsForGroupGuidQueryElement.setAssetGuid(gStaticGroupA.getGuid());
        assetIdsForGroupGuidQueryElement.setOccurrenceDate(tomorrow);
        AssetIdsForGroupRequest request = new AssetIdsForGroupRequest();
        request.setAssetIdsForGroupGuidQueryElement(assetIdsForGroupGuidQueryElement);
        AssetListPagination assetListPagination = new AssetListPagination();
        assetListPagination.setListSize(15);
        assetListPagination.setPage(1);
        assetIdsForGroupGuidQueryElement.setPagination(assetListPagination);

        assetIdsForGroupGuidEventBean.findAndSendAssetIdsForGroupGuid(new AssetMessageEvent(null,request));
        Assert.assertFalse(interceptorForTests.isFailed());
    }

    @Test
    @OperateOnDeployment("normal")
    public void testFindAndSendAssetIdsForGroupGuidWithInvalidPage() throws AssetException {


        AssetHistory ah1 = makeLongAsset( "GRC", DERMERSAL) ;
        AssetHistory ah2 = makeShortAsset("GRC", DERMERSAL);

        AssetEntity assetEntity = makeAsset();
        List<AssetHistory> assetHistoryList = new ArrayList<>();
        assetHistoryList.add(ah1);
        assetHistoryList.add(ah2);
        assetEntity.setHistories(assetHistoryList);
        ah1.setAsset(assetEntity);
        ah2.setAsset(assetEntity);

        assetEntity = assetDao.createAsset(assetEntity);
        Assert.assertNotNull(assetEntity);

        eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup gStaticGroupA = makeAssetGroup(false, "StaticGroupA");
        addField(gStaticGroupA, "GUID", assetEntity.getGuid());
        assetGroupDao.createAssetGroup(gStaticGroupA);

        AssetIdsForGroupGuidQueryElement assetIdsForGroupGuidQueryElement = new AssetIdsForGroupGuidQueryElement();
        assetIdsForGroupGuidQueryElement.setAssetGuid(gStaticGroupA.getGuid());
        assetIdsForGroupGuidQueryElement.setOccurrenceDate(tomorrow);
        AssetIdsForGroupRequest request = new AssetIdsForGroupRequest();
        request.setAssetIdsForGroupGuidQueryElement(assetIdsForGroupGuidQueryElement);
        AssetListPagination assetListPagination = new AssetListPagination();
        assetListPagination.setListSize(15);
        assetListPagination.setPage(0);
        assetIdsForGroupGuidQueryElement.setPagination(assetListPagination);

        assetIdsForGroupGuidEventBean.findAndSendAssetIdsForGroupGuid(new AssetMessageEvent(null,request));
        Assert.assertTrue(interceptorForTests.isFailed());
    }

    private eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup makeAssetGroup(boolean isDynamic, String name) {
        eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup g = new eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup();
        g.setDynamic(isDynamic);
        g.setName(name);
        g.setOwner("rep_power");
        return g;
    }

    private void addField(eu.europa.ec.fisheries.uvms.entity.assetgroup.AssetGroup group, String field, String value) {
        AssetGroupField f = new AssetGroupField();
        f.setAssetGroup(group);
        f.setField(field);
        f.setValue(value);
        group.getFields().add(f);
    }

    private AssetEntity makeAsset() {
        AssetEntity a = new AssetEntity();
        a.setCFR("CFR");
        a.setGfcm("GFCM");
        a.setIccat("ICCAT");
        a.setIMO("123");
        a.setIRCS("IRCS");
        a.setMMSI("456");
        a.setUvi("UVI");
        addRequiredReferences(a);
        return a;
    }

    private void addRequiredReferences(AssetEntity assetEntity){

        Notes notes = new Notes();
        notes.setContact("TEST");
        notes.setAsset(assetEntity);
        notes.setActivity("naID");
        notes.setSource(NotesSourceEnum.INTERNAL);
        List<Notes> notesList = new ArrayList<>();
        notesList.add(notes);
        assetEntity.setNotes(notesList);

        Carrier carrier = new Carrier();
        carrier.setActive(true);
        carrier.setSource(CarrierSourceEnum.INTERNAL);
        assetEntity.setCarrier(carrier);

        NotesActivityCode activityCode = new NotesActivityCode();
        activityCode.setId("naID");
        em.persist(activityCode);
    }
    private AssetHistory makeAssetHistory(String countryOfRegistration, GearFishingTypeEnum gearType) {
        AssetHistory h = new AssetHistory();
        h.setCountryOfRegistration(countryOfRegistration);
        h.setName("Name");
        h.setPortOfRegistration("port");
        h.setExternalMarking("em");
        h.setPowerOfMainEngine(BigDecimal.valueOf(100L));
        h.setType(GearFishingTypeEnum.UNKNOWN);
        h.setLicenceType("LT");
        h.setType(gearType);
        h.setActive(true);
        h.setDateOfEvent(today);
        addContactInfo(h,"testContactName"+countryOfRegistration);
        return h;
    }


    private static void addContactInfo(AssetHistory ah,String name){
        List<ContactInfo> contactInfoList = new ArrayList<>();
        ContactInfo contactInfo1 = new ContactInfo();
        contactInfo1.setName(name);
        contactInfo1.setSource(ContactInfoSourceEnum.INTERNAL);
        contactInfo1.setAsset(ah);
        contactInfoList.add(contactInfo1);
        ah.setContactInfo(contactInfoList);
    }

    private AssetHistory makeShortAsset(String countryOfRegistration, GearFishingTypeEnum gearType) {
        AssetHistory h = makeAssetHistory(countryOfRegistration, gearType);
        h.setLengthOverAll(BigDecimal.valueOf(5L));
        return h;
    }

    private AssetHistory makeLongAsset(String countryOfRegistration, GearFishingTypeEnum gearType) {
        AssetHistory h = makeAssetHistory(countryOfRegistration, gearType);
        h.setLengthOverAll(BigDecimal.valueOf(25L));
        return h;
    }

}
