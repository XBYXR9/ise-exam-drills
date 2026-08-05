package ise.testing.s16_contracts;

import java.util.Objects;

/** Value object: equals / hashCode / toString are its whole contract. */
public class Isbn {

    private final String code;
    private final String title;

    public Isbn(String code, String title) {
        this.code = code;
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    /** Identity is the CODE alone -- two copies of the same book are the same ISBN. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Isbn)) {
            return false;
        }
        return Objects.equals(code, ((Isbn) other).code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Isbn{code=" + code + ", title=" + title + "}";
    }
}
