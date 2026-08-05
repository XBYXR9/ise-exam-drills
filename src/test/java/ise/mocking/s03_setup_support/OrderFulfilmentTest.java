package ise.mocking.s03_setup_support;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * SETUP STYLE C -- extend EasyMockSupport and use replayAll()/verifyAll().
 * With three or more mocks this removes the single most common silent failure:
 * forgetting one mock in the replay(...) or verify(...) argument list, which
 * quietly turns that mock into an unverified no-op.
 */
class OrderFulfilmentTest extends EasyMockSupport {

    private StockLedger ledger;
    private ShippingLabelPrinter printer;
    private AuditTrail audit;
    private OrderFulfilment orderFulfilment;

    @BeforeEach
    void setUp() {
        ledger = createMock(StockLedger.class);       // inherited from EasyMockSupport
        printer = createMock(ShippingLabelPrinter.class);
        audit = createMock(AuditTrail.class);
        orderFulfilment = new OrderFulfilment(ledger, printer, audit);
    }

    @Test
    @DisplayName("a reservable order is printed and audited as shipped")
    void reservableOrderIsShipped() {
        expect(ledger.reserve("SKU-9", 2)).andReturn(true);
        expect(printer.print("SKU-9", 2)).andReturn("LABEL-9");
        audit.log("shipped: SKU-9");
        expectLastCall();

        replayAll();

        String label = orderFulfilment.fulfil("SKU-9", 2);

        verifyAll();
        assertEquals("LABEL-9", label);
    }

    @Test
    @DisplayName("an unreservable order is audited and never printed")
    void unreservableOrderIsNotPrinted() {
        expect(ledger.reserve("SKU-9", 2)).andReturn(false);
        audit.log("out of stock: SKU-9");
        expectLastCall();
        // printer deliberately gets NO expectation: printing here would be the bug.

        replayAll();

        String label = orderFulfilment.fulfil("SKU-9", 2);

        verifyAll();
        assertNull(label);
    }
}
