package eu.europa.ec.fisheries.uvms.asset.client;
import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchFields;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AssetSearchTest extends AbstractClientTest {

    @Inject
    AssetClient assetClient;
    
    @Before
    public void before() throws NamingException{
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/asset_endpoint", "http://localhost:8080/asset/rest");
    }

    @Test
    @OperateOnDeployment("normal")
    public void pingTest() {
        String response = assetClient.ping();
        assertEquals(response, "pong");
    }

    @Test
    @OperateOnDeployment("normal")
    public void getAssetListByQueryTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        asset.setIrcs("F777777");
        assetBo.setAsset(asset);
        AssetBO firstAssetBo = assetClient.upsertAsset(assetBo);
        AssetDTO nAsset = firstAssetBo.getAsset();
        SearchBranch query = new SearchBranch(true);
        query.addNewSearchLeaf(SearchFields.IRCS, " 34 F -777777 ");
        
        System.out.println("asset.getIrcs(): " + asset.getIrcs());
        System.out.println("query: " + query);
        List<AssetDTO> assetList = assetClient.getAssetList(query);
        assertNotNull(assetList.size());
        assertEquals(assetList.size(), 1);
        
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void queryAssetsTest() {
        AssetDTO asset = AssetHelper.createBasicAsset();
        AssetBO assetBo = new AssetBO();
        assetBo.setAsset(asset);
        AssetBO upsertAssetBo = assetClient.upsertAsset(assetBo);

        SearchBranch trunk = new SearchBranch(true);
        trunk.addNewSearchLeaf(SearchFields.FLAG_STATE, asset.getFlagStateCode());

        List<AssetDTO> assets = assetClient.getAssetList(trunk, 1, 1000);
        assertEquals(1, assets.stream()
                .filter(a -> a.getId().equals(upsertAssetBo.getAsset().getId()))
                .count());
    }
    
}
