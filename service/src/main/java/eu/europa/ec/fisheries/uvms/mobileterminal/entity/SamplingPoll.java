package eu.europa.ec.fisheries.uvms.mobileterminal.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "poll_sampling")
public class SamplingPoll extends PollBase {

    @Column(name = "startdate")
    private Instant startDate;

    @Column(name = "stopdate")
    private Instant stopDate;

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getStopDate() {
        return stopDate;
    }

    public void setStopDate(Instant stopDate) {
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
