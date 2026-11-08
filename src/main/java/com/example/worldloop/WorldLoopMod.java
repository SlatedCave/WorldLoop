package com.example.worldloop;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worldloop's first gameplay prototype.
 *
 * Players walking in the Overworld wrap across the east/west and north/south
 * edges of a flat coordinate plane. This does not alter chunk storage or
 * terrain generation, and it does not yet wrap non-player entities.
 */
public final class WorldLoopMod implements ModInitializer {
    public static final String MOD_ID = "worldloop";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** Approximate Earth's equatorial circumference, assuming 1 block ~= 1 metre. */
    public static final long WORLD_WIDTH_BLOCKS = 40_075_000L;

    private static final double HALF_WORLD_WIDTH = WORLD_WIDTH_BLOCKS / 2.0;

    @Override
    public void onInitialize() {
        validateConfiguration();
        ServerTickEvents.END_LEVEL_TICK.register(WorldLoopMod::onLevelTick);

        LOGGER.info(
                "Worldloop loaded: Overworld player wrapping enabled; horizontal circumference={} blocks; coordinate range=[{}, {}).",
                WORLD_WIDTH_BLOCKS,
                -HALF_WORLD_WIDTH,
                HALF_WORLD_WIDTH
        );
    }

    private static void validateConfiguration() {
        if (WORLD_WIDTH_BLOCKS <= 0 || (WORLD_WIDTH_BLOCKS & 1L) != 0L) {
            throw new IllegalStateException("WORLD_WIDTH_BLOCKS must be a positive, even number.");
        }

        // Minecraft's practical horizontal coordinate ceiling is around 30M.
        // Keep the wrap edge comfortably inside that limit.
        if (HALF_WORLD_WIDTH >= 29_000_000.0) {
            throw new IllegalStateException(
                    "Worldloop's half-width is too close to Minecraft's horizontal coordinate limit."
            );
        }
    }

    private static void onLevelTick(ServerLevel level) {
        // The Nether and End are deliberately unchanged in this prototype.
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            // A passenger's vehicle controls its position. Wrapping only the
            // player would detach or desynchronise a mounted player, so riding
            // support is intentionally deferred until vehicle trees are handled.
            if (player.isPassenger()) {
                continue;
            }

            double oldX = player.getX();
            double oldZ = player.getZ();
            double newX = wrapCoordinate(oldX);
            double newZ = wrapCoordinate(oldZ);

            if (newX != oldX || newZ != oldZ) {
                // Same-dimension player teleport. The Y coordinate and view
                // rotation are left unchanged by this position-only overload.
                player.teleportTo(newX, player.getY(), newZ);
                LOGGER.debug(
                        "Wrapped player {} from ({}, {}) to ({}, {}).",
                        player.getGameProfile().name(), oldX, oldZ, newX, newZ
                );
            }
        }
    }

    /** Maps any finite X or Z coordinate into [-WORLD_WIDTH_BLOCKS/2, WORLD_WIDTH_BLOCKS/2). */
    static double wrapCoordinate(double coordinate) {
        return WorldLoopMath.wrapCoordinate(coordinate, WORLD_WIDTH_BLOCKS);
    }
}
