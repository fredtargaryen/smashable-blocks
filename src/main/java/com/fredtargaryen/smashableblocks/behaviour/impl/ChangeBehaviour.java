// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.behaviour.impl;

import com.fredtargaryen.smashableblocks.behaviour.BehaviourValidationException;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviour;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourInternal;
import com.fredtargaryen.smashableblocks.util.BlockStateSubsetDescription;
import com.fredtargaryen.smashableblocks.util.BlockStateUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public final class ChangeBehaviour extends SmashableBehaviourInternal {
    private Optional<BlockState> newState;
    private final Optional<HashMap<String, String>> propertyMap;

    public ChangeBehaviour(SmashableBehaviour sb) throws BehaviourValidationException {
        super(sb);
        Optional<String> newState = sb.getParameterValue("newState");
        if (newState.isEmpty()) throw BehaviourValidationException.missingParameter("newState");
        BlockStateSubsetDescription bssd = BlockStateUtil.getDescriptionFromBlockStateStringGeneral(newState.get());
        if (bssd.nameIsTag) throw new BehaviourValidationException("Cannot use a tag as a newState value");

        if (bssd.name.equals(BlockStateUtil.CURRENT_STATE)) {
            this.newState = Optional.empty();
        } else {
            this.newState = Optional.of(BlockStateUtil.getBlockFromString(bssd.name).defaultBlockState());
        }

        if (bssd.properties.isEmpty()) {
            this.propertyMap = Optional.empty();
            return;
        }
        String props = bssd.properties.get();
        if (props.isEmpty()) {
            this.propertyMap = Optional.empty();
            return;
        }
        HashMap<String, String> propertyMap = buildPropertyMap(props);
        if (propertyMap.isEmpty()) {
            this.propertyMap = Optional.empty();
            return;
        }
        if (this.newState.isPresent()) {
            this.newState = Optional.of(applyPropertyMap(this.newState.get(), propertyMap));
            this.propertyMap = Optional.empty();
            return;
        }
        this.propertyMap = Optional.of(propertyMap);
    }

    @Override
    public void onSmash(Level level, Entity crasher, BlockPos pos, BlockState state, float speedSq, BlockEntity be) {
        newState.ifPresentOrElse(
                s -> level.setBlockAndUpdate(pos, s),
                () -> this.propertyMap.ifPresent(props ->
                        level.setBlockAndUpdate(pos, applyPropertyMap(state, props))));
    }

    /**
     * Build a mapp between properties and their values using the BlockState properties string
     *
     * @param properties The properties string
     * @return The built map
     * @throws BehaviourValidationException If the string is formatted unexpectedly
     */
    private static HashMap<String, String> buildPropertyMap(String properties) throws BehaviourValidationException {
        HashMap<String, String> map = new HashMap<>();
        String[] pairs = properties.split(",");
        if (pairs.length == 0) return map;

        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length != 2)
                throw new BehaviourValidationException("The text '%s' must be two pieces of text separated by one = sign.", pair);
            map.put(parts[0], parts[1]);
        }

        return map;
    }

    /**
     * Get properties on the BlockState and apply the corresponding properties and values in the map if any
     *
     * @param oldState    The original BlockState to apply property values to
     * @param propertyMap A map between BlockState property names and values
     * @return The modified BlockState
     */
    private static BlockState applyPropertyMap(BlockState oldState, HashMap<String, String> propertyMap) {
        BlockState newState = oldState;
        Collection<Property<?>> existingProps = oldState.getProperties();
        for (Property<?> prop : existingProps) {
            String propName = prop.getName();
            if (propertyMap.containsKey(propName)) {
                newState = setValueByString(newState, prop, propertyMap.get(propName));
            }
        }
        return newState;
    }

    private static <T extends Comparable<T>> BlockState setValueByString(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }
}
