package pro.landlabs.pricing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Objects;

import java.util.List;

public class PriceDataChunk {

    private final List<Price<JsonNode>> prices;

    @JsonCreator
    public PriceDataChunk(@JsonProperty("prices") List<Price<JsonNode>> prices) {
        this.prices = prices;
    }

    public List<Price<JsonNode>> getPrices() {
        return prices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceDataChunk that = (PriceDataChunk) o;
        return Objects.equal(prices, that.prices);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prices);
    }

}
