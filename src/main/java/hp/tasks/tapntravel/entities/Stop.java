package hp.tasks.tapntravel.entities;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "stop")
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "bus_company_id", nullable = false)
    private Integer busCompanyId;

    @Column(name = "zone", nullable = false)
    private Integer zone;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getBusCompanyId() { return busCompanyId; }
    public void setBusCompanyId(Integer busCompanyId) { this.busCompanyId = busCompanyId; }

    public Integer getZone() { return zone; }
    public void setZone(Integer zone) { this.zone = zone; }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("busCompanyId", busCompanyId)
                .append("zone", zone)
                .toString();
    }
}