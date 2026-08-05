package ise.testing.s08_nested_ordered;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Ordered tests share state on purpose, so the class needs PER_CLASS lifecycle to
 * keep one instance alive.
 *
 * Use this only when the task explicitly describes a workflow. Order-dependent tests
 * hide bugs: test 3 can pass purely because test 2 left the right state behind.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderedWorkflowTest {

    private final LibraryCatalogue catalogue = new LibraryCatalogue();

    @Test
    @Order(1)
    @DisplayName("step 1: the catalogue starts out empty")
    void step1_startsEmpty() {
        assertEquals(0, catalogue.size());
    }

    @Test
    @Order(2)
    @DisplayName("step 2: a title is added")
    void step2_addTitle() {
        catalogue.add("Dune");
        assertEquals(1, catalogue.size());
    }

    @Test
    @Order(3)
    @DisplayName("step 3: the same title is removed again")
    void step3_removeTitle() {
        catalogue.remove("Dune");
        assertEquals(0, catalogue.size());
        assertFalse(catalogue.contains("Dune"));
    }
}
