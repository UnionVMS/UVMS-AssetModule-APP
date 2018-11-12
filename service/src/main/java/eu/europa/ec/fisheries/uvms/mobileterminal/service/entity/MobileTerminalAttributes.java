package eu.europa.ec.fisheries.uvms.mobileterminal.service.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/*possible way to solve the Attribute problem using sql instead of creating a new table:
          select
                CASE
                        WHEN length(attributes) - length(regexp_replace(attributes,';','','g')) / length(';') = 1 THEN REPLACE(SUBSTRING(attributes from position('serialNumber=' in attributes)+13 for 60),';','')
                        WHEN length(attributes) - length(regexp_replace(attributes,';','','g')) / length(';') > 1 THEN REPLACE(SUBSTRING(split_part(attributes, ';',length(LEFT(attributes,position('serialNumber=' in attributes))) - (length(regexp_replace(LEFT(attributes,position('serialNumber=' in attributes)),';','','g'))-1) / length(';')) from position('serialNumber=' in split_part(attributes, ';',length(LEFT(attributes,position('serialNumber=' in attributes))) - (length(regexp_replace(LEFT(attributes,position('serialNumber=' in attributes)),';','','g'))-1) / length(';')))+13 for 60),';','')


                END

          FROM mobterm.mobileterminalevent
           */

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

    @NotNull
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
