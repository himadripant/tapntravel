package hp.tasks.tapntravel.models;

import org.apache.commons.lang3.builder.ToStringBuilder;

public record BusCompanyZones(Integer busCompanyId, Integer zoneFrom, Integer zoneTo) {
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("busCompanyId", busCompanyId)
                .append("zoneFrom", zoneFrom)
                .append("zoneTo", zoneTo)
                .toString();
    }
}
