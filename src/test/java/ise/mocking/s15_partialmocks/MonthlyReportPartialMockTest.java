package ise.mocking.s15_partialmocks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.partialMockBuilder;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A partial mock replaces named methods of a REAL class and leaves the rest alone.
 * Use it for the one unmockable dependency (clock, random, filesystem) in an
 * otherwise perfectly testable class -- never as a way to avoid designing a seam.
 */
class MonthlyReportPartialMockTest {

    @Test
    @DisplayName("mocking only today() makes the report title deterministic")
    void titleUsesTheMockedDate() {
        MonthlyReport report = partialMockBuilder(MonthlyReport.class)
                .addMockedMethod("today")     // only this one is mocked
                .createMock();

        expect(report.today()).andReturn(LocalDate.of(2025, 10, 2)).times(2);
        replay(report);

        // title() is the REAL method running against a fake clock.
        assertEquals("Monthly report 2025-10", report.title());
        verify(report);
    }

    @Test
    @DisplayName("the same seam drives the year-end branch in both directions")
    void yearEndBranchIsReachable() {
        MonthlyReport december = partialMockBuilder(MonthlyReport.class)
                .addMockedMethod("today")
                .createMock();
        expect(december.today()).andReturn(LocalDate.of(2025, 12, 31));
        replay(december);
        assertTrue(december.isYearEnd());
        verify(december);

        MonthlyReport june = partialMockBuilder(MonthlyReport.class)
                .addMockedMethod("today")
                .createMock();
        expect(june.today()).andReturn(LocalDate.of(2025, 6, 30));
        replay(june);
        // The negative case matters: without it, a method hard-coded to return true
        // would pass the test above and nothing else would notice.
        assertFalse(june.isYearEnd());
        verify(june);
    }
}
