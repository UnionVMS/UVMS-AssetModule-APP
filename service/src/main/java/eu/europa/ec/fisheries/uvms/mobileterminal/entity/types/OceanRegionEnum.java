package eu.europa.ec.fisheries.uvms.mobileterminal.entity.types;

public enum OceanRegionEnum {

    AOR_W(0,"AOR-W","WEST_ATLANTIC","West Atlantic"),
    AOR_E(1,"AOR-E","EAST_ATLANTIC","East Atlantic"),
    POR(2,"POR","PACIFIC","Pacific"),
    IOR(3,"IOR","INDIAN","Indian");

    private final int code;
    private final String abbreviation;
    private final String name;
    private final String description;

    OceanRegionEnum(int code, String abbreviation, String name, String description) {
        this.code = code;
        this.abbreviation = abbreviation;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
