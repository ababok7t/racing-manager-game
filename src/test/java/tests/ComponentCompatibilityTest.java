package tests;

import model.components.Engine;
import model.components.Suspension;
import model.components.Transmission;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentCompatibilityTest {

    @Test
    void transmissionIsCompatibleWithMatchingEngineType() {
        Engine engine = new Engine("E", 10, 80, 70, "Atmospheric", 150);
        Transmission transmission = new Transmission("T", 10, 0.9, 70, "Atmospheric");

        assertTrue(transmission.isCompatibleWith(engine));
    }

    @Test
    void transmissionIsNotCompatibleWithDifferentEngineType() {
        Engine engine = new Engine("E", 10, 80, 70, "Atmospheric", 150);
        Transmission transmission = new Transmission("T", 10, 0.9, 70, "Turbo");

        assertFalse(transmission.isCompatibleWith(engine));
    }

    @Test
    void suspensionSupportsEngineWhenMaxWeightIsEnough() {
        Engine engine = new Engine("E", 10, 80, 60, "Atmospheric", 150);
        Suspension suspension = new Suspension("S", 10, 7, 70, 70);

        assertTrue(suspension.canSupportWeight(engine.getWeight()));
    }

    @Test
    void suspensionDoesNotSupportTooHeavyEngine() {
        Engine engine = new Engine("E", 10, 80, 90, "Atmospheric", 150);
        Suspension suspension = new Suspension("S", 10, 7, 70, 70);

        assertFalse(suspension.canSupportWeight(engine.getWeight()));
    }
}

