package ise.blackbox.practice_streaming;

/**
 * PRACTICE exercise (not from a real exam) -- same shape as the Graphics Configurator,
 * different numbers, so you can run the whole recipe once more with nobody to copy from.
 *
 * Task text you would be given:
 *   A video player picks a stream quality from `plan` ("Basic" or "Premium") and
 *   `bandwidthKbps`.
 *   Basic  : 1000..3000 -> "SD",  3001..8000  -> "HD"
 *   Premium: 1000..6000 -> "HD",  6001..8000  -> "UHD"
 *   bandwidthKbps <= 999 or >= 8001 -> "unavailable";  any other plan -> "unavailable".
 */
public class StreamQualitySelector {

    public String select(String plan, int bandwidthKbps) {
        if (bandwidthKbps < 1000 || bandwidthKbps > 8000) {
            return "unavailable";
        }
        if ("Basic".equals(plan)) {
            return bandwidthKbps <= 3000 ? "SD" : "HD";
        }
        if ("Premium".equals(plan)) {
            return bandwidthKbps <= 6000 ? "HD" : "UHD";
        }
        return "unavailable";
    }
}
