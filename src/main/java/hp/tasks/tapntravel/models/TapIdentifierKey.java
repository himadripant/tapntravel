package hp.tasks.tapntravel.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public record TapIdentifierKey(String pan, Integer busCompanyId, String busId) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TapIdentifierKey that = (TapIdentifierKey) o;

        return new EqualsBuilder().append(pan, that.pan).append(busId, that.busId).append(busCompanyId, that.busCompanyId).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(pan).append(busCompanyId).append(busId).toHashCode();
    }
}
