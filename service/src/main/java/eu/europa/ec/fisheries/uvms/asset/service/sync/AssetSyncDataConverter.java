package eu.europa.ec.fisheries.uvms.asset.service.sync;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.entity.asset.types.ContactInfoSourceEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.EventCodeEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.HullMaterialEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.PublicAidEnum;
import eu.europa.ec.fisheries.uvms.entity.asset.types.SegmentFUP;
import eu.europa.ec.fisheries.uvms.entity.asset.types.TypeOfExportEnum;
import eu.europa.ec.fisheries.uvms.entity.model.AssetHistory;
import eu.europa.ec.fisheries.uvms.entity.model.ContactInfo;
import eu.europa.ec.fisheries.uvms.entity.model.FishingGear;
import eu.europa.ec.mare.fisheries.vessel.common.v1.BooleanType;
import eu.europa.ec.mare.fisheries.vessel.common.v1.FluxContactRoleType;
import eu.europa.ec.mare.fisheries.vessel.common.v1.FluxGearRoleType;
import eu.europa.ec.mare.fisheries.vessel.common.v1.FluxVesselEngineRoleType;
import eu.europa.ec.mare.fisheries.vessel.common.v1.VesselEventType;
import eu.europa.ec.mare.fisheries.vessel.common.v1.VesselTransportMeansType;

@ApplicationScoped
public class AssetSyncDataConverter {

    private static final String FLEETSYNC = "fleetsync";

    public AssetHistory convertFromExtendedData(VesselEventType vesselEventType) {
        VesselTransportMeansType relatedVesselTransportMeans = vesselEventType.getRelatedVesselTransportMeans();

        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setGfcm(relatedVesselTransportMeans.getThirdPartyVesselID().getGFCM());
        assetHistory.setIccat(relatedVesselTransportMeans.getThirdPartyVesselID().getICCAT());
        return assetHistory;
    }

    public AssetHistory convert(VesselEventType vesselEventType) {
        VesselTransportMeansType relatedVesselTransportMeans = vesselEventType.getRelatedVesselTransportMeans();
        AssetHistory assetHistory = new AssetHistory();

        assetHistory.setHashKey(relatedVesselTransportMeans.getHaskKey());
        assetHistory.setEventCode(EventCodeEnum.valueOf(vesselEventType.getEventType()));
        assetHistory.setActive(vesselEventType.getEventActiveIndicator().equals(BooleanType.Y));
        assetHistory.setDateOfEvent(vesselEventType.getOccurrenceDateTime());
//        assetHistory.setUpdateTime();
        assetHistory.setUpdatedBy(FLEETSYNC);
        /*
        Ignore the values of these fields for now as they are not used in FluxFMC and the enum has not all the incoming values.
        assetHistory.setSegment(SegmentFUP.valueOf(relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics().getSegment()));
        assetHistory.setSegmentOfAdministrativeDecision(SegmentFUP.valueOf(relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics().getSegment()));
        */
        mapVesselIdentifiers(vesselEventType, assetHistory);
        mapRegistrationInfo(relatedVesselTransportMeans, assetHistory);
        mapFishingGear(relatedVesselTransportMeans, assetHistory);
        mapPhysicalVesselCharacteristics(relatedVesselTransportMeans, assetHistory);
        mapIndicators(relatedVesselTransportMeans, assetHistory);
        mapEngines(relatedVesselTransportMeans, assetHistory);
        mapContacts(relatedVesselTransportMeans, assetHistory);
        mapConstructionInfo(relatedVesselTransportMeans, assetHistory);

        return assetHistory;
    }

