package ise.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ise.rest.exam_pullover.PulloverServerApplication;
import ise.rest.exam_pullover.PulloverStock;
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
 * RETAKE EXAM, EXERCISE 6 -- proof that the three endpoints behave as the task
 * describes them. This is the exercise that scored 0/22 because no commit was made;
 * run these before you submit and you will know the server works.
 */
@SpringBootTest(classes = PulloverServerApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PulloverStockResourceTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper json = new ObjectMapper();

    private long createStock(String colour, String brand, int quantity) throws Exception {
        MvcResult result = mockMvc.perform(post("/pulloverstocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new PulloverStock(colour, brand, quantity))))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("TASK 1: a seller creates a stock and gets it back with an id")
    void createPulloverStock() throws Exception {
        mockMvc.perform(post("/pulloverstocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new PulloverStock("navy", "Acme", 7))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.colour").value("navy"))
                .andExpect(jsonPath("$.brand").value("Acme"))
                .andExpect(jsonPath("$.quantity").value(7));
    }

    @Test
    @DisplayName("TASK 1: a stock that already carries an id is refused with 400")
    void createPulloverStockWithIdIsBadRequest() throws Exception {
        PulloverStock withId = new PulloverStock("navy", "Acme", 7);
        withId.setId(42L);

        mockMvc.perform(post("/pulloverstocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(withId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TASK 2: buying one pullover decreases the quantity by exactly one")
    void buyPulloverDecreasesQuantity() throws Exception {
        long id = createStock("navy", "Acme", 7);

        mockMvc.perform(post("/pulloverstocks/{id}/buy", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(6));

        // Read it back: the decrement has to have been persisted by the service,
        // not merely applied to the copy that was sent to the client.
        mockMvc.perform(get("/pulloverstocks"))
                .andExpect(jsonPath("$[0].quantity").value(6));
    }

    @Test
    @DisplayName("TASK 2: buying from an unknown stock is 404")
    void buyUnknownStockIsNotFound() throws Exception {
        mockMvc.perform(post("/pulloverstocks/{id}/buy", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("TASK 2: buying from an empty stock is 400")
    void buyEmptyStockIsBadRequest() throws Exception {
        long id = createStock("navy", "Acme", 0);

        mockMvc.perform(post("/pulloverstocks/{id}/buy", id))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TASK 3: without the parameter every stock is listed, empty ones included")
    void listAllStocksByDefault() throws Exception {
        createStock("navy", "Acme", 7);
        createStock("red", "Acme", 0);

        mockMvc.perform(get("/pulloverstocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("TASK 3: onlyAvailable=true lists only stocks with quantity greater than 0")
    void listOnlyAvailableStocks() throws Exception {
        createStock("navy", "Acme", 7);
        createStock("red", "Acme", 0);

        mockMvc.perform(get("/pulloverstocks").param("onlyAvailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                // Naming the survivor is what distinguishes "filtered correctly" from
                // "returned the first element by luck".
                .andExpect(jsonPath("$[0].colour").value("navy"));
    }
}
