package eu.europa.ec.fisheries.uvms.asset.remote.dto;

import java.util.List;

public class ChannelChangeHistory {
    String changeType;

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
}
