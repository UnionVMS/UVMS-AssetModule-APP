package eu.europa.ec.fisheries.uvms.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

import static eu.europa.ec.fisheries.uvms.entity.CustomCodes.CUSTOMCODES_GET;
import static eu.europa.ec.fisheries.uvms.entity.CustomCodes.CUSTOMCODES_GETALLFOR;

@Entity
@Table(name = "customcodes")
@NamedQueries({
        @NamedQuery(name = CUSTOMCODES_GET, query = "SELECT m FROM CustomCodes m where  m.constant=:constant and m.code=:code"),
        @NamedQuery(name = CUSTOMCODES_GETALLFOR, query = "SELECT m FROM CustomCodes m where  m.constant=:constant"),
})


public class CustomCodes {

    public static final String CUSTOMCODES_GET = "CUSTOMCODES.EXISTS";
    public static final String CUSTOMCODES_GETALLFOR = "CUSTOMCODES.MDRLITE_GETALLFOR";

    public CustomCodes(){
        // for json
    }


    @Id
    @GeneratedValue(generator = "CUSTOMCODES_UUID")
    @GenericGenerator(name = "CUSTOMCODES_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;


    @Column(name = "constant")
    private String constant;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "jsonstr")
    private String jsonstr;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
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
