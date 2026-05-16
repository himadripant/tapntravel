package hp.tasks.tapntravel.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "zone_fare")
public class ZoneFare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "zone_from", nullable = false)
    private Integer zoneFrom;

    @Column(name = "zone_to", nullable = false)
    private Integer zoneTo;

    @Column(name = "bus_company_id", nullable = false)
    private Integer busCompanyId;

    @Column(name = "price", precision = 5, scale = 2, nullable = false)
    private BigDecimal price;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getZoneFrom() { return zoneFrom; }
    public void setZoneFrom(Integer zoneFrom) { this.zoneFrom = zoneFrom; }

    public Integer getZoneTo() { return zoneTo; }
    public void setZoneTo(Integer zoneTo) { this.zoneTo = zoneTo; }

    public Integer getBusCompanyId() { return busCompanyId; }
    public void setBusCompanyId(Integer busCompanyId) { this.busCompanyId = busCompanyId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}