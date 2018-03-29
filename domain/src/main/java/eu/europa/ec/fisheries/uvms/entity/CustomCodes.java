package eu.europa.ec.fisheries.uvms.entity;


import javax.persistence.*;

import static eu.europa.ec.fisheries.uvms.entity.CustomCodes.CUSTOMCODES_GETALLFOR;

@Entity
@Table(name = "customcodes")
@NamedQueries({
        @NamedQuery(name = CUSTOMCODES_GETALLFOR, query = "SELECT m FROM CustomCodes m where  m.primaryKey.constant=:constant"),
})
public class CustomCodes {

    public static final String CUSTOMCODES_GETALLFOR = "CUSTOMCODES.MDRLITE_GETALLFOR";

    public CustomCodes(){
        // for json
    }

    CustomCodesPK primaryKey;

    @Column(name = "description")
    private String description;

    @Column(name = "jsonstr")
    private String jsonstr;


    @EmbeddedId
    public CustomCodesPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(CustomCodesPK primaryKey){
        this.primaryKey = primaryKey;
    }

    public String getJsonstr() {
        return jsonstr;
    }

    public void setJsonstr(String jsonstr) {
        this.jsonstr = jsonstr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
