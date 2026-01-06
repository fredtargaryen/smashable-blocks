// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class SmashableBehaviourParentInternal {
    private final List<SmashableBehaviourInternal> behaviours;

    public SmashableBehaviourParentInternal(List<SmashableBehaviourInternal> behaviours) {
        this.behaviours = behaviours;
    }

    /**
     * This method executes when an entity with the Smasher capability is about to collide with the block with this set of behaviours.
     * @param level   The Level in which the collision will take place
     * @param smasher The entity doing the collision
     * @param pos     The position of the block to be collided with
     * @param speedSq The squared speed of the smasher
     * @param state   The BlockState that is about to be smashed
     * @param be      The BlockEntity, if any, on the block to be collided with
     */
    public void onSmash(Level level, Entity smasher, BlockPos pos, BlockState state, float speedSq, BlockEntity be) {
        this.behaviours.forEach(sbi -> {
            if (speedSq >= sbi.minSpeedSq && speedSq < sbi.maxSpeedSq) {
                sbi.onSmash(level, smasher, pos, state, speedSq, be);
            }
        });
    }
}
