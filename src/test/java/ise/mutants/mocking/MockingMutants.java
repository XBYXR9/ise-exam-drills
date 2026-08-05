package ise.mutants.mocking;

import ise.mocking.s01_setup_annotations.AccessLog;
import ise.mocking.s01_setup_annotations.MembershipRegistry;
import ise.mocking.s01_setup_annotations.TurnstileController;
import ise.mocking.s02_setup_manual.FineCollector;
import ise.mocking.s02_setup_manual.PaymentTerminal;
import ise.mocking.s03_setup_support.AuditTrail;
import ise.mocking.s03_setup_support.OrderFulfilment;
import ise.mocking.s03_setup_support.ShippingLabelPrinter;
import ise.mocking.s03_setup_support.StockLedger;
import ise.mocking.s05_stubbing.BoxOffice;
import ise.mocking.s05_stubbing.TicketSequence;
import ise.mocking.s06_voidmethods.SampleFreezer;
import ise.mocking.s06_voidmethods.TemperatureLog;
import ise.mocking.s07_callcounts.AlertDispatcher;
import ise.mocking.s07_callcounts.SmsGateway;
import ise.mocking.s08_exceptions.CardProcessor;
import ise.mocking.s08_exceptions.DonationService;
import ise.mocking.s08_exceptions.GatewayTimeoutException;
import ise.mocking.s10_capture.BillingService;
import ise.mocking.s10_capture.Invoice;
import ise.mocking.s10_capture.InvoiceRepository;
import ise.mocking.s13_multiplemocks.CourierApi;
import ise.mocking.s13_multiplemocks.DispatchService;
import ise.mocking.s13_multiplemocks.WarehouseLedger;
import org.easymock.Capture;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * MUTANTS for the mocking scenarios.
 *
 * Unlike the testing mutants, these are standalone test classes rather than
 * subclasses: the EasyMock record phase is part of the test body, so there is
 * nothing to inherit. Each one repeats the original expectations verbatim and
 * points them at a broken SUT.
 *
 * All are @Tag("mutant") and are executed only by MutationDrillTest, which asserts
 * that every one of them FAILS.
 */
final class MockingMutants {

    private MockingMutants() {
    }
}

// ---------------------------------------------------------------------------
// s01 -- MUTATION: the collaborator answer is ignored, entry is always granted
// ---------------------------------------------------------------------------
class TurnstileThatAlwaysOpens extends TurnstileController {

    private final MembershipRegistry registry;
    private final AccessLog log;

    TurnstileThatAlwaysOpens(MembershipRegistry registry, AccessLog log) {
        this.registry = registry;
        this.log = log;
    }

    @Override
    public boolean requestEntry(String memberId) {
        registry.isMembershipValid(memberId);   // asked, then ignored
        log.record(memberId, true);
        return true;
    }
}

@Tag("mutant")
class TurnstileControllerTest_AlwaysOpens {

    @Test
    void invalidMembershipDeniesEntry() {
        MembershipRegistry registry = createMock(MembershipRegistry.class);
        AccessLog log = createMock(AccessLog.class);
        expect(registry.isMembershipValid("M-2")).andReturn(false);
        log.record("M-2", true);
        expectLastCall().anyTimes();
        replay(registry, log);

        boolean granted = new TurnstileThatAlwaysOpens(registry, log).requestEntry("M-2");

        verify(registry);
        // CAUGHT BY the return-value assertion. verify() passes here -- the mock WAS
        // consulted. Only assertFalse notices that the answer was thrown away.
        assertFalse(granted);
    }
}

// ---------------------------------------------------------------------------
// s02 -- MUTATION: the terminal result is ignored
// ---------------------------------------------------------------------------
class FineCollectorThatAlwaysSucceeds extends FineCollector {

    private final PaymentTerminal terminal;

    FineCollectorThatAlwaysSucceeds(PaymentTerminal terminal) {
        super(terminal);
        this.terminal = terminal;
    }

    @Override
    public boolean settle(String memberId, double amount) {
        if (amount <= 0.0) {
            return false;
        }
        terminal.charge(memberId, amount);
        return true;   // the declined card is reported as settled
    }
}

@Tag("mutant")
class FineCollectorTest_AlwaysSucceeds {

    @Test
    void declinedCardIsNotSettled() {
        PaymentTerminal terminal = createMock(PaymentTerminal.class);
        expect(terminal.charge("M-1", 4.50)).andReturn(false);
        replay(terminal);

        boolean settled = new FineCollectorThatAlwaysSucceeds(terminal).settle("M-1", 4.50);

        verify(terminal);
        assertFalse(settled);   // CAUGHT BY: the return-value assertion
    }
}

