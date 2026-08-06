package ise.mocking.exam_smarthome;

/** Mock exam (Jun 2026), exercise 3. The person a SmartHome belongs to. */
public class HomeOwner {

    private final String name;

    public HomeOwner(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
