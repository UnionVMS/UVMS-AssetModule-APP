package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;    //leave be
import java.time.ZoneOffset;
import java.util.Objects;

@Embeddable
public class CustomCodesPK  implements Serializable {

    @Transient
    public static final Instant STANDARD_START_DATE = Instant.EPOCH;
    @Transient
    public static final Instant STANDARD_END_DATE = OffsetDateTime.of(3070,01,01,01,1,1,1, ZoneOffset.UTC).toInstant();

    private String constant;
    private String code;

    private Instant validFromDate = STANDARD_START_DATE;

    private Instant validToDate = STANDARD_END_DATE;

    public CustomCodesPK(){
        // intentionally required by JPA
    }

    public CustomCodesPK(String constant, String code, Instant validFromDate, Instant validToDate){
        this.constant = constant;
        this.code = code;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
    }

    public CustomCodesPK(String constant, String code){
        this.constant = constant;
        this.code = code;
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

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(Instant validFromDate) {
        this.validFromDate = validFromDate;
    }

    public Instant getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(Instant validToDate) {
        this.validToDate = validToDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomCodesPK that = (CustomCodesPK) o;
        return Objects.equals(constant, that.constant) &&
                Objects.equals(code, that.code) &&
                Objects.equals(validFromDate, that.validFromDate) &&
                Objects.equals(validToDate, that.validToDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constant, code, validFromDate, validToDate);
    }
}
