package ise.mocking.s09_matchers;

/** Small real object, used to demonstrate same() and isA(). */
public class Customer {

    private final String name;

    public Customer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
