package eu.europa.ec.fisheries.uvms.asset.service.sync.collector;

import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.entity.model.AssetRawHistory;
import eu.europa.ec.mare.fisheries.vessel.common.v1.*;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class AssetSyncRawDataConverter {

    private static final String FLEETSYNC = "fleetsync";
    private static final String DEFAULT_UNIT_TONNAGE = "LONDON";
    private static final String EMPTY_NAME = "No Name";

    public AssetRawHistory rawConvert(VesselEventType vesselEventType) {

        VesselTransportMeansType relatedVesselTransportMeans =
                                    vesselEventType.getRelatedVesselTransportMeans();
        AssetRawHistory assetRawHistory = new AssetRawHistory();

        //Event info
        mapEventInfo(vesselEventType, assetRawHistory);
        //Vessel identifiers
        mapVesselIdentifiers(vesselEventType, assetRawHistory);
        //Registration and Construction
        mapVesselRegistrationAndConstruction(relatedVesselTransportMeans, assetRawHistory);
        //VesselEngine
        mapEngines(relatedVesselTransportMeans, assetRawHistory);
        //SpecifiedVesselDimensions
        mapPhysicalVesselCharacteristics(relatedVesselTransportMeans, assetRawHistory);
        //OnBoardFishingGear
        mapFishingGear(relatedVesselTransportMeans, assetRawHistory);
        //ApplicableVesselAdministrativeCharacteristics
        mapAdministrativeCharacteristics(relatedVesselTransportMeans, assetRawHistory);
        //ApplicableVesselEquipmentCharacteristics
        mapIndicators(relatedVesselTransportMeans, assetRawHistory);
        //SpecifiedContactParty
        mapContacts(relatedVesselTransportMeans, assetRawHistory);
        //Remaining values
        mapOtherValues(relatedVesselTransportMeans, assetRawHistory);

        return assetRawHistory;
    }

    private void  mapEventInfo(VesselEventType vesselEventType,
                               AssetRawHistory assetRawHistory) {
        assetRawHistory.setEventCodeType(vesselEventType.getEventType());
        assetRawHistory.setDateOfEvent(vesselEventType.getOccurrenceDateTime());
        assetRawHistory.setEventActive(BooleanType.Y.equals(vesselEventType.getEventActiveIndicator()));
    }

    private void mapVesselIdentifiers(VesselEventType vesselEventType,
                                      AssetRawHistory assetRawHistory) {
        VesselTransportMeansType vesselTransportMeansType = vesselEventType.getRelatedVesselTransportMeans();

        assetRawHistory.setCfr(vesselTransportMeansType.getCFR());
        assetRawHistory.setUvi(vesselTransportMeansType.getUVI());
        assetRawHistory.setRegistrationNumber(vesselTransportMeansType.getRegistrationNumber());
        assetRawHistory.setExternalMarking(vesselTransportMeansType.getExternalMarking());
        assetRawHistory.setIrcs(vesselTransportMeansType.getIRCS());
        assetRawHistory.setMmsi(vesselTransportMeansType.getMMSI());
        assetRawHistory.setHashKey(vesselTransportMeansType.getHaskKey());

        ThirdPartyVesselIDType thirdPartyVesselIDType = vesselTransportMeansType.getThirdPartyVesselID();
        if (thirdPartyVesselIDType != null) {
            assetRawHistory.setIccat(thirdPartyVesselIDType.getICCAT());
            assetRawHistory.setGfcm(thirdPartyVesselIDType.getGFCM());
        }

        assetRawHistory.setName(vesselTransportMeansType.getVesselName());
        assetRawHistory.setVesselType(vesselTransportMeansType.getVesselType());
    }

    private void mapVesselRegistrationAndConstruction(VesselTransportMeansType relatedVesselTransportMeans,
                                                      AssetRawHistory assetRawHistory) {
        assetRawHistory.setCountryOfRegistration(relatedVesselTransportMeans.getRegistrationVesselCountry());
        assetRawHistory.setPlaceOfRegistration(
                relatedVesselTransportMeans.getSpecifiedRegistrationEvent().stream()
                        .findFirst()
                        .map(e -> e.getRelatedRegistrationLocation().getPlaceOfRegistrationPortID())
                        .orElse(""));
        assetRawHistory.setDateOfConstruction (
                relatedVesselTransportMeans
                        .getSpecifiedConstructionEvent()
                        .getOccurrenceDateTime());
    }

    private void mapEngines(VesselTransportMeansType relatedVesselTransportMeans,
                            AssetRawHistory assetRawHistory) {
        relatedVesselTransportMeans.getAttachedVesselEngine().forEach(e -> {
            if (FluxVesselEngineRoleType.MAIN.equals(e.getRole())) {
                assetRawHistory.setPowerOfMainEngine((e.getPower() != null ? e.getPower().getValue() : null));
            } else if (FluxVesselEngineRoleType.AUX.equals(e.getRole())) {
                assetRawHistory.setPowerOfAuxEngine((e.getPower() != null ? e.getPower().getValue() : null));
            }
        });
    }

    private void mapPhysicalVesselCharacteristics(VesselTransportMeansType relatedVesselTransportMeans,
                                                  AssetRawHistory assetRawHistory) {
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions()
                .getLOA())
                .ifPresent(v -> assetRawHistory.setLengthOverAll(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions()
                .getLBP())
                .ifPresent(v -> assetRawHistory.setLengthBetweenPerpendiculars(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions()
                .getTonnageGT())
                .ifPresent(v -> assetRawHistory.setGrossTonnageUnit(DEFAULT_UNIT_TONNAGE));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions()
                .getTonnageGT())
                .ifPresent(v -> assetRawHistory.setGrossTonnage(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions()
                .getOtherTonnage())
                .ifPresent(v -> assetRawHistory.setOtherTonnage(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getSpecifiedVesselDimensions()
                .getGTS())
                .ifPresent(v -> assetRawHistory.setSafteyGrossTonnage(v.getValue()));
        Optional.ofNullable(relatedVesselTransportMeans.getApplicableVesselTechnicalCharacteristics())
                .ifPresent(v ->
                        Optional.ofNullable(v.getHullMaterial()).ifPresent(h -> {
                            assetRawHistory.setHullMaterial(h.longValue());
                        }));
    }

    private void mapFishingGear(VesselTransportMeansType relatedVesselTransportMeans,
                                AssetRawHistory assetRawHistory) {
        relatedVesselTransportMeans.getOnBoardFishingGear().forEach(f -> {
            if (FluxGearRoleType.MAIN.equals(f.getGearRole())) {
                Optional.ofNullable(f.getGearCharacteristic()).ifPresent(c ->
                    assetRawHistory.setMainFishingGearCharacteristics(c.toString())
                );
                Optional.ofNullable(f.getGearRole()).ifPresent(r ->
                        assetRawHistory.setMainFishingGearRole(r.name())
                );
                assetRawHistory.setMainFishingGearType(f.getGearType());
            } else {
                //does not handle multiple subsidiary fishing gear, it just overwrites with the last value
                Optional.ofNullable(f.getGearCharacteristic()).ifPresent(c ->
                        assetRawHistory.setSubFishingGearCharacteristics(c.toString())
                );
                Optional.ofNullable(f.getGearRole()).ifPresent(r ->
                        assetRawHistory.setSubFishingGearRole(r.name())
                );
                assetRawHistory.setSubFishingGearType(f.getGearType());
            }
        });
    }

    private void mapAdministrativeCharacteristics(VesselTransportMeansType relatedVesselTransportMeans,
                                             AssetRawHistory assetRawHistory) {
        Optional.ofNullable(
                relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics())
                .ifPresent(v -> {
                    assetRawHistory.setHasLicence(BooleanType.Y.equals(v.getLicenceIndicator()));
                    assetRawHistory.setSegment(v.getSegment());
                    assetRawHistory.setDateOfServiceEntry(v.getDateOfEntryIntoService());
                    assetRawHistory.setPublicAid(v.getPublicAidCode());
                });
    }

    private void mapIndicators(VesselTransportMeansType relatedVesselTransportMeans,
                               AssetRawHistory assetRawHistory) {

        VesselEquipmentCharacteristicsType vesselEquipmentCharacteristicsType =
                relatedVesselTransportMeans.getApplicableVesselEquipmentCharacteristics();

        if (vesselEquipmentCharacteristicsType != null) {
            assetRawHistory.setHasIrcs(
                    BooleanType.Y.equals(vesselEquipmentCharacteristicsType.getIRCSIndicator()));
            assetRawHistory.setHasVms(
                    BooleanType.Y.equals(vesselEquipmentCharacteristicsType.getVMSIndicator()));
            assetRawHistory.setHasErs(
                    BooleanType.Y.equals(vesselEquipmentCharacteristicsType.getERSIndicator()));
            assetRawHistory.setHasAis(
                    BooleanType.Y.equals(vesselEquipmentCharacteristicsType.getAISIndicator()));
        }
    }

    private void mapContacts(VesselTransportMeansType relatedVesselTransportMeans,
                             AssetRawHistory assetRawHistory) {
        relatedVesselTransportMeans.getSpecifiedContactParty().forEach(contactParty -> {
            FluxContactRoleType fluxContactRoleType = contactParty.getContactRole();
            List<EmailCommunicationType> emailCommunicationTypes = contactParty.getURIEmailCommunication();
            List<UniversalCommunicationType> universalCommunication = contactParty.getSpecifiedUniversalCommunication();
            if (FluxContactRoleType.OWNER.equals(fluxContactRoleType)) {
                if (Optional.ofNullable(contactParty.getName()).isPresent()) {
                    assetRawHistory.setOwnerName(contactParty.getName());
                } else {
                    assetRawHistory.setOwnerName(EMPTY_NAME);
                }
                Optional.of(contactParty.getSpecifiedStructuredAddress())
                        .ifPresent(a -> assetRawHistory.setOwnerAddress(a.getStreetName()));
                assetRawHistory.setImo(contactParty.getIMOCompanyNumber());
                emailCommunicationTypes.stream().findFirst()
                        .ifPresent(e->assetRawHistory.setOwnerEmailAddress(e.getEmailAddress()));
                universalCommunication.stream().findFirst()
                        .ifPresent(p->assetRawHistory.setOwnerPhoneNumber(p.getPhoneNumber()));
            } else if (FluxContactRoleType.AGENT.equals(fluxContactRoleType) ||
                    FluxContactRoleType.OPERATOR.equals(fluxContactRoleType)) {
                if (Optional.ofNullable(contactParty.getName()).isPresent()) {
                    assetRawHistory.setAgentName(contactParty.getName());
                } else {
                    assetRawHistory.setAgentName(EMPTY_NAME);
                }
                Optional.of(contactParty.getSpecifiedStructuredAddress())
                        .ifPresent(a -> assetRawHistory.setAgentAddress(a.getStreetName()));
                emailCommunicationTypes.stream().findFirst()
                        .ifPresent(e->assetRawHistory.setAgentEmailAddress(e.getEmailAddress()));
                universalCommunication.stream().findFirst()
                        .ifPresent(p->assetRawHistory.setAgentPhoneNumber(p.getPhoneNumber()));
            }
        });
    }

    private void mapOtherValues(VesselTransportMeansType relatedVesselTransportMeans,
                                AssetRawHistory assetRawHistory) {

        ConstructionEventType constructionEventType =
                relatedVesselTransportMeans.getSpecifiedConstructionEvent();
        VesselAdministrativeCharacteristicsType vesselAdministrativeCharacteristicsType =
                relatedVesselTransportMeans.getApplicableVesselAdministrativeCharacteristics();

        ConstructionLocationType constructionLocationType = constructionEventType.getRelatedConstructionLocation();
        if (constructionEventType != null && constructionLocationType != null) {
            assetRawHistory.setConstructionPlace(constructionLocationType.getPlaceOfContructionID());
            Optional.ofNullable(constructionLocationType.getConstructionAddress())
                    .ifPresent(a -> assetRawHistory.setConstructionAddress(a.toString()));
        }

        assetRawHistory.setUpdateTime(DateUtils.getNowDateUTC());

        relatedVesselTransportMeans.getSpecifiedRegistrationEvent()
                .stream()
                .findFirst()
                .ifPresent(e ->
                        Optional.ofNullable(e.getRelatedRegistrationLocation())
                        .ifPresent(l -> assetRawHistory.setCountryOfImportOrExport(l.getCountryOfImpExpID())));

        if (vesselAdministrativeCharacteristicsType != null) {
            String typeOfExport = vesselAdministrativeCharacteristicsType.getTypeOfExport();
            if (typeOfExport != null) {
                assetRawHistory.setTypeOfExport(typeOfExport);
            }

            String segment = vesselAdministrativeCharacteristicsType.getSegment();
            if (segment != null) {
                assetRawHistory.setSegment(segment);
            }
        }

        assetRawHistory.setUpdatedBy(FLEETSYNC);
    }

}
