package pro.landlabs.pricing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.landlabs.pricing.testdata.PriceDataMother;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriceRegistryServiceTest {

    private PriceRegistryService subject;

    @BeforeEach
    void setUp() {
        subject = new PriceRegistryService();
    }

    @Test
    void shouldCreateBatchWithProperId() {
        long batchId = subject.createBatch();

        assertThat(batchId, greaterThan(0L));
    }

    @Test
    void shouldCreateBatchAndAddDataToIt() {
        long batchId = subject.createBatch();
        assertThat(batchId, greaterThan(0L));

        subject.addData(batchId, PriceDataMother.createRandomPrice());
    }

    @Test
    void shouldNotAddDataToNonExistingBatch() {
        assertThrows(BatchNotFoundException.class, () -> subject.addData(1, PriceDataMother.createRandomPrice()));
    }

}
