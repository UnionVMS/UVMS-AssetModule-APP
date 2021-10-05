package eu.europa.ec.fisheries.uvms.entity.model;

import eu.europa.ec.fisheries.uvms.constant.UnitTonnage;
import eu.europa.ec.fisheries.uvms.constant.UvmsConstants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "assetrawhistoryrecords",
       schema = "asset",
       indexes = {
            @Index(name = "assetrawhist_cfr_idx", columnList = "assetrawhist_cfr")
       }
)
@NamedQueries({
        @NamedQuery(
                name = UvmsConstants.FIND_ALL_DISTINCT_RAW_CFRS,
                query = "SELECT DISTINCT r.cfr FROM AssetRawHistory r WHERE r.cfr IS NOT NULL"
        ),
        @NamedQuery(
                name = UvmsConstants.FIND_ASSET_BY_CFR_ORDER_BY_EVENT_DESC,
                query = "SELECT r FROM AssetRawHistory r " +
                        "WHERE r.cfr = :cfr " +
                        "ORDER BY r.dateOfEvent DESC"
        )
})
public class AssetRawHistory {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assetRawHistorySequence")
    @SequenceGenerator(name = "assetRawHistorySequence", allocationSize = 1000, initialValue = 1, sequenceName = "seq_asset_raw_hist")
    @Column(name = "assetrawhist_id")
    @Getter @Setter private Long id;

    //VesselEvent
    @Column(name = "assetrawhist_eventcodetype")
    @Getter @Setter private String eventCodeType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assetrawhist_dateofevent")
    @Getter @Setter private Date dateOfEvent;

    @Column(name = "assetrawhist_eventactive")
    @Getter @Setter private Boolean eventActive;

    //there is also an EventLastIndicator in the XML....?

    //RelatedVesselTransportMeans - general entries
    @Size(max = 12)
    @Column(name = "assetrawhist_cfr")
    @Getter @Setter private String cfr;

    @Size(max = 50)
    @Column(name = "assetrawhist_uvi")
    @Getter @Setter private String uvi;

    @Size(max = 14)
    @Column(name = "assetrawhist_registrationnumber")
    @Getter @Setter private String registrationNumber; //also known as licence type?

    @Size(max = 14)
    @Column(name = "assetrawhist_externalmarking")
    @Getter @Setter private String externalMarking;

    @Size(max = 8)
    @Column(name = "assetrawhist_ircs")
    @Getter @Setter private String ircs;

    @Size(min = 9, max = 9)
    @Column(name = "assetrawhist_mmsi", unique = true)
    @Getter @Setter private String mmsi;

    @Size(max = 256)
    @Column(name = "hash_key")
    @Getter @Setter private String hashKey;

    @Size(max = 50)
    @Column(name = "assetrawhist_iccat")
    @Getter @Setter private String iccat;

    @Size(max = 50)
    @Column(name = "assetrawhist_gfcm")
    @Getter @Setter private String gfcm;

    @Size(max = 40)
    @Column(name = "assetrawhist_nameofasset")
    @Getter @Setter private String name;

    @Size(max = 40)
    @Column(name = "assetrawhist_vesseltype")
    @Getter @Setter private String vesselType;

    //SpecifiedRegistrationEvent
    @Size(min = 3, max = 3)
    @Column(name = "assetrawhist_countryregistration")
    @Getter @Setter private String countryOfRegistration;

    @Size(max = 30)
    @Column(name = "assetrawhist_placeofregistration")
    @Getter @Setter private String placeOfRegistration;

    //SpecifiedConstructionEvent
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assetrawhist_dateofconstruction")
    @Getter @Setter private Date dateOfConstruction;

