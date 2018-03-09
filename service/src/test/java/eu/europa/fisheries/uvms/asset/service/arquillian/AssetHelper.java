package eu.europa.fisheries.uvms.asset.service.arquillian;

import eu.europa.ec.fisheries.uvms.asset.enums.AssetIdTypeEnum;
import eu.europa.ec.fisheries.uvms.asset.enums.CarrierSourceEnum;
import eu.europa.ec.fisheries.uvms.asset.types.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.types.AssetGroupDTO;
import eu.europa.ec.fisheries.uvms.asset.types.AssetId;
import eu.europa.ec.fisheries.uvms.asset.types.AssetProdOrgModel;
import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.asset.types.GearFishingTypeEnum;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public abstract class AssetHelper {

    public static AssetDTO helper_createAsset(AssetIdTypeEnum assetIdType, String ircs) {


        AssetDTO asset = new AssetDTO();
        AssetId assetId = new AssetId();
        assetId.setType(assetIdType);
        switch (assetIdType) {
            case INTERNAL_ID:
                UUID tmp = UUID.randomUUID();
                assetId.setValue("INTERNALID_" + tmp.toString());
                assetId.setGuid(tmp);
                break;
        }

        asset.setActive(true);
        asset.setAssetId(assetId);
        asset.setActive(true);

        asset.setSource(CarrierSourceEnum.INTERNAL);
        //asset.setEventHistory();
        asset.setName("TEST_NAME");
        asset.setCountryCode("SWE");
        asset.setGearType(GearFishingTypeEnum.UNKNOWN.name());
        asset.setHasIrcs("1");

        asset.setIrcs(ircs);
        asset.setExternalMarking("13");

        String cfr = "CF" + UUID.randomUUID().toString();

        asset.setCfr(cfr.substring(0, 12));

        String imo = generateARandomStringWithMaxLength(2);
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


    public static AssetDTO helper_createAsset(AssetIdTypeEnum assetIdType) {
        String ircs = generateARandomStringWithMaxLength(1);
        return AssetHelper.helper_createAsset(assetIdType, ircs);

    }

    public static String generateARandomStringWithMaxLength(int len) {
        String ret = "";
        for (int i = 0; i < len; i++) {
            int val = new Random().nextInt(10);
            ret += String.valueOf(val);
        }
        return ret;
    }

    public static AssetGroupDTO create_asset_group(){
        AssetGroupDTO ag = new AssetGroupDTO();
        ag.setDynamic(false);
        ag.setGlobal(true);
        ag.setName("TEST_NAME");
        ag.setUser("TEST");


        return ag;

    }

}
