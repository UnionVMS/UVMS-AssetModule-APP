package eu.europa.ec.fisheries.uvms.mobileterminal.dto;

public class ListCriteria {
    private SearchKey key;
    private String value;

    public SearchKey getKey() {
        return key;
    }

    public void setKey(SearchKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
