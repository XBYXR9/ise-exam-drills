package ise.testing.s12_voidstate;

import java.util.ArrayList;
import java.util.List;

/**
 * THE scenario behind the exam feedback:
 *   "Your test for registering members passed, but the registerMember() method was
 *    broken and did not register any members."
 *
 * registerMember returns void. A test that only calls it and checks that nothing
 * blew up is green against an EMPTY method body. The state after the call is the
 * only evidence there is.
 */
public class ClubRegistry {

    private final List<Member> members = new ArrayList<>();

    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        members.add(member);
    }

    public void removeMember(Member member) {
        members.remove(member);
    }

    public List<Member> getMembers() {
        return members;
    }

    public int getMemberCount() {
        return members.size();
    }

    public boolean isRegistered(Member member) {
        return members.contains(member);
    }
}
