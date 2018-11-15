package eu.europa.ec.fisheries.uvms.asset.client;

import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class AssetClientTest extends AbstractClientTest {

    @Inject
    AssetClient assetClient;
    
    @Before
    public void before() throws NamingException{
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/asset_endpoint", "http://localhost:8080/asset/rest");
    }

    @Test
    public void pingTest() {
        String response = assetClient.ping();
        assertThat(response, CoreMatchers.is("pong"));
    }

    @Test
    public void getAssetByGuidTest() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);

        AssetDTO asset = assetClient.getAssetById(AssetIdentifier.GUID, upsertAssetBo.getAsset().getId().toString());
        assertThat(asset, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(asset.getId(), CoreMatchers.is(upsertAssetBo.getAsset().getId()));
    }

    @Test
    public void upsertAssetTest() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        AssetBO upsertAsset = assetClient.upsertAsset(assetBo);
        
        assertThat(upsertAsset.getAsset().getId(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(upsertAsset.getAsset().getHistoryId(), CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(upsertAsset, CoreMatchers.is(CoreMatchers.notNullValue()));
    }
    
    @Test
    public void upsertAssertUpdateNameShouldCreateHistory() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);
        assetBo.getAsset().setName("New" + UUID.randomUUID());
        AssetBO upsertAssetBo2 = assetClient.upsertAsset(assetBo);
        
        assertThat(upsertAssetBo.getAsset().getHistoryId(), CoreMatchers.is(CoreMatchers.not(upsertAssetBo2.getAsset().getHistoryId())));
    }
    
    @Test
    public void upsertAssertTwiceShouldNotCreateNewHistory() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);
        AssetBO upsertAssetBo2 = assetClient.upsertAsset(assetBo);
        
        assertThat(upsertAssetBo.getAsset().getHistoryId(), CoreMatchers.is(upsertAssetBo2.getAsset().getHistoryId()));
    }

    @Test
    public void queryAssetsTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);
        AssetQuery assetQuery = new AssetQuery();
        assetQuery.setFlagState(Collections.singletonList(asset.getFlagStateCode()));
        List<AssetDTO> assets = assetClient.getAssetList(assetQuery, 1, 1000, true);
        assertEquals(1, assets.stream()
                .filter(a -> a.getId().equals(upsertAssetBo.getAsset().getId()))
                .count());
    }
    
    @Test
    public void upsertAssetJMSTest() throws Exception {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        assetClient.upsertAssetAsync(assetBo);
        Thread.sleep(5000); // Needed due to async call
        AssetDTO fetchedAsset = assetClient.getAssetById(AssetIdentifier.CFR, asset.getCfr());
        assertThat(fetchedAsset.getCfr(), CoreMatchers.is(asset.getCfr()));
    }

    @Test
    public void getConstantsTest() {
        CustomCode customCode = AssetHelper.createCustomCode("TEST_Constant");
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);
        List<String> rs = assetClient.getConstants();
        assertTrue(rs.size() > 0);
    }

    @Test
    public void getCodesForConstantsTest() {

        String constant = "Test_Constant" + UUID.randomUUID().toString();

        for (int i = 0; i < 5; i++) {
            CustomCode customCode = AssetHelper.createCustomCode(constant);
            CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
            assertNotNull(createdCustomCode);
        }
        List<CustomCode> rs = assetClient.getCodesForConstant(constant);
        assertEquals(5, rs.size());
    }

    @Test
    public void isCodeValidTest() {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        OffsetDateTime validFromDate = createdCustomCode.getPrimaryKey().getValidFromDate();

        Boolean ok = assetClient.isCodeValid(cst,code,validFromDate.plusDays(5));
        assertTrue(ok);
    }

    @Test
    public void isCodeValidNegativeTest() {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        OffsetDateTime validToDate = createdCustomCode.getPrimaryKey().getValidToDate();

        Boolean ok = assetClient.isCodeValid(cst,code,validToDate.plusDays(5));
        Assert.assertFalse(ok);
    }

    @Test
    public void getCodeForDateTest() {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        OffsetDateTime validFromDate = createdCustomCode.getPrimaryKey().getValidFromDate();
        OffsetDateTime validToDate = createdCustomCode.getPrimaryKey().getValidToDate();

        List<CustomCode> retrievedCustomCode = assetClient.getCodeForDate(cst, code, validToDate);
        assertNotNull(retrievedCustomCode);
        assertTrue(retrievedCustomCode.size() > 0 );
    }

    @Test
    public void getCodeForDateNegativeTest() {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        assertNotNull(createdCustomCode);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        OffsetDateTime validFromDate = createdCustomCode.getPrimaryKey().getValidFromDate();
        OffsetDateTime validToDate = createdCustomCode.getPrimaryKey().getValidToDate();

        List<CustomCode> retrievedCustomCode = assetClient.getCodeForDate(cst, code, validToDate.plusDays(5));
        assertNotNull(retrievedCustomCode);
        assertEquals(0, retrievedCustomCode.size());
    }

    @Test
    public void customCodesReplaceTest() {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        assetClient.replace(customCode);
        customCode.setDescription("replaced");
        assetClient.replace(customCode);
    }

    @Test
    public void collectAssetMTTest() {

        //public AssetMTEnrichmentResponse collectAssetMT(AssetMTEnrichmentRequest request) throws Exception {
        AssetMTEnrichmentRequest request = new AssetMTEnrichmentRequest();
        AssetMTEnrichmentResponse response = assetClient.collectAssetMT(request);

        assertNotNull(response);  // proofs we reach the endpoint  . . .
    }
}
