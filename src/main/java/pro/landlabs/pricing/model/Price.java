package pro.landlabs.pricing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.joda.time.LocalDateTime;

public class Price<T> {

    private final long refId;
    private final LocalDateTime asOf;
    private final T payload;

    @JsonCreator
    public Price(
            @JsonProperty("refId") long refId,
            @JsonProperty("asOf") LocalDateTime asOf,
            @JsonProperty("payload") T payload) {
        this.refId = refId;
        this.asOf = asOf;
        this.payload = payload;
    }

    public long getRefId() {
        return refId;
    }

    public LocalDateTime getAsOf() {
        return asOf;
    }

    public T getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price<?> price = (Price<?>) o;
        return refId == price.refId &&
                Objects.equal(asOf, price.asOf) &&
                Objects.equal(payload, price.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(refId, asOf, payload);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("refId", refId)
                .add("asOf", asOf)
                .add("payload", payload)
                .toString();
    }

}
