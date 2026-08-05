package ise.rest.model;

/**
 * The shared model between client and server -- the "Common" package on the exam
 * component diagram.
 *
 * A no-arg constructor plus getters and setters is not optional: Jackson needs them
 * to deserialise the JSON request body into this object.
 *
 * (The exam project used a UUID id. This project uses Long because the repository is
 * an in-memory Map with a counter; everything else is identical.)
 */
public class Product {

    private Long id;
    private String name;
    private double price;
    private int quantity;

    public Product() {
        // required by Jackson
    }

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
