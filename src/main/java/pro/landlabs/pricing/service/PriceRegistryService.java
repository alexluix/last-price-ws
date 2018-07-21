package pro.landlabs.pricing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import pro.landlabs.pricing.common.CompactingBatch;
import pro.landlabs.pricing.common.CompactingRegistry;
import pro.landlabs.pricing.model.Price;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

public class PriceRegistryService {

    private final BiFunction<Price<JsonNode>, Price<JsonNode>, Price<JsonNode>> mergeFunction =
            (existing, incoming) -> existing.getAsOf().isBefore(incoming.getAsOf()) ? incoming : existing;

    private final ConcurrentMap<Long, CompactingBatch<Long, Price<JsonNode>>> batches = Maps.newConcurrentMap();
    private final AtomicLong batchSequenceId = new AtomicLong(1);

    private final CompactingRegistry<Long, Price<JsonNode>> registry = new CompactingRegistry<>(mergeFunction);

    long createBatch() {
        CompactingBatch<Long, Price<JsonNode>> batch = registry.createBatch();

        long batchId = batchSequenceId.incrementAndGet();
        batches.put(batchId, batch);

        return batchId;
    }

    void addData(long batchId, Price<JsonNode> price) {
        getBatch(batchId).add(price.getRefId(), price);
    }

    void completeBatch(long batchId) {
        getBatch(batchId).complete();
    }

    void cancelBatch(long batchId) {
        getBatch(batchId).cancel();
    }

    public Price<JsonNode> getPrice(long refId) {
        return registry.getValue(refId);
    }

    private CompactingBatch<Long, Price<JsonNode>> getBatch(long batchId) {
        CompactingBatch<Long, Price<JsonNode>> batch = batches.get(batchId);

        if (batch == null) throw new BatchNotFoundException();

        return batch;
    }

}
