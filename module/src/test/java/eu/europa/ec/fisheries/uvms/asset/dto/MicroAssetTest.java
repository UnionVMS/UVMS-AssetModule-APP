package eu.europa.ec.fisheries.uvms.asset.dto;


import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class MicroAssetTest {

    MicroAsset ma = new MicroAsset(UUID.randomUUID(), "flagstate", "assetname", "vesseltype", "ircs",
            "cfr", "externalmarking", 15.35,false);

    @Test
    public void createMicroAssetTest () {
        MicroAsset ma2 = new MicroAsset();
        assertNotNull(ma2);
    }

    @Test
    public void setAndGetAssetIdTest () {
        ma.setAssetId(UUID.randomUUID());
        assertNotNull(ma.getAssetId());
    }

    @Test
    public void setAndGetFlagstateTest () {
        ma.setFlagstate("testflagstate");
        assertEquals("testflagstate", ma.getFlagstate());
    }

    @Test
    public void setAndGetAssetNameTest () {
        ma.setAssetName("testassetname");
        assertEquals("testassetname", ma.getAssetName());
    }

    @Test
    public void setAndGetVesselTypeTest () {
        ma.setVesselType("optimistjolle");
        assertEquals("optimistjolle", ma.getVesselType());
    }

    @Test
    public void setAndGetIrcsTest () {
        ma.setIrcs("testircs");
        assertEquals("testircs", ma.getIrcs());
    }

    @Test
    public void setAndGetCfrTest () {
        ma.setCfr("testcfr");
        assertEquals("testcfr", ma.getCfr());
    }

    @Test
    public void setAndGetExternalMarkingTest () {
        ma.setExternalMarking("testexternalmarking");
        assertEquals("testexternalmarking", ma.getExternalMarking());
    }

    @Test
    public void setAndGetLengthOverAllTest () {
        ma.setLengthOverAll(16.45);
        assertEquals(16.45, ma.getLengthOverAll(), 0.0);
    }

    @Test
    public void setAndGetHasLicenceTest () {
        ma.setHasLicence(true);
        assertEquals(true, ma.getHasLicence());
    }


}
