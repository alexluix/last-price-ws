package pro.landlabs.pricing.common;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompactingBatchTest {

    private CompactingBatch<Long, DateTime> subject;

    private final Queue<Map<Long, DateTime>> consumerQueue = new LinkedList<>();

    @BeforeEach
    void setUp() {
        BiFunction<DateTime, DateTime, DateTime> mergeFunction =
                (existing, incoming) -> existing.isBefore(incoming) ? incoming : existing;

        subject = new CompactingBatch<>(mergeFunction, consumerQueue::offer);

        consumerQueue.clear();
    }

    @Test
    void shouldNotPublishUntilCompleted() {
        Long key = 1L;
        DateTime value = DateTime.now();

        subject.add(key, value);

        assertThat(consumerQueue.size(), equalTo(0));
    }

    @Test
    void shouldPublishSingleEntry() {
        Long key = 1L;
        DateTime value = DateTime.now();

        subject.add(key, value);
        subject.complete();

        assertThat(consumerQueue.size(), equalTo(1));
        Map<Long, DateTime> map = consumerQueue.poll();
        assertNotNull(map);
        assertThat(map.get(key), equalTo(value));
    }

    @Test
    void shouldNotAddNullableKey() {
        assertThrows(IllegalArgumentException.class, () -> subject.add(null, DateTime.now()));
    }

    @Test
    void shouldComplete() {
        subject.complete();
    }

    @Test
    void shouldCancel() {
        subject.cancel();
    }

}
