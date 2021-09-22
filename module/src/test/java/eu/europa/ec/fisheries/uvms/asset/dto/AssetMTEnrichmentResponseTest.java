package eu.europa.ec.fisheries.uvms.asset.dto;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class AssetMTEnrichmentResponseTest {

    AssetMTEnrichmentResponse  aer = new AssetMTEnrichmentResponse();

    @Test
    public void setAndGetAssetnameTest () {
        aer.setAssetName("assetname");
        assertEquals("assetname", aer.getAssetName());
    }

    @Test
    public void setAndGetassetFilterListTest () {
        List<String> assetFilterList = new ArrayList<>();
        assetFilterList.add("test");
        assetFilterList.add("test2");
        aer.setAssetFilterList(assetFilterList);
        assertThat(aer.getAssetFilterList(), hasItem("test2"));
    }

    @Test
    public void setAndGetFlagstateTest () {
        aer.setFlagstate("Flagstate");
        assertEquals("Flagstate", aer.getFlagstate());
    }

    @Test
    public void setAndGetVesselTypeTest () {
        aer.setVesselType("Vesseltype");
        assertEquals("Vesseltype", aer.getVesselType());
    }

    @Test
    public void setAndGetMobileTerminalConnectIdTest () {
        aer.setMobileTerminalConnectId("MobileTerminalConnectId");
        assertEquals("MobileTerminalConnectId", aer.getMobileTerminalConnectId());
    }

    @Test
    public void setAndGetMobileTerminalTypeTest () {
        aer.setMobileTerminalType("MobileTerminalType");
        assertEquals("MobileTerminalType", aer.getMobileTerminalType());
    }

    @Test
    public void setAndGetAssetUUIDTest () {
        aer.setAssetUUID("AssetUUID");
        assertEquals("AssetUUID", aer.getAssetUUID());
    }

    @Test
    public void setAndGetChannelGuidTest () {
        aer.setChannelGuid("ChannelGuid");
        assertEquals("ChannelGuid", aer.getChannelGuid());
    }

    @Test
    public void setAndGetAssetHistoryIdTest () {
        aer.setAssetHistoryId("AssetHistoryId");
        assertEquals("AssetHistoryId", aer.getAssetHistoryId());
    }

    @Test
    public void setAndGetExternalMarkingTest () {
        aer.setExternalMarking("ExternalMarking");
        assertEquals("ExternalMarking", aer.getExternalMarking());
    }

    @Test
    public void setAndGetGearTypeTest () {
        aer.setGearType("GearType");
        assertEquals("GearType", aer.getGearType());
    }

    @Test
    public void setAndGetCfrTest () {
        aer.setCfr("Cfr");
        assertEquals("Cfr", aer.getCfr());
    }

    @Test
    public void setAndGetIrcsTest () {
        aer.setIrcs("Ircs");
        assertEquals("Ircs", aer.getIrcs());
    }

    @Test
    public void setAndGetAssetStatusTest () {
        aer.setAssetStatus("AssetStatus");
        assertEquals("AssetStatus", aer.getAssetStatus());
    }

    @Test
    public void setAndGetMmsiTest () {
        aer.setMmsi("Mmsi");
        assertEquals("Mmsi", aer.getMmsi());
    }

    @Test
    public void setAndGetImoTest () {
        aer.setImo("Imo");
        assertEquals("Imo", aer.getImo());
    }

    @Test
    public void setAndGetMobileTerminalGuidTest () {
        aer.setMobileTerminalGuid("MobileTerminalGuid");
        assertEquals("MobileTerminalGuid", aer.getMobileTerminalGuid());
    }

    @Test
    public void setAndGetDNIDTest () {
        aer.setDNID("DNID");
        assertEquals("DNID", aer.getDNID());
    }

    @Test
    public void setAndGetMemberNumberTest () {
        aer.setMemberNumber("MemberNumber");
        assertEquals("MemberNumber", aer.getMemberNumber());
    }

    @Test
    public void setAndGetSerialNumberTest () {
        aer.setSerialNumber("SerialNumber");
        assertEquals("SerialNumber", aer.getSerialNumber());
    }

    @Test
    public void setAndGetMobileTerminalIsInactiveTest () {
        aer.setMobileTerminalIsInactive(true);
        assertEquals(true, aer.getMobileTerminalIsInactive());
    }

    @Test
    public void setAndGetParkedTest () {
        aer.setParked(false);
        assertFalse(aer.isParked());
    }
}
