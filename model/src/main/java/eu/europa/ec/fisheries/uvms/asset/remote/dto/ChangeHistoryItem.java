package eu.europa.ec.fisheries.uvms.asset.remote.dto;

public class ChangeHistoryItem {
    private String field;

    private Object oldValue;

    private Object newValue;

    public ChangeHistoryItem(String field, Object oldValue, Object newValue) {
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public ChangeHistoryItem() {
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
