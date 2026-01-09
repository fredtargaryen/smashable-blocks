// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A parameter for a SmashableBehaviour. A simple key-value pair
 * @param name The parameter name
 * @param value The parameter value
 */
public record SmashableBehaviourParameter(String name, String value) {
    public static final Codec<SmashableBehaviourParameter> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("name").forGetter(SmashableBehaviourParameter::name),
                    Codec.STRING.fieldOf("value").forGetter(SmashableBehaviourParameter::value)
            ).apply(instance, SmashableBehaviourParameter::new));
}
