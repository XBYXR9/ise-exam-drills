package ise.mocking.s14_mustnotbecalled;

/**
 * The exact mutation the exam grades against: the plagiarism result is computed and
 * then ignored, so submitFor() runs even when the check failed.
 *
 * A test that only asserts "the student has no theses" still PASSES against this
 * class, because submitFor() returns false anyway. That is precisely the feedback
 * "does not fail on a wrong implementation" -- the missing piece is proving the
 * forbidden CALL did not happen.
 */
public class CarelessThesisOffice {

    public void submit(Student student, Thesis thesis) {
        boolean original = thesis.checkPlagiarism();
        if (thesis.submitFor(student) && original) {
            student.addSubmittedThesis(thesis);
        }
    }
}
