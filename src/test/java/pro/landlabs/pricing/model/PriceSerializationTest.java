package pro.landlabs.pricing.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PriceSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldSerializeAndDeserializeBack() throws Exception {
        long refId = 3;
        LocalDateTime asOf = LocalDateTime.now();
        JsonNode pricePayload = objectMapper.readValue("{ \"price\": 7 }", JsonNode.class);
        Price<JsonNode> price = new Price<>(refId, asOf, pricePayload);

        String jsonString = objectMapper.writeValueAsString(price);

        System.out.println(jsonString);

        Price deserializedPrice = objectMapper.readValue(jsonString, new TypeReference<Price<JsonNode>>() {});
        assertThat(deserializedPrice, equalTo(price));
    }

}
