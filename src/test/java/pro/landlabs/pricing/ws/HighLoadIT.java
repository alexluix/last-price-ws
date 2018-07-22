package pro.landlabs.pricing.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import pro.landlabs.pricing.App;
import pro.landlabs.pricing.model.Price;
import pro.landlabs.pricing.model.PriceDataChunk;
import pro.landlabs.pricing.test.PriceDataMother;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class HighLoadIT {

    private static final int NUMBER_OF_INSTRUMENTS = 100;
    private static final int PRICE_CHUNK_SIZE = 1_000;
    private static final int NUMBER_OF_CHUNKS_PER_BATCH = 10;
    private static final int NUMBER_OF_BATCHES = 10;

    private static final String PORT = nullEmptyOrElse(System.getProperty("test.server.port"), "8080");
    private static final String BASE_URL = "http://localhost:" + PORT;

    private final LocalDateTime baseDate = LocalDateTime.now();
    private final AtomicInteger dateSequence = new AtomicInteger(1);
    private final AtomicInteger instrumentIdSequence = new AtomicInteger(1);

    private final AsyncHttpClient httpClient = asyncHttpClient();
    private final ObjectMapper objectMapper = new App().objectMapper();

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ConcurrentMap<Long, Price<JsonNode>> expectedPrices = Maps.newConcurrentMap();
    private final ConcurrentLinkedQueue<Exception> exceptions = Queues.newConcurrentLinkedQueue();

    public static String nullEmptyOrElse(String value, String defaultValue) {
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    @BeforeEach
    void setUp() {
        expectedPrices.clear();
        exceptions.clear();
    }

    @Test
    public void loadTest() throws Exception {
        List<Future> futures = performBatchesConcurrently();

        awaitCompletionOf(futures);
        assertThat(expectedPrices.size(), equalTo(NUMBER_OF_INSTRUMENTS));

        for (Price<JsonNode> expectedPrice : expectedPrices.values()) {
            Price<JsonNode> actualPrice = await().atMost(1, TimeUnit.MINUTES)
                    .until(() -> readPrice(expectedPrice.getRefId()),
                            price -> {
                                if (price != null) {
                                    System.out.println(
                                            "Expected: " + expectedPrice.getAsOf() + " Actual: " + price.getAsOf());
                                }

                                return price != null &&
                                        expectedPrice.getAsOf().equals(price.getAsOf());
                            });

            assertThat(exceptions, emptyIterable());
            assertThat(actualPrice, equalTo(expectedPrice));
        }
    }

    private Price<JsonNode> readPrice(long refId) {
        try {
            Response response = httpClient
                    .prepareGet(BASE_URL + "/pricing/instruments/" + refId + "/price")
                    .execute().get();

            if (response.getStatusCode() == HttpStatus.OK.value()) {
                return objectMapper.readValue(response.getResponseBody(), new TypeReference<Price<JsonNode>>(){});
            } else {
                return null;
            }
        } catch (Exception e) {
            exceptions.add(e);
            return null;
        }
    }

    private void awaitCompletionOf(List<Future> futures) throws Exception {
        for (Future future : futures) {
            future.get();
        }
    }

    private List<Future> performBatchesConcurrently() {
        List<Future> futures = Lists.newArrayList();
        for (int i = 0; i < NUMBER_OF_BATCHES; i++) {
            List<PriceDataChunk> chunks = createRandomPriceDataChunks();

            futures.add(executorService.submit(() -> performBatchHandleExceptions(chunks)));
        }

        return futures;
    }

    private void performBatchHandleExceptions(List<PriceDataChunk> chunks) {
        try {
            performBatch(chunks);
        } catch (Exception e) {
            exceptions.add(e);
        }
    }

    private void performBatch(List<PriceDataChunk> chunks) throws Exception {
        long batchId = createBatch();

        for (PriceDataChunk chunk : chunks) {
            postPriceDataChunk(batchId, chunk);
        }

        completeBatch(batchId);
    }

    private void postPriceDataChunk(long batchId, PriceDataChunk priceDataChunk) throws Exception {
        Response response = httpClient.preparePost(BASE_URL + "/pricing/batches/" + batchId)
                .setHeader("Content-type", "application/json")
                .setBody(objectMapper.writeValueAsString(priceDataChunk))
                .execute().get();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK.value()));
    }

    private void completeBatch(long batchId) throws Exception {
        Response response = httpClient
                .preparePost(BASE_URL + "/pricing/batches/" + batchId + "/complete")
                .execute().get();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK.value()));
    }

    private List<PriceDataChunk> createRandomPriceDataChunks() {
        List<PriceDataChunk> chunks = Lists.newArrayList();
        for (int i = 0; i < NUMBER_OF_CHUNKS_PER_BATCH; i++) {
            PriceDataChunk chunk = createRandomPriceDataChunkAlwaysIncreasingDateTime();

            chunks.add(chunk);

            populateExpectedValues(chunk);
        }

        return chunks;
    }

    private void populateExpectedValues(PriceDataChunk chunk) {
        for (Price<JsonNode> price : chunk.getPrices()) {
            expectedPrices.put(price.getRefId(), price);
        }
    }

    private PriceDataChunk createRandomPriceDataChunkAlwaysIncreasingDateTime() {
        List<Price<JsonNode>> list = Lists.newArrayList();

        for (int i = 0; i < PRICE_CHUNK_SIZE; i++) {
            int refId = instrumentIdSequence.getAndIncrement() % NUMBER_OF_INSTRUMENTS + 1;
            LocalDateTime dateTime = baseDate.plusSeconds(dateSequence.getAndIncrement());

            list.add(PriceDataMother.createRandomPrice(refId, dateTime));
        }

        return new PriceDataChunk(list);
    }

    private long createBatch() throws Exception {
        Response response = httpClient.preparePost(BASE_URL + "/pricing/batches").execute().get();
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK.value()));

        long batchId = objectMapper.readValue(response.getResponseBody(), BatchId.class).batchId;
        assertThat(batchId, greaterThan(0L));

        return batchId;
    }

}
