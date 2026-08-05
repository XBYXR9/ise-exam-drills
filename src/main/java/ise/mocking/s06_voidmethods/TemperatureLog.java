package ise.mocking.s06_voidmethods;

/** Collaborator with only void methods -- expect() cannot be used on these. */
public interface TemperatureLog {

    void record(double celsius);

    void flush();
}
