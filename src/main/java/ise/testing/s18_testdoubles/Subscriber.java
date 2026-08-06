package ise.testing.s18_testdoubles;

public class Subscriber {

    private final String id;
    private boolean active;

    public Subscriber(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
