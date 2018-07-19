package pro.landlabs.pricing;

public class SimplePriceBatch implements PriceBatch {

    @Override
    public void add(Price price) {
        if (price == null) throw new IllegalArgumentException();
    }

}
