package ise.mocking.s13_multiplemocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Two mocks, replayed and verified TOGETHER.
 * replay(a, b) and verify(a, b) take varargs for exactly this reason: a mock left
 * out of verify() is never checked, and the test then passes no matter what the SUT
 * did to it. That omission is invisible in a code review.
 */
class DispatchServiceTest {

    private WarehouseLedger ledger;
    private CourierApi courier;
    private DispatchService dispatchService;

    @BeforeEach
    void setUp() {
        ledger = createMock(WarehouseLedger.class);
        courier = createMock(CourierApi.class);
        dispatchService = new DispatchService(ledger, courier);
    }

    @Test
    @DisplayName("reserved stock is handed to the courier and the tracking code is returned")
    void reservedStockIsDispatched() {
        expect(ledger.reserve("SKU-1", 2)).andReturn(true);
        expect(courier.bookPickup("SKU-1", 2)).andReturn("TRK-1");
        replay(ledger, courier);

        assertEquals("TRK-1", dispatchService.dispatch("SKU-1", 2));

        verify(ledger, courier);
    }

    @Test
    @DisplayName("unavailable stock is never offered to the courier")
    void unavailableStockIsNotDispatched() {
        expect(ledger.reserve("SKU-1", 2)).andReturn(false);
        // courier gets no expectation at all: any call to it fails the test.
        replay(ledger, courier);

        assertNull(dispatchService.dispatch("SKU-1", 2));

        verify(ledger, courier);
    }

    @Test
    @DisplayName("a courier that refuses the pickup causes the reservation to be released")
    void refusedPickupReleasesTheReservation() {
        expect(ledger.reserve("SKU-1", 2)).andReturn(true);
        expect(courier.bookPickup("SKU-1", 2)).andReturn(null);
        ledger.release("SKU-1", 2);
        expectLastCall();                 // the compensating call is the whole point
        replay(ledger, courier);

        assertNull(dispatchService.dispatch("SKU-1", 2));

        verify(ledger, courier);
    }
}
