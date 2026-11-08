package com.example.worldloop;

/**
 * Coordinate math for Worldloop's flat, wraparound horizontal plane.
 *
 * The result is always in the half-open interval [-circumference / 2,
 * circumference / 2). Minecraft coordinates in the current configuration
 * are small enough that double precision represents every block boundary
 * exactly.
 */
final class WorldLoopMath {
    private WorldLoopMath() {
    }

    static double wrapCoordinate(double coordinate, long circumference) {
        if (!Double.isFinite(coordinate)) {
            throw new IllegalArgumentException("Coordinate must be finite.");
        }
        if (circumference <= 0 || (circumference & 1L) != 0L) {
            throw new IllegalArgumentException("Circumference must be a positive, even number of blocks.");
        }

        double width = (double) circumference;
        double halfWidth = width / 2.0;

        // floor (rather than remainder) makes negative coordinates wrap
        // symmetrically and handles travel by more than one full lap.
        double wrapped = coordinate - Math.floor((coordinate + halfWidth) / width) * width;

        // Protect the documented half-open interval from a possible final
        // floating-point rounding step at an exact boundary.
        if (wrapped >= halfWidth) {
            wrapped -= width;
        } else if (wrapped < -halfWidth) {
            wrapped += width;
        }

        return wrapped;
    }
}
