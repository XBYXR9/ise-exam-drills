package ise.testing.exam_library;

import java.util.ArrayList;
import java.util.List;

public class Member {

    private final String name;
    private final String id;
    private final List<Book> borrowedBooks = new ArrayList<>();

    public Member(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }
}
