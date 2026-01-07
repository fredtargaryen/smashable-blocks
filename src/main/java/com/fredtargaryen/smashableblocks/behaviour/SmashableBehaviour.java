// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

/**
 * A behaviour as parsed from a Smashable Blocks block json file
 * @param behaviour The behaviour represented. Used by SmashableImporter to map to a SmashableBehaviourInternal
 * @param minSpeed The minimum speed of a colliding entity to trigger the behaviour
 * @param maxSpeed The maximum speed of a colliding entity to trigger the behaviour
 * @param requiredWeight Either "light" or "heavy" depending on the kind of smasher that can trigger the behaviour. If null, any weight is valid
 * @param parameters Extra data which may be used by children of SmashableBehaviourInternal
 */
public record SmashableBehaviour(String behaviour, Optional<Float> minSpeed, Optional<Float> maxSpeed, Optional<String> requiredWeight,
                                 Optional<List<SmashableBehaviourParameter>> parameters) {
    public static final Codec<SmashableBehaviour> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("behaviour").forGetter(SmashableBehaviour::behaviour),
                    Codec.FLOAT.optionalFieldOf("min_speed").forGetter(SmashableBehaviour::minSpeed),
                    Codec.FLOAT.optionalFieldOf("max_speed").forGetter(SmashableBehaviour::maxSpeed),
                    Codec.STRING.optionalFieldOf("required_weight").forGetter(SmashableBehaviour::requiredWeight),
                    Codec.list(SmashableBehaviourParameter.CODEC).optionalFieldOf("parameters").forGetter(SmashableBehaviour::parameters)
            ).apply(instance, SmashableBehaviour::new));

    /**
     * Look up a behaviour parameter by name
     * @param paramName The parameter name
     * @return The corresponding value for the parameter
     * @throws BehaviourValidationException if the parameter was defined more than once in the behaviour json
     */
    public Optional<String> getParameterValue(String paramName) throws BehaviourValidationException {
        if (parameters().isEmpty()) return Optional.empty();

        List<SmashableBehaviourParameter> paramsFound = parameters().get().stream()
                .filter(sbp -> sbp.name().equals(paramName))
                .toList();

        return switch (paramsFound.size()) {
            case 0 -> Optional.empty();
            case 1 -> Optional.of(paramsFound.getFirst().value());
            default -> throw new BehaviourValidationException(String.format("Parameter '%s' specified more than once", paramName));
        };
    }
}
