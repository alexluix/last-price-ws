package pro.landlabs.pricing.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class PriceSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldSerializeAndDeserializeBack() throws Exception {
        Price<JsonNode> price = PriceMother.createRandomPrice();

        String jsonString = objectMapper.writeValueAsString(price);

        Price deserializedPrice = objectMapper.readValue(jsonString, new TypeReference<Price<JsonNode>>() {});
        assertThat(deserializedPrice, equalTo(price));
    }

}
