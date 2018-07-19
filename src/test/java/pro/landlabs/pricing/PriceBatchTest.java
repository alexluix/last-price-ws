package pro.landlabs.pricing;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PriceBatchTest {

    private PriceBatch subject = new SimplePriceBatch();

    @Test
    void shouldAddPrice() {
        long id = 1;
        double amount = 1.1;
        DateTime asOf = DateTime.now();

        Price<Double> price = new Price<>(id, asOf, amount);

        subject.add(price);
    }

    @Test
    void shouldNotAddNullablePrice() {
        assertThrows(IllegalArgumentException.class, () -> subject.add(null));
    }

}
