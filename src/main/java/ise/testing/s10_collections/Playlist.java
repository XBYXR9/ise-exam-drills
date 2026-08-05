package ise.testing.s10_collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Two getters on purpose:
 *   getTracks()          leaks the internal list -- callers can mutate it
 *   getTracksProtected() returns an unmodifiable view
 * Scenario 10 has a test that proves the leak, so you can recognise the smell.
 */
public class Playlist {

    private final List<String> tracks = new ArrayList<>();

    public void add(String track) {
        tracks.add(track);
    }

    public boolean remove(String track) {
        return tracks.remove(track);
    }

    public List<String> getTracks() {
        return tracks;                                   // ENCAPSULATION LEAK
    }

    public List<String> getTracksProtected() {
        return Collections.unmodifiableList(tracks);     // the fix
    }

    public int size() {
        return tracks.size();
    }
}
