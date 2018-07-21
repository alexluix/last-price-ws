package pro.landlabs.pricing.testdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.joda.time.LocalDateTime;
import pro.landlabs.pricing.model.Price;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;

public class PriceMother {

    private static final String TEST_DATA_FOLDER = "test-data";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String getJsonPriceDataChunk1() {
        return readResourceFile(TEST_DATA_FOLDER + "/price-data-chunk-1.json");
    }

    public static String getJsonPriceDataChunkCorrupted() {
        return readResourceFile(TEST_DATA_FOLDER + "/price-data-chunk-corrupted.json");
    }

    private static String readResourceFile(String filePath) {
        URL url = Resources.getResource(filePath);
        try {
            return Resources.toString(url, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Price<JsonNode> createRandomPrice() {
        Random random = new Random();
        int refId = random.nextInt(1_000);
        double randomAmount = 1 + Math.pow(random.nextInt(10), -1);

        JsonNode pricePayload;
        try {
            pricePayload = objectMapper.readValue("{ \"price\": " + randomAmount + " }", JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return createPrice(refId, LocalDateTime.now(), pricePayload);
    }

    private static Price<JsonNode> createPrice(long refId, LocalDateTime asOf, JsonNode pricePayload) {
        return new Price<>(refId, asOf, pricePayload);
    }

}
