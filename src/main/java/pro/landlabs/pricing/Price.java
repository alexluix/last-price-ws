package pro.landlabs.pricing;

import org.joda.time.DateTime;

public class Price<T> {

    private final long refId;
    private final DateTime asOf;
    private final T payload;

    public Price(long refId, DateTime asOf, T payload) {
        this.refId = refId;
        this.asOf = asOf;
        this.payload = payload;
    }

    public long getRefId() {
        return refId;
    }

    public DateTime getAsOf() {
        return asOf;
    }

    public T getPayload() {
        return payload;
    }

}
