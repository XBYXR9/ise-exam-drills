package de.tum.ise;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * REST TEST TEMPLATE  (MockMvc: test your endpoints WITHOUT starting a server)
 * Works together with RestResourceTemplate (Item / ItemService / ItemResource). Rename to your resource.
 *
 * standaloneSetup(...) needs no Spring Boot application class and no port. A NEW service per test
 * (BeforeEach) means ids always start at 1 and tests never see each other's data.
 * One test per row of the status-code table: success AND every error row.
 */
public class RestTestTemplate {

    private MockMvc mockMvc;
    private final ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ItemResource(new ItemService())).build();
    }

    // helper: create an item through the API and return the id the server assigned
    private long createItem(String name, int quantity) throws Exception {
        Item item = new Item();
        item.setName(name);
        item.setQuantity(quantity);
        MvcResult result = mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    // ---------- POST ----------
    @Test
    void testCreateOk() throws Exception {
        Item item = new Item();
        item.setName("pen");
        item.setQuantity(3);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))                 // server-assigned
                .andExpect(jsonPath("$.name").value("pen"));
    }

    @Test
    void testCreateWithIdIsBadRequest() throws Exception {
        Item item = new Item();
        item.setId(5L);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }

    // ---------- GET one / all ----------
    @Test
    void testGetOneFound() throws Exception {
        long id = createItem("pen", 3);
        mockMvc.perform(get("/items/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("pen"));
    }

    @Test
    void testGetOneNotFound() throws Exception {
        mockMvc.perform(get("/items/{id}", 99)).andExpect(status().isNotFound());
    }

    @Test
    void testGetAllAndQueryParameter() throws Exception {
        createItem("pen", 3);
        createItem("empty", 0);
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));          // default: everything
        mockMvc.perform(get("/items").param("onlyAvailable", "true"))
                .andExpect(jsonPath("$.length()").value(1));          // filter applied
    }

    // ---------- PUT ----------
    @Test
    void testUpdateOkIsPersisted() throws Exception {
        long id = createItem("old", 1);
        Item changed = new Item();
        changed.setId(id);
        changed.setName("new");
        changed.setQuantity(9);
        mockMvc.perform(put("/items/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(changed)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new"));
        mockMvc.perform(get("/items/{id}", id))                       // read it BACK: proves it was stored
                .andExpect(jsonPath("$.name").value("new"))
                .andExpect(jsonPath("$.quantity").value(9));
    }

    @Test
    void testUpdateIdMismatchIsBadRequest() throws Exception {
        long id = createItem("a", 1);
        Item other = new Item();
        other.setId(id + 1);
        mockMvc.perform(put("/items/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(other)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUnknownIsNotFound() throws Exception {
        Item ghost = new Item();
        ghost.setId(77L);
        mockMvc.perform(put("/items/{id}", 77)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(ghost)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/items")).andExpect(jsonPath("$.length()").value(0));   // nothing was created
    }

    // ---------- DELETE ----------
    @Test
    void testDelete() throws Exception {
        long id = createItem("a", 1);
        mockMvc.perform(delete("/items/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/items/{id}", id)).andExpect(status().isNotFound());   // really gone
        mockMvc.perform(delete("/items/{id}", 12345)).andExpect(status().isNoContent());   // "always 204"
    }

    // ---------- Quick reference ----------
    // status().isOk() 200 | isNoContent() 204 | isBadRequest() 400 | isNotFound() 404 | isConflict() 409
    // jsonPath("$.field").value(x) | jsonPath("$.length()").value(n) | jsonPath("$[0].name").value("a")
    // post/put: .contentType(MediaType.APPLICATION_JSON).content(jsonString)   get: .param("name", "value")
    // path variable: get("/items/{id}", id)
}
