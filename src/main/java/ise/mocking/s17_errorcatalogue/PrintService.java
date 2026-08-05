package ise.mocking.s17_errorcatalogue;

/** Deliberately trivial: the point of scenario 17 is the error messages, not the logic. */
public class PrintService {

    private final PrinterQueue queue;

    public PrintService(PrinterQueue queue) {
        this.queue = queue;
    }

    public boolean print(String documentId, int copies) {
        return queue.submit(documentId, copies);
    }
}
