// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour.impl;

import com.fredtargaryen.smashableblocks.behaviour.BehaviourValidationException;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviour;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourInternal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class SoundBehaviour extends SmashableBehaviourInternal {
    private final SoundEvent soundToPlay;

    public SoundBehaviour(SmashableBehaviour sb) throws BehaviourValidationException {
        super(sb);
        Optional<String> soundIdHolder = sb.getParameterValue("sound");
        if (soundIdHolder.isEmpty()) throw BehaviourValidationException.missingParameter("sound");
        String soundIdStr = soundIdHolder.get();
        Identifier soundId = Identifier.tryParse(soundIdStr);
        if (soundId == null) throw new BehaviourValidationException("Invalid sound identifier '%s'", soundIdStr);
        Optional<Holder.Reference<SoundEvent>> se = BuiltInRegistries.SOUND_EVENT.get(soundId);
        if (se.isEmpty()) {
            throw new BehaviourValidationException("Unrecognised sound identifier '%s'", soundIdStr);
        }
        this.soundToPlay = se.get().value();
    }

    public void onSmash(Level level, Entity crasher, BlockPos pos, BlockState state, float speedSq, BlockEntity be) {
        level.playSound(null, pos, this.soundToPlay, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
