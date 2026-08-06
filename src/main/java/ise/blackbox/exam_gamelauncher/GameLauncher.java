package ise.blackbox.exam_gamelauncher;

/**
 * Mock exam (Jun 2026), exercise 4 -- "Game Launcher Beta Access", 10 points.
 *
 * The exam task is text-only (you fill in tables), but the rules are precise enough
 * to implement, and implementing them is the fastest way to check your table is right:
 * the parameterized test next to this class runs every TC you would have written down.
 *
 * THE RULES
 *   valid accountType: "standard" and "vip"
 *   standard accepts characterLevel 20..50 inclusive
 *   vip      accepts characterLevel 10..80 inclusive
 *   a level inside 1..100 but outside the bracket  -> rejected
 *   characterLevel <= 0 or >= 101                  -> invalid
 *   any other accountType                          -> invalid
 *
 * NOTE THE PRECEDENCE: the level bounds are system boundaries, so they are checked
 * before the account type. ("admin", 0) is invalid for two independent reasons and
 * the order does not change the answer -- but it does change which class your test
 * case actually covers, so decide it deliberately.
 */
public class GameLauncher {

    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 100;
    public static final int STANDARD_MIN = 20;
    public static final int STANDARD_MAX = 50;
    public static final int VIP_MIN = 10;
    public static final int VIP_MAX = 80;

    public AccessDecision checkBetaAccess(String accountType, int characterLevel) {
        if (characterLevel < MIN_LEVEL || characterLevel > MAX_LEVEL) {
            return AccessDecision.INVALID;
        }
        if ("standard".equals(accountType)) {
            return inBracket(characterLevel, STANDARD_MIN, STANDARD_MAX);
        }
        if ("vip".equals(accountType)) {
            return inBracket(characterLevel, VIP_MIN, VIP_MAX);
        }
        return AccessDecision.INVALID;
    }

    private AccessDecision inBracket(int level, int min, int max) {
        // Both bounds are INCLUSIVE. Every >= and <= here is a boundary the test suite
        // has to pin down with an L-1 / L / L+1 triple.
        return (level >= min && level <= max) ? AccessDecision.ACCEPTED : AccessDecision.REJECTED;
    }
}
