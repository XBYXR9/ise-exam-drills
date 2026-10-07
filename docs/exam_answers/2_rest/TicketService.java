package de.tum.ise.service;

import de.tum.ise.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {
    private final List<Ticket> tickets = new ArrayList<>();
    private long nextId = 1L;

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    public Optional<Ticket> findTicketById(Long ticketId) {
        for (Ticket ticket : tickets) {
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
        } else {
            Optional<Ticket> stored = findTicketById(ticket.getId());
            if (stored.isEmpty()) {
                return null;
            }
            Ticket existing = stored.get();
            existing.setTitle(ticket.getTitle());
            existing.setDescription(ticket.getDescription());
            existing.setPriority(ticket.getPriority());
            existing.setStatus(ticket.getStatus());
            return existing;
        }
    }

    public void deleteTicket(Long ticketId) {
        tickets.removeIf(ticket -> ticket.getId().equals(ticketId));
    }
}
