package eu.europa.fisheries.uvms.asset.service.arquillian;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetProdOrgModel;
import eu.europa.ec.fisheries.wsdl.asset.types.CarrierSource;

public abstract class AssetHelper {

    public static Asset helper_createAsset(AssetIdType assetIdType, String ircs) {
        Asset asset = new Asset();
        AssetId assetId = new AssetId();
        assetId.setType(assetIdType);
        switch (assetIdType) {
            case GUID:
                assetId.setGuid(UUID.randomUUID().toString());
                break;
            case INTERNAL_ID:
                assetId.setValue("INTERNALID_" + UUID.randomUUID().toString());
                break;
        }

        asset.setActive(true);
        asset.setAssetId(assetId);
        asset.setActive(true);

        asset.setSource(CarrierSource.INTERNAL);
        //asset.setEventHistory();
        asset.setName("TEST_NAME");
        asset.setCountryCode("SWE");
        asset.setGearType(GearFishingTypeEnum.UNKNOWN.name());
        asset.setHasIrcs("1");

        asset.setIrcs(ircs);
        asset.setExternalMarking("13");

        String cfr = "CF" + UUID.randomUUID().toString();

        asset.setCfr(cfr.substring(0, 12));

        String imo = generateARandomStringWithMaxLength(5);
        asset.setImo(imo);
        String mmsi = generateARandomStringWithMaxLength(9);
        asset.setMmsiNo(mmsi);
        asset.setHasLicense(true);
        asset.setLicenseType("MOCK-license-DB");
        asset.setHomePort("TEST_GOT");
        asset.setLengthOverAll(new BigDecimal(15l));
        asset.setLengthBetweenPerpendiculars(new BigDecimal(3l));
        asset.setGrossTonnage(new BigDecimal(200));

        asset.setGrossTonnageUnit(UnitTonnage.OSLO.name());
        asset.setOtherGrossTonnage(new BigDecimal(200));
        asset.setSafetyGrossTonnage(new BigDecimal(80));
        asset.setPowerMain(new BigDecimal(10));
        asset.setPowerAux(new BigDecimal(10));

        AssetProdOrgModel assetProdOrgModel = new AssetProdOrgModel();
        assetProdOrgModel.setName("NAME");
        assetProdOrgModel.setCity("CITY");
        assetProdOrgModel.setAddress("ADDRESS");
        assetProdOrgModel.setCode("CODE");
        assetProdOrgModel.setPhone("070 111 222");
        asset.getContact();
        asset.getNotes();

        return asset;
    }


    public static Asset helper_createAsset(AssetIdType assetIdType) {
        String ircs = generateARandomStringWithMaxLength(5);
        return helper_createAsset(assetIdType, ircs);
    }

    public static String generateARandomStringWithMaxLength(int len) {
        Random r = new Random();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int val = r.nextInt(10);
            ret.append(val);
        }
        return ret.toString();
    }

    public static AssetGroup create_asset_group(){
        AssetGroup ag = new AssetGroup();
        ag.setDynamic(false);
        ag.setGlobal(true);
        ag.setName("TEST_NAME");
        ag.setUser("TEST");
        return ag;
    }
}
