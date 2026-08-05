package ise.testing.s10_collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The full collection checklist: empty, add, remove, duplicates, order, leakage. */
public class PlaylistCollectionTest {

    private Playlist playlist;

    protected Playlist newPlaylist() {
        return new Playlist();
    }

    @BeforeEach
    void setUp() {
        playlist = newPlaylist();
    }

    @Test
    @DisplayName("a new playlist is empty, checked as both a size and a flag")
    void emptyOnCreation() {
        assertNotNull(playlist.getTracks());
        assertTrue(playlist.getTracks().isEmpty());
        assertEquals(0, playlist.size());
        // TRAP from the exam: assertEquals(new ArrayList<>(), playlist.size()) compares
        // a List to an int and produces "expected: <[]> but was: <0>". Compare like
        // with like: size() against a number, getTracks() against a list.
        assertEquals(List.of(), playlist.getTracks());
    }

    @Test
    @DisplayName("adding a track stores that exact track")
    void addStoresTheTrack() {
        playlist.add("Kid A");

        assertEquals(1, playlist.size());
        assertTrue(playlist.getTracks().contains("Kid A"));
        assertSame("Kid A", playlist.getTracks().get(0));
    }

    @Test
    @DisplayName("removing one track deletes only that track")
    void removeDeletesOnlyThatTrack() {
        playlist.add("Kid A");
        playlist.add("Idioteque");

        assertTrue(playlist.remove("Kid A"));

        assertEquals(1, playlist.size());
        assertFalse(playlist.getTracks().contains("Kid A"));  // it is gone ...
        assertTrue(playlist.getTracks().contains("Idioteque")); // ... and only it
    }

    @Test
    @DisplayName("a list keeps duplicates: adding the same track twice gives size 2")
    void duplicatesAreKept() {
        playlist.add("Kid A");
        playlist.add("Kid A");

        assertEquals(2, playlist.size());
    }

    @Test
    @DisplayName("insertion order is preserved")
    void orderIsPreserved() {
        playlist.add("first");
        playlist.add("second");
        playlist.add("third");

        // assertIterableEquals compares position by position, so a SUT that prepends
        // instead of appending fails here while a size check would not notice.
        assertIterableEquals(List.of("first", "second", "third"), playlist.getTracks());
    }

    @Test
    @DisplayName("ENCAPSULATION LEAK: the getter hands out the internal list, so callers can wreck it")
    void returnedListLeaksInternalState() {
        playlist.add("Kid A");

        playlist.getTracks().clear();   // a caller mutating the object from outside

        // If this line is reached with size 0, encapsulation is broken. That it passes
        // is the bug report, not the feature.
        assertEquals(0, playlist.size());
    }

    @Test
    @DisplayName("the protected getter returns an unmodifiable view, which is the fix")
    void protectedGetterCannotBeMutated() {
        playlist.add("Kid A");

        assertThrows(UnsupportedOperationException.class, () -> playlist.getTracksProtected().clear());
        assertEquals(1, playlist.size());
    }
}
