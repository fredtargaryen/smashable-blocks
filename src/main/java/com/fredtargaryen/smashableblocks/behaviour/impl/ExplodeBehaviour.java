// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour.impl;

import com.fredtargaryen.smashableblocks.behaviour.BehaviourValidationException;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviour;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourInternal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class ExplodeBehaviour extends SmashableBehaviourInternal {
    private final float strength;
    private final Level.ExplosionInteraction interaction;

    public ExplodeBehaviour(SmashableBehaviour sb) throws BehaviourValidationException {
        super(sb);
        Optional<String> strength = sb.getParameterValue("strength");
        if (strength.isEmpty()) throw BehaviourValidationException.missingParameter("strength");

        try {
            this.strength = Float.parseFloat(strength.get());
            if (this.strength < 1f || this.strength > 20f) throw new BehaviourValidationException(String.format("Invalid value \"%s\" for explosion strength: must be between 0 and 20 inclusive", strength.get()));
        }
        catch (NumberFormatException nfe) {
            throw new BehaviourValidationException("Invalid value \"%s\" for strength", strength.get());
        }

        Optional<String> destroyBlocks = sb.getParameterValue("destroyBlocks");
        if (destroyBlocks.isEmpty()) {
            // Assume false
            this.interaction = Level.ExplosionInteraction.BLOW;
        }
        else {
            switch(destroyBlocks.get()) {
                case "true":
                    this.interaction = Level.ExplosionInteraction.BLOCK;
                    break;
                case "false":
                    this.interaction = Level.ExplosionInteraction.BLOW;
                    break;
                default:
                    throw new BehaviourValidationException("Invalid value \"%s\" for destroyBlocks. Should be either \"true\" or \"false\"", destroyBlocks.get());
            }
        }
    }

    public void onSmash(Level level, Entity crasher, BlockPos pos, BlockState state, float speedSq, BlockEntity be) {
        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                this.strength, this.interaction);
    }
}