// ---------------------------------------------------------------------------
// s03 -- MUTATION: a collaborator is called that should have been skipped.
// This is the mocking mutation the exam grades hardest.
// ---------------------------------------------------------------------------
class OrderFulfilmentThatPrintsAnyway extends OrderFulfilment {

    private final StockLedger ledger;
    private final ShippingLabelPrinter printer;
    private final AuditTrail audit;

    OrderFulfilmentThatPrintsAnyway(StockLedger ledger, ShippingLabelPrinter printer, AuditTrail audit) {
        super(ledger, printer, audit);
        this.ledger = ledger;
        this.printer = printer;
        this.audit = audit;
    }

    @Override
    public String fulfil(String sku, int quantity) {
        boolean reserved = ledger.reserve(sku, quantity);
        String label = printer.print(sku, quantity);   // printed before the check
        if (!reserved) {
            audit.log("out of stock: " + sku);
            return null;
        }
        audit.log("shipped: " + sku);
        return label;
    }
}

@Tag("mutant")
class OrderFulfilmentTest_PrintsAnyway {

    @Test
    void unreservableOrderIsNotPrinted() {
        StockLedger ledger = createMock(StockLedger.class);
        ShippingLabelPrinter printer = createMock(ShippingLabelPrinter.class);
        AuditTrail audit = createMock(AuditTrail.class);

        expect(ledger.reserve("SKU-9", 2)).andReturn(false);
        audit.log("out of stock: SKU-9");
        expectLastCall();
        // printer gets no expectation -- printing here is the defect.
        replay(ledger, printer, audit);

        // CAUGHT BY the unrecorded printer mock: "Unexpected method call print(...)".
        // The return value is still null, so an assertNull-only test would pass.
        new OrderFulfilmentThatPrintsAnyway(ledger, printer, audit).fulfil("SKU-9", 2);

        verify(ledger, printer, audit);
    }
}

// ---------------------------------------------------------------------------
// s05 -- MUTATION: the sequence is consulted once and the value reused
// ---------------------------------------------------------------------------
class BoxOfficeThatReusesOneNumber extends BoxOffice {

    private final TicketSequence sequence;

    BoxOfficeThatReusesOneNumber(TicketSequence sequence) {
        super(sequence);
        this.sequence = sequence;
    }

    @Override
    public List<String> issueTickets(int count) {
        List<String> tickets = new ArrayList<>();
        if (count > 0) {
            String ticket = sequence.getVenueCode() + "-" + sequence.nextTicketNumber();
            for (int i = 0; i < count; i++) {
                tickets.add(ticket);
            }
        }
        return tickets;
    }
}

@Tag("mutant")
class BoxOfficeTest_ReusesOneNumber {

    @Test
    void consecutiveReturns() {
        TicketSequence sequence = createMock(TicketSequence.class);
        expect(sequence.getVenueCode()).andReturn("AUD1").anyTimes();
        expect(sequence.nextTicketNumber()).andReturn(41).andReturn(42).andReturn(43);
        replay(sequence);

        List<String> tickets = new BoxOfficeThatReusesOneNumber(sequence).issueTickets(3);

        // CAUGHT twice over: verify() finds two unconsumed expectations, and the
        // content assertion finds three identical ticket codes. A size-only check
        // would have passed.
        verify(sequence);
        assertIterableEquals(List.of("AUD1-41", "AUD1-42", "AUD1-43"), tickets);
    }
}

// ---------------------------------------------------------------------------
// s06 -- MUTATION: the loop body is deleted, only the flush survives
// ---------------------------------------------------------------------------
class SampleFreezerThatLogsNothing extends SampleFreezer {

    private final TemperatureLog log;

    SampleFreezerThatLogsNothing(TemperatureLog log) {
        super(log);
        this.log = log;
    }

    @Override
    public void runCycle(double... readings) {
        log.flush();   // the record() calls are gone
    }
}

@Tag("mutant")
class SampleFreezerTest_LogsNothing {

    @Test
    void allReadingsAreRecordedThenFlushed() {
        TemperatureLog log = createMock(TemperatureLog.class);
        log.record(-18.0);
        expectLastCall();
        log.record(-19.5);
        expectLastCall();
        log.flush();
        expectLastCall();
        replay(log);

        new SampleFreezerThatLogsNothing(log).runCycle(-18.0, -19.5);

        // CAUGHT BY verify() alone. runCycle returns void and the SUT has no state,
        // so there is literally nothing else that could notice.
        verify(log);
    }
}

// ---------------------------------------------------------------------------
// s07 -- MUTATION: off-by-one in the retry loop
// ---------------------------------------------------------------------------
class AlertDispatcherWithOneRetryTooFew extends AlertDispatcher {

    private final SmsGateway gateway;
    private final int maxAttempts;

