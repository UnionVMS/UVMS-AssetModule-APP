package eu.europa.ec.fisheries.uvms.asset.service.bean;

import eu.europa.ec.fisheries.uvms.asset.message.AssetDataSourceQueue;
import eu.europa.ec.fisheries.uvms.asset.message.ModuleQueue;
import eu.europa.ec.fisheries.uvms.asset.message.producer.MessageProducer;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.asset.service.AssetService;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JAXBMarshaller.class, ExchangeModuleRequestMapper.class})
public class UpdatedAssetServiceBeanTest {

    private static final int SYNC_TO_FLUX_AFTER_MINUTES = 30;

    @InjectMocks
    private UpdatedAssetServiceBean updatedAssetServiceBean;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private AssetService assetService;

    @Test
    public void testProcessUpdatedAssets() throws Exception {
        final String cfr = "CFR";
        final String cfr2 = "RFC";

        AssetId assetId1 = new AssetId();
        assetId1.setType(AssetIdType.CFR);
        assetId1.setValue(cfr);

        AssetId assetId2 = new AssetId();
        assetId2.setType(AssetIdType.CFR);
        assetId2.setValue(cfr2);

        Asset asset1 = new Asset();
        asset1.setCfr(cfr);
        Asset asset2 = new Asset();
        asset2.setCfr(cfr2);

        mockStatic(JAXBMarshaller.class);
        mockStatic(ExchangeModuleRequestMapper.class);
        when(JAXBMarshaller.marshallJaxBObjectToString(asset1)).thenReturn("asset1");
        when(JAXBMarshaller.marshallJaxBObjectToString(asset2)).thenReturn("asset2");
        when(ExchangeModuleRequestMapper.createSendAssetInformation("asset1", "asset")).thenReturn("asset1 in exchange wrapper");
        when(ExchangeModuleRequestMapper.createSendAssetInformation("asset2", "asset")).thenReturn("asset2 in exchange wrapper");
        doReturn("").when(messageProducer).sendModuleMessage("asset1 in exchange wrapper", ModuleQueue.EXCHANGE);
        doReturn("").when(messageProducer).sendModuleMessage("asset2 in exchange wrapper", ModuleQueue.EXCHANGE);
        doReturn(asset1).when(assetService).getAssetById(assetId1, AssetDataSourceQueue.INTERNAL);
        doReturn(asset2).when(assetService).getAssetById(assetId2, AssetDataSourceQueue.INTERNAL);


        updatedAssetServiceBean.putCfrAndDate(cfr, DateTime.now().minusMinutes(SYNC_TO_FLUX_AFTER_MINUTES).minusMinutes(1));
        updatedAssetServiceBean.putCfrAndDate(cfr2, DateTime.now().minusMinutes(SYNC_TO_FLUX_AFTER_MINUTES).minusMinutes(1));
        updatedAssetServiceBean.putCfrAndDate("random cfr", DateTime.now()); // this one shouldn;t be synced
        updatedAssetServiceBean.processUpdatedAssets();

        verifyStatic();
        JAXBMarshaller.marshallJaxBObjectToString(asset1);
        JAXBMarshaller.marshallJaxBObjectToString(asset2);
        ExchangeModuleRequestMapper.createSendAssetInformation("asset1", "asset");
        ExchangeModuleRequestMapper.createSendAssetInformation("asset2", "asset");


        verify(messageProducer).sendModuleMessage("asset1 in exchange wrapper", ModuleQueue.EXCHANGE);
        verify(messageProducer).sendModuleMessage("asset2 in exchange wrapper", ModuleQueue.EXCHANGE);
        verify(assetService).getAssetById(assetId1, AssetDataSourceQueue.INTERNAL);
        verify(assetService).getAssetById(assetId2, AssetDataSourceQueue.INTERNAL);
        verifyNoMoreInteractions(messageProducer, assetService);
    }

}