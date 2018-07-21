package pro.landlabs.pricing.common;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.landlabs.pricing.testdata.TestCommons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CompactingRegistryTest {

    private CompactingRegistry<Long, DateTime> subject;

    @BeforeEach
    void setUp() {
        subject = new CompactingRegistry<>(TestCommons.LAST_DATE_MERGE);
    }

    @Test
    void shouldCreateBatch() {
        CompactingBatch batch = subject.createBatch();

        assertThat(batch, notNullValue());
    }

    @Test
    void shouldReturnNullWhenNotFound() {
        assertThat(subject.getValue(1L), nullValue());
    }

    @Test
    void shouldBePopulatedWithSingleBatch() {
        CompactingBatch<Long, DateTime> batch = subject.createBatch();
        long refId = 1L;
        DateTime dateTime = DateTime.now();
        batch.add(refId, dateTime);
        batch.complete();

        DateTime actualDateTime = subject.getValue(refId);

        assertThat(actualDateTime, equalTo(dateTime));
    }

    @Test
    void shouldNotBePopulatedWhenCancelled() {
        CompactingBatch<Long, DateTime> batch = subject.createBatch();
        long refId = 1L;
        DateTime dateTime = DateTime.now();
        batch.add(refId, dateTime);
        batch.cancel();

        assertThat(subject.getValue(refId), nullValue());
    }

    @Test
    void shouldNotBePopulatedWhenNotCompleted() {
        CompactingBatch<Long, DateTime> batch = subject.createBatch();
        long refId = 1L;
        DateTime dateTime = DateTime.now();
        batch.add(refId, dateTime);

        assertThat(subject.getValue(refId), nullValue());
    }

    @Test
    void shouldMergeWithinBatch() {
        CompactingBatch<Long, DateTime> batch = subject.createBatch();
        DateTime dateTime = DateTime.now();
        DateTime latestDateTime = dateTime.plusMinutes(1);

        long refId = 1L;
        batch.add(refId, dateTime);
        batch.add(refId, latestDateTime);
        batch.complete();

        assertThat(subject.getValue(refId), equalTo(latestDateTime));
    }

    @Test
    void shouldNotMergeWithinBatchWhenEarlierUpdateArrivesLater() {
        CompactingBatch<Long, DateTime> batch = subject.createBatch();
        DateTime dateTime = DateTime.now();
        DateTime latestDateTime = dateTime.plusHours(1);

        long refId = 1L;
        batch.add(refId, latestDateTime);
        batch.add(refId, dateTime);
        batch.complete();

        assertThat(subject.getValue(refId), equalTo(latestDateTime));
    }

    @Test
    void shouldBePopulatedWithMultipleBatches() {
        DateTime dateTime = DateTime.now();
        DateTime dateTime2 = dateTime.plusHours(1);

        executeInBatch(1L, dateTime);
        executeInBatch(2L, dateTime2);

        assertThat(subject.getValue(1L), equalTo(dateTime));
        assertThat(subject.getValue(2L), equalTo(dateTime2));
    }

    @Test
    void shouldMergeFromMultipleBranches() {
        DateTime dateTime = DateTime.now();
        DateTime latestDateTime = dateTime.plusMinutes(1);
        DateTime dateTime2 = dateTime.plusHours(1);

        executeInBatch(1L, dateTime);
        executeInBatch(1L, latestDateTime);
        executeInBatch(2L, dateTime2);

        assertThat(subject.getValue(1L), equalTo(latestDateTime));
        assertThat(subject.getValue(2L), equalTo(dateTime2));
    }

    private void executeInBatch(long refId, DateTime dateTime) {
        CompactingBatch<Long, DateTime> batch = subject.createBatch();
        batch.add(refId, dateTime);
        batch.complete();
    }

}
