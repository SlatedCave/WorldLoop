package com.example.worldloop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldLoopMathTest {
    private static final long WIDTH = 40_075_000L;
    private static final double HALF = WIDTH / 2.0;

    @Test
    void keepsCoordinatesInsideTheHalfOpenWorldInterval() {
        assertEquals(0.0, WorldLoopMath.wrapCoordinate(0.0, WIDTH));
        assertEquals(-HALF, WorldLoopMath.wrapCoordinate(-HALF, WIDTH));
        assertEquals(-HALF, WorldLoopMath.wrapCoordinate(HALF, WIDTH));
        assertEquals(HALF - 1.0, WorldLoopMath.wrapCoordinate(HALF - 1.0, WIDTH));
    }

    @Test
    void wrapsAcrossThePositiveAndNegativeEdges() {
        assertEquals(-HALF, WorldLoopMath.wrapCoordinate(HALF, WIDTH));
        assertEquals(-HALF + 1.0, WorldLoopMath.wrapCoordinate(HALF + 1.0, WIDTH));
        assertEquals(HALF - 1.0, WorldLoopMath.wrapCoordinate(-HALF - 1.0, WIDTH));
    }

    @Test
    void supportsMoreThanOneFullLap() {
        assertEquals(123.0, WorldLoopMath.wrapCoordinate(3.0 * WIDTH + 123.0, WIDTH));
        assertEquals(-456.0, WorldLoopMath.wrapCoordinate(-2.0 * WIDTH - 456.0, WIDTH));
    }

    @Test
    void resultAlwaysStaysInsideTheWorldInterval() {
        for (double coordinate = -100_000_000.0; coordinate <= 100_000_000.0; coordinate += 123_457.0) {
            double wrapped = WorldLoopMath.wrapCoordinate(coordinate, WIDTH);
            assertTrue(wrapped >= -HALF, "wrapped coordinate was below the minimum: " + wrapped);
            assertTrue(wrapped < HALF, "wrapped coordinate was at or above the maximum: " + wrapped);
        }
    }

    @Test
    void rejectsInvalidInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> WorldLoopMath.wrapCoordinate(Double.NaN, WIDTH));
        assertThrows(IllegalArgumentException.class,
                () -> WorldLoopMath.wrapCoordinate(Double.POSITIVE_INFINITY, WIDTH));
        assertThrows(IllegalArgumentException.class,
                () -> WorldLoopMath.wrapCoordinate(0.0, 0L));
        assertThrows(IllegalArgumentException.class,
                () -> WorldLoopMath.wrapCoordinate(0.0, 101L));
    }
}
