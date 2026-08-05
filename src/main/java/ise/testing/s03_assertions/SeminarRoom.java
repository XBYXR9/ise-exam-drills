package ise.testing.s03_assertions;

import java.util.ArrayList;
import java.util.List;

/** Wide-enough surface to reach every JUnit assertion in one scenario. */
public class SeminarRoom {

    private final String code;
    private final int capacity;
    private final List<String> attendees = new ArrayList<>();
    private String projectorModel;

    public SeminarRoom(String code, int capacity) {
        this.code = code;
        this.capacity = capacity;
    }

    public String getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

    public double occupancyRate() {
        return attendees.size() / (double) capacity;
    }

    public void admit(String attendee) {
        attendees.add(attendee);
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public String[] getAttendeeArray() {
        return attendees.toArray(new String[0]);
    }

    public String getProjectorModel() {
        return projectorModel;
    }

    public void installProjector(String model) {
        this.projectorModel = model;
    }

    public Object describe() {
        return code + " (" + capacity + " seats)";
    }
}
