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
package eu.europa.ec.fisheries.uvms.asset.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import javax.jms.Message;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroupSearchField;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.module.PingRequest;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

@RunWith(Arquillian.class)
public class AssetEventQueueTest extends AbstractMessageTest {

    private JMSHelper jmsHelper = new JMSHelper();

    @Test
    @RunAsClient
    public void pingTest() throws Exception {
        PingRequest request = new PingRequest();
        request.setMethod(AssetModuleMethod.PING);
        String requestString = JAXBMarshaller.marshallJaxBObjectToString(request);
        String correlationId = jmsHelper.sendAssetMessage(requestString);
        Message response = jmsHelper.listenForResponse(correlationId);
        assertThat(response, is(notNullValue()));
    }
    
    @Test
    @RunAsClient
    public void getAssetByCFRTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        // TODO Find better solution, this is needed due to async jms call
        Thread.sleep(5000);
        Asset assetById = jmsHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);
        
        assertThat(assetById, is(notNullValue()));
        assertThat(assetById.getCfr(), is(asset.getCfr()));
        assertThat(assetById.getName(), is(asset.getName()));
        assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
        assertThat(assetById.getIrcs(), is(asset.getIrcs()));
    }
    
    @Test
    @RunAsClient
    public void getAssetByIRCSTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        Thread.sleep(5000);
        Asset assetById = jmsHelper.getAssetById(asset.getIrcs(), AssetIdType.IRCS);

        assertThat(assetById, is(notNullValue()));
        assertThat(assetById.getCfr(), is(asset.getCfr()));
        assertThat(assetById.getName(), is(asset.getName()));
        assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
        assertThat(assetById.getIrcs(), is(asset.getIrcs()));

        assertEquals(AssetIdType.GUID, assetById.getAssetId().getType());
        assertEquals(asset.getAssetId().getGuid(), asset.getAssetId().getGuid());
        assertEquals(asset.getAssetId().getGuid(), asset.getAssetId().getValue()); //since guid and value are supposed t obe the same
    }
    
    @Test
    @RunAsClient
    public void getAssetByMMSITest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        Thread.sleep(5000);
        Asset assetById = jmsHelper.getAssetById(asset.getMmsiNo(), AssetIdType.MMSI);
        
        assertThat(assetById, is(notNullValue()));
        assertThat(assetById.getCfr(), is(asset.getCfr()));
        assertThat(assetById.getName(), is(asset.getName()));
        assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
        assertThat(assetById.getIrcs(), is(asset.getIrcs()));
    }
    
    @Test
    @RunAsClient
    public void getAssetListByQueryTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        Thread.sleep(5000);
        
        AssetListQuery assetListQuery = AssetTestHelper.createBasicAssetQuery();
        AssetListCriteriaPair assetListCriteriaPair = new AssetListCriteriaPair();
        assetListCriteriaPair.setKey(ConfigSearchField.FLAG_STATE);
        assetListCriteriaPair.setValue(asset.getCountryCode());
        assetListQuery.getAssetSearchCriteria().getCriterias().add(assetListCriteriaPair);
        
        List<Asset> assets = jmsHelper.getAssetByAssetListQuery(assetListQuery);
        assertTrue(assets.stream().filter(a -> asset.getCfr() == asset.getCfr()).count() > 0);
    }
    
    @Test
    @RunAsClient
    public void upsertAssetTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        jmsHelper.upsertAsset(asset);
        Thread.sleep(5000);

        String newName = "Name upserted";
        asset.setName(newName);
        jmsHelper.upsertAsset(asset);
        Thread.sleep(5000);
        
        Asset assetById = jmsHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);
        
        assertThat(assetById, is(notNullValue()));
        assertThat(assetById.getCfr(), is(asset.getCfr()));
        assertThat(assetById.getName(), is(newName));
        assertThat(assetById.getExternalMarking(), is(asset.getExternalMarking()));
        assertThat(assetById.getIrcs(), is(asset.getIrcs()));
    }
    
    @Test
    @RunAsClient
    public void assetSourceTest() throws Exception {
        Asset asset = AssetTestHelper.createBasicAsset();
        asset.setSource(CarrierSource.INTERNAL);
        jmsHelper.upsertAsset(asset);
        Thread.sleep(5000);
        
        Asset fetchedAsset = jmsHelper.getAssetById(asset.getCfr(), AssetIdType.CFR);
        assertThat(fetchedAsset.getSource(), is(asset.getSource()));
    }
    
