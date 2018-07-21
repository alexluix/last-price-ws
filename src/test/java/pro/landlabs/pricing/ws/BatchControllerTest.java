package pro.landlabs.pricing.ws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import pro.landlabs.pricing.model.Price;
import pro.landlabs.pricing.model.PriceDataChunk;
import pro.landlabs.pricing.testdata.PriceDataMother;

import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BatchControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void shouldCreateBatch() throws Exception {
        String response = mockMvc.perform(post("/pricing/batch"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andReturn().getResponse().getContentAsString();

        assertThat(Long.valueOf(response), greaterThan(0L));
    }

    @Test
    public void shouldNotPostDataToNonExistingBatch() throws Exception {
        mockMvc.perform(post("/pricing/batch/7")
                .contentType(contentType)
                .content(PriceDataMother.getJsonPriceDataChunk1()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldPostDataToBatch() throws Exception {
        String response = mockMvc.perform(post("/pricing/batch")
                .contentType(contentType))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long batchId = Long.valueOf(response);
        assertThat(batchId, greaterThan(0L));

        mockMvc.perform(post("/pricing/batch/" + batchId)
                .contentType(contentType)
                .content(PriceDataMother.getJsonPriceDataChunk1()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldPostDataAndReadData() throws Exception {
        String response = mockMvc.perform(post("/pricing/batch")
                .contentType(contentType))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long batchId = Long.valueOf(response);
        assertThat(batchId, greaterThan(0L));

        Price<JsonNode> price1 = PriceDataMother.createRandomPrice();
        Price<JsonNode> price2 = PriceDataMother.createRandomPrice();
        Price<JsonNode> price3 = PriceDataMother.createRandomPrice();

        PriceDataChunk priceDataChunk1 = new PriceDataChunk(ImmutableList.of(price1, price2));
        mockMvc.perform(post("/pricing/batch/" + batchId)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(priceDataChunk1)))
                .andExpect(status().isOk());

        PriceDataChunk priceDataChunk2 = new PriceDataChunk(ImmutableList.of(price2, price3));
        mockMvc.perform(post("/pricing/batch/" + batchId)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(priceDataChunk2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/pricing/batch/" + batchId + "/complete"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/pricing/instrument/" + price1.getRefId() + "/price"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotReadDataWhenBatchCancelledOrNotCompleted() throws Exception {
        String response = mockMvc.perform(post("/pricing/batch")
                .contentType(contentType))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long batchId = Long.valueOf(response);
        assertThat(batchId, greaterThan(0L));

        Price<JsonNode> price = PriceDataMother.createRandomPrice();
        PriceDataChunk priceDataChunk =
                new PriceDataChunk(ImmutableList.of(price));
        mockMvc.perform(post("/pricing/batch/" + batchId)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(priceDataChunk)))
                .andExpect(status().isOk());

        String getPriceUrl = "/pricing/instrument/" + price.getRefId() + "/price";

        mockMvc.perform(get(getPriceUrl)).andExpect(status().isNotFound());

        mockMvc.perform(post("/pricing/batch/" + batchId + "/cancel")).andExpect(status().isOk());
        mockMvc.perform(post("/pricing/batch/" + batchId + "/complete")).andExpect(status().isOk());

        mockMvc.perform(get(getPriceUrl)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldThrowErrorWhenCompletingNonExistingBatch() throws Exception {
        String response = mockMvc.perform(post("/pricing/batch/333/complete"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertThat(response, containsString(GlobalExceptionHandler.BATCH_NOT_FOUND_MESSAGE));
    }

    @Test
    public void shouldThrowErrorWhenCancellingNonExistingBatch() throws Exception {
        String response = mockMvc.perform(post("/pricing/batch/333/cancel"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertThat(response, containsString(GlobalExceptionHandler.BATCH_NOT_FOUND_MESSAGE));
    }

    @Test
    public void shouldThrowErrorWhenPriceNotFound() throws Exception {
        mockMvc.perform(get("/pricing/instrument/777/price")
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldThrowErrorWhenPriceDataIsEmpty() throws Exception {
        mockMvc.perform(post("/pricing/batch/1")
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldThrowErrorWhenPriceDataIsCorrupted() throws Exception {
        mockMvc.perform(post("/pricing/batch/1")
                .contentType(contentType)
                .content(PriceDataMother.getJsonPriceDataChunkCorrupted()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldResolveUnhandledExceptionAsBadRequestResponse() throws Exception {
        String response = mockMvc.perform(post("/pricing/batch/0"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertThat(response, containsString(GlobalExceptionHandler.BAD_REQUEST_MESSAGE));
    }

}
