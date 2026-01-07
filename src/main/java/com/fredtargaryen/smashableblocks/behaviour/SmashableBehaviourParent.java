// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * A series of SmashableBehaviours executed in order on collision
 * @param blockStates The blockstate string describing the block states that will have these behaviours
 * @param behaviours The behaviours themselves, executed in order
 */
public record SmashableBehaviourParent(String blockStates, List<SmashableBehaviour> behaviours) {
    public static final Codec<SmashableBehaviourParent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("blockstates").forGetter(SmashableBehaviourParent::blockStates),
                    Codec.list(SmashableBehaviour.CODEC).fieldOf("behaviours").forGetter(SmashableBehaviourParent::behaviours)
            ).apply(instance, SmashableBehaviourParent::new));
}
