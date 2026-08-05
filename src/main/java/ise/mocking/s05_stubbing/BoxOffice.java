package ise.mocking.s05_stubbing;

import java.util.ArrayList;
import java.util.List;

/** SUT: builds human-readable ticket codes from the venue code and the sequence. */
public class BoxOffice {

    private final TicketSequence sequence;

    public BoxOffice(TicketSequence sequence) {
        this.sequence = sequence;
    }

    public List<String> issueTickets(int count) {
        List<String> tickets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tickets.add(sequence.getVenueCode() + "-" + sequence.nextTicketNumber());
        }
        return tickets;
    }
}
