package de.tum.ise;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

/*
 * EASYMOCK -- ANNOTATION STYLE (@ExtendWith + @TestSubject + @Mock)   plus: capture, strict order,
 * dynamic answers, argument matchers, verifying a void call. MockTemplate covers the manual style.
 *
 * THREE things must ALL be true or the injection silently does nothing:
 *   1. class has  @ExtendWith(EasyMockExtension.class)
 *   2. SUT field has @TestSubject and is ALREADY instantiated:  = new Sut()
 *   3. each collaborator field has @Mock and its TYPE matches a field of the SUT
 * The SUT needs a no-arg constructor + fields (setter/field injection). With a constructor-only SUT use
 * the manual style from MockTemplate (createMock + new Sut(mock)).
 *
 * EVERY test ends with BOTH: verify(mock)  AND  an assertion on the real object / return value.
 */
@ExtendWith(EasyMockExtension.class)
public class EasyMockAnnotationTemplate {

    @TestSubject
    private OrderService service = new OrderService();           // instantiated inline

    @Mock                                                        // default mock: unexpected call fails
    private PriceClient priceClient;

    @Mock(type = MockType.NICE)                                  // nice: unrecorded calls return 0/false/null
    private AuditLog auditLog;                                   //   use ONLY for noise you do not verify

    // ---------- 1. return value ----------
    @Test
    void testReturnValue() {
        expect(priceClient.price("apple")).andReturn(2.0);
        replay(priceClient, auditLog);                           // replay EVERY mock

        double total = service.total("apple", 3);

        verify(priceClient, auditLog);
        assertEquals(6.0, total, 0.0001);                        // interaction AND state
    }

    // ---------- 2. void method: record it, then expectLastCall() ----------
    @Test
    void testVoidCall() {
        expect(priceClient.price("apple")).andReturn(2.0);
        auditLog.record("apple");                                // the void call itself
        expectLastCall();                                        // (.once() / .times(n) / .andThrow(..) optional)
        replay(priceClient, auditLog);

        service.totalAndLog("apple", 1);

        verify(priceClient, auditLog);                           // ONLY witness that record() happened
    }

    // ---------- 3. must NEVER be called: do NOT record it (default mock) ----------
    @Test
    void testNeverCalled() {
        replay(priceClient, auditLog);

        double total = service.total("apple", 0);                // quantity 0 -> must not ask the client

        verify(priceClient, auditLog);                           // an unexpected price() call would fail here
        assertEquals(0.0, total, 0.0001);                        // empty body returns 0 too, so BOTH halves matter
    }

    // ---------- 4. exception from the collaborator ----------
    @Test
    void testCollaboratorThrows() {
        expect(priceClient.price("ghost")).andThrow(new IllegalArgumentException("unknown"));
        replay(priceClient, auditLog);

        assertThrows(IllegalArgumentException.class, () -> service.total("ghost", 1));

        verify(priceClient, auditLog);
    }

    // ---------- 5. matchers: if ONE argument is a matcher, ALL must be ----------
    @Test
    void testMatchers() {
        expect(priceClient.priceWithDiscount(anyString(), eq(10))).andReturn(1.0);   // NOT (anyString(), 10)
        replay(priceClient, auditLog);

        assertEquals(1.0, service.discounted("x", 10), 0.0001);

        verify(priceClient, auditLog);
    }

    // ---------- 6. capture: WHAT was inside the argument the SUT passed on ----------
    @Test
    void testCapture() {
        Capture<String> captured = newCapture();                 // CaptureType.ALL for several calls
        expect(priceClient.price("pear")).andReturn(1.0);
        auditLog.record(capture(captured));
        expectLastCall();
        replay(priceClient, auditLog);

        service.totalAndLog("pear", 1);

        verify(priceClient, auditLog);
        assertEquals("pear", captured.getValue());               // verify() alone cannot see the argument
    }

    // ---------- 7. answer depends on the input ----------
    @Test
    void testDynamicAnswer() {
        expect(priceClient.price(anyString())).andAnswer(() -> {
            String item = (String) getCurrentArguments()[0];
            return item.length() * 1.0;
        });
        replay(priceClient, auditLog);

        assertEquals(5.0, service.total("apple", 1), 0.0001);    // "apple".length() == 5

        verify(priceClient, auditLog);
    }

    // ---------- 8. ORDER matters: strict mock (manual style; annotation: @Mock(type = MockType.STRICT)) ----------
    @Test
    void testOrder() {
        PriceClient strict = EasyMock.createStrictMock(PriceClient.class);
        expect(strict.price("a")).andReturn(1.0);                // must come FIRST
        expect(strict.price("b")).andReturn(2.0);                // then this
        replay(strict);

        strict.price("a");
        strict.price("b");                                       // swapping these two lines fails the test

        verify(strict);
    }

    // ---------- Quick reference ----------
    // replay(a, b) / verify(a, b): list EVERY mock - a forgotten one is never checked
    // times: .once() .times(n) .times(min,max) .atLeastOnce() .anyTimes() (includes 0 -> proves nothing)
    // andStubReturn(..) is NEVER checked by verify() - do not use it for a call you must prove
    // Errors: "missing behavior definition" = forgot replay | "Unexpected method call" = call not recorded
    //         "expected: 1, actual: 0" = recorded but SUT never called it | mixed matchers = wrap with eq()

    // ---------- Dummy classes so this file compiles. DELETE these. ----------
    public interface PriceClient {
        double price(String item);
        double priceWithDiscount(String item, int percent);
    }

    public interface AuditLog {
        void record(String item);
    }

    public static class OrderService {
        private PriceClient priceClient;                         // injected BY TYPE into these fields
        private AuditLog auditLog;

        double total(String item, int qty) {
            if (qty == 0) return 0.0;
            return priceClient.price(item) * qty;
        }

        double totalAndLog(String item, int qty) {
            double t = priceClient.price(item) * qty;
            auditLog.record(item);
            return t;
        }

        double discounted(String item, int percent) {
            return priceClient.priceWithDiscount(item, percent);
        }
    }
}
