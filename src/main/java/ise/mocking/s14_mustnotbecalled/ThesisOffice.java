package ise.mocking.s14_mustnotbecalled;

/**
 * SUT. Two nested guards:
 *   plagiarism check fails -> submitFor() must NOT be called
 *   submitFor() returns false -> the student list must stay empty
 */
public class ThesisOffice {

    public void submit(Student student, Thesis thesis) {
        if (thesis.checkPlagiarism()) {
            if (thesis.submitFor(student)) {
                student.addSubmittedThesis(thesis);
            }
        }
    }
}
