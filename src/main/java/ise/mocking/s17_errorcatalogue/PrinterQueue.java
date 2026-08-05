package ise.mocking.s17_errorcatalogue;

public interface PrinterQueue {

    boolean submit(String documentId, int copies);

    void cancel(String documentId);
}
