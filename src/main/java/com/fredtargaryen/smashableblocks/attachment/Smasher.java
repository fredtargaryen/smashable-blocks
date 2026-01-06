// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.attachment;

import net.minecraft.world.phys.Vec3;

/**
 * Attachment Type for entities that may be able to break fragile blocks. Entities without this Capability won't be able to.
 */
public interface Smasher {
    /**
     * Called to update the speed of the entity if necessary. Called before determining whether the entity breaks any
     * blocks.
     */
    void update();

    Vec3 getMovementVector();

    /**
     * Return whether the entity is able to break fragile blocks at this particular tick
     */
    boolean isAbleToBreak();

    /**
     * Multiplies the entity's movement vector for the purposes of collecting blocks to break.
     * A value of 1 is good for normal entities on the server, whose motion values are updated every tick.
     * A higher value might be good for entities which project some kind of aura that breaks blocks further away. It is
     * also used for players to try to compromise for the latency of speed updates from the client.
     */
    byte getBreakRangeMultiplier();
}
