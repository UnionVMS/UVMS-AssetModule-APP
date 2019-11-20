package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.OffsetDateTimeDeserializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "poll_sampling")
public class SamplingPoll extends PollBase {

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "startdate")
    private OffsetDateTime startDate;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "stopdate")
    private OffsetDateTime stopDate;

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getStopDate() {
        return stopDate;
    }

    public void setStopDate(OffsetDateTime stopDate) {
        this.stopDate = stopDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamplingPoll that = (SamplingPoll) o;
        return Objects.equals(getId(), that.getId()) &&
               Objects.equals(startDate, that.startDate) &&
               Objects.equals(stopDate, that.stopDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), startDate, stopDate);
    }
}
