package pro.landlabs.pricing.common;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.landlabs.pricing.testdata.TestCommons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

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

}
