package eu.europa.ec.fisheries.uvms.asset.dto;


import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class MicroAssetTest {

    @Test
    public void createMicroAssetTest () {
        MicroAsset ma = new MicroAsset(UUID.randomUUID(), "flagstate", "assetname", "vesseltype", "ircs",
                                        "cfr", "externalmarking", 15.35,false);

        MicroAsset ma2 = new MicroAsset();
        assertNotNull(ma2);

        ma.setAssetId(UUID.randomUUID());
        assertNotNull(ma.getAssetId());

        ma.setFlagstate("testflagstate");
        assertEquals("testflagstate", ma.getFlagstate());

        ma.setAssetName("testassetname");
        assertEquals("testassetname", ma.getAssetName());

        ma.setVesselType("optimistjolle");
        assertEquals("optimistjolle", ma.getVesselType());

        ma.setIrcs("testircs");
        assertEquals("testircs", ma.getIrcs());

        ma.setCfr("testcfr");
        assertEquals("testcfr", ma.getCfr());

        ma.setExternalMarking("testexternalmarking");
        assertEquals("testexternalmarking", ma.getExternalMarking());

        ma.setLengthOverAll(16.45);
        assertEquals(16.45, ma.getLengthOverAll(), 0.0);

        ma.setHasLicence(true);
        assertEquals(true, ma.getHasLicence());
    }
}
