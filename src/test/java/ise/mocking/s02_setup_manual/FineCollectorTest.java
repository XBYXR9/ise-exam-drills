package ise.mocking.s02_setup_manual;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SETUP STYLE B -- createMock() by hand + constructor injection.
 * No extension, no annotations, works in any project. Use it when the SUT has no
 * no-arg constructor, or when you cannot remember whether the extension is on the
 * classpath: this style has nothing to configure wrongly.
 */
class FineCollectorTest {

    private PaymentTerminal terminal;
    private FineCollector fineCollector;

    @BeforeEach
    void setUp() {
        terminal = createMock(PaymentTerminal.class);
        fineCollector = new FineCollector(terminal);   // constructor injection
    }

    @Test
    @DisplayName("a positive fine is charged to the terminal and reported as settled")
    void positiveFineIsCharged() {
        expect(terminal.charge("M-1", 4.50)).andReturn(true);
        replay(terminal);

        boolean settled = fineCollector.settle("M-1", 4.50);

        verify(terminal);
        assertTrue(settled);
    }

    @Test
    @DisplayName("a zero fine never reaches the terminal")
    void zeroFineIsNotCharged() {
        // Nothing is recorded on purpose. On a default mock, "not recorded" means
        // "must not be called": any charge() would fail the test immediately.
        replay(terminal);

        boolean settled = fineCollector.settle("M-1", 0.0);

        verify(terminal);
        assertFalse(settled);
    }

    @Test
    @DisplayName("a declined card is reported as unsettled")
    void declinedCardIsNotSettled() {
        expect(terminal.charge("M-1", 4.50)).andReturn(false);
        replay(terminal);

        boolean settled = fineCollector.settle("M-1", 4.50);

        verify(terminal);
        // Without this assertion a SUT that ignores the terminal's answer and
        // always returns true would still pass. That is the classic lost point.
        assertFalse(settled);
    }
}
