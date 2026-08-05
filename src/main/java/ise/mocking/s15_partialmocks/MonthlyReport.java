package ise.mocking.s15_partialmocks;

import java.time.LocalDate;

/**
 * A class with ONE untestable method (today() reads the system clock) and real
 * logic around it. Rather than injecting a Clock, a partial mock replaces just
 * that one method and keeps the rest of the class real.
 *
 * Neither the class nor the mocked method may be final -- EasyMock creates a
 * subclass at runtime.
 */
public class MonthlyReport {

    /** The seam. Mock this and the rest of the class becomes deterministic. */
    public LocalDate today() {
        return LocalDate.now();
    }

    public String title() {
        return "Monthly report " + today().getYear() + "-" + today().getMonthValue();
    }

    public boolean isYearEnd() {
        return today().getMonthValue() == 12;
    }
}
