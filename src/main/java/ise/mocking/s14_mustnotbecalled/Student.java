package ise.mocking.s14_mustnotbecalled;

import java.util.ArrayList;
import java.util.List;

/** REAL object, never mocked: its state is the second half of every assertion. */
public class Student {

    private final List<Thesis> submittedTheses = new ArrayList<>();

    public void addSubmittedThesis(Thesis thesis) {
        submittedTheses.add(thesis);
    }

    public List<Thesis> getSubmittedTheses() {
        return submittedTheses;
    }
}
