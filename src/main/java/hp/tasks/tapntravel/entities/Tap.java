package hp.tasks.tapntravel.entities;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "begin_stop_id", referencedColumnName = "id", nullable = false)
    private Stop beginStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_stop_id", referencedColumnName = "id", nullable = false)
    private Stop endStop;

    @Column(name = "begin_date_time", nullable = false)
    private LocalDateTime beginDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    public BigInteger getId() { return id; }
    public void setId(BigInteger id) { this.id = id; }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }

    public Integer getBusCompanyId() { return busCompanyId; }
    public void setBusCompanyId(Integer busCompanyId) { this.busCompanyId = busCompanyId; }

    public Stop getBeginStop() { return beginStop; }
    public void setBeginStop(Stop stop) { this.beginStop = stop; }

    public Stop getEndStop() { return endStop; }
    public void setEndStop(Stop stop) { this.endStop = stop; }

    public LocalDateTime getBeginDateTime() { return beginDateTime; }
    public void setBeginDateTime(LocalDateTime dateTime) { this.beginDateTime = dateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime dateTime) { this.endDateTime = dateTime; }
}