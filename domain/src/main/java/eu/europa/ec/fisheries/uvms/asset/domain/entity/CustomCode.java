package eu.europa.ec.fisheries.uvms.asset.domain.entity;


import static eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode.CUSTOMCODES_GETALLCONSTANTS;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode.CUSTOMCODES_GETALLFOR;
import static eu.europa.ec.fisheries.uvms.asset.domain.entity.CustomCode.CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "customcodes")
@NamedQueries({
        @NamedQuery(name = CUSTOMCODES_GETALLFOR, query = "SELECT m FROM CustomCode m where  m.primaryKey.constant=:constant"),
        @NamedQuery(name = CUSTOMCODES_GETALLCONSTANTS, query = "SELECT distinct m.primaryKey.constant FROM CustomCode m "),
        @NamedQuery(name = CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE, query = "SELECT  m FROM CustomCode m where m.primaryKey.constant = :constant and  m.primaryKey.code = :code and ( :aDate Between m.primaryKey.validFromDate and m.primaryKey.validToDate)"),
})
public class CustomCode {

    public static final String CUSTOMCODES_GETALLFOR = "CUSTOMCODES.MDRLITE_GETALLFOR";
    public static final String CUSTOMCODES_GETALLCONSTANTS = "CUSTOMCODES.MDRLITE_GETALLCONSTANTS";
    public static final String CUSTOMCODES_GETCUSTOMCODE_FOR_SPECIFIC_DATE = "CUSTOMCODES.MDRLITE_GETCUSTOMCODE_FOR_SPECIFIC_DATE";

    public CustomCode(){
        // for json
    }

    CustomCodesPK primaryKey;

    @Column(name = "description")
    private String description;

    @Column(name = "namevalue")
    private Map<String,String> namevalue = new HashMap<>();




    @EmbeddedId
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

    @ElementCollection // this is a collection of primitives
    @MapKeyColumn(name="key") // column name for map "key"
    @Column(name="value") // column name for map "value"
    public Map<String,String> getNameValue() {
        return namevalue;
    }

    public void  setNameValue(Map<String,String> namevalue) {
        this.namevalue=namevalue;
    }

}