    AlertDispatcherWithOneRetryTooFew(SmsGateway gateway, int maxAttempts) {
        super(gateway, maxAttempts);
        this.gateway = gateway;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public boolean dispatch(String number, String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        for (int attempt = 0; attempt < maxAttempts - 1; attempt++) {   // mutated
            if (gateway.send(number, text)) {
                return true;
            }
        }
        return false;
    }
}

@Tag("mutant")
class AlertDispatcherTest_OneRetryTooFew {

    @Test
    void allAttemptsAreExhausted() {
        SmsGateway gateway = createMock(SmsGateway.class);
        expect(gateway.send("+49170", "flood")).andReturn(false).times(3);
        replay(gateway);

        assertFalse(new AlertDispatcherWithOneRetryTooFew(gateway, 3).dispatch("+49170", "flood"));

        // CAUGHT BY verify(): times(3) recorded, 2 actually happened. The return value
        // is false either way, so times(3) is doing all the work.
        verify(gateway);
    }
}

// ---------------------------------------------------------------------------
// s08 -- MUTATION: the catch block is emptied
// ---------------------------------------------------------------------------
class DonationServiceWithEmptyCatch extends DonationService {

    private final CardProcessor processor;

    DonationServiceWithEmptyCatch(CardProcessor processor) {
        super(processor);
        this.processor = processor;
    }

    @Override
    public boolean donateSafely(String donorId, double amount) {
        try {
            return processor.capture(donorId, amount);
        } catch (GatewayTimeoutException timeout) {
            return false;   // the failedAttempts counter is no longer touched
        }
    }
}

@Tag("mutant")
class DonationServiceTest_EmptyCatch {

    @Test
    void failureIsHandledGracefully() {
        CardProcessor processor = createMock(CardProcessor.class);
        expect(processor.capture("D-1", 25.0)).andThrow(new GatewayTimeoutException("timeout"));
        replay(processor);

        DonationService service = new DonationServiceWithEmptyCatch(processor);

        assertFalse(service.donateSafely("D-1", 25.0));
        // CAUGHT BY the state assertion only. Both verify() and assertFalse pass.
        assertEquals(1, service.getFailedAttempts());
        verify(processor);
    }
}

// ---------------------------------------------------------------------------
// s10 -- MUTATION: the object handed to the collaborator is built wrongly
// ---------------------------------------------------------------------------
class BillingServiceWithWrongTotal extends BillingService {

    private final InvoiceRepository repository;

    BillingServiceWithWrongTotal(InvoiceRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public boolean bill(String customer, int quantity, double unitPrice) {
        return repository.save(new Invoice(customer, unitPrice));   // quantity dropped
    }
}

@Tag("mutant")
class BillingServiceCaptureTest_WrongTotal {

    @Test
    void singleCapture() {
        InvoiceRepository repository = createMock(InvoiceRepository.class);
        Capture<Invoice> captured = newCapture();
        expect(repository.save(capture(captured))).andReturn(true);
        replay(repository);

        new BillingServiceWithWrongTotal(repository).bill("bob", 3, 10.0);

        verify(repository);
        assertEquals("bob", captured.getValue().getCustomer());
        // CAUGHT BY the captured value: 30.0 expected, 10.0 built. save() was called
        // exactly once either way, so verify() is blind to this.
        assertEquals(30.0, captured.getValue().getTotal(), 0.0001);
    }
}

// ---------------------------------------------------------------------------
// s13 -- MUTATION: the courier is booked without a reservation
// ---------------------------------------------------------------------------
class DispatchServiceThatSkipsTheCheck extends DispatchService {

    private final WarehouseLedger ledger;
    private final CourierApi courier;

    DispatchServiceThatSkipsTheCheck(WarehouseLedger ledger, CourierApi courier) {
        super(ledger, courier);
        this.ledger = ledger;
        this.courier = courier;
    }

    @Override
    public String dispatch(String sku, int quantity) {
        boolean reserved = ledger.reserve(sku, quantity);
        String tracking = courier.bookPickup(sku, quantity);   // booked regardless
        return reserved ? tracking : null;
    }
}

@Tag("mutant")
class DispatchServiceTest_SkipsTheCheck {

    @Test
    void unavailableStockIsNotDispatched() {
        WarehouseLedger ledger = createMock(WarehouseLedger.class);
        CourierApi courier = createMock(CourierApi.class);

        expect(ledger.reserve("SKU-1", 2)).andReturn(false);
        // courier deliberately unrecorded
        replay(ledger, courier);

        // CAUGHT BY the unrecorded courier mock. The returned value is null in both
        // implementations, so assertNull alone proves nothing at all.
        assertNull(new DispatchServiceThatSkipsTheCheck(ledger, courier).dispatch("SKU-1", 2));

        verify(ledger, courier);
    }
}
