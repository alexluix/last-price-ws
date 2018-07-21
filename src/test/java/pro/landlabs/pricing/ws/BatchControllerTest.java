package pro.landlabs.pricing.ws;

import org.hamcrest.Matchers;
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
import pro.landlabs.pricing.testdata.PriceMother;

import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
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

        assertThat(Long.valueOf(response), Matchers.greaterThan(0L));
    }

    @Test
    public void shouldPostDataToBatch() throws Exception {
        mockMvc.perform(post("/pricing/batch/1")
                .contentType(contentType)
                .content(PriceMother.getJsonPriceDataChunk1()))
                .andExpect(status().isOk());
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
                .content(PriceMother.getJsonPriceDataChunkCorrupted()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldResolveUnhandledExceptionAsBadRequestResponse() throws Exception {
        mockMvc.perform(get("/pricing/batch/0")).andExpect(status().isBadRequest());
    }

}
