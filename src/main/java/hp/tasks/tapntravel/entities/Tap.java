package hp.tasks.tapntravel.entities;

import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "tap")
public class Tap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "PAN", columnDefinition = "char(16)", nullable = false)
    private String pan;

    @Column(name = "bus_id", columnDefinition = "varchar(8)", nullable = false)
    private String busId;

    @Column(name = "bus_company_id", nullable = false)
    private Integer busCompanyId;

    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "begin_stop_id", referencedColumnName = "id", nullable = false)
    private Stop beginStop;

    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "end_stop_id", referencedColumnName = "id")
    private Stop endStop;

    @Column(name = "begin_date_time", nullable = false)
    private ZonedDateTime beginDateTime;

    @Column(name = "end_date_time")
    private ZonedDateTime endDateTime;

    public Tap() {
    }

    public Tap(String pan, String busId, Integer busCompanyId, Stop beginStop, ZonedDateTime beginDateTime) {
        this.pan = pan;
        this.busId = busId;
        this.busCompanyId = busCompanyId;
        this.beginStop = beginStop;
        this.beginDateTime = beginDateTime;
    }

    public BigInteger getId() {
        return id;
    }

    public String getPan() {
        return pan;
    }

    public Tap setPan(String pan) {
        this.pan = pan;
        return this;
    }

    public String getBusId() {
        return busId;
    }

    public Tap setBusId(String busId) {
        this.busId = busId;
        return this;
    }

    public Integer getBusCompanyId() {
        return busCompanyId;
    }

    public Tap setBusCompanyId(Integer busCompanyId) {
        this.busCompanyId = busCompanyId;
        return this;
    }

    public Stop getBeginStop() {
        return beginStop;
    }

    public Tap setBeginStop(Stop beginStop) {
        this.beginStop = beginStop;
        return this;
    }

    public Stop getEndStop() {
        return endStop;
    }

    public Tap setEndStop(Stop endStop) {
        this.endStop = endStop;
        return this;
    }

    public ZonedDateTime getBeginDateTime() {
        return beginDateTime;
    }

    public Tap setBeginDateTime(ZonedDateTime beginDateTime) {
        this.beginDateTime = beginDateTime;
        return this;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public Tap setEndDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("pan",
                        StringUtils.left(pan, 6) + "..." + StringUtils.right(busId, 4))
                .append("busId", busId)
                .append("busCompanyId", busCompanyId)
                .append("beginStop", beginStop)
                .append("endStop", endStop)
                .append("beginDateTime", beginDateTime)
                .append("endDateTime", endDateTime)
                .toString();
    }
}