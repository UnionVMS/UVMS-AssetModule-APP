package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@Audited
@Entity
@Table(name = "mobileterminalattributes", indexes = { @Index(columnList = "mobileterminal_id", name = "mobileterminalattributes_Mobterm_FK_INX10", unique = false),})
@JsonIdentityInfo(generator=ObjectIdGenerators.UUIDGenerator.class/*, property="id"*/)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MobileTerminalAttributes implements Serializable {

    @Id
    @GeneratedValue(generator = "MOBILETERMINALATTRIBUTES_UUID")
    @GenericGenerator(name = "MOBILETERMINALATTRIBUTES_UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="mobileterminal_id", foreignKey = @ForeignKey(name = "MobileTerminalAttributes_MobileTerminal_FK"))
    @Fetch(FetchMode.SELECT)
    private MobileTerminal mobileTerminal;

    @Size(max = 60)
    @Column(name = "attribute")
    private String attribute;

    @Size(max = 60)
    @Column(name = "value")
    private String value;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MobileTerminal getMobileTerminal() {
        return mobileTerminal;
    }

    public void setMobileTerminal(MobileTerminal mobileTerminal) {
        this.mobileTerminal = mobileTerminal;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
