package ise.rest.model;

/** Nested resource: a review always belongs to exactly one product. */
public class Review {

    private Long id;
    private Long productId;
    private String author;
    private int stars;

    public Review() {
    }

    public Review(String author, int stars) {
        this.author = author;
        this.stars = stars;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
