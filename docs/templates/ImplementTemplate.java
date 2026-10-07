package de.tum.ise;

/*
 * IMPLEMENT-THEN-TEST TEMPLATE  (exam: "complete the class, then write the tests")
 *
 * Build order, always: enum -> fields -> constructor with guards -> getters -> calculation
 *                      -> boolean action (compute, check, mutate) -> capped/clamped method.
 * Rename: Tier, Server, Result, ram, level, taskLoad ... to what the task says, EXACTLY.
 * Split the three classes into three files when you copy (one public class per file).
 */

// ---------- FORM A: plain enum (task only names categories) ----------
enum Tier {
    MICRO, STANDARD, HIGH_MEM, GPU_BOOST
}

// ---------- FORM B: enum that carries a number (task gives a factor per constant) ----------
enum FlightType {
    SHORT_HAUL(0.5), LONG_HAUL(2.0), CARGO(3.5);          // <- semicolon after the last constant

    private final double factor;

    FlightType(double factor) { this.factor = factor; }
    public double getFactor() { return factor; }          // then: distance * type.getFactor()  (no branch needed)
}

// ---------- Result object: only fields + getters (add equals/hashCode ONLY if a test compares two) ----------
class Result {
    private final boolean success;
    private final double speed;
    private final double cost;

    public Result(boolean success, double speed, double cost) {
        this.success = success;
        this.speed = speed;
        this.cost = cost;
    }

    public boolean isSuccess() { return success; }
    public double getSpeed() { return speed; }
    public double getCost() { return cost; }
}

// ---------- The class with the logic ----------
class Server {
    private double ram;                                   // NOT final: it changes
    private final Tier tier;                              // final: set once
    private final int level;

    public Server(double ram, Tier tier, int level) {
        if (ram < 0) {                                    // every "must not / invalid" sentence = a guard,
            throw new IllegalArgumentException("RAM must not be negative.");   // BEFORE any assignment
        }
        this.ram = ram;
        this.tier = tier;
        this.level = level;
    }

    public double getRam() { return ram; }
    public Tier getTier() { return tier; }
    public int getLevel() { return level; }

    /** Boolean-ish action: compute -> check -> refuse WITHOUT changing anything -> mutate -> return. */
    public Result allocate(Tier taskTier, double load) {
        boolean same = tier == taskTier;                  // compare enums with ==
        double cost = same ? load * 0.6 : load * 1.5;     // the "if matches, cheaper" rule
        if (ram < cost) {                                 // "<" : exactly enough RAM must SUCCEED
            return new Result(false, 0.0, 0.0);           // failure path touches NOTHING
        }
        ram -= cost;                                      // state changes only on the success path
        double speed = (level * 80.0) / (load + 10.0);    // 80.0 not 80: avoid integer division
        if (same) {
            speed += 40.0;
        }
        return new Result(true, speed, cost);
    }

    /** Capped method: add, then clamp with Math.min. NEVER use % as a cap. */
    public double free(double amount) {
        if (amount <= 0.0) {
            return ram;                                   // "return unchanged" = no modification at all
        }
        double effective = amount * (1.2 + (level * 0.05));
        ram = Math.min(ram + effective, 600.0 + (level * 40.0));
        return ram;
    }
}
