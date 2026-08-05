package ise.testing.s08_nested_ordered;

import java.util.ArrayList;
import java.util.List;

/** Two clearly different states (empty / stocked), which is what @Nested organises. */
public class LibraryCatalogue {

    private final List<String> titles = new ArrayList<>();

    public void add(String title) {
        titles.add(title);
    }

    public boolean remove(String title) {
        return titles.remove(title);
    }

    public int size() {
        return titles.size();
    }

    public boolean contains(String title) {
        return titles.contains(title);
    }
}
