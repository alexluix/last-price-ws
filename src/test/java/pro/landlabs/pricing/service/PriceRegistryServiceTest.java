package pro.landlabs.pricing.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pro.landlabs.pricing.model.Price;
import pro.landlabs.pricing.testdata.PriceDataMother;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
    void shouldCreateBatchAddDataToItAndThenReadTheDataFromRegistry() {
        long batchId = subject.createBatch();
        Price<JsonNode> price = PriceDataMother.createRandomPrice();

        subject.addData(batchId, price);
        subject.completeBatch(batchId);

        Price<JsonNode> actualPrice = subject.getPrice(price.getRefId());
        assertThat(actualPrice, equalTo(price));
    }

    @Test
    void shouldNotReadDataFromIncompleteBatch() {
        long batchId = subject.createBatch();
        Price<JsonNode> price = PriceDataMother.createRandomPrice();

        subject.addData(batchId, price);

        Price<JsonNode> actualPrice = subject.getPrice(price.getRefId());
        assertThat(actualPrice, nullValue());
    }

    @Test
    void shouldNotReadDataFromCancelledBatch() {
        long batchId = subject.createBatch();
        Price<JsonNode> price = PriceDataMother.createRandomPrice();

        subject.addData(batchId, price);
        subject.cancelBatch(batchId);

        Price<JsonNode> actualPrice = subject.getPrice(price.getRefId());
        assertThat(actualPrice, nullValue());
    }

    @Test
    void completeAfterCancelShouldHaveNoEffect() {
        long batchId = subject.createBatch();
        Price<JsonNode> price = PriceDataMother.createRandomPrice();

        subject.addData(batchId, price);
        subject.cancelBatch(batchId);
        subject.completeBatch(batchId);

        Price<JsonNode> actualPrice = subject.getPrice(price.getRefId());
        assertThat(actualPrice, nullValue());
    }

    @Test
    void shouldNotAddDataToNonExistingBatch() {
        assertThrows(BatchNotFoundException.class, () -> subject.addData(1, PriceDataMother.createRandomPrice()));
    }

    @Test
    void shouldNotCompleteExistingBatch() {
        assertThrows(BatchNotFoundException.class, () -> subject.completeBatch(1));
    }

    @Test
    void shouldNotCancelExistingBatch() {
        assertThrows(BatchNotFoundException.class, () -> subject.cancelBatch(1));
    }

}
