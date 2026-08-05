package ise.testing.s01_lifecycle;

import java.util.ArrayList;
import java.util.List;

/** Mutable SUT, used to show that each test really does get a fresh instance. */
public class ThesisTracker {

    private final List<String> submitted = new ArrayList<>();

    public void submit(String title) {
        submitted.add(title);
    }

    public int getSubmissionCount() {
        return submitted.size();
    }
}
