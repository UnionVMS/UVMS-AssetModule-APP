package eu.europa.ec.fisheries.uvms.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

import static eu.europa.ec.fisheries.uvms.entity.MDR_Lite.MDRLITE_GET;
import static eu.europa.ec.fisheries.uvms.entity.MDR_Lite.MDRLITE_GETALLFOR;

@Entity
@Table(name = "mdrlite")
@NamedQueries({
        @NamedQuery(name = MDRLITE_GET, query = "SELECT m FROM MDR_Lite m where  m.constant=:constant and m.code=:code"),
        @NamedQuery(name = MDRLITE_GETALLFOR, query = "SELECT m FROM MDR_Lite m where  m.constant=:constant"),
})


public class MDR_Lite {

    public static final String MDRLITE_GET = "MDRLITE.EXISTS";
    public static final String MDRLITE_GETALLFOR = "MDRLITE.MDRLITE_GETALLFOR";

    public MDR_Lite(){
        // for json
    }


    @Id
    @GeneratedValue(generator = "MDRLITE_UUID")
    @GenericGenerator(name = "MDRLITE_UUID", strategy = "org.hibernate.id.UUIDGenerator")
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
