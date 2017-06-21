package eu.europa.ec.fisheries.uvms.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the parameter database table.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Parameter.FIND_BY_ID, query = "SELECT p FROM Parameter p WHERE p.id = :id"),
        @NamedQuery(name = Parameter.LIST_ALL_BY_IDS, query = "SELECT p FROM Parameter p WHERE p.id IN :ids"),
        @NamedQuery(name = Parameter.LIST_ALL, query = "SELECT p FROM Parameter p")
})
public class Parameter implements Serializable {

    public static final String FIND_BY_ID = "Parameter.findByName";
    public static final String LIST_ALL = "Parameter.listAll";
    public static final String LIST_ALL_BY_IDS = "Paramater.listAllByIds";

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "param_id")
    private String id;

    @Column(name = "param_value")
    private String value;

    @Column(name = "param_description")
    private String description;

    public String getParamId() {
        return this.id;
    }

    public void setParamId(String paramId) {
        this.id = paramId;
    }

    public String getParamValue() {
        return this.value;
    }

    public void setParamValue(String value) {
        this.value = value;
    }

    public String getParamDescription() {
        return this.description;
    }

    public void setParamDescription(String description) {
        this.description = description;
    }

}