package hp.tasks.tapntravel.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

public record BusCompanyZone(Integer busCompanyId, Integer zoneFrom) {
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("busCompanyId", busCompanyId)
                .append("zoneFrom", zoneFrom)
                .toString();
    }
}
