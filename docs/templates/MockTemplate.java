package de.tum.ise;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * EASYMOCK TEMPLATE
 *
 * A mock is a fake version of a dependency (an interface). You tell it what it
 * should return, run your code, then check that it was used the right way.
 *
 * The 4 steps, ALWAYS in this order:
 *   1. CREATE   EasyMock.createMock(Interface.class)
 *   2. RECORD   EasyMock.expect(mock.method(args)).andReturn(value)
 *   3. REPLAY   EasyMock.replay(mock)      <- do not forget, this is a classic mistake
 *   4. VERIFY   EasyMock.verify(mock)      <- checks all expected calls happened
 */
public class MockTemplate {

    private static final double DELTA = 0.0001;

    private PriceService priceService;   // the mock
    private OrderProcessor processor;    // the real class we test

    @BeforeEach
    void setUp() {
        // 1. CREATE the mock and inject it into the real class
        priceService = EasyMock.createMock(PriceService.class);
        processor = new OrderProcessor(priceService);
    }

    // ---------- Method WITH a return value ----------
    @Test
    void testReturnValue() {
        // 2. RECORD: when getPrice("apple") is called, return 2.0
        EasyMock.expect(priceService.getPrice("apple")).andReturn(2.0);

        // 3. REPLAY
        EasyMock.replay(priceService);

        // ACT + ASSERT
        double total = processor.calculateTotal("apple", 3);
        assertEquals(6.0, total, DELTA);

        // 4. VERIFY
        EasyMock.verify(priceService);
    }

    // ---------- Method that returns void ----------
    @Test
    void testVoidMethod() {
        // For void methods: call it once on the mock, then use expectLastCall()
        priceService.log("apple");
        EasyMock.expectLastCall();

        EasyMock.expect(priceService.getPrice("apple")).andReturn(2.0);
        EasyMock.replay(priceService);

        processor.calculateTotalWithLog("apple", 1);

        EasyMock.verify(priceService);
    }

    // ---------- Method called several times ----------
    @Test
    void testCalledTwice() {
        EasyMock.expect(priceService.getPrice("apple")).andReturn(2.0).times(2);
        // other options:  .once()  .anyTimes()  .atLeastOnce()  .times(1, 3)
        EasyMock.replay(priceService);

        processor.calculateTotal("apple", 1);
        processor.calculateTotal("apple", 1);

        EasyMock.verify(priceService);
    }

    // ---------- Different return value on each call ----------
    @Test
    void testDifferentValuesPerCall() {
        EasyMock.expect(priceService.getPrice("apple"))
                .andReturn(2.0)
                .andReturn(3.0);
        EasyMock.replay(priceService);

        assertEquals(2.0, processor.calculateTotal("apple", 1), DELTA);
        assertEquals(3.0, processor.calculateTotal("apple", 1), DELTA);

        EasyMock.verify(priceService);
    }

    // ---------- Mock throws an exception ----------
    @Test
    void testMockThrows() {
        EasyMock.expect(priceService.getPrice("unknown"))
                .andThrow(new IllegalArgumentException("unknown item"));
        EasyMock.replay(priceService);

        assertThrows(IllegalArgumentException.class,
                () -> processor.calculateTotal("unknown", 1));

        EasyMock.verify(priceService);
    }

    // ---------- Any argument (when you do not care about the exact value) ----------
    @Test
    void testAnyArgument() {
        EasyMock.expect(priceService.getPrice(EasyMock.anyString())).andReturn(1.0);
        EasyMock.replay(priceService);

        assertEquals(5.0, processor.calculateTotal("whatever", 5), DELTA);

        EasyMock.verify(priceService);
    }

    // ---------- Method must NEVER be called ----------
    @Test
    void testNeverCalled() {
        // Record nothing, replay, and verify. If the real code calls the mock, the test fails.
        EasyMock.replay(priceService);

        processor.calculateTotal("apple", 0);   // quantity 0: should not ask the service

        EasyMock.verify(priceService);
    }

    // ---------- Quick reference ----------
    // Mixing matchers and plain values is NOT allowed:
    //   WRONG:  mock.foo(EasyMock.anyInt(), 5)
    //   RIGHT:  mock.foo(EasyMock.anyInt(), EasyMock.eq(5))
    // Calls on the mock BEFORE replay() are recording. AFTER replay() they are real use.
    // createMock = order of calls does not matter. createStrictMock = order matters.
    // If you forget replay(), you get "missing behavior definition" errors.
    // If you forget verify(), missing calls are not detected (points lost!).

    // ---------- Dummy classes so this file compiles. DELETE these. ----------
    interface PriceService {
        double getPrice(String item);
        void log(String item);
    }

    static class OrderProcessor {
        private final PriceService service;

        OrderProcessor(PriceService service) { this.service = service; }

        double calculateTotal(String item, int quantity) {
            if (quantity == 0) return 0.0;
            return service.getPrice(item) * quantity;
        }

        double calculateTotalWithLog(String item, int quantity) {
            service.log(item);
            return service.getPrice(item) * quantity;
        }
    }
}
