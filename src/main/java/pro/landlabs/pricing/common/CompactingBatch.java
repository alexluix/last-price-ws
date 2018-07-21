package pro.landlabs.pricing.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@NotThreadSafe
public class CompactingBatch<K, V> {

    private final BiFunction<V, V, V> mergeFunction;
    private final ConcurrentMap<K, V> batchMap = Maps.newConcurrentMap();

    private Consumer<Map<K, V>> consumer;

    public CompactingBatch(BiFunction<V, V, V> mergeFunction, Consumer<Map<K, V>> consumer) {
        this.mergeFunction = mergeFunction;
        this.consumer = consumer;
    }

    public void add(K key, V value) {
        if (key == null) throw new IllegalArgumentException("nullable key");
        if (value == null) throw new IllegalArgumentException("nullable value");

        batchMap.merge(key, value, mergeFunction);
    }

    public void complete() {
        consumer.accept(ImmutableMap.copyOf(batchMap));
        reset();
    }

    public void cancel() {
        reset();
    }

    private void reset() {
        batchMap.clear();
        consumer = map -> {};
    }

}
