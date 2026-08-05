package ise.testing.s05_assertall;

import java.util.ArrayList;
import java.util.List;

/** Several independent properties at once, which is what assertAll is for. */
public class ShoppingBasket {

    private final List<String> items = new ArrayList<>();
    private double total;

    public void add(String item, double price) {
        items.add(item);
        total += price;
    }

    public List<String> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