    private void mapConstructionInfo(VesselTransportMeansType relatedVesselTransportMeans, AssetHistory assetHistory) {
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedConstructionEvent().getRelatedConstructionLocation()).ifPresent(v -> assetHistory.setPlaceOfConstruction(v.getPlaceOfContructionID()));
        assetHistory.setConstructionDate(relatedVesselTransportMeans.getSpecifiedConstructionEvent().getOccurrenceDateTime());
        assetHistory.setCommisionDate(relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics().getDateOfEntryIntoService());
    }

    private void mapIndicators(VesselTransportMeansType relatedVesselTransportMeans, AssetHistory assetHistory) {
        Optional.ofNullable(relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics().getPublicAidCode()).ifPresent(v -> assetHistory.setPublicAid(PublicAidEnum.valueOf(relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics().getPublicAidCode())));
        assetHistory.setHasVms(relatedVesselTransportMeans.getApplicableVesselEquipmentCharacteristics().getVMSIndicator().equals(BooleanType.Y));
        assetHistory.setIrcsIndicator(relatedVesselTransportMeans.getApplicableVesselEquipmentCharacteristics().getIRCSIndicator().equals(BooleanType.Y) ? "1" : "0");
    }

    private void mapRegistrationInfo(VesselTransportMeansType relatedVesselTransportMeans, AssetHistory assetHistory) {
        assetHistory.setCountryOfRegistration(relatedVesselTransportMeans.getRegistrationVesselCountry());
        assetHistory.setRegistrationNumber(relatedVesselTransportMeans.getRegistrationNumber());
        assetHistory.setHasLicence(relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics().getLicenceIndicator().equals(BooleanType.Y));
        assetHistory.setPortOfRegistration(relatedVesselTransportMeans.getSpecifiedRegistrationEvent().stream().findFirst().map(e -> e.getRelatedRegistrationLocation().getPlaceOfRegistrationPortID()).orElse(""));
        Optional.ofNullable(relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics().getTypeOfExport()).ifPresent(e -> assetHistory.setTypeOfExport(TypeOfExportEnum.valueOf(e)));
    }

    private void mapPhysicalVesselCharacteristics(VesselTransportMeansType relatedVesselTransportMeans, AssetHistory assetHistory) {
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions().getLOA()).ifPresent(v -> assetHistory.setLengthOverAll(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions().getLBP()).ifPresent(v -> assetHistory.setLengthBetweenPerpendiculars(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions().getTonnageGT()).ifPresent(v -> assetHistory.setGrossTonnageUnit(UnitTonnage.LONDON));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions().getTonnageGT()).ifPresent(v -> assetHistory.setGrossTonnage(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions().getOtherTonnage()).ifPresent(v -> assetHistory.setOtherTonnage(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions().getGTS()).ifPresent(v -> assetHistory.setSafteyGrossTonnage(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getApplicableVesselTechnicalCharacteristics()).ifPresent(v ->
                Optional.ofNullable(v.getHullMaterial()).ifPresent(h -> {
                    Optional<HullMaterialEnum> hullMaterial = Arrays.stream(HullMaterialEnum.values()).filter(k -> k.getId().equals(h.longValue())).findFirst();
                    hullMaterial.ifPresent(assetHistory::setHullMaterial);
                })
        );
    }

    private void mapContacts(VesselTransportMeansType relatedVesselTransportMeans, AssetHistory assetHistory) {
        List<ContactInfo> contactInfoList = new ArrayList<>();
        relatedVesselTransportMeans.getSpecifiedContactParty().forEach(c -> {
            ContactInfo contactInfo = new ContactInfo();
            if (FluxContactRoleType.OWNER.equals(c.getContactRole())) {
                Optional.of(c.getSpecifiedStructuredAddress()).ifPresent(a -> assetHistory.setOwnerAddress(a.getStreetName()));
                assetHistory.setOwnerName(c.getName());
                assetHistory.setImo(c.getIMOCompanyNumber());
                contactInfo.setOwner(true);
            } else if (FluxContactRoleType.AGENT.equals(c.getContactRole()) || FluxContactRoleType.OPERATOR.equals(c.getContactRole())) {
                Optional.of(c.getSpecifiedStructuredAddress()).ifPresent(a -> assetHistory.setAssetAgentAddress(a.getStreetName()));
                contactInfo.setOwner(false);
            }

            c.getURIEmailCommunication().stream().findFirst().ifPresent(e -> contactInfo.setEmail(e.getEmailAddress()));
            contactInfo.setName(c.getName());
            c.getSpecifiedUniversalCommunication().stream().findFirst().ifPresent(p -> contactInfo.setPhoneNumber(p.getPhoneNumber()));
            contactInfo.setSource(ContactInfoSourceEnum.INTERNAL);
            contactInfo.setUpdatedBy(FLEETSYNC);
//          contactInfo.setUpdateTime();
            contactInfoList.add(contactInfo);
        });
        assetHistory.setContactInfo(contactInfoList);
//        assetHistory.setAssetAgentIsAlsoOwner(assetHistory.getAssetAgentAddress().equalsIgnoreCase(assetHistory.getOwnerAddress()));
    }

    private void mapEngines(VesselTransportMeansType relatedVesselTransportMeans, AssetHistory assetHistory) {
        relatedVesselTransportMeans.getAttachedVesselEngine().forEach(e -> {
            if (FluxVesselEngineRoleType.MAIN.equals(e.getRole())) {
                assetHistory.setPowerOfMainEngine(e.getPower().getValue());
            } else if (FluxVesselEngineRoleType.AUX.equals(e.getRole())) {
                assetHistory.setPowerOfAuxEngine(e.getPower().getValue());
            }
        });
    }

    private void mapFishingGear(VesselTransportMeansType relatedVesselTransportMeans, AssetHistory assetHistory) {
        relatedVesselTransportMeans.getOnBoardFishingGear().forEach(f -> {
            FishingGear fishingGear = new FishingGear();
            fishingGear.setCode(f.getGearType());
            if (FluxGearRoleType.MAIN.equals(f.getGearRole())) {
                assetHistory.setMainFishingGear(fishingGear);
            } else {
                assetHistory.setSubFishingGear(fishingGear);
            }
        });
    }

    private void mapVesselIdentifiers(VesselEventType vesselEventType, AssetHistory assetHistory) {
        assetHistory.setCfr(vesselEventType.getRelatedVesselTransportMeans().getCFR());
        assetHistory.setUvi(vesselEventType.getRelatedVesselTransportMeans().getUVI());
        assetHistory.setIrcs(vesselEventType.getRelatedVesselTransportMeans().getIRCS());
        assetHistory.setName(vesselEventType.getRelatedVesselTransportMeans().getVesselName());
        assetHistory.setExternalMarking(vesselEventType.getRelatedVesselTransportMeans().getExternalMarking());
        assetHistory.setMmsi(vesselEventType.getRelatedVesselTransportMeans().getMMSI());
    }
}
