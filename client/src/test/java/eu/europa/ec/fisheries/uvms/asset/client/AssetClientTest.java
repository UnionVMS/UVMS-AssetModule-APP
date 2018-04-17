package eu.europa.ec.fisheries.uvms.asset.client;

import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class AssetClientTest extends AbstractClientTest {

    @Inject
    AssetClient assetClient;

    @Test
    public void pingTest() {
        String response = assetClient.ping();
        assertThat(response, CoreMatchers.is("pong"));
    }

    @Test
    public void getAssetByGuidTest() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        Asset upsertAsset = assetClient.upsertAsset(assetBo);

        Asset asset = assetClient.getAssetById(AssetIdentifier.GUID, upsertAsset.getId().toString());
        assertThat(asset, CoreMatchers.is(CoreMatchers.notNullValue()));
        assertThat(asset.getId(), CoreMatchers.is(upsertAsset.getId()));
    }

    @Test
    public void upsertAssetTest() {
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(AssetHelper.createBasicAsset());
        Asset upsertAsset = assetClient.upsertAsset(assetBo);
        assertThat(upsertAsset, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void queryAssetsTest() {
        Asset asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        Asset upsertAsset = assetClient.upsertAsset(assetBo);
        AssetQuery assetQuery = new AssetQuery();
        assetQuery.setFlagState(Arrays.asList(asset.getFlagStateCode()));
        List<Asset> assets = assetClient.getAssetList(assetQuery);
        assertTrue(assets.stream()
                .filter(a -> a.getId().equals(upsertAsset.getId()))
                .count() == 1);
    }

    @Test
    public void upsertAssetJMSTest() throws Exception {
        Asset asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        assetClient.upsertAssetAsync(assetBo);
        Thread.sleep(5000); // Needed due to async call
        Asset fetchedAsset = assetClient.getAssetById(AssetIdentifier.CFR, asset.getCfr());
        assertThat(fetchedAsset.getCfr(), CoreMatchers.is(asset.getCfr()));
    }

    @Test
    public void getConstantsTest() throws Exception {
        CustomCode customCode = AssetHelper.createCustomCode("TEST_Constant");
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        Assert.assertTrue(createdCustomCode != null);
        List<String> rs = assetClient.getConstants();
        Assert.assertTrue(rs.size() > 0);
    }

    @Test
    public void getCodesForConstantsTest() throws Exception {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        for (int i = 0; i < 5; i++) {
            CustomCode customCode = AssetHelper.createCustomCode(constant);
            CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
            Assert.assertTrue(createdCustomCode != null);
        }
        List<CustomCode> rs = assetClient.getCodesForConstant(constant);
        Assert.assertTrue(rs.size() == 5);
    }

    @Test
    public void isCodeValidTest() throws Exception {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        Assert.assertTrue(createdCustomCode != null);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        LocalDateTime validFromDate = createdCustomCode.getPrimaryKey().getValidFromDate();

        Boolean ok =  assetClient.isCodeValid(cst,code,validFromDate.plusDays(5));
        Assert.assertTrue(ok);
    }

    @Test
    public void isCodeValidNegativeTest() throws Exception {

        String constant = "Test_Constant" + UUID.randomUUID().toString();
        CustomCode customCode = AssetHelper.createCustomCode(constant);
        CustomCode createdCustomCode = assetClient.createCustomCode(customCode);
        Assert.assertTrue(createdCustomCode != null);

        String cst = createdCustomCode.getPrimaryKey().getConstant();
        String code = createdCustomCode.getPrimaryKey().getCode();
        LocalDateTime validToDate = createdCustomCode.getPrimaryKey().getValidToDate();

        Boolean ok =  assetClient.isCodeValid(cst,code,validToDate.plusDays(5));
        Assert.assertFalse(ok);
    }


}
