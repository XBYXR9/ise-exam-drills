package ise.mutants.testing;

import ise.testing.s08_nested_ordered.CatalogueStructureTest;
import ise.testing.s08_nested_ordered.LibraryCatalogue;
import ise.testing.s10_collections.Playlist;
import ise.testing.s10_collections.PlaylistCollectionTest;
import ise.testing.s11_validation.CatalogueItem;
import ise.testing.s11_validation.CatalogueItemValidationTest;
import org.junit.jupiter.api.Tag;

/** MUTANTS for testing scenarios 08, 10 and 11. */
final class CollectionMutants {

    private CollectionMutants() {
    }
}

// ---------------------------------------------------------------------------
// s08 -- MUTATION: remove wipes the whole collection instead of one element
// ---------------------------------------------------------------------------
class CatalogueThatRemovesEverything extends LibraryCatalogue {

    private final java.util.List<String> titles = new java.util.ArrayList<>();

    @Override
    public void add(String title) {
        titles.add(title);
    }

    @Override
    public boolean remove(String title) {
        boolean had = titles.contains(title);
        titles.clear();          // mutated: takes the survivors with it
        return had;
    }

    @Override
    public int size() {
        return titles.size();
    }

    @Override
    public boolean contains(String title) {
        return titles.contains(title);
    }
}

@Tag("mutant")
class CatalogueStructureTest_RemovesEverything extends CatalogueStructureTest {

    @Override
    protected LibraryCatalogue newCatalogue() {
        return new CatalogueThatRemovesEverything();
    }
    // CAUGHT BY: removeDeletesOnlyThatTitle -- specifically the SURVIVOR assertion
    // assertTrue(contains("Solaris")). Checking only that "Dune" is gone would pass.
}

// ---------------------------------------------------------------------------
// s10 -- MUTATION: operation swapped, the list is built back to front
// ---------------------------------------------------------------------------
class PlaylistThatPrepends extends Playlist {

    @Override
    public void add(String track) {
        getTracks().add(0, track);   // mutated: prepend instead of append
    }
}

@Tag("mutant")
class PlaylistCollectionTest_Prepends extends PlaylistCollectionTest {

    @Override
    protected Playlist newPlaylist() {
        return new PlaylistThatPrepends();
    }
    // CAUGHT BY: orderIsPreserved -- assertIterableEquals compares position by
    // position. A size check and a contains check both pass against this mutant.
}

// ---------------------------------------------------------------------------
// s10 -- MUTATION: remove deletes every element
// ---------------------------------------------------------------------------
class PlaylistThatRemovesEverything extends Playlist {

    @Override
    public boolean remove(String track) {
        boolean had = getTracks().contains(track);
        getTracks().clear();
        return had;
    }
}

@Tag("mutant")
class PlaylistCollectionTest_RemovesEverything extends PlaylistCollectionTest {

    @Override
    protected Playlist newPlaylist() {
        return new PlaylistThatRemovesEverything();
    }
    // CAUGHT BY: removeDeletesOnlyThatTrack -- the exact size AND the survivor check.
}

// ---------------------------------------------------------------------------
// s11 -- MUTATION: inverted boundary, zero is wrongly rejected
// ---------------------------------------------------------------------------
class CatalogueItemThatRejectsZero extends CatalogueItem {

    CatalogueItemThatRejectsZero(String name, double price, int stock) {
        super(name, price, stock);
        if (price <= 0.0) {          // mutated from price < 0.0
            throw new IllegalArgumentException("Price must not be negative");
        }
        if (stock <= 0) {            // mutated from stock < 0
            throw new IllegalArgumentException("Stock must not be negative");
        }
    }
}

@Tag("mutant")
class CatalogueItemValidationTest_RejectsZero extends CatalogueItemValidationTest {

    @Override
    protected CatalogueItem newItem(String name, double price, int stock) {
        return new CatalogueItemThatRejectsZero(name, price, stock);
    }
    // CAUGHT BY: zeroIsAccepted -- the ONLY test in the class that catches it. Every
    // invalid-input test still passes, which is why the valid boundary needs its own test.
}