//    @Test
//    @RunAsClient
//    public void getAssetGroupListByUserTest() throws Exception {
//        AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
//        
//        Asset asset1 = AssetTestHelper.createTestAsset();
//        Asset asset2 = AssetTestHelper.createTestAsset();
//        
//        // Add assets to group
//        AssetGroupSearchField assetGroupSearchField1 = new AssetGroupSearchField();
//        assetGroupSearchField1.setKey(ConfigSearchField.GUID);
//        assetGroupSearchField1.setValue(asset1.getAsset().getGuid());
//        assetGroup.getSearchFields().add(assetGroupSearchField1);
//    
//        AssetGroupSearchField assetGroupSearchField2 = new AssetGroupSearchField();
//        assetGroupSearchField2.setKey(ConfigSearchField.GUID);
//        assetGroupSearchField2.setValue(asset2.getAsset().getGuid());
//        assetGroup.getSearchFields().add(assetGroupSearchField2);
//    
//        // Create Group
//        assetGroup = AssetTestHelper.createAssetGroup(assetGroup);
//
//        List<AssetGroup> assetGroups = AssetJMSHelper.getAssetGroupByUser(assetGroup.getUser());
//        
//        assertTrue(assetGroups.contains(assetGroup));
//    }
//    
//    @Test
//    @RunAsClient
//    public void getAssetGroupByAssetGuidTest() throws Exception {
//        AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
//        
//        Asset asset1 = AssetTestHelper.createTestAsset();
//        
//        // Add asset to group
//        AssetGroupSearchField assetGroupSearchField1 = new AssetGroupSearchField();
//        assetGroupSearchField1.setKey(ConfigSearchField.GUID);
//        assetGroupSearchField1.setValue(asset1.getAsset().getGuid());
//        assetGroup.getSearchFields().add(assetGroupSearchField1);
//    
//        // Create Group
//        assetGroup = AssetTestHelper.createAssetGroup(assetGroup);
//
//        List<AssetGroup> assetGroups = AssetJMSHelper.getAssetGroupListByAssetGuid(asset1.getAsset().getGuid());
//        
//        assertTrue(assetGroups.contains(assetGroup));
//    }
//    
//    @Test
//    @RunAsClient
//    public void getAssetListByAssetGroups() throws Exception {
//        AssetGroup assetGroup = AssetTestHelper.createBasicAssetGroup();
//        
//        Asset asset1 = AssetTestHelper.createTestAsset();
//        Asset asset2 = AssetTestHelper.createTestAsset();
//        
//        // Add assets to group
//        AssetGroupSearchField assetGroupSearchField1 = new AssetGroupSearchField();
//        assetGroupSearchField1.setKey(ConfigSearchField.GUID);
//        assetGroupSearchField1.setValue(asset1.getAsset().getGuid());
//        assetGroup.getSearchFields().add(assetGroupSearchField1);
//    
//        AssetGroupSearchField assetGroupSearchField2 = new AssetGroupSearchField();
//        assetGroupSearchField2.setKey(ConfigSearchField.GUID);
//        assetGroupSearchField2.setValue(asset2.getAsset().getGuid());
//        assetGroup.getSearchFields().add(assetGroupSearchField2);
//    
//        // Create Group
//        assetGroup = AssetTestHelper.createAssetGroup(assetGroup);
//
//        List<AssetGroup> assetGroups = new ArrayList<AssetGroup>();
//        assetGroups.add(assetGroup);
//        List<Asset> assets = AssetJMSHelper.getAssetListByAssetGroups(assetGroups);
//        
//        setDecimalScaleAndNullNotes(assets);
//        assertTrue(assets.contains(asset1));
//        assertTrue(assets.contains(asset2));
//    }

}
