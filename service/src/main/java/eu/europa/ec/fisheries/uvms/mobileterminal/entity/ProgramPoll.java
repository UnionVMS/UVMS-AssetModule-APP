package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "poll_program")
@NamedQueries({
        @NamedQuery(name = ProgramPoll.PROGRAM_FIND_ALL, query = "SELECT p FROM ProgramPoll p"),
        @NamedQuery(name = ProgramPoll.PROGRAM_FIND_BY_ID, query = "SELECT p FROM ProgramPoll p WHERE p.id = :id"),
        @NamedQuery(name = ProgramPoll.PROGRAM_FIND_ALIVE, query = "SELECT p FROM ProgramPoll  p WHERE p.stopDate > :currentDate " +
                "AND p.pollState <> eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum.ARCHIVED"),
        @NamedQuery(name = ProgramPoll.PROGRAM_FIND_RUNNING_AND_STARTED,
                query = "SELECT p FROM ProgramPoll  p WHERE p.startDate < :currentDate AND p.pollState = eu.europa.ec.fisheries.uvms.mobileterminal.entity.types.PollStateEnum.STARTED") })
public class ProgramPoll extends PollBase {

    public static final String PROGRAM_FIND_ALL = "PollProgram.findAll";
    public static final String PROGRAM_FIND_BY_ID = "PollProgram.findById";
    public static final String PROGRAM_FIND_ALIVE = "PollProgram.findAlive";
    public static final String PROGRAM_FIND_RUNNING_AND_STARTED = "PollProgram.findRunningAndStarted";

    @Column(name = "frequency")
    private Integer frequency;

    @Column(name = "startdate")
    private Instant startDate;

    @Column(name = "stopdate")
    private Instant stopDate;

    @Column(name = "latestruntime")
    private Instant latestRun;

    @Enumerated(EnumType.STRING)
    @Column(name = "pollstate")
    private PollStateEnum pollState;

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

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

    public Instant getLatestRun() {
        return latestRun;
    }

    public void setLatestRun(Instant latestRun) {
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
