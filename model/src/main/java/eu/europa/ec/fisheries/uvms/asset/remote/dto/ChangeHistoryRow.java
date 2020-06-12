package eu.europa.ec.fisheries.uvms.asset.remote.dto;

import java.time.Instant;
import java.util.*;

public class ChangeHistoryRow {

    private String updatedBy;
    private Instant updateTime;

    private Map<UUID, List<ChangeHistoryItem>> channelChanges = new HashMap<>();

    private List<ChangeHistoryItem> changes = new ArrayList<>();

    private Object snapshot;

    public ChangeHistoryRow(String updatedBy, Instant updateTime) {
        this.updatedBy = updatedBy;
        this.updateTime = updateTime;
    }

    public ChangeHistoryRow() {
    }

    public void addNewItem(String field, Object oldValue, Object newValue){
        changes.add(new ChangeHistoryItem(field, oldValue, newValue));
    }


    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public List<ChangeHistoryItem> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeHistoryItem> changes) {
        this.changes = changes;
    }

    public Map<UUID, List<ChangeHistoryItem>> getChannelChanges() {
        return channelChanges;
    }

    public void setChannelChanges(Map<UUID, List<ChangeHistoryItem>> channelChanges) {
        this.channelChanges = channelChanges;
    }

    public Object getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Object snapshot) {
        this.snapshot = snapshot;
    }
}
