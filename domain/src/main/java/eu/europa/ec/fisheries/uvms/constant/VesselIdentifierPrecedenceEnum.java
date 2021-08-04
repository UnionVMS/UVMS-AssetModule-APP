package eu.europa.ec.fisheries.uvms.constant;

import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

public enum VesselIdentifierPrecedenceEnum {

    CFR(ConfigSearchField.CFR, 1),
    UVI(ConfigSearchField.UVI, 2),
    IRCS(ConfigSearchField.IRCS, 3),
    EXTERNAL_MARKING(ConfigSearchField.EXTERNAL_MARKING, 4),
    ICCAT(ConfigSearchField.ICCAT, 5),
    GFCM(ConfigSearchField.GFCM, 6),
    MMSI(ConfigSearchField.MMSI, 7)
    ;

    private ConfigSearchField field;
    private Integer precedence;

    VesselIdentifierPrecedenceEnum(ConfigSearchField field, Integer precedence) {
        this.field = field;
        this.precedence=precedence;
    }

    public ConfigSearchField getField() {
        return field;
    }

    public Integer getPrecedence() {
        return precedence;
    }


    static private Map<ConfigSearchField, VesselIdentifierPrecedenceEnum> map =
            new HashMap<>();

    static {
        map = Stream.of(VesselIdentifierPrecedenceEnum.values()).collect(toMap(e->e.getField(), Function.identity()));
    }

    public static VesselIdentifierPrecedenceEnum getByField(ConfigSearchField field) {
        return  map.get(field);
    }
}
