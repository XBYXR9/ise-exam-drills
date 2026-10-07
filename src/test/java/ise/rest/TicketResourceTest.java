package ise.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ise.rest.exam_ticket.Ticket;
import ise.rest.exam_ticket.TicketServerApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ISE HN 2026 exam, exercise 2 -- every row of the status-code table, executed.
 * Fresh server state per test so ids always start at 1.
 */
@SpringBootTest(classes = TicketServerApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TicketResourceTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper json = new ObjectMapper();

    private long createTicket(String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Ticket(title, "desc", "HIGH", "OPEN"))))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    @DisplayName("POST: created -> 200 with a server-assigned sequential id")
    void createAssignsSequentialIds() throws Exception {
        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Ticket("VPN down", "cannot connect", "HIGH", "OPEN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("VPN down"));
        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Ticket("Printer", "jammed", "LOW", "OPEN"))))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @DisplayName("POST: body already has an id -> 400")
    void createWithIdIsBadRequest() throws Exception {
        Ticket withId = new Ticket("x", "y", "LOW", "OPEN");
        withId.setId(5L);
        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(withId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET one: found -> 200 and the right ticket")
    void getOneFound() throws Exception {
        createTicket("first");
        long second = createTicket("second");
        mockMvc.perform(get("/tickets/{id}", second))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("second"));
    }

    @Test
    @DisplayName("GET one: unknown id -> 404")
    void getOneNotFound() throws Exception {
        mockMvc.perform(get("/tickets/{id}", 99)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET all: 200 even when empty, and lists everything otherwise")
    void getAll() throws Exception {
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        createTicket("a");
        createTicket("b");
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("PUT: updates every field -> 200, and the change is persisted")
    void updateOk() throws Exception {
        long id = createTicket("old title");
        Ticket changed = new Ticket("new title", "new desc", "LOW", "CLOSED");
        changed.setId(id);

        mockMvc.perform(put("/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(changed)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("new title"))
                .andExpect(jsonPath("$.status").value("CLOSED"));

        mockMvc.perform(get("/tickets/{id}", id))
                .andExpect(jsonPath("$.title").value("new title"))
                .andExpect(jsonPath("$.description").value("new desc"))
                .andExpect(jsonPath("$.priority").value("LOW"))
                .andExpect(jsonPath("$.status").value("CLOSED"));
        mockMvc.perform(get("/tickets")).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT: path id and body id differ -> 400")
    void updateIdMismatch() throws Exception {
        long id = createTicket("t");
        Ticket other = new Ticket("t", "d", "LOW", "OPEN");
        other.setId(id + 1);
        mockMvc.perform(put("/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(other)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT: body without an id -> 400 as well (null never equals the path id)")
    void updateBodyWithoutId() throws Exception {
        long id = createTicket("t");
        mockMvc.perform(put("/tickets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new Ticket("t", "d", "LOW", "OPEN"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT: unknown id -> 404 and NOTHING is created")
    void updateUnknownId() throws Exception {
        Ticket ghost = new Ticket("t", "d", "LOW", "OPEN");
        ghost.setId(77L);
        mockMvc.perform(put("/tickets/{id}", 77)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(ghost)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/tickets")).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("DELETE: 204 and the ticket is gone")
    void deleteOk() throws Exception {
        long keep = createTicket("keep");
        long drop = createTicket("drop");
        mockMvc.perform(delete("/tickets/{id}", drop)).andExpect(status().isNoContent());
        mockMvc.perform(get("/tickets/{id}", drop)).andExpect(status().isNotFound());
        mockMvc.perform(get("/tickets/{id}", keep)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE: 204 'always' -- even for an id that never existed")
    void deleteUnknownIsStillNoContent() throws Exception {
        mockMvc.perform(delete("/tickets/{id}", 12345)).andExpect(status().isNoContent());
    }
}
