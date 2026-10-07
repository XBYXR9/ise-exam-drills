package ise.rest.exam_ticket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

/**
 * ISE HN 2026 exam, exercise 2 -- the five endpoints and their status codes.
 *
 *   POST   /tickets            200, or 400 if the body already has an id
 *   GET    /tickets/{id}       200, or 404
 *   GET    /tickets            200 always
 *   PUT    /tickets/{id}       200, 400 if path id != body id, 404 if unknown
 *   DELETE /tickets/{id}       204 always (even for an id that does not exist)
 *
 * Closed layered style: this class talks to TicketService only.
 */
@RestController
@RequestMapping("/tickets")
public class TicketResource {

    private final TicketService ticketService;

    public TicketResource(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        if (ticket.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(ticketService.saveTicket(ticket));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long ticketId) {
        return ticketService.findTicketById(ticketId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long ticketId, @RequestBody Ticket ticket) {
        // Objects.equals also covers a body with NO id: null != 7, so it is a 400 too.
        if (!Objects.equals(ticketId, ticket.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Ticket updated = ticketService.saveTicket(ticket);
        if (updated == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
