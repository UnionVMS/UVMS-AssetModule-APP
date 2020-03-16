package eu.europa.ec.fisheries.uvms.rest.asset.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChangeHistoryRow {

    private String updatedBy;
    private Instant updateTime;

    private List<ChangeHistoryItem> changes = new ArrayList<>();

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

}
