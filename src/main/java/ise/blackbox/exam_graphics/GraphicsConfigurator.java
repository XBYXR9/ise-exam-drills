package ise.blackbox.exam_graphics;

/**
 * ISE HN 2026 exam, exercise 3 -- black-box testing of the Graphics Configurator.
 *
 * THE RULES
 *   deviceType: "Mobile" or "Console" -- anything else (Handheld, PC, "", null) is unsupported
 *   vramMB <= 2047 or >= 16385                       -> unsupported (system limits)
 *   Mobile : 2048..4096 -> performance, 4097..16384 -> quality
 *   Console: 2048..8192 -> performance, 8193..16384 -> quality
 */
public class GraphicsConfigurator {

    public static final String PERFORMANCE = "performance";
    public static final String QUALITY = "quality";
    public static final String UNSUPPORTED = "unsupported";

    public static final int MIN_VRAM = 2048;
    public static final int MAX_VRAM = 16384;
    public static final int MOBILE_PERFORMANCE_MAX = 4096;
    public static final int CONSOLE_PERFORMANCE_MAX = 8192;

    public String selectPreset(String deviceType, int vramMB) {
        if (vramMB < MIN_VRAM || vramMB > MAX_VRAM) {
            return UNSUPPORTED;
        }
        if ("Mobile".equals(deviceType)) {
            return vramMB <= MOBILE_PERFORMANCE_MAX ? PERFORMANCE : QUALITY;
        }
        if ("Console".equals(deviceType)) {
            return vramMB <= CONSOLE_PERFORMANCE_MAX ? PERFORMANCE : QUALITY;
        }
        return UNSUPPORTED;
    }
}
