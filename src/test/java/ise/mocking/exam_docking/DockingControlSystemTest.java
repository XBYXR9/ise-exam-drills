package ise.mocking.exam_docking;

import org.easymock.EasyMockExtension;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FINAL EXAM, EXERCISE 4 ("Mocky") -- the same exercise as exam_vehicle with one
 * guard instead of two. Worth drilling separately because the attribute names are
 * part of the grading.
 *
 * Task 1: set up DockingControlSystemTest following the Mock Object Pattern, with
 *         the attributes dockingControlSystem and spaceshipMock, annotated.
 *         Graded feedback last time: "You did not configure the test subject correctly."
 * Task 2: testDockAstronautSuccessful()
 * Task 3: testDockAstronautFailure()
 *         Graded feedback: "Make sure that your implementation of
 *         testDockAstronautFailure() fails if the dock() method is never executed."
 *         -> that sentence is a literal instruction to call verify(spaceshipMock).
 */
@ExtendWith(EasyMockExtension.class)
class DockingControlSystemTest {

    @TestSubject
    private DockingControlSystem dockingControlSystem = new DockingControlSystem();

    @Mock
    private Spaceship spaceshipMock;

    private Astronaut astronaut;

    @BeforeEach
    void setUp() {
        astronaut = new Astronaut();
    }

    @Test
    @DisplayName("the ship accepts the docking, so the astronaut is assigned to it")
    void testDockAstronautSuccessful() {
        expect(spaceshipMock.dock(astronaut)).andReturn(true);
        replay(spaceshipMock);

        dockingControlSystem.dock(astronaut, spaceshipMock);

        verify(spaceshipMock);
        assertEquals(1, astronaut.getAssignedShips().size());
        assertTrue(astronaut.getAssignedShips().contains(spaceshipMock));
    }

    @Test
    @DisplayName("the ship refuses: dock() still had to be attempted, and no ship is assigned")
    void testDockAstronautFailure() {
        expect(spaceshipMock.dock(astronaut)).andReturn(false);
        replay(spaceshipMock);

        dockingControlSystem.dock(astronaut, spaceshipMock);

        // Without this call the test passes against an empty dock() body, because an
        // astronaut with no ships is exactly what an empty body produces. verify() is
        // the only thing that distinguishes "refused" from "never asked".
        verify(spaceshipMock);

        assertEquals(0, astronaut.getAssignedShips().size());
        assertTrue(astronaut.getAssignedShips().isEmpty());
        assertFalse(astronaut.getAssignedShips().contains(spaceshipMock));
    }
}
