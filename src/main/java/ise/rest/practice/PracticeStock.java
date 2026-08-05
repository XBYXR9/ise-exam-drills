package ise.rest.practice;

/** Model for the REST practice drill. Complete -- you do not need to change this. */
public class PracticeStock {

    private Long id;
    private String colour;
    private String brand;
    private int quantity;

    public PracticeStock() {
    }

    public PracticeStock(String colour, String brand, int quantity) {
        this.colour = colour;
        this.brand = brand;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
