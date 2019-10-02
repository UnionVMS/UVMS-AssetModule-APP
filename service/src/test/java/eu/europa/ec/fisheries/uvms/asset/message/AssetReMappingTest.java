package eu.europa.ec.fisheries.uvms.asset.message;

import eu.europa.ec.fisheries.uvms.asset.domain.dao.AssetDao;
import eu.europa.ec.fisheries.uvms.asset.domain.entity.AssetRemapMapping;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;
import eu.europa.ec.fisheries.uvms.tests.TransactionalTests;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class AssetReMappingTest extends TransactionalTests {

    private JMSHelper jmsHelper = new JMSHelper();

    @Inject
    AssetDao assetDao;


    @Test
    @OperateOnDeployment("normal")
    public void assetInformationTest2CheckIfAssetMappingsContainsInfo() throws Exception {

        Asset assetWithsMMSI = AssetTestHelper.createBasicAsset();
        assetWithsMMSI.setIrcs(null);
        assetWithsMMSI.setName("ShouldNotBeThis");
        jmsHelper.upsertAsset(assetWithsMMSI);
        Thread.sleep(2000);
        eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset mmsiEntity = assetDao.getAssetByMmsi(assetWithsMMSI.getMmsiNo());
        mmsiEntity.setComment("Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!Comment at length 255!");
        UUID mmsiEntityId = mmsiEntity.getId();

        Asset assetWithsIRCS = AssetTestHelper.createBasicAsset();
        assetWithsIRCS.setMmsiNo(null);
        assetWithsIRCS.setName("namnetestfall2");
        assetWithsIRCS.setSource(CarrierSource.NATIONAL);
        jmsHelper.upsertAsset(assetWithsIRCS);
        Thread.sleep(2000);
        eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset ircsEntity = assetDao.getAssetByIrcs(assetWithsIRCS.getIrcs());

        userTransaction.commit();
        userTransaction.begin();

        eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset newAsset = new eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset();
        newAsset.setMmsi(assetWithsMMSI.getMmsiNo());
        newAsset.setIrcs(assetWithsIRCS.getIrcs());
        newAsset.setName("ShouldNotBeThis2");
        List<eu.europa.ec.fisheries.uvms.asset.domain.entity.Asset> assetList = new ArrayList<>();
        assetList.add(newAsset);
        jmsHelper.assetInfo(assetList);
        Thread.sleep(2000);

        Asset fetchedAsset = jmsHelper.getAssetById(assetWithsMMSI.getMmsiNo(), AssetIdType.MMSI);
        assertTrue(fetchedAsset != null);
        assertTrue(fetchedAsset.getName() != null);
        assertTrue(fetchedAsset.getName(), fetchedAsset.getName().equals(assetWithsIRCS.getName()));
        assertTrue(fetchedAsset.getMmsiNo() != null);
        assertTrue(fetchedAsset.getIrcs() != null);

        List<AssetRemapMapping> mappingList = assetDao.getAllAssetRemappings();
        assertTrue(mappingList.stream().anyMatch(mapping -> (mapping.getOldAssetId().equals(mmsiEntityId) && mapping.getNewAssetId().equals(ircsEntity.getId()))));

        mmsiEntity = assetDao.getAssetById(mmsiEntityId);
        assertTrue(mmsiEntity.getComment().length() == 255);
        assertFalse(mmsiEntity.getActive());

        assetDao.deleteAsset(mmsiEntity);
        assetDao.deleteAsset(ircsEntity);

        userTransaction.commit();
        userTransaction.begin();
    }
}
