package eu.europa.ec.fisheries.uvms.asset.client.model;


import javax.persistence.EmbeddedId;
import java.util.HashMap;
import java.util.Map;


public class CustomCode {

    public CustomCode(){
        // for json
    }

    @EmbeddedId
    CustomCodesPK primaryKey;

    private String description;
    private Map<String,String> namevalue = new HashMap<>();
    public CustomCodesPK getPrimaryKey() {
        return primaryKey;
    }
    public void setPrimaryKey(CustomCodesPK primaryKey){
        this.primaryKey = primaryKey;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
