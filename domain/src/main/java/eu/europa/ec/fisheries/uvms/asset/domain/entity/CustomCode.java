package eu.europa.ec.fisheries.uvms.asset.domain.entity;


import static eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode.CUSTOMCODES_GETALLCONSTANTS;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode.CUSTOMCODES_GETALLFOR;
import javax.persistence.*;

@Entity
@Table(name = "customcodes")
@NamedQueries({
        @NamedQuery(name = CUSTOMCODES_GETALLFOR, query = "SELECT m FROM CustomCode m where  m.primaryKey.constant=:constant"),
        @NamedQuery(name = CUSTOMCODES_GETALLCONSTANTS, query = "SELECT distinct m.primaryKey.constant FROM CustomCode m "),
})
public class CustomCode {

    public static final String CUSTOMCODES_GETALLFOR = "CUSTOMCODES.MDRLITE_GETALLFOR";
    public static final String CUSTOMCODES_GETALLCONSTANTS = "CUSTOMCODES.MDRLITE_GETALLCONSTANTS";

    public CustomCode(){
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
