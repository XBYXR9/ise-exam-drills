package ise.testing.s11_validation;

/** One invalid input class per guard, so each gets its own test. */
public class CatalogueItem {

    private final String name;
    private final double price;
    private final int stock;

    public CatalogueItem(String name, double price, int stock) {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock must not be negative");
        }
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
}
