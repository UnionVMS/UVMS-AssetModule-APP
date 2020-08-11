package eu.europa.ec.fisheries.uvms.asset.remote.dto;

import java.util.List;
import java.util.UUID;

public class ChannelChangeHistory {
    String changeType;

    private UUID id;
    private UUID historyId;

    List<ChangeHistoryItem> changes;


    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public List<ChangeHistoryItem> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeHistoryItem> changes) {
        this.changes = changes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getHistoryId() {
        return historyId;
    }

    public void setHistoryId(UUID historyId) {
        this.historyId = historyId;
    }
}
