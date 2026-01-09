// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks.registry;

import com.fredtargaryen.smashableblocks.DataReference;
import com.fredtargaryen.smashableblocks.behaviour.SmashableBehaviourParent;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class CustomRegistries {
    public static final ResourceKey<Registry<List<SmashableBehaviourParent>>> BLOCK_REGISTRY_KEY = ResourceKey.createRegistryKey(DataReference.getResourceLocation("blocks"));
    public static final DeferredRegister<List<SmashableBehaviourParent>> BLOCK_DEFERRED_REGISTER = DeferredRegister.create(BLOCK_REGISTRY_KEY, DataReference.MODID);

    public static void init(IEventBus bus) {
        BLOCK_DEFERRED_REGISTER.register(bus);
    }

    public static void onDataPackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BLOCK_REGISTRY_KEY, Codec.list(SmashableBehaviourParent.CODEC));
    }
}
