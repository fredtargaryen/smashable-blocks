// Copyright 2026 FredTargaryen
// See README.md for full copyright notice
package com.fredtargaryen.smashableblocks;

import com.fredtargaryen.smashableblocks.registry.CustomRegistries;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

import static com.fredtargaryen.smashableblocks.DataReference.MODID;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MODID)
@EventBusSubscriber
public final class SmashableBlocksBase {
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SmashableBlocksBase(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::addModRegistries);

        AttachmentTypes.register(modEventBus);

        CustomRegistries.init(modEventBus);
    }

    private void addModRegistries(DataPackRegistryEvent.NewRegistry event) {
        CustomRegistries.onDataPackRegistry(event);
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        SmashSystem.INSTANCE.setup(event.getServer());
    }

    //////////////////
    //LOGGER METHODS//
    //////////////////
    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }
}
