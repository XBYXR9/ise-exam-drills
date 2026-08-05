package ise.testing.s09_assumptions;

/** Something genuinely environment-dependent, so assumeTrue has a real job. */
public class PlatformProbe {

    public String pathSeparator() {
        return System.getProperty("file.separator");
    }

    public boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
