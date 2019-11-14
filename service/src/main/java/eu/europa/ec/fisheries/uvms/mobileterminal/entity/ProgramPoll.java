package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import eu.europa.ec.fisheries.uvms.mobileterminal.constants.MobileTerminalConstants;
import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;
import eu.europa.ec.fisheries.uvms.mobileterminal.util.OffsetDateTimeDeserializer;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "poll_program")
@NamedQueries({
        @NamedQuery(name = "PollProgram.findAll", query = "SELECT p FROM ProgramPoll p"),
        @NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_BY_ID, query = "SELECT p FROM ProgramPoll p WHERE p.id = :id"),
        @NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_ALIVE, query = "SELECT p FROM ProgramPoll  p WHERE p.stopDate > :currentDate " +
                "AND p.pollState <> eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum.ARCHIVED"),
        @NamedQuery(name = MobileTerminalConstants.POLL_PROGRAM_FIND_RUNNING_AND_STARTED,
                query = "SELECT p FROM ProgramPoll  p WHERE p.startDate < :currentDate AND p.pollState = eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum.STARTED") })
public class ProgramPoll extends PollBase {

    @Column(name = "frequency")
    private Integer frequency;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "startdate")
    private OffsetDateTime startDate;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Column(name = "stopdate")
    private OffsetDateTime stopDate;

    @Column(name = "latestruntime")
    private OffsetDateTime latestRun;

    @Enumerated(EnumType.STRING)
    @Column(name = "pollstate")
    private PollStateEnum pollState;

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

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

    public OffsetDateTime getLatestRun() {
        return latestRun;
    }

    public void setLatestRun(OffsetDateTime latestRun) {
        this.latestRun = latestRun;
    }

    public PollStateEnum getPollState() {
        return pollState;
    }

    public void setPollState(PollStateEnum pollState) {
        this.pollState = pollState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramPoll that = (ProgramPoll) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(frequency, that.frequency) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(stopDate, that.stopDate) &&
                Objects.equals(latestRun, that.latestRun) &&
                Objects.equals(pollState, that.pollState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), frequency, startDate, stopDate, latestRun, pollState);
    }
}
