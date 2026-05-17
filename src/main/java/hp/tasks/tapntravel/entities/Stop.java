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

    public String getName() { return name; }

    public Integer getBusCompanyId() { return busCompanyId; }

    public Integer getZone() { return zone; }

    public Stop setId(Integer id) {
        this.id = id;
        return this;
    }

    public Stop setName(String name) {
        this.name = name;
        return this;
    }

    public Stop setBusCompanyId(Integer busCompanyId) {
        this.busCompanyId = busCompanyId;
        return this;
    }

    public Stop setZone(Integer zone) {
        this.zone = zone;
        return this;
    }

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