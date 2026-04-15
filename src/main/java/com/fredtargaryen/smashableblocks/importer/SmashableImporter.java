// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.importer;

import com.fredtargaryen.smashableblocks.SmashableBlocksBase;
import com.fredtargaryen.smashableblocks.behaviour.*;
import com.fredtargaryen.smashableblocks.behaviour.impl.*;
import com.fredtargaryen.smashableblocks.util.BlockStateUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import static com.fredtargaryen.smashableblocks.registry.CustomRegistries.BLOCK_REGISTRY_KEY;

public final class SmashableImporter {
    private final HashMap<String, Function<SmashableBehaviour, SmashableBehaviourInternal>> behaviourFactories;

    public SmashableImporter() {
        this.behaviourFactories = new HashMap<>();
    }

    public void resetBehaviours() {
        this.behaviourFactories.clear();
    }

    public void addDefaultBehaviourFactories() {
        this.behaviourFactories.put("break", BreakBehaviour::new);
        this.behaviourFactories.put("change", ChangeBehaviour::new);
        this.behaviourFactories.put("explode", ExplodeBehaviour::new);
        this.behaviourFactories.put("fall", FallBehaviour::new);
        this.behaviourFactories.put("sound", SoundBehaviour::new);
    }

    public void addBehaviourFactory(String behaviourName, Function<SmashableBehaviour, SmashableBehaviourInternal> behaviourFactory) {
        if (behaviourFactories.containsKey(behaviourName)) {
            SmashableBlocksBase.warn(String.format("Overwriting an existing behaviour for the behaviour '%s'!", behaviourName));
        }
        else {
            SmashableBlocksBase.info(String.format("Adding third-party smashable behaviour %s", behaviourName));
        }
        behaviourFactories.put(behaviourName, behaviourFactory);
    }

    public void collectAndImportBehaviourFiles(MinecraftServer server,
                                               HashMap<Block, SmashableBehaviourParentInternal> blockBehaviourMap,
                                               HashSet<Block> blocksWithStateOverrides,
                                               HashMap<BlockState, SmashableBehaviourParentInternal> stateBehaviourMap) {
        server.registryAccess().lookupOrThrow(BLOCK_REGISTRY_KEY).entrySet()
                .forEach(entry -> {
                    SmashableBlocksBase.info(String.format("Loading smashable behaviour data file %s", entry.getKey()));
                    List<SmashableBehaviourParent> parents = entry.getValue();
                    parents.forEach(sbp -> {
                        String states = sbp.blockStates();
                        SmashableBlocksBase.warn(String.format("Loading behaviours for %s", states));

                        try {
                            // Parse blockStates string into a list of BlockStates to apply behaviours to
                            BlockStateUtil.BlockStatesParseResult result = BlockStateUtil.getAllBlocksOrStatesForString(states);
                            SmashableBehaviourParentInternal behaviourParent = this.importBehaviourParent(sbp);
                            // Map list of blockStates to imported behaviours
                            result.blocks.forEach(b -> blockBehaviourMap.put(b, behaviourParent));
                            blocksWithStateOverrides.addAll(result.blocksWithStateOverrides);
                            result.blockStates.forEach(bs -> stateBehaviourMap.put(bs, behaviourParent));
                        } catch (BehaviourValidationException e) {
                            SmashableBlocksBase.error(String.format("An error occurred when loading %s:", entry.getKey()));
                            SmashableBlocksBase.error(e.getMessage());
                        }
                    });
                });
    }

    public SmashableBehaviourParentInternal importBehaviourParent(SmashableBehaviourParent sbp) throws BehaviourValidationException {
        return new SmashableBehaviourParentInternal(
                sbp.behaviours().stream()
                        .map(sb -> {
                            if (!behaviourFactories.containsKey(sb.behaviour())) {
                                throw new BehaviourValidationException("The behaviour type '%s' has not been defined", sb.behaviour());
                            }

                            return behaviourFactories.get(sb.behaviour()).apply(sb);
                        })
                        .toList());
    }
}
