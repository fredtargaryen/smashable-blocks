// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour.impl;

import com.fredtargaryen.smashableblocks.behaviour.BehaviourValidationException;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviour;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourInternal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BreakBehaviour extends SmashableBehaviourInternal {
    public BreakBehaviour(SmashableBehaviour sb) throws BehaviourValidationException {
        super(sb);
    }

    @Override
    public void onSmash(Level level, Entity crasher, BlockPos pos, BlockState state, float speedSq, BlockEntity be) {
        level.destroyBlock(pos, true);
    }
}
