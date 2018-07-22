package pro.landlabs.pricing.ws;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class BatchId {
    public final long batchId;

    @JsonCreator
    private BatchId(@JsonProperty("batchId") long batchId) {
        this.batchId = batchId;
    }
}
