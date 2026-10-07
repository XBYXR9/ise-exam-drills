package ise.rest.exam_ticket;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ISE HN 2026 exam, exercise 2 -- business layer. The exam gives you a plain list and a
 * counter instead of a database, so every operation is a loop over that list.
 */
@Service
public class TicketService {

    private final List<Ticket> tickets = new ArrayList<>();
    private long nextId = 1L;

    public List<Ticket> getAllTickets() {
        // A COPY: the TODO says "not a reference to internal storage". Returning `tickets`
        // itself would let any caller add to or clear the service's data.
        return new ArrayList<>(tickets);
    }

    public Optional<Ticket> findTicketById(Long ticketId) {
        for (Ticket ticket : tickets) {
            // equals(), never ==: Long compares by identity outside the -128..127 cache.
            if (ticket.getId().equals(ticketId)) {
                return Optional.of(ticket);
            }
        }
        return Optional.empty();
    }

    public Ticket saveTicket(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(nextId);
            nextId++;
            tickets.add(ticket);
            return ticket;
        }
        Optional<Ticket> stored = findTicketById(ticket.getId());
        if (stored.isEmpty()) {
            // The update must FAIL, not silently create. The resource turns null into 404.
            return null;
        }
        Ticket existing = stored.get();
        existing.setTitle(ticket.getTitle());
        existing.setDescription(ticket.getDescription());
        existing.setPriority(ticket.getPriority());
        existing.setStatus(ticket.getStatus());
        return existing;
    }

    public void deleteTicket(Long ticketId) {
        tickets.removeIf(ticket -> ticket.getId().equals(ticketId));
    }
}
