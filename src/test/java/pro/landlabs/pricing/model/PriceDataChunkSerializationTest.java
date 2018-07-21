package pro.landlabs.pricing.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.landlabs.pricing.testdata.PriceMother;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PriceDataChunkSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldSerializeAndDeserializeBack() throws Exception {
        // given
        Price<JsonNode> randomPrice1 = PriceMother.createRandomPrice();
        Price<JsonNode> randomPrice2 = PriceMother.createRandomPrice();
        Price<JsonNode> randomPrice3 = PriceMother.createRandomPrice();
        ImmutableList<Price<JsonNode>> prices = ImmutableList.of(
                randomPrice1, randomPrice2, randomPrice3
        );
        PriceDataChunk priceDataChunk = new PriceDataChunk(prices);

        // when
        String jsonString = objectMapper.writeValueAsString(priceDataChunk);
        System.out.println(jsonString);

        // then
        PriceDataChunk deserializedPriceDataChunk = objectMapper.readValue(jsonString, PriceDataChunk.class);
        assertThat(deserializedPriceDataChunk, equalTo(priceDataChunk));
    }

}
