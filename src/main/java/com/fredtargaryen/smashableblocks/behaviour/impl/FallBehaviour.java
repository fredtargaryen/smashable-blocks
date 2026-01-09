// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour.impl;

import com.fredtargaryen.smashableblocks.behaviour.BehaviourValidationException;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviour;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourInternal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FallBehaviour extends SmashableBehaviourInternal {
    public FallBehaviour(SmashableBehaviour sb) throws BehaviourValidationException {
        super(sb);
    }

    public void onSmash(Level level, Entity crasher, BlockPos pos, BlockState state, float speedSq, BlockEntity be) {
        if (FallingBlock.isFree(level.getBlockState(pos.below()))) {
            FallingBlockEntity.fall(level, pos, state);
        }
    }
}
