package ise.mocking.s10_capture;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Capture answers a question verify() cannot: not "was save() called" but
 * "WHAT did the SUT put inside the object it saved".
 */
class BillingServiceCaptureTest {

    @Test
    @DisplayName("the invoice handed to the repository carries the customer and the computed total")
    void singleCapture() {
        InvoiceRepository repository = createMock(InvoiceRepository.class);
        Capture<Invoice> captured = newCapture();

        expect(repository.save(capture(captured))).andReturn(true);
        replay(repository);

        new BillingService(repository).bill("bob", 3, 10.0);

        verify(repository);
        Invoice invoice = captured.getValue();
        assertEquals("bob", invoice.getCustomer());
        // 3 x 10.0 = 30.0. A SUT that multiplied wrongly still calls save() exactly
        // once, so verify() stays green -- only this assertion catches it.
        assertEquals(30.0, invoice.getTotal(), 0.0001);
    }

    @Test
    @DisplayName("CaptureType.ALL collects every argument across repeated calls")
    void captureAllValues() {
        InvoiceRepository repository = createMock(InvoiceRepository.class);
        Capture<Invoice> all = newCapture(CaptureType.ALL);

        expect(repository.save(capture(all))).andReturn(true).times(3);
        replay(repository);

        new BillingService(repository).billSeparately("bob", 10.0, 20.0, 30.0);

        verify(repository);
        assertEquals(3, all.getValues().size());
        // A default Capture keeps only the LAST value, which silently hides a SUT
        // that saved the same invoice three times. ALL is what exposes that.
        assertEquals(10.0, all.getValues().get(0).getTotal(), 0.0001);
        assertEquals(20.0, all.getValues().get(1).getTotal(), 0.0001);
        assertEquals(30.0, all.getValues().get(2).getTotal(), 0.0001);
        assertTrue(all.hasCaptured());
    }
}
