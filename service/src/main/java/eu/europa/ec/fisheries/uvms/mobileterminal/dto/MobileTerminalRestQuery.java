package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

import java.io.Serializable;
import java.util.UUID;

public class MobileTerminalRestQuery implements Serializable {

    private UUID mobileTerminalId;
    private UUID connectId;
    private String comment;

    private MobileTerminalRestQuery(/* Required for Jackson*/) {
    }

    private MobileTerminalRestQuery(QueryBuilder builder) {
        this.mobileTerminalId = builder.mobileTerminalId;
        this.connectId = builder.connectId;
        this.comment = builder.comment;
    }

    public UUID getMobileTerminalId() {
        return mobileTerminalId;
    }

    public UUID getConnectId() {
        return connectId;
    }

    public String getComment() {
        return comment;
    }

    public static class QueryBuilder {

        private UUID mobileTerminalId;
        private UUID connectId;
        private String comment;

        public QueryBuilder(UUID mobileTerminalId) {
            this.mobileTerminalId = mobileTerminalId;
        }

        public QueryBuilder withConnectId(UUID connectId) {
            this.connectId = connectId;
            return this;
        }

        public QueryBuilder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public MobileTerminalRestQuery build() {
            return new MobileTerminalRestQuery(this);
        }
    }
}
