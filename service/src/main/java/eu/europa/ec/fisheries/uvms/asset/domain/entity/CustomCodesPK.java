package eu.europa.ec.fisheries.uvms.asset.domain.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Embeddable
public class CustomCodesPK  implements Serializable {

    private String constant;
    private String code;

    private OffsetDateTime validFromDate = OffsetDateTime.of(1970,01,01,1,1,1,1, ZoneOffset.UTC);
    private OffsetDateTime validToDate = OffsetDateTime.of(3070,01,01,01,1,1,1, ZoneOffset.UTC);


    public CustomCodesPK(){
        // intentionally required by JPA
    }

    public CustomCodesPK(String constant, String code, OffsetDateTime validFromDate, OffsetDateTime validToDate){
        this.constant = constant;
        this.code = code;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
    }

    public CustomCodesPK(String constant, String code){
        this.constant = constant;
        this.code = code;
        this.validFromDate = OffsetDateTime.of(1970,01,01,1,1,1,1, ZoneOffset.UTC);
        this.validToDate = OffsetDateTime.of(3070,01,01,01,1,1,1, ZoneOffset.UTC);
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


    public OffsetDateTime getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(OffsetDateTime validFromDate) {
        this.validFromDate = validFromDate;
    }

    public OffsetDateTime getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(OffsetDateTime validToDate) {
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
