package pro.landlabs.pricing.common;

import com.google.common.collect.Maps;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@ThreadSafe
public class CompactingRegistry<K, V> {

    private final Map<K, V> registryMap = Maps.newConcurrentMap();

    private final BiFunction<V, V, V> mergeFunction;
    private final Consumer<Map<K, V>> consumer;

    public CompactingRegistry(BiFunction<V, V, V> mergeFunction) {
        this.mergeFunction = mergeFunction;
        this.consumer = map -> map.forEach(
                (key, value) -> registryMap.merge(key, value, mergeFunction));
    }

    public CompactingBatch<K, V> createBatch() {
        return new CompactingBatch<>(mergeFunction, consumer);
    }

    public V getValue(K key) {
        return registryMap.get(key);
    }

}
