// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour;

import com.fredtargaryen.smashableblocks.DataReference;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A "compiled" version of SmashableBehaviour which takes and transforms the necessary data from the json file
 * and discards the unnecessary information. Custom behaviours must extend this class.
 */
public abstract class SmashableBehaviourInternal {
    /**
     * The squared minimum speed (inclusive) an entity should travel at to trigger the behaviour.
     */
    public final float minSpeedSq;

    /**
     * The squared maximum speed (exclusive) an entity should travel at to trigger the behaviour.
     */
    public final float maxSpeedSq;

    public SmashableBehaviourInternal(SmashableBehaviour sb) throws BehaviourValidationException {
        float minSpeed = sb.minSpeed().orElse(DataReference.MINIMUM_ENTITY_SPEED);
        float maxSpeed = sb.maxSpeed().orElse(DataReference.MAXIMUM_ENTITY_SPEED);
        if (minSpeed < 0f) throw new BehaviourValidationException("min_speed %s cannot be less than 0", minSpeed);
        if (maxSpeed > DataReference.MAXIMUM_ENTITY_SPEED) throw new BehaviourValidationException("max_speed %s cannot be greater than %s", maxSpeed, DataReference.MAXIMUM_ENTITY_SPEED);
        this.minSpeedSq = minSpeed * minSpeed;
        this.maxSpeedSq = maxSpeed * maxSpeed;
    }

    /**
     * This method executes when an entity with the Smasher capability is about to collide with the block with this behaviour.
     * @param level   The Level in which the collision will take place
     * @param smasher The entity doing the collision
     * @param pos     The position of the block to be collided with
     * @param state   The BlockState that is about to be smashed
     * @param speedSq The squared speed of the smasher
     * @param be      The BlockEntity, if any, on the block to be collided with
     */
    public abstract void onSmash(Level level, Entity smasher, BlockPos pos, BlockState state, float speedSq, BlockEntity be);
}
