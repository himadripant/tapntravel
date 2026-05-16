package hp.tasks.tapntravel.models;

import java.time.ZonedDateTime;

public record TapFromFile(Long id, ZonedDateTime timestamp, TapType tapType, Integer stopId, Integer companyId, String busId, String pan) {

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TapFromFile{");
        sb.append("id=").append(id);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", tapType=").append(tapType);
        sb.append(", stopId=").append(stopId);
        sb.append(", companyId=").append(companyId);
        sb.append(", busId='").append(busId).append('\'');
        sb.append(", pan='").append(pan).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
