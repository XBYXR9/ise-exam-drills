package ise.practice.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ise.rest.practice.PracticeServerApplication;
import ise.rest.practice.PracticeStock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PRACTICE DRILL -- the GRADER for ise.rest.practice.
 *
 * These tests are given to you, exactly as Artemis gives them: your job is the
 * server code, not the tests. Delete the @Disabled below and run
 *     gradlew.bat test --tests "ise.practice.rest.*"
 * until all seven are green.
 *
 * If a test fails with UnsupportedOperationException you have not written that
 * method yet. If it fails with 404 on a URL you think you mapped, check the mapping
 * annotation and the path spelling.
 */
@Disabled("PRACTICE DRILL: delete this line once you start filling in ise.rest.practice")
@SpringBootTest(classes = PracticeServerApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PracticeStockResourceTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper json = new ObjectMapper();

    private long createStock(String colour, String brand, int quantity) throws Exception {
        MvcResult result = mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new PracticeStock(colour, brand, quantity))))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("TASK 1: POST /stocks creates the stock and assigns an id")
    void createStock() throws Exception {
        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new PracticeStock("navy", "Acme", 7))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.colour").value("navy"))
                .andExpect(jsonPath("$.quantity").value(7));
    }

    @Test
    @DisplayName("TASK 1: a stock that already carries an id is refused with 400")
    void createStockWithIdIsBadRequest() throws Exception {
        PracticeStock withId = new PracticeStock("navy", "Acme", 7);
        withId.setId(42L);

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(withId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TASK 2: POST /stocks/{id}/buy decreases the quantity by exactly one")
    void buyDecreasesQuantity() throws Exception {
        long id = createStock("navy", "Acme", 7);

        mockMvc.perform(post("/stocks/{id}/buy", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(6));

        mockMvc.perform(get("/stocks"))
                .andExpect(jsonPath("$[0].quantity").value(6));
    }

    @Test
    @DisplayName("TASK 2: buying from an unknown stock is 404")
    void buyUnknownStockIsNotFound() throws Exception {
        mockMvc.perform(post("/stocks/{id}/buy", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TASK 2: buying from an empty stock is 400")
    void buyEmptyStockIsBadRequest() throws Exception {
        long id = createStock("navy", "Acme", 0);

        mockMvc.perform(post("/stocks/{id}/buy", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TASK 3: GET /stocks without the parameter lists everything")
    void listAllByDefault() throws Exception {
        createStock("navy", "Acme", 7);
        createStock("red", "Acme", 0);

        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("TASK 3: GET /stocks?onlyAvailable=true lists only stocks with quantity > 0")
    void listOnlyAvailable() throws Exception {
        createStock("navy", "Acme", 7);
        createStock("red", "Acme", 0);

        mockMvc.perform(get("/stocks").param("onlyAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].colour").value("navy"));
    }
}
