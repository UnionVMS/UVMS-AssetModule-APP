package eu.europa.ec.fisheries.uvms.mobileterminal.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "poll_configuration")
public class ConfigurationPoll extends PollBase {

    @Column(name = "reporting_freq")
    private Integer reportingFrequency;

    @Column(name = "grace_period")
    private Integer gracePeriod;

    @Column(name = "in_port_grace")
    private Integer inPortGrace;

    public Integer getReportingFrequency() {
        return reportingFrequency;
    }

    public void setReportingFrequency(Integer reportingFrequency) {
        this.reportingFrequency = reportingFrequency;
    }

    public Integer getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Integer gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public Integer getInPortGrace() {
        return inPortGrace;
    }

    public void setInPortGrace(Integer inPortGrace) {
        this.inPortGrace = inPortGrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationPoll that = (ConfigurationPoll) o;
        return
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(reportingFrequency, that.reportingFrequency) &&
                Objects.equals(gracePeriod, that.gracePeriod) &&
                Objects.equals(inPortGrace, that.inPortGrace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), reportingFrequency, gracePeriod, inPortGrace);
    }
}
