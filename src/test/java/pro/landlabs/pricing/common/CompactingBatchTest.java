package pro.landlabs.pricing.common;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pro.landlabs.pricing.testdata.TestCommons.LAST_DATE_MERGE;

class CompactingBatchTest {

    private CompactingBatch<Long, DateTime> subject;

    private final Queue<Map<Long, DateTime>> consumerQueue = new LinkedList<>();

    @BeforeEach
    void setUp() {
        subject = new CompactingBatch<>(LAST_DATE_MERGE, consumerQueue::offer);

        consumerQueue.clear();
    }

    @Test
    void shouldNotPublishUntilCompleted() {
        Long key = 1L;
        DateTime value = DateTime.now();

        subject.add(key, value);

        assertThat(consumerQueue.isEmpty(), is(true));
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
        assertThat(map.size(), equalTo(1));
        assertThat(map.get(key), equalTo(value));
    }

    @Test
    void shouldPublishMultipleEntries() {
        DateTime currentTime = DateTime.now();

        Long key1 = 1L;
        DateTime value1 = currentTime.plusMinutes(1);

        Long key2 = 2L;
        DateTime value2 = currentTime.plusMinutes(2);

        subject.add(key1, value1);
        subject.add(key2, value2);
        subject.complete();

        assertThat(consumerQueue.size(), equalTo(1));
        Map<Long, DateTime> map = consumerQueue.poll();
        assertNotNull(map);
        assertThat(map.size(), equalTo(2));
        assertThat(map.get(key1), equalTo(value1));
        assertThat(map.get(key2), equalTo(value2));
    }

    @Test
    void shouldOverwriteEntriesIfRemapped() {
        DateTime currentTime = DateTime.now();

        Long key1 = 1L;
        DateTime value1 = currentTime.plusMinutes(1);

        Long key2 = 2L;
        DateTime olderValue2 = currentTime.plusMinutes(2);
        DateTime latestValue2 = currentTime.plusMinutes(3);

        subject.add(key1, value1);
        subject.add(key2, olderValue2);
        subject.add(key2, latestValue2);
        subject.complete();

        assertThat(consumerQueue.size(), equalTo(1));
        Map<Long, DateTime> map = consumerQueue.poll();
        assertNotNull(map);
        assertThat(map.size(), equalTo(2));
        assertThat(map.get(key1), equalTo(value1));
        assertThat(map.get(key2), equalTo(latestValue2));
    }

    @Test
    void shouldNotOverwriteEntriesIfNotRemapped() {
        DateTime currentTime = DateTime.now();

        Long key1 = 1L;
        DateTime value1 = currentTime.plusMinutes(1);

        Long key2 = 2L;
        DateTime latestValue2 = currentTime.plusMinutes(2);
        DateTime olderValue2 = currentTime.plusMinutes(1);

        subject.add(key1, value1);
        subject.add(key2, latestValue2);
        subject.add(key2, olderValue2);
        subject.complete();

        assertThat(consumerQueue.size(), equalTo(1));
        Map<Long, DateTime> map = consumerQueue.poll();
        assertNotNull(map);
        assertThat(map.size(), equalTo(2));
        assertThat(map.get(key1), equalTo(value1));
        assertThat(map.get(key2), equalTo(latestValue2));
    }

    @Test
    void shouldNotAddNullableKey() {
        assertThrows(IllegalArgumentException.class, () -> subject.add(null, DateTime.now()));
    }

    @Test
    void shouldNotAddNullableValue() {
        assertThrows(IllegalArgumentException.class, () -> subject.add(1L, null));
    }

    @Test
    void shouldCancel() {
        Long key = 1L;
        DateTime value = DateTime.now();
        subject.add(key, value);

        subject.cancel();
        subject.complete();

        assertThat(consumerQueue.isEmpty(), is(true));
    }

}
