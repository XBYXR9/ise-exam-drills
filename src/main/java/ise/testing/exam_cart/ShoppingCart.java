package ise.testing.exam_cart;

import java.util.ArrayList;
import java.util.List;

/** Exam SUT, with the exact method names from the task description. */
public class ShoppingCart {

    private final List<Product> items = new ArrayList<>();

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        items.add(product);
    }

    public void removeProduct(Product product) {
        items.remove(product);
    }

    public int getProductCount() {
        return items.size();
    }

    public double calculateTotalPrice() {
        double total = 0.0;
        for (Product product : items) {
            total += product.getPrice();
        }
        return total;
    }

    public double applyDiscount(double discountRate) {
        return calculateTotalPrice() * (1.0 - discountRate);
    }
}
