package ise.mocking.s17_errorcatalogue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * THE ERROR CATALOGUE.
 *
 * Every test here triggers exactly ONE classic EasyMock failure. They are all
 * @Disabled so the suite stays green; the reason string carries the exact message
 * you will see, and the @DisplayName carries the cause. Delete one @Disabled,
 * rerun, and read the real stack trace.
 *
 * Under exam pressure the fastest path is to recognise the message and jump
 * straight to the fix -- that is what this class is for.
 */
class EasyMockErrorCatalogueTest {

    @Test
    @Disabled("IllegalStateException: missing behavior definition for the preceding method call: "
            + "PrinterQueue.submit(\"doc-1\", 1). FIX: every recorded call needs an expect(...).andReturn(...), "
            + "and you must call replay(mock) before exercising the SUT.")
    @DisplayName("CAUSE: a mock method was called in the record phase without an expectation, or replay() is missing")
    void missingBehaviorDefinition() {
        PrinterQueue queue = createMock(PrinterQueue.class);

        queue.submit("doc-1", 1);   // recorded, but no andReturn follows
        queue.cancel("doc-1");      // the NEXT recorded call is what triggers the message

        replay(queue);
    }

    @Test
    @Disabled("AssertionError: Unexpected method call PrinterQueue.submit(\"doc-1\", 5): "
            + "expected: 1, actual: 1 (+1). FIX: record the call the SUT really makes -- "
            + "or leave it unrecorded on purpose, because that IS the bug you wanted to catch.")
    @DisplayName("CAUSE: the SUT called something that was never recorded, or called it once too often")
    void unexpectedMethodCall() {
        PrinterQueue queue = createMock(PrinterQueue.class);
        expect(queue.submit("doc-1", 1)).andReturn(true);
        replay(queue);

        PrintService service = new PrintService(queue);
        service.print("doc-1", 1);
        service.print("doc-1", 5);   // never recorded
    }

    @Test
    @Disabled("AssertionError: Expectation failure on verify: PrinterQueue.submit(\"doc-1\", 1): "
            + "expected: 1, actual: 0. FIX: either the SUT is broken and genuinely skipped the call "
            + "(the finding you wanted), or you recorded a call the SUT was never supposed to make.")
    @DisplayName("CAUSE: a recorded expectation was never satisfied -- verify() is the only thing that notices")
    void expectationFailureOnVerify() {
        PrinterQueue queue = createMock(PrinterQueue.class);
        expect(queue.submit("doc-1", 1)).andReturn(true);
        replay(queue);

        // The SUT is never exercised, so submit() never happens.
        verify(queue);
    }

    @Test
    @Disabled("IllegalStateException: 2 matchers expected, 1 recorded. "
            + "FIX: once ONE argument uses a matcher, ALL arguments of that call must -- wrap the fixed "
            + "ones in eq(), here eq(\"doc-1\").")
    @DisplayName("CAUSE: a raw value was mixed with an argument matcher in the same call")
    void matchersExpectedNRecordedM() {
        PrinterQueue queue = createMock(PrinterQueue.class);

        expect(queue.submit("doc-1", anyInt())).andReturn(true);

        replay(queue);
    }

    @Test
    @Disabled("IllegalStateException: calling verify is not allowed in record state. "
            + "FIX: the four phases are record, replay, exercise, verify -- replay(mock) is missing.")
    @DisplayName("CAUSE: verify() was called while the mock was still recording, i.e. replay() was forgotten")
    void verifyInRecordState() {
        PrinterQueue queue = createMock(PrinterQueue.class);
        expect(queue.submit("doc-1", 1)).andReturn(true);

        // No replay(queue) here.
        verify(queue);
    }
}
