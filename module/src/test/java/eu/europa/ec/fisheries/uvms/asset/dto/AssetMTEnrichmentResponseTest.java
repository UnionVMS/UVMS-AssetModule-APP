package eu.europa.ec.fisheries.uvms.asset.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssetMTEnrichmentResponseTest {

    @Test
    public void assetMTEnrichmentResponseTest () {
        AssetMTEnrichmentResponse  aer = new AssetMTEnrichmentResponse();

        aer.setAssetName("assetname");
        assertEquals("assetname", aer.getAssetName());

    }

}
