package ise.blackbox.exam_graphics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ISE HN 2026 exam, exercise 3 -- the answer tables, executable. Every row is one row of
 * the text you would paste into Artemis; the filled-in submission is in docs/ISEHN_2026_EXAM.md.
 */
public class GraphicsConfiguratorBlackBoxTest {

    private GraphicsConfigurator configurator;

    protected GraphicsConfigurator newConfigurator() {
        return new GraphicsConfigurator();
    }

    @BeforeEach
    void setUp() {
        configurator = newConfigurator();
    }

    @Nested
    @DisplayName("Part 2 -- the 7 representative test cases")
    class RepresentativeTestCases {

        @ParameterizedTest(name = "{0} | {1} | {2} | expect {3}  ({4})")
        @CsvSource({
                "TC1, Mobile,   1000,  unsupported, DC1 + VC1 Mobile below the baseline",
                "TC2, Mobile,   3000,  performance, DC1 + VC2 Mobile performance band",
                "TC3, Mobile,   6000,  quality,     DC1 + VC3 Mobile quality band",
                "TC4, Console,  5000,  performance, DC2 + VC4 Console performance band",
                "TC5, Console,  12000, quality,     DC2 + VC5 Console quality band",
                "TC6, Console,  24000, unsupported, DC2 + VC6 above the memory ceiling",
                "TC7, Handheld, 6000,  unsupported, DC3 invalid device, VRAM otherwise fine"
        })
        void representativeCases(String tc, String device, int vram, String expected, String covers) {
            assertEquals(expected, configurator.selectPreset(device, vram), tc + " (" + covers + ")");
        }
    }

    @Nested
    @DisplayName("Part 3 -- three-point boundary on the Mobile minimum baseline")
    class BoundaryValues {

        @ParameterizedTest(name = "{0} | Mobile | {1} | expect {2}")
        @CsvSource({
                "TC10, 2047, unsupported",   // L-1
                "TC11, 2048, performance",   // L  (first valid value)
                "TC12, 2049, performance"    // L+1
        })
        void mobileMinimumBaseline(String tc, int vram, String expected) {
            assertEquals(expected, configurator.selectPreset("Mobile", vram), tc);
        }
    }

    @Nested
    @DisplayName("The other boundaries the task did not ask for, but that exist")
    class OtherBoundaries {

        @ParameterizedTest(name = "Mobile {0} -> {1}")
        @CsvSource({"4096, performance", "4097, quality", "16384, quality", "16385, unsupported"})
        void mobileLimits(int vram, String expected) {
            assertEquals(expected, configurator.selectPreset("Mobile", vram));
        }

        @ParameterizedTest(name = "Console {0} -> {1}")
        @CsvSource({"2047, unsupported", "2048, performance", "8192, performance",
                "8193, quality", "16384, quality", "16385, unsupported"})
        void consoleLimits(int vram, String expected) {
            assertEquals(expected, configurator.selectPreset("Console", vram));
        }

        @ParameterizedTest(name = "device {0} -> unsupported")
        @CsvSource(value = {"Handheld", "PC", "mobile", "CONSOLE", "''", "'  '", "NULL"}, nullValues = "NULL")
        void invalidDevices(String device) {
            // Case matters: "mobile" is not "Mobile".
            assertEquals("unsupported", configurator.selectPreset(device, 6000));
        }
    }
}
