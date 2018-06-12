package eu.europa.ec.fisheries.uvms.mobileterminal.service.entity.types;

public enum EventCodeEnum {
    CREATE(1),
    MODIFY(2),
    ACTIVATE(3),
    INACTIVATE(4),
    ARCHIVE(5),
    LINK(6),
    UNLINK(7);

    private final int id;

    EventCodeEnum(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
