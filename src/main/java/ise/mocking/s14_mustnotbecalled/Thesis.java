package ise.mocking.s14_mustnotbecalled;

/**
 * Collaborator interface -- deliberately the same shape as the exam Vehicle:
 * one guard method and one action method, where the action must be skipped
 * entirely when the guard says no.
 */
public interface Thesis {

    boolean checkPlagiarism();

    boolean submitFor(Student student);
}