    //AttachedVesselEngine
    @Digits(integer = 8, fraction = 2)
    @Column(name = "assetrawhist_powerofmainengine")
    @Getter @Setter private BigDecimal powerOfMainEngine;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "assetrawhist_powerofauxengine")
    @Getter @Setter private BigDecimal powerOfAuxEngine;

    //SpecifiedVesselDimensions
    @Digits(integer = 6, fraction = 2)
    @Column(name = "assetrawhist_loa")
    @Getter @Setter private BigDecimal lengthOverAll;

    @Digits(integer = 6, fraction = 2)
    @Column(name = "assetrawhist_lbp")
    @Getter @Setter private BigDecimal lengthBetweenPerpendiculars;

    @Column(name = "assetrawhist_tonnagegtunit")
    @Getter @Setter private String grossTonnageUnit = UnitTonnage.LONDON.toString();

    @Digits(integer = 8, fraction = 2)
    @Column(name = "assetrawhist_tonnagegt")
    @Getter @Setter private BigDecimal grossTonnage;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "assetrawhist_othertonnage")
    @Getter @Setter private BigDecimal otherTonnage;

    @Digits(integer = 7, fraction = 2)
    @Column(name = "assetrawhist_gts")
    @Getter @Setter private BigDecimal safteyGrossTonnage;

    //OnBoardFishingGear
    @Column(name = "assetrawhist_main_fishgear_characteristics")
    @Getter @Setter private String mainFishingGearCharacteristics;

    @Column(name = "assetrawhist_main_fishgear_role")
    @Getter @Setter private String mainFishingGearRole;

    @Column(name = "assetrawhist_main_fishgear_type")
    @Getter @Setter private String mainFishingGearType;

    @Column(name = "assetrawhist_sub_fishgear_characteristics")
    @Getter @Setter private String subFishingGearCharacteristics;

    @Column(name = "assetrawhist_sub_fishgear_role")
    @Getter @Setter private String subFishingGearRole;

    @Column(name = "assetrawhist_sub_fishgear_type")
    @Getter @Setter private String subFishingGearType;

    //ApplicableVesselAdministrativeCharacteristics
    @Column(name = "assetrawhist_licenceindicator")
    @Getter @Setter private Boolean hasLicence;

    @Column(name = "assetrawhist_segment")
    @Getter @Setter private String segment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assetrawhist_dateofserviceentry")
    @Getter @Setter private Date dateOfServiceEntry;

    @Column(name = "assetrawhist_publicaid")
    @Getter @Setter private String publicAid;

    //ApplicableVesselEquipmentCharacteristics
    @Column(name = "assetrawhist_ircsindicator")
    @Getter @Setter private Boolean hasIrcs;

    @Column(name = "assetrawhist_vmsindicator")
    @Getter @Setter private Boolean hasVms;

    @Column(name = "assetrawhist_ersindicator")
    @Getter @Setter private Boolean hasErs;

    @Column(name = "assetrawhist_aisindicator")
    @Getter @Setter private Boolean hasAis;

    //ApplicableVesselTechnicalCharacteristics
    @Column(name = "assetrawhist_hullmaterial")
    @Getter @Setter private Long hullMaterial;

    //SpecifiedContactParty
    @Size(max = 100)
    @Column(name = "assetrawhist_nameowner")
    @Getter @Setter private String ownerName;

    @Size(max = 200)
    @Column(name = "assetrawhist_addressowner")
    @Getter @Setter private String ownerAddress;

    @Size(max = 100)
    @Column(name = "assetrawhist_emailowner")
    @Getter @Setter private String ownerEmailAddress;

    @Size(max = 40)
    @Column(name = "assetrawhist_phoneowner")
    @Getter @Setter private String ownerPhoneNumber;

    @Size(max = 7)
    @Column(name = "assetrawhist_imo")
    @Getter @Setter private String imo;

    @Column(name = "assetrawhist_indicatorowner")
    @Getter @Setter private Boolean assetAgentIsAlsoOwner;

    @Size(max = 100)
    @Column(name = "assetrawhist_nameagent")
    @Getter @Setter private String agentName;

    @Size(max = 200)
    @Column(name = "assetrawhist_addressagent")
    @Getter @Setter private String agentAddress;

    @Size(max = 100)
    @Column(name = "assetrawhist_emailagent")
    @Getter @Setter private String agentEmailAddress;

    @Size(max = 40)
    @Column(name = "assetrawhist_phoneagent")
    @Getter @Setter private String agentPhoneNumber;

    @Size(max = 100)
    @Column(name = "assetrawhist_asset_placeofconstruction")
    @Getter @Setter private String constructionPlace;

    @Size(max = 100)
    @Column(name = "assetrawhist_asset_addressofconstruction")
    @Getter @Setter private String constructionAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "assetrawhist_updattim")
    @Getter @Setter private Date updateTime;

    @Size(min = 3, max = 3)
    @Column(name = "assetrawhist_countryofimpexp")
    @Getter @Setter private String countryOfImportOrExport;

    @Column(name = "assetrawhist_typeofexport")
    @Getter @Setter private String typeOfExport;

    @Size(max = 60)
    @Column(name = "assetrawhist_upuser")
    @Getter @Setter private String updatedBy;
}
