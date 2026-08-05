package ise.mocking.s14_mustnotbecalled;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SCENARIO 14 -- "prove the collaborator method was NOT called".
 *
 * This is the exercise that produced the exam feedback:
 *   "testAssignWithEngineFailure() does not fail on a wrong implementation.
 *    It should check that the assign method on the vehicle mock is not called."
 *
 * Every technique below is paired with a run against CarelessThesisOffice, the
 * broken SUT that DOES make the forbidden call. If a technique cannot turn that run
 * red, the technique is worthless for the exam -- and one of the three the playbook
 * lists cannot.
 */
class MustNotBeCalledTest {

    // -----------------------------------------------------------------
    //  Baseline: the correct SUT, so the comparisons below mean something.
    // -----------------------------------------------------------------

    @Test
    @DisplayName("baseline: a clean thesis is submitted and lands in the student record")
    void happyPath() {
        Thesis thesis = createMock(Thesis.class);
        Student student = new Student();

        expect(thesis.checkPlagiarism()).andReturn(true);
        expect(thesis.submitFor(student)).andReturn(true);
        replay(thesis);

        new ThesisOffice().submit(student, thesis);

        verify(thesis);
        assertEquals(1, student.getSubmittedTheses().size());
        assertTrue(student.getSubmittedTheses().contains(thesis));
    }

    @Nested
    @DisplayName("OPTION A -- do not record the forbidden call (the one to use)")
    class OptionA {

        @Test
        @DisplayName("a failed plagiarism check leaves submitFor() unrecorded and therefore forbidden")
        void correctImplementationPasses() {
            Thesis thesis = createMock(Thesis.class);   // default mock, NOT nice
            Student student = new Student();

            expect(thesis.checkPlagiarism()).andReturn(false);
            // submitFor is deliberately NOT recorded. On a default or strict mock every
            // unrecorded call is an immediate AssertionError, so "absent from the record
            // phase" IS the assertion that it must not happen.
            replay(thesis);

            new ThesisOffice().submit(student, thesis);

            verify(thesis);
            assertEquals(0, student.getSubmittedTheses().size());
            assertFalse(student.getSubmittedTheses().contains(thesis));
        }

        @Test
        @DisplayName("and it goes RED on the SUT that submits anyway")
        void brokenImplementationFails() {
            Thesis thesis = createMock(Thesis.class);
            Student student = new Student();

            expect(thesis.checkPlagiarism()).andReturn(false);
            replay(thesis);

            // "Unexpected method call submitFor(...)" -- thrown the instant the broken
            // SUT touches it. This is the assertion the exam was looking for.
            assertThrows(AssertionError.class,
                    () -> new CarelessThesisOffice().submit(student, thesis));
        }
    }

    @Nested
    @DisplayName("OPTION B -- times(0): the playbook says this works. It does not.")
    class OptionB {

        @Test
        @DisplayName("PLAYBOOK ERROR: times(0) throws IllegalArgumentException, so this option is unusable")
        void timesZeroDoesNotCompileIntoAWorkingExpectation() {
            Thesis thesis = createMock(Thesis.class);
            expect(thesis.checkPlagiarism()).andReturn(false);

            // MockingPlaybook.java section 14, OPTION 2:
            //     expect(v.assign(anyObject())).andReturn(true).times(0);
            // EasyMock 5.2.0 requires 1 <= maximum, so this line throws before the test
            // can even reach replay(). Writing it in the exam costs you the whole task.
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> expect(thesis.submitFor(anyObject())).andReturn(true).times(0));

            assertTrue(error.getMessage().contains("maximum must be >= 1"),
                    "unexpected message: " + error.getMessage());
        }

        @Test
        @DisplayName("WORKING SUBSTITUTE: record the forbidden call with andThrow, so calling it explodes")
        void andThrowIsTheExplicitAlternative() {
            Thesis clean = createMock(Thesis.class);
            Student student = new Student();

            expect(clean.checkPlagiarism()).andReturn(false);
            // If you want the prohibition spelled out rather than implied by absence,
            // this is the idiom that actually runs. Note anyTimes(): without it the
            // expectation would itself be mandatory and verify() would demand the call.
            expect(clean.submitFor(anyObject()))
                    .andThrow(new AssertionError("submitFor must not be called"))
                    .anyTimes();
            replay(clean);

            new ThesisOffice().submit(student, clean);
            verify(clean);
            assertEquals(0, student.getSubmittedTheses().size());
        }

        @Test
        @DisplayName("and the andThrow substitute goes RED on the broken SUT too")
        void andThrowCatchesTheBrokenImplementation() {
            Thesis thesis = createMock(Thesis.class);
            Student student = new Student();

            expect(thesis.checkPlagiarism()).andReturn(false);
            expect(thesis.submitFor(anyObject()))
                    .andThrow(new AssertionError("submitFor must not be called"))
                    .anyTimes();
            replay(thesis);

            AssertionError error = assertThrows(AssertionError.class,
                    () -> new CarelessThesisOffice().submit(student, thesis));
            assertEquals("submitFor must not be called", error.getMessage());
        }
    }

    @Nested
    @DisplayName("OPTION C -- a mock with zero expectations: nothing at all may happen")
    class OptionC {

        @Test
        @DisplayName("replay() on an empty record phase forbids every method on the collaborator")
        void untouchedCollaborator() {
            Thesis thesis = createMock(Thesis.class);
            Student student = new Student();

            replay(thesis);   // zero expectations recorded

            // Nothing is done with the thesis at all, so nothing may be called on it.
            assertEquals(0, student.getSubmittedTheses().size());
            verify(thesis);
        }

        @Test
        @DisplayName("even checkPlagiarism() is forbidden by a zero-expectation mock")
        void anyCallAtAllFails() {
            Thesis thesis = createMock(Thesis.class);
            Student student = new Student();

            replay(thesis);

            // Use this only when the task really says the collaborator is untouched.
            // Applied to the plagiarism scenario it is too strict: the guard call is legal.
            assertThrows(AssertionError.class,
                    () -> new ThesisOffice().submit(student, thesis));
        }
    }

    @Nested
    @DisplayName("THE ANTI-PATTERN -- why a nice mock scores zero here")
    class WhyNiceMockFails {

        @Test
        @DisplayName("a nice mock lets the forbidden submitFor() through, so the test cannot go red")
        void niceMockHidesTheBug() {
            Thesis thesis = createNiceMock(Thesis.class);
            Student student = new Student();

            expect(thesis.checkPlagiarism()).andReturn(false);
            replay(thesis);

            // The broken SUT calls the forbidden method and the nice mock shrugs. Both
            // the interaction check AND the state check stay green, which is exactly the
            // "your test does not fail on a wrong implementation" grade.
            assertDoesNotThrow(() -> new CarelessThesisOffice().submit(student, thesis));
            assertDoesNotThrow(() -> verify(thesis));
            assertEquals(0, student.getSubmittedTheses().size());
        }
    }
}
