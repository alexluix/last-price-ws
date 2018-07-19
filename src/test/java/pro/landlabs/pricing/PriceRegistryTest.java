package pro.landlabs.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class PriceRegistryTest {

    private PriceRegistry subject;

    @BeforeEach
    void setUp() {
        subject = new PriceRegistry();
    }

    @Test
    void shouldCreateBatch() {
        PriceBatch priceBatch = subject.createBatch();

        assertThat(priceBatch, notNullValue());
    }

}
