package pro.landlabs.pricing.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pro.landlabs.pricing.App;
import pro.landlabs.pricing.model.Price;
import pro.landlabs.pricing.model.PriceDataChunk;
import pro.landlabs.pricing.test.PriceDataMother;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class HighLoadIT {

    private static final int PRICE_CHUNK_SIZE = 1_000;
    private static final int NUMBER_OF_CHUNKS_PER_BATCH = 1_000;

    private static final String PORT = System.getProperty("test.server.port", "8080");
    private static final String BASE_URL = "http://localhost:" + PORT;

    private final LocalDateTime baseDate = LocalDateTime.now().year().roundFloorCopy();
    private final AtomicInteger dayCount = new AtomicInteger(1);
    private final AtomicInteger instrumentIdSequence = new AtomicInteger(1);

    private AsyncHttpClient httpClient = asyncHttpClient();
    private ObjectMapper objectMapper = new App().objectMapper();

    @Test
    public void loadTest() throws Exception {
        long batchId = createBatch();

        List<PriceDataChunk> chunks = createRandomPriceDataChunks();

        for (PriceDataChunk chunk : chunks) {
            postPriceDataChunk(batchId, chunk);
        }

        Response response = httpClient
                .preparePost(BASE_URL + "/pricing/batches/" + batchId + "/complete")
                .execute().get();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK.value()));
    }

    private List<PriceDataChunk> createRandomPriceDataChunks() {
        List<PriceDataChunk> chunks = Lists.newArrayList();
        for (int i = 0; i < NUMBER_OF_CHUNKS_PER_BATCH; i++) {
            chunks.add(createRandomPriceDataChunk());
        }
        return chunks;
    }

    private void postPriceDataChunk(long batchId, PriceDataChunk priceDataChunk) throws Exception {
        Response response = httpClient.preparePost(BASE_URL + "/pricing/batches/" + batchId)
                .setHeader("Content-type", "application/json")
                .setBody(objectMapper.writeValueAsString(priceDataChunk))
                .execute().get();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK.value()));
    }

    private long createBatch() throws Exception {
        Response response = httpClient.preparePost(BASE_URL + "/pricing/batches").execute().get();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK.value()));

        long batchId = objectMapper.readValue(response.getResponseBody(), BatchId.class).batchId;
        assertThat(batchId, greaterThan(0L));

        return batchId;
    }

    private PriceDataChunk createRandomPriceDataChunk() {
        List<Price<JsonNode>> list = Lists.newArrayList();

        for (int i = 0; i < PRICE_CHUNK_SIZE; i++) {
            int refId = instrumentIdSequence.getAndIncrement();
            LocalDateTime dateTime = baseDate.plusSeconds(dayCount.getAndIncrement());
            list.add(PriceDataMother.createRandomPrice(refId, dateTime));
        }

        return new PriceDataChunk(list);
    }

}
