package hp.tasks.tapntravel.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.ZonedDateTime;

public record TapFromFile(
        Long id,
        ZonedDateTime timestamp,
        TapType tapType,
        Integer stopId,
        Integer companyId,
        String busId,
        String pan
) {
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("timestamp", timestamp)
                .append("tapType", tapType)
                .append("stopId", stopId)
                .append("companyId", companyId)
                .append("busId", busId)
                .append("pan", StringUtils.left(pan, 6) + "..." + StringUtils.right(pan, 4))
                .toString();
    }
}
