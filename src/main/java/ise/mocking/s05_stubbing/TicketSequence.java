package ise.mocking.s05_stubbing;

/** Collaborator: the venue's ticket numbering service. */
public interface TicketSequence {

    int nextTicketNumber();

    String getVenueCode();
}
