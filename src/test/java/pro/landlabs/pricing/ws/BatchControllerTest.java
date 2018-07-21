package pro.landlabs.pricing.ws;

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

import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        mockMvc.perform(post("/pricing/batch")
                .contentType(contentType)
                .content("{ }"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldPostDataToBatch() throws Exception {
        mockMvc.perform(post("/pricing/batch/1")
                .contentType(contentType)
                .content("{ }"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldResolveUnhandledExceptionAsBadRequestResponse() throws Exception {
        mockMvc.perform(get("/pricing/batch/0")).andExpect(status().isBadRequest());
    }

}
