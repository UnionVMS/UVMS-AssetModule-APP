package eu.europa.ec.fisheries.uvms.mobileterminal.entity.types;

public enum  MobileTerminalStatus {
    ACTIVE,
    INACTIVE,
    ARCHIVE,
    UNARCHIVE;

    public String value() {
        return name();
    }

    public static MobileTerminalStatus fromValue(String v) {
        return valueOf(v);
    }
}
